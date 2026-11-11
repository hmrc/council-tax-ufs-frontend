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

import base.SpecBase
import org.scalatest.OptionValues
import pages.CouncilTaxResponsiblePage
import play.api.test.Helpers._
import viewmodels.checkAnswers.CouncilTaxResponsibleSummary

class CouncilTaxResponsibleOptionsSpec extends SpecBase with OptionValues {

  "CouncilTaxResponsible.options" - {
    "must build a radio item per value with sequential ids" in {
      val application = applicationBuilder().build()
      running(application) {
        val items = CouncilTaxResponsible.options(messages(application))

        items.map(_.value.value) mustBe CouncilTaxResponsible.values.map(_.toString)
        items.map(_.id.value) mustBe Seq("value_0", "value_1", "value_2")
      }
    }
  }

  "CouncilTaxResponsibleSummary" - {
    "must show the answer with a change link" in {
      val application = applicationBuilder().build()
      running(application) {
        implicit val msgs: play.api.i18n.Messages = messages(application)
        val answers = emptyUserAnswers.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.Yes).success.value
        val row     = CouncilTaxResponsibleSummary.row(answers).value

        row.value.content.asHtml.toString must include(msgs("councilTaxResponsible.yes"))
        row.actions.value.items.head.href mustBe controllers.routes.CouncilTaxResponsibleController.onPageLoad(CheckMode).url
      }
    }

    "must return None when unanswered" in {
      val application = applicationBuilder().build()
      running(application) {
        implicit val msgs: play.api.i18n.Messages = messages(application)
        CouncilTaxResponsibleSummary.row(emptyUserAnswers) mustBe None
      }
    }
  }
}
