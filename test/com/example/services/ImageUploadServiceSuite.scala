package com.example.services

import akka.util.ByteString
import com.example.base.BaseAsyncSpec
import com.example.exceptions.{
  FileLengthExceededException,
  InvalidMimeTypeException,
  InvalidResponseException,
  JobNotFoundException
}
import com.example.models._
import com.example.services.TestData._
import mockws.RouteNotDefined._
import mockws.{ MockWS, MockWSHelpers }
import org.scalatest.PrivateMethodTester
import play.api.mvc.Results._

import scala.collection.immutable
import scala.concurrent.Future
import scala.util.Try

final class ImageUploadServiceSuite extends BaseAsyncSpec with PrivateMethodTester with MockWSHelpers {

  private val imageUploadService = injector.instanceOf[ImageUploadService]

  private final val NotFoundDotCom = "http://notfound.com"

  private final val NotAnImageDotCom = "http://notanimage.com"

  private final val ContentLengthExceededDotCom = "http://contentlengthexceeded.com"

  val exceededContent: immutable.Seq[Int] = Range.apply(1, 10485761)

  val wss = MockWS {
    case ("GET", NotAnImageDotCom) => Action { Ok("http response") }
    case ("GET", NotFoundDotCom)   => Action { NotFound("http response") }
    case ("GET", ContentLengthExceededDotCom) =>
      Action { Ok(exceededContent.mkString(",")).withHeaders(("Content-Type", "image/jpeg")) }
  }

  private val validateResponse = PrivateMethod[Try[ByteString]]('validateResponse)

  "ImageUploadService".should("Validate the errors for URLs that fail due to invalid mime type").in {
    wss.url(NotAnImageDotCom).get.flatMap { wsResponse =>
      val triedByteString = imageUploadService invokePrivate validateResponse(wsResponse, NotAnImageDotCom)
      recoverToSucceededIf[InvalidMimeTypeException](Future.fromTry(triedByteString))
    }
  }

  it.should("Validate the errors for URLs that fail due to exceeded content length").in {
    wss.url(ContentLengthExceededDotCom).get.flatMap { wsResponse =>
      val triedByteString = imageUploadService invokePrivate validateResponse(wsResponse, ContentLengthExceededDotCom)
      recoverToSucceededIf[FileLengthExceededException](Future.fromTry(triedByteString))
    }
  }

  it.should("Validate the errors for URLs that fail due to invalid responses").in {
    wss.url(NotFoundDotCom).get.flatMap { wsResponse =>
      val triedByteString = imageUploadService invokePrivate validateResponse(wsResponse, NotFoundDotCom)
      recoverToSucceededIf[InvalidResponseException](Future.fromTry(triedByteString))
    }
  }

  it.should("Get upload job status for the given Job ID").in {
    for {
      _               <- cache.set(jobId, jobInfo)
      jobInfoResponse <- imageUploadService.getJobInfo(jobId)
    } yield {

      val uploaded = Uploaded(
        pending = List("http://localhost:8081/%20hofCa.png"),
        complete = List("https://i.imgur.com/AgcgSyx.jpg"),
        failed = List("https://google.com")
      )

      val expectedJobInfoResponse = JobInfoResponse(jobId, jobInfo.created, jobInfo.finished, jobInfo.status, uploaded)

      assert(expectedJobInfoResponse === jobInfoResponse, "Invalid Job Status response")
    }
  }

  it.should("Throw `JobNotFoundException` when the status of a job that does not exist is requested").in {
    recoverToSucceededIf[JobNotFoundException](imageUploadService.getJobInfo(java.util.UUID.randomUUID.toString))
  }

  it.should("Gets the links of all images uploaded to Imgur").in {
    val uploadedURLsKey      = imageUploadService.UploadedURLs
    val expectedUploadedURLs = List("https://i.imgur.com/AgcgSyx.jpg", "https://i.imgur.com/BhfGj.jpg")
    for {
      _            <- cache.set(uploadedURLsKey, expectedUploadedURLs)
      uploadedURLs <- imageUploadService.getImgurUploadedImages
    } yield {
      assert(expectedUploadedURLs.forall(uploadedURLs.uploaded.contains))

      assert(expectedUploadedURLs.length === uploadedURLs.uploaded.length)
    }
  }
}
