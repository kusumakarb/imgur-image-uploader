package com.example.exceptions

/**
  * Thrown when the Mime Type is not of the expected type
  */
final case class InvalidMimeTypeException(message: String) extends RuntimeException
