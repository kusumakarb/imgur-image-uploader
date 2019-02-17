package com.example.models

import enumeratum.EnumEntry.Lowercase
import enumeratum._

sealed trait JobStatus extends EnumEntry

object JobStatus extends Enum[JobStatus] with CirceEnum[JobStatus] {

  /**
    * `findValues` is a protected method that invokes a macro to find all `Greeting` object declarations inside an
    * `Enum`. You use it to implement the `val values` member.
    */
  val values = findValues

  case object Pending extends JobStatus with Lowercase

  case object InProgress extends JobStatus with InProgressMixin

  case object Complete extends JobStatus with Lowercase

  trait InProgressMixin extends EnumEntry {
    override def entryName: String = stableEntryName

    private[this] lazy val stableEntryName: String = "in-progress"
  }
}
