package com.example.models

import play.api.libs.json.{ Json, Reads }

/**
  * Data for the error response
  *
  * @param error Reason for the failure of the request
  * @param request Request made
  * @param method Method used in the API request
  */
final case class ErrorData(error: String, request: String, method: String)

object ErrorData {
  implicit val errorDataReads: Reads[ErrorData] = Json.reads[ErrorData]
}

/**
  * API response when the image upload to Imgur failed
  *
  * @param data Data for the error response
  * @param success Success
  * @param status
  */
final case class ImgurErrorResponse(data: ErrorData, success: Boolean, status: Int)

object ImgurErrorResponse {
  implicit val imgurErrorResponseReads: Reads[ImgurErrorResponse] = Json.reads[ImgurErrorResponse]
}
