package com.example.base

import play.api.Application
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder

object AppProvider {
  val appBuilder: GuiceApplicationBuilder = new GuiceApplicationBuilder()

  val application: Application = appBuilder.build()

  val injector: Injector = application.injector
}
