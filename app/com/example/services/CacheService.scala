package com.example.services

import java.time.ZonedDateTime

import akka.Done
import com.example.exceptions.InvalidCacheStateException
import com.example.models.Types.{ JobId, URL }
import com.example.models._
import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import play.api.cache.AsyncCacheApi

import scala.concurrent.{ ExecutionContext, Future }

final class CacheService @Inject()(cache: AsyncCacheApi)(implicit ec: ExecutionContext) extends LazyLogging {

  /**
    * Updates the status of a URL being processed
    *
    * @param jobId Id of the Job
    * @param url URL of the file being processed
    * @param uploadStatus Status of URL being processed
    * @param uploadedURL Link of Imgur URL on returned upon successful upload
    * @return
    */
  def updateURLStatus(jobId: JobId, url: URL, uploadStatus: UploadStatus, uploadedURL: Option[URL]): Future[Done] =
    cache.get[JobInfo](jobId).flatMap {
      case Some(jobInfo) =>
        val updatedUploadInfo: Map[URL, UploadInfo] = jobInfo.uploadInfo + (url -> UploadInfo(uploadedURL,
                                                                                              uploadStatus))
        cache.set(jobId, jobInfo.copy(uploadInfo = updatedUploadInfo))
      case None => Future.failed[Done](InvalidCacheStateException(s"Job with Id $jobId is not found in the cache"))
    }

  /**
    * Updates the status of the job
    *
    * @param jobId Id of the job
    * @param jobStatus Current Status of the job
    * @return
    */
  def updateJobStatus(jobId: JobId,
                      jobStatus: JobStatus,
                      finishTime: Option[ZonedDateTime],
                      urls: List[URL]): Future[Done] =
    cache.get[JobInfo](jobId).flatMap { maybeJobInfo =>
      val updatedJobInfo = maybeJobInfo match {
        case Some(jobInfo) =>
          jobInfo.copy(status = jobStatus, finished = finishTime)
        case None =>
          JobInfo.create(jobStatus, urls)
      }
      cache.set(jobId, updatedJobInfo)
    }

  /**
    * Updates the list of successfully uploaded URLs with latest entry
    *
    * @param key Cache key under which these URLs are stored
    * @param url Imgur URL
    * @return
    */
  def updateUploadedURLs(key: String, url: URL): Future[Done] =
    cache.get[List[URL]](key).flatMap { completedURLs =>
      cache.set(key, url +: completedURLs.getOrElse(List.empty[URL]))
    }

  /**
    * Fetches the list of successfully uploaded URLs
    *
    * @param key Cache key under which these URLs are stored
    * @return List of successfully uploaded URLs
    */
  def getImgurUploadedImages(key: String): Future[Option[List[URL]]] =
    cache.get[List[URL]](key)
}
