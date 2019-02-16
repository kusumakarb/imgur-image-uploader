package com.example.models

import java.time.ZonedDateTime

import com.example.models.Types.URL

/**
  * Job information object stored in the cache
  *
  * @param created Time at which the job is created
  * @param finished Time at which the job is finished
  * @param status Status of the job
  * @param uploadInfo Status of URLs being processed for the job
  */
final case class JobInfo(created: ZonedDateTime,
                         finished: Option[ZonedDateTime],
                         status: JobStatus,
                         uploadInfo: Map[URL, UploadInfo])

object JobInfo {

  def create(status: JobStatus, urls: List[URL]): JobInfo = {
    val uploadInfo = urls.foldLeft(Map.empty[URL, UploadInfo]) { (acc, url) =>
      acc + (url -> UploadInfo(None, UploadStatus.Pending))
    }
    JobInfo(created = ZonedDateTime.now, finished = None, status = status, uploadInfo = uploadInfo)
  }
}
