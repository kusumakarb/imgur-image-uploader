package com.example.models

import com.example.models.Types.URL
import octopus.dsl._
import org.apache.commons.validator.routines.UrlValidator

/**
  * Input for the Upload images API
  *
  * @param urls List of image URLs to be processed
  */
final case class Upload(urls: List[URL])

object Upload {
  val urlValidator: UrlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS)

  implicit val uploadValidator: Validator[Upload] = Validator
    .derived[Upload]
    .rule(_.urls.forall(url => urlValidator.isValid(url)), "Invalid URL detected")

  def getInValidURLs(upload: Upload): List[URL] =
    upload.urls.filterNot(urlValidator.isValid)
}
