package com.example.exceptions

/**
  * Thrown when a job with the requested Id is not found
  */
final case class JobNotFoundException(message: String) extends RuntimeException
