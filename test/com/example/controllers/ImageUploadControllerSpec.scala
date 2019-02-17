package com.example.controllers

import java.util.UUID

import com.example.base.{ AppProvider, BaseAsyncSpec }
import com.example.models.{ JobInfo, JobStatus }
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

final class ImageUploadControllerSpec extends BaseAsyncSpec {

//  private final val application = AppProvider.appBuilder.build

  "ImageUploadController".should("Get list of all uploaded image URLs").in {
    val request      = FakeRequest(GET, "/v1/images/")
    val uploadedURLs = route(application, request).get

    status(uploadedURLs) mustBe OK
    contentType(uploadedURLs) mustBe Some("application/json")
  }

  it.should("Return status of the requested job").in {
    val jobId   = UUID.randomUUID.toString
    val jobInfo = JobInfo.create(JobStatus.Pending, List("http://localhost:8081/%20hofCa.png", "https://google.com"))
    cache.set(jobId, jobInfo).map { _ =>
      val request   = FakeRequest(GET, s"/v1/images/upload/$jobId")
      val jobStatus = route(application, request).get
      status(jobStatus) mustBe OK
      contentType(jobStatus) mustBe Some("application/json")
    }
  }

  it.should("Throw Job Not Found error when a job which does not exist is requested").in {
    val jobId     = UUID.randomUUID.toString
    val request   = FakeRequest(GET, s"/v1/images/upload/$jobId")
    val jobStatus = route(application, request).get

    status(jobStatus) mustBe NOT_FOUND
    contentType(jobStatus) mustBe Some("application/json")
  }

  it.should("Download the images and upload to imgur").in {
    val request = FakeRequest(POST, "/v1/images/upload")
      .withJsonBody(Json.parse(
        """{"urls": [	"https://google.com","https://farm4.staticflickr.com/3790/11244125445_3c2f32cd83_k_d.jpg","http://localhost:8082/%20hofCa.png"]}"""))
    val jobStatus = route(application, request).get

    status(jobStatus) mustBe OK
    contentType(jobStatus) mustBe Some("application/json")
  }

  it.should("Throw UnProcessable entity if the input contains invalid URLs").in {
    val request = FakeRequest(POST, "/v1/images/upload")
      .withJsonBody(Json.parse("""{"urls": [	"htps//google.com", "http://localhost:8082/%20hofCa.png"]}"""))
    val jobStatus = route(application, request).get

    status(jobStatus) mustBe UNPROCESSABLE_ENTITY
    contentType(jobStatus) mustBe Some("application/json")
  }
}
