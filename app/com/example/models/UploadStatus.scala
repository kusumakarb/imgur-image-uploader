package com.example.models

import enumeratum._

import scala.collection.immutable

/**
  * Status of a given URL
  */
sealed trait UploadStatus extends EnumEntry

object UploadStatus extends Enum[UploadStatus] {

  /**
    * `findValues` is a protected method that invokes a macro to find all `Greeting` object declarations inside an
    * `Enum`. You use it to implement the `val values` member.
    */
  val values: immutable.IndexedSeq[UploadStatus] = findValues

  case object Pending extends UploadStatus

  case object Complete extends UploadStatus

  case object Failed extends UploadStatus
}
