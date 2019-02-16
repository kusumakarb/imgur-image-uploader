package com.example.services

import java.time.ZonedDateTime
import java.util.UUID

import akka.Done
import akka.actor.ActorSystem
import akka.util.ByteString
import com.example.exceptions.{ FileLengthExceededException, InvalidMimeTypeException, JobNotFoundException }
import com.example.models.APIResponses.{ ImgurUploadedURLs, JobInfo => JobInfoResponse, Upload => UploadResponse }
import com.example.models.Types.{ JobId, URL }
import com.example.models._
import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import play.api.Configuration
import play.api.cache.AsyncCacheApi
import play.api.libs.json.{ JsError, JsResult, JsSuccess }
import play.api.libs.ws.{ WSClient, WSResponse }

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{ Failure, Success, Try }

final class ImageUploadService @Inject()(
  cache: AsyncCacheApi,
  ws: WSClient,
  configuration: Configuration,
  cacheService: CacheService)(implicit system: ActorSystem, fileUploadExecutionContext: FileUploadExecutionContext)
    extends LazyLogging {

  final val UploadedURLs = "uploadedURLs"

  /**
    * Downloads the files from given URL and uploads to Imgur
    *
    * @param urls List of URLs
    * @return Immediately returns JobId and runs job in the background
    */
  def upload(urls: List[URL]): Future[UploadResponse] = {

    val jobId: JobId = s"${Prefixes.JobId}${UUID.randomUUID.toString}"

    (for {
      _ <- cacheService.updateJobStatus(jobId, JobStatus.Pending, None, urls)
      _ <- uploadUtil(urls, jobId)
    } yield {
      cacheService.updateJobStatus(jobId, JobStatus.Complete, Some(ZonedDateTime.now), urls)
    }).recoverWith {
      case NonFatal(e) =>
        cacheService.updateJobStatus(jobId, JobStatus.Complete, Some(ZonedDateTime.now), urls)
        Future.failed(e)
    }
    Future.successful(UploadResponse(jobId))
  }

  /**
    * Returns the Job status information
    *
    * @param jobId Id of the job for which status is requested
    * @return Job status information
    */
  def getJobInfo(jobId: JobId): Future[JobInfoResponse] =
    cache.get[JobInfo](jobId).flatMap {
      case Some(jobInfo) =>
        val uploaded =
          Uploaded(pending = List.empty[URL], complete = List.empty[URL], failed = List.empty[URL])

        val updatedUploaded = jobInfo.uploadInfo.foldLeft(uploaded) {
          case (accumulator, (url, uploadInfo)) =>
            uploadInfo.uploadStatus match {
              case UploadStatus.Pending => accumulator.copy(pending = url +: accumulator.pending)
              case UploadStatus.Complete =>
                accumulator.copy(complete = uploadInfo.uploadedURL.get +: accumulator.complete)
              case UploadStatus.Failed => accumulator.copy(failed = url +: accumulator.failed)
            }
        }

        Future.successful(
          JobInfoResponse(id = jobId,
                          created = jobInfo.created,
                          finished = jobInfo.finished,
                          status = jobInfo.status,
                          uploaded = updatedUploaded))
      case None =>
        Future.failed[JobInfoResponse](JobNotFoundException(s"Job with Id $jobId is not found in the cache"))
    }

  /**
    * Fetches Imgur links of all successfully uploaded images
    *
    * @return List of Imgur links of all successfully uploaded images
    */
  def getImgurUploadedImages: Future[ImgurUploadedURLs] =
    cacheService.getImgurUploadedImages(UploadedURLs).map {
      case Some(uploadedURLs) => ImgurUploadedURLs(uploadedURLs)
      case None               => ImgurUploadedURLs(List.empty[URL])
    }

  /**
    * Utility function that Downloads the images from the given URLs and uploads them to Imgur.
    *
    * @param urls List of URLs whose content should be downloaded and uploaded
    * @param jobId Id of the current Job
    * @return
    */
  private def uploadUtil(urls: List[URL], jobId: JobId): Future[List[Done]] =
    Future.sequence(urls.map { url =>
      (for {
        _                <- cacheService.updateJobStatus(jobId, JobStatus.InProgress, None, urls)
        imgurAPIResponse <- downloadAndUpload(url, jobId)
      } yield {
        val dataJSResult: JsResult[Data] = (imgurAPIResponse.json \ "data").validate[Data]
        dataJSResult match {
          case data: JsSuccess[Data] =>
            val imgurURL = data.get.link
            cacheService.updateURLStatus(jobId, url, UploadStatus.Complete, Some(imgurURL))
            cacheService.updateUploadedURLs(UploadedURLs, imgurURL)
          case _: JsError =>
            imgurAPIResponse.json.validate[ImgurErrorResponse].map { imgurErrorResponse =>
              logger.error(s"$url upload to imgur failed: ${imgurErrorResponse.data.error}")
            }
            cacheService.updateURLStatus(jobId, url, UploadStatus.Failed, None)
        }
      }).flatten.recoverWith {
        case NonFatal(_) =>
          cacheService.updateURLStatus(jobId, url, UploadStatus.Failed, None)
      }
    })

  /**
    * Downloads the image from the given URL, validates the mime type and uploads it to Imgur
    *
    * @param url URL of the image
    * @param jobId Job Id of the upload job
    * @return API Response of the Imgur upload
    */
  private def downloadAndUpload(url: URL, jobId: JobId): Future[WSResponse] = {

    val clientID  = configuration.underlying.getString("imgur.clientID")
    val uploadURL = configuration.underlying.getString("imgur.uploadURL")

    // TODO: Retries and timeout
    for {
      downloadedFileResponse <- ws.url(url).get().recoverWith(downloadAndUploadErrorHandler(jobId, url, "Download"))
      downloadedFile         <- Future.fromTry(getByteString(downloadedFileResponse, url))
      imgurAPIResponse <- ws
        .url(uploadURL)
        .addHttpHeaders("Authorization" -> s"Client-ID $clientID")
        .post(downloadedFile)
        .recoverWith(downloadAndUploadErrorHandler(jobId, url, "Upload"))
    } yield imgurAPIResponse
  }

  /**
    * Updates the status of the of the given URL on failure in the Download/Upload process
    *
    * @param jobId JobId of the upload job
    * @param url URL that caused the error
    * @param action Upload/Download action
    * @return Throwable that caused the exception
    */
  private def downloadAndUploadErrorHandler(jobId: JobId,
                                            url: URL,
                                            action: String): PartialFunction[Throwable, Future[WSResponse]] = {
    case NonFatal(e) =>
      cacheService.updateURLStatus(jobId, url, UploadStatus.Failed, None)
      logger.error(s"$action of $url failed")
      Future.failed[WSResponse](e)
  }

  /**
    * Validates the mime type of the response  and returns ByteString of the response
    *
    * @param downloadedFile Data downloaded from the URL
    * @param url URL from which the data is downloaded
    * @return ByteString data
    */
  private def getByteString(downloadedFile: WSResponse, url: URL): Try[ByteString] = {
    val isValidMimeType         = validateMimeType(downloadedFile)
    val fileAsBytes: ByteString = downloadedFile.bodyAsBytes
    val fileLengthInBytes       = fileAsBytes.length
    val maxFileSizeInBytes      = configuration.underlying.getLong("imgur.maxFileSizeInBytes")
    val isValidContentLength    = fileLengthInBytes < maxFileSizeInBytes
    if (isValidMimeType && isValidContentLength) {
      Success[ByteString](fileAsBytes)
    } else {
      if (!isValidMimeType) {
        logger.error(s"$url is not a valid Image. Content-Type: ${downloadedFile.contentType}")
        Failure[ByteString](InvalidMimeTypeException(s"$url is not a valid Image"))
      } else {
        logger.error(s"$url file length $fileLengthInBytes Bytes exceeds the allowed size of $maxFileSizeInBytes Bytes")
        Failure[ByteString](FileLengthExceededException("File size exceeded the allowed size"))
      }

    }
  }

  /**
    * Validates if the obtained response has the Image mime type.
    *
    * @param downloadedFile Downloaded data from the URL
    * @return True if the mime type is of Image, False otherwise
    */
  private def validateMimeType(downloadedFile: WSResponse): Boolean = {
    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types#Image_types
    val listOfImageMimeTypes =
      List("image/jpeg", "image/gif", "image/png", "image/svg+xml", "image/x-icon", "image/vnd.microsoft.icon")
    listOfImageMimeTypes.contains(downloadedFile.contentType)
  }
}
