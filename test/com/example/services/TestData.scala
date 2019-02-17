package com.example.services

import java.time.ZonedDateTime

import com.example.models.Types.URL
import com.example.models.{ JobInfo, JobStatus, UploadInfo, UploadStatus }

object TestData {

  val pendingURL: URL = "http://localhost:8081/%20hofCa.png"

  val uploadInfo = Map(
    "https://google.com" -> UploadInfo(None, UploadStatus.Failed),
    "https://farm3.staticflickr.com/2879/11234651086_681b3c2c00_b_d.jpg" -> UploadInfo(
      Some("https://i.imgur.com/AgcgSyx.jpg"),
      UploadStatus.Complete),
    pendingURL -> UploadInfo(None, UploadStatus.Pending)
  )

  val jobId: String = java.util.UUID.randomUUID.toString

  val jobInfo =
    JobInfo(ZonedDateTime.now(),
            finished = Some(ZonedDateTime.now()),
            status = JobStatus.Complete,
            uploadInfo = uploadInfo)
}
