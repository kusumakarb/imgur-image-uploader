package com.example.exceptions

/**
  * Thrown when the API responds with an error
  */
final case class InvalidResponseException(message: String) extends RuntimeException
