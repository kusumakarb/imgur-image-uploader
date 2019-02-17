package com.example.exceptions

/** Thrown when size of the response is greater than the allowed size of Imgur */
final case class FileLengthExceededException(message: String) extends RuntimeException
