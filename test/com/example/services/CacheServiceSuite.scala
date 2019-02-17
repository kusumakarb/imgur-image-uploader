package com.example.services

import java.time.ZonedDateTime

import com.example.base.BaseAsyncSpec
import com.example.models.Types.URL
import com.example.models.{ JobInfo, JobStatus, UploadInfo, UploadStatus }
import com.example.services.TestData._

final class CacheServiceSuite extends BaseAsyncSpec {
  private val imageUploadService = injector.instanceOf[ImageUploadService]
  private val cacheService       = injector.instanceOf[CacheService]

  "Cache Service".should("Update URL status").in {
    for {
      _                <- cache.set(jobId, jobInfo)
      _                <- cacheService.updateURLStatus(jobId, pendingURL, UploadStatus.Failed, None)
      resultantJobInfo <- cache.get[JobInfo](jobId)
    } yield {
      val expectedJobInfo =
        jobInfo.copy(uploadInfo = jobInfo.uploadInfo + (pendingURL -> UploadInfo(None, UploadStatus.Failed)))
      assert(resultantJobInfo.get === expectedJobInfo)
    }
  }

  it.should("Update Job Status").in {
    val jobId = java.util.UUID.randomUUID.toString
    val pendingJobInfo =
      JobInfo(ZonedDateTime.now(), finished = None, status = JobStatus.Pending, uploadInfo = uploadInfo)
    for {
      _              <- cache.set(jobId, pendingJobInfo)
      _              <- cacheService.updateJobStatus(jobId, JobStatus.InProgress, None, List.empty[URL])
      updatedJobInfo <- cache.get[JobInfo](jobId)
    } yield {
      val expectedJobInfo = pendingJobInfo.copy(status = JobStatus.InProgress)
      assert(updatedJobInfo.get === expectedJobInfo)
    }
  }

  it.should("Create if the Job info is not already present").in {
    val jobId = java.util.UUID.randomUUID.toString
    for {
      _ <- cacheService.updateJobStatus(jobId,
                                        JobStatus.Pending,
                                        None,
                                        List("http://localhost:8081/%20hofCa.png", "https://google.com"))
      updatedJobInfo <- cache.get[JobInfo](jobId)
    } yield {
      val expectedJobInfo =
        JobInfo(
          updatedJobInfo.get.created,
          updatedJobInfo.get.finished,
          JobStatus.Pending,
          Map("http://localhost:8081/%20hofCa.png" -> UploadInfo(None, UploadStatus.Pending),
              "https://google.com"                 -> UploadInfo(None, UploadStatus.Pending))
        )
      assert(expectedJobInfo === updatedJobInfo.get)
    }
  }

  it.should("Update uploaded URLs").in {
    val uploadedURLsKey = imageUploadService.UploadedURLs
    val urls            = List("https://i.imgur.com/AgcgSyx.jpg", "https://i.imgur.com/BhfGj.jpg")
    val urlToAdd        = "https://i.imgur.com/KnPyt.jpg"
    for {
      _             <- cache.set(uploadedURLsKey, urls)
      _             <- cacheService.updateUploadedURLs(uploadedURLsKey, urlToAdd)
      resultantURLS <- cache.get[List[URL]](uploadedURLsKey)
    } yield {
      val expectedURLs = urlToAdd +: urls
      assert(expectedURLs.length === resultantURLS.get.length)
      assert(resultantURLS.get.forall(expectedURLs.contains))
    }

  }

  it.should("Get images uploaded to Imgur").in {
    val uploadedURLsKey = imageUploadService.UploadedURLs
    val urls            = List("https://i.imgur.com/AgcgSyx.jpg", "https://i.imgur.com/BhfGj.jpg")
    for {
      _             <- cache.set(uploadedURLsKey, urls)
      _             <- cacheService.getImgurUploadedImages(uploadedURLsKey)
      resultantURLS <- cache.get[List[URL]](uploadedURLsKey)
    } yield {
      assert(urls.length === resultantURLS.get.length)
      assert(resultantURLS.get.forall(urls.contains))
    }

  }

}
