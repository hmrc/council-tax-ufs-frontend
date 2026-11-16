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

package utils

object ExternalLink {

  private val Scheme = "^([a-zA-Z][a-zA-Z0-9+.-]*):".r

  /** The API returns council websites without a scheme ("www.manchester.gov.uk"), which a browser treats as a
    * path relative to this service. Only http(s) links are kept.
    */
  def absolute(pointer: String): Option[String] = {
    val trimmed = pointer.trim
    Scheme.findPrefixMatchOf(trimmed).map(_.group(1).toLowerCase) match {
      case _ if trimmed.isEmpty                  => None
      case Some("http") | Some("https")          => Some(trimmed)
      case Some(_)                               => None
      case None                                  => Some(s"https://${trimmed.stripPrefix("//")}")
    }
  }
}
