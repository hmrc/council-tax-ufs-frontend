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

package views

import java.util.Locale

/** Countries with their own council tax band table. To support a new country add a case object here, include it
  * in `all`, and add its `propertyDetail.band.*.<key>` messages (en and cy).
  */
sealed abstract class BandCountry(val key: String, val codes: Set[String], val bands: Seq[String])

object BandCountry {

  private val standardBands = Seq("A", "B", "C", "D", "E", "F", "G", "H")

  case object England extends BandCountry("england", Set("england", "e92000001"), standardBands)
  case object Wales   extends BandCountry("wales", Set("wales", "w92000004"), standardBands :+ "I")

  val all: Seq[BandCountry] = Seq(England, Wales)

  /** Matches a country name or ONS code, defaulting to England for anything unrecognised. */
  def fromCode(country: String): BandCountry = {
    val normalised = country.trim.toLowerCase(Locale.ROOT)
    all.find(_.codes.contains(normalised)).getOrElse(England)
  }
}
