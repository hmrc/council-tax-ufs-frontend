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

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.BandCountry

sealed trait WhichBand

object WhichBand extends Enumerable.Implicits {

  case object Banda extends WithName("bandA") with WhichBand
  case object Bandb extends WithName("bandB") with WhichBand
  case object Bandc extends WithName("bandC") with WhichBand
  case object Bandd extends WithName("bandD") with WhichBand
  case object Bande extends WithName("bandE") with WhichBand
  case object Bandf extends WithName("bandF") with WhichBand
  case object Bandg extends WithName("bandG") with WhichBand
  case object Bandh extends WithName("bandH") with WhichBand
  case object Bandi extends WithName("bandI") with WhichBand
  case object NotSure extends WithName("notSure") with WhichBand

  val values: Seq[WhichBand] = Seq(
    Banda, Bandb, Bandc, Bandd, Bande, Bandf, Bandg, Bandh, Bandi, NotSure
  )

  def options(country: BandCountry)(implicit messages: Messages): Seq[RadioItem] = {
    val bandValues = country.bands.map {
      case "A" => Banda
      case "B" => Bandb
      case "C" => Bandc
      case "D" => Bandd
      case "E" => Bande
      case "F" => Bandf
      case "G" => Bandg
      case "H" => Bandh
      case "I" => Bandi
      case _ => NotSure
    } :+ NotSure

    bandValues.zipWithIndex.map {
      case (value, index) =>
        RadioItem(
          content = Text(messages(s"whichBand.${value.toString}")),
          value = Some(value.toString),
          id = Some(s"value_$index")
        )
    }
  }

  implicit val enumerable: Enumerable[WhichBand] =
    Enumerable(values.map(v => v.toString -> v): _*)
}