package com.example.base

import com.example.models.FileUploadExecutionContext
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ AsyncFlatSpec, BeforeAndAfter, MustMatchers }
import play.api.Application
import play.api.cache.AsyncCacheApi
import play.api.inject.Injector

trait BaseAsyncSpec extends AsyncFlatSpec with ScalaFutures with BeforeAndAfter with LazyLogging with MustMatchers {

  val injector: Injector = AppProvider.injector

  val application: Application = AppProvider.application

  implicit override val executionContext: FileUploadExecutionContext = injector.instanceOf[FileUploadExecutionContext]

  val cache: AsyncCacheApi = injector.instanceOf[AsyncCacheApi]
}
