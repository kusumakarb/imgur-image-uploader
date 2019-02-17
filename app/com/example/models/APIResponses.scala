package com.example.models

import java.time.ZonedDateTime

import com.example.models.Types.{ JobId, URL }
import play.api.http.Status

/**
  * Response for the `Submit image URLs for upload`
  * @param jobId Id of the Job
  */
final case class UploadResponse(jobId: JobId)

/**
  * Response for the `Get upload job status` API
  *
  * @param id Id of thr job
  * @param created GMT Timestamp of when the job was created
  * @param finished GMT Timestamp of when the job finished
  * @param status Status of the Job
  * @param uploaded Specific status of the URLs in the job
  */
final case class JobInfoResponse(id: JobId,
                                 created: ZonedDateTime,
                                 finished: Option[ZonedDateTime],
                                 status: JobStatus,
                                 uploaded: Uploaded)

/**
  * Response of the get all uploaded URLs API
  *
  * @param uploaded List of uploaded URLs
  */
final case class ImgurUploadedURLs(uploaded: List[URL])

/**
  * Response for the `Get upload job status` API when a job with the requested Id does not exist
  */
final case class JobNotFound(message: String, success: Boolean = false, status: Int = Status.NOT_FOUND)

/**
  * Response for `Submit image URLs for upload` when the input contains invalid URLs
  */
final case class InvalidURLs(message: String,
                             invalidURLs: List[URL],
                             success: Boolean = false,
                             status: Int = Status.UNPROCESSABLE_ENTITY)
