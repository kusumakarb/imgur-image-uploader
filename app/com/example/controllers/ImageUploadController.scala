package com.example.controllers

import com.example.exceptions.JobNotFoundException
import com.example.models.APIResponses.{ InvalidURLs, JobNotFound }
import com.example.models.Types.JobId
import com.example.models.Upload
import com.example.models.Upload._
import com.example.services.ImageUploadService
import io.circe.generic.auto._
import io.circe.syntax._
import javax.inject._
import octopus.syntax._
import play.api.libs.circe.Circe
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class ImageUploadController @Inject()(val controllerComponents: ControllerComponents,
                                      imageUploadService: ImageUploadService)(
  implicit ec: ExecutionContext
) extends BaseController
    with Circe {

  /**
    * Downloads the images from the given URLs and uploads them to Imgur
    */
  def upload: Action[Upload] = Action.async(circe.json[Upload]) { request =>
    if (request.body.isValid) {
      imageUploadService.upload(request.body.urls).map(uploadResponse => Ok(uploadResponse.asJson))
    } else {
      Future.successful(
        UnprocessableEntity(InvalidURLs("Input contains invalid URLs", Upload.getInValidURLs(request.body)).asJson)
      )
    }
  }

  /**
    * Fetches information of a Job
    *
    * @param jobId ID of the job
    */
  def getJobInfo(jobId: JobId): Action[AnyContent] = Action.async { _ =>
    imageUploadService
      .getJobInfo(jobId)
      .map { jobInfo =>
        Ok(jobInfo.asJson)
      }
      .recover {
        case _: JobNotFoundException =>
          NotFound(JobNotFound(s"No Job found with requested Id $jobId").asJson)
      }
  }

  /**
    * Fetches Imgur links of all successfully uploaded images
    */
  def getUploadedImageLinks: Action[AnyContent] = Action.async { _ =>
    imageUploadService.getImgurUploadedImages.map(uploadedURLs => Ok(uploadedURLs.asJson))
  }
}
