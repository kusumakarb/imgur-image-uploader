package com.example.models

import com.example.models.Types.ImgurURL
import play.api.libs.json.{ Json, Reads }

/**
  * API response of the Imgur API on successful upload of the image
  *
  * @param data Data for the success response
  * @param success Boolean value indicating the success of the API request.
  * @param status HTTP Status code of the API request
  */
final case class ImgurAPIResponse(data: Data, success: Boolean, status: Int)

object ImgurAPIResponse {
  implicit val imgurAPIResponseReads: Reads[ImgurAPIResponse] = Json.reads[ImgurAPIResponse]
}

/**
  * Data for the success response
  *
  * @param id Id of the request
  * @param link Link to uploaded image on Imgur
  */
final case class Data(id: String, link: ImgurURL)

object Data {
  implicit val dataReads: Reads[Data] = Json.reads[Data]
}
