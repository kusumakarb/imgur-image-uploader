package com.example.models

import com.example.models.Types.URL

/**
  * URLs grouped according to their statuses
  *
  * @param pending URLs that still needs to downloaded and uploaded
  * @param complete URLs whose download and upload are finished
  * @param failed URLs that failed during upload or download
  */
final case class Uploaded(pending: List[URL], complete: List[URL], failed: List[URL])
