package com.example.models

/**
  * Validation information of the file download response
  *
  * @param isValidStatusCode If the status code of the response is a valid status code
  * @param isValidMimeType If the mime type of the response is of a valid image
  * @param isValidContentLength If the content length of the response is less than the upload limit
  */
final case class Validations(isValidStatusCode: Boolean, isValidMimeType: Boolean, isValidContentLength: Boolean)

object Validations {

  /**
    * List of accepted mime types for image
    */
  // https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types#Image_types
  val listOfImageMimeTypes =
    List("image/jpeg", "image/gif", "image/png", "image/svg+xml", "image/x-icon", "image/vnd.microsoft.icon")

  /**
    * List of HTTP Status codes which are considered as Success
    */
  val successStatusCodes = List(200, 201, 202, 203, 206)
}
