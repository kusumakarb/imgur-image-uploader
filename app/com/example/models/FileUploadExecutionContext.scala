package com.example.models

import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.libs.concurrent.CustomExecutionContext

/**
  * Execution context for the file upload job
  */
class FileUploadExecutionContext @Inject()(system: ActorSystem)
    extends CustomExecutionContext(system, "database.dispatcher")
