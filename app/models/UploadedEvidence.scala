/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import play.api.libs.json.{Json, OFormat}

import java.util.UUID

// details of a file chosen as evidence (the file itself isn't stored yet)
final case class UploadedEvidence(id: String, fileName: String, contentType: String, sizeInBytes: Long) {

  def displaySize: String =
    if (sizeInBytes >= 1024 * 1024) f"${sizeInBytes / (1024.0 * 1024)}%.1f MB"
    else math.max(1L, math.round(sizeInBytes / 1024.0)).toString + " KB"
}

object UploadedEvidence {
  implicit val format: OFormat[UploadedEvidence] = Json.format[UploadedEvidence]

  def apply(fileName: String, contentType: String, sizeInBytes: Long): UploadedEvidence =
    UploadedEvidence(UUID.randomUUID().toString, fileName, contentType, sizeInBytes)

  val maxFiles: Int = 5

  val maxSizeInBytes: Long = 10L * 1024 * 1024

  // allowed extensions and the content types browsers send for them
  val allowedTypes: Map[String, Set[String]] = Map(
    "jpg"  -> Set("image/jpeg"),
    "jpeg" -> Set("image/jpeg"),
    "png"  -> Set("image/png"),
    "pdf"  -> Set("application/pdf")
  )

  def isAllowedType(fileName: String, contentType: Option[String]): Boolean =
    fileName.lastIndexOf('.') match {
      case -1 => false
      case i  =>
        allowedTypes
          .get(fileName.substring(i + 1).toLowerCase)
          .exists(types => contentType.exists(ct => types.contains(ct.toLowerCase)))
    }
}
