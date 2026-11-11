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

object PostcodeFormatter {

  // Inward code is always a digit followed by two letters; anything else (e.g. a partial postcode) is left as is
  private val FullPostcode = """^(.+)(\d[A-Z]{2})$""".r

  def format(postcode: String): String = {
    val cleaned = postcode.trim.toUpperCase.replaceAll("\\s+", "")
    cleaned match {
      case FullPostcode(outward, inward) => s"$outward $inward"
      case other                         => other
    }
  }
}
