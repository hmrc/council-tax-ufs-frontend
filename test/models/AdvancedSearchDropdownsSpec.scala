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

import models.AdvancedSearchDropdowns.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.test.Helpers.stubMessagesApi

class AdvancedSearchDropdownsSpec extends AnyFreeSpec with Matchers {

  private val messagesApi: MessagesApi = stubMessagesApi(Map("default" -> Map("label.key" -> "Label", "other.key" -> "Other")))
  private given Messages = messagesApi.preferred(Seq(Lang("en")))

  "DropdownList" - {

    "must translate each label key and keep the values" in {
      DropdownList("a" -> "label.key", "b" -> "other.key").valuesWithLabels mustBe Seq("a" -> "Label", "b" -> "Other")
    }

    "must fall back to the key when no message is defined" in {
      DropdownList("a" -> "missing.key").valuesWithLabels mustBe Seq("a" -> "missing.key")
    }

    "must be empty when created without items" in {
      DropdownList().valuesWithLabels mustBe empty
    }
  }

  "AdvancedSearchDropdowns" - {

    "localAuthorityList must start with the default empty option and contain unique codes" in {
      localAuthorityList.items.head mustBe ("" -> "advancedPropertySearch.localAuthority.default")
      localAuthorityList.items.map(_._1).distinct.size mustBe localAuthorityList.items.size
      localAuthorityList.items must contain("6815" -> "CARDIFF")
    }

    "councilTaxBands must have an all-bands option followed by bands A to I" in {
      councilTaxBands.items.map(_._1) mustBe ("" +: ('A' to 'I').map(_.toString))
    }

    "bandStatusList must offer Current and Deleted" in {
      bandStatusList.items.map(_._1) mustBe Seq("", "Current", "Deleted")
    }

    "courtCodeList must offer the expected court codes" in {
      courtCodeList.items.map(_._1) mustBe Seq("", "V", "C", "D", "H", "R", "E")
    }

    "propertyUsedForList must offer composite and non-composite" in {
      propertyUsedForList.items.map(_._1) mustBe Seq("", "C", "N")
    }

    "every label key of the non-authority lists and the authority default must exist in the English messages" in {
      val app = new play.api.inject.guice.GuiceApplicationBuilder().build()
      try {
        val msgs = app.injector.instanceOf[MessagesApi].preferred(Seq(Lang("en")))
        val keys = Seq(councilTaxBands, bandStatusList, courtCodeList, propertyUsedForList, DropdownList(localAuthorityList.items.head))
          .flatMap(_.items.map(_._2))
        keys.filterNot(msgs.isDefinedAt) mustBe empty
      } finally app.stop()
    }
  }
}
