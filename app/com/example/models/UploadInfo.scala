package com.example.models

import com.example.models.Types.ImgurURL

/**
  * Information of the URL being processed
  *
  * @param uploadedURL Imgur URL to which the image is uploaded
  * @param uploadStatus Status of the image URL
  */
final case class UploadInfo(uploadedURL: Option[ImgurURL], uploadStatus: UploadStatus)
