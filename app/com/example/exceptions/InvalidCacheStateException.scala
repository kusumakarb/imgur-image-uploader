package com.example.exceptions

/**
  * Thrown when the cache the data expected to be in the Cache goes missing
  */
final case class InvalidCacheStateException(message: String) extends RuntimeException
