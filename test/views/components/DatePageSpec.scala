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

package views.components

import base.SpecBase
import controllers.routes
import forms.{CouncilTaxLiabilityStartDateFormProvider, DateNoticeFormProvider}
import models.NormalMode
import org.jsoup.Jsoup
import play.api.test.FakeRequest
import views.html.{CouncilTaxLiabilityStartDateView, DateNoticeView}

class DatePageSpec extends SpecBase {

  private val application = applicationBuilder(userAnswers = None).build()
  private implicit val request: FakeRequest[_] = FakeRequest()
  private implicit val msgs: play.api.i18n.Messages = messages(application)

  "DatePage" - {

    "must render a heading, hint and date inputs without an example (date notice)" in {
      val form = new DateNoticeFormProvider()()
      val doc  = Jsoup.parse(application.injector.instanceOf[DateNoticeView].apply(form, NormalMode).toString)

      doc.select("h1").text() mustBe msgs("dateNotice.heading")
      doc.select(".govuk-hint").text() mustBe msgs("dateNotice.hint")
      doc.select("input.govuk-date-input__input").size() mustBe 3
      doc.select("form").attr("action") must endWith(routes.DateNoticeController.onSubmit(NormalMode).url)
    }

    "must append the example to the hint and render the back link when provided (liability start date)" in {
      val form = new CouncilTaxLiabilityStartDateFormProvider()()
      val doc  = Jsoup.parse(
        application.injector.instanceOf[CouncilTaxLiabilityStartDateView].apply(form, NormalMode, "/back").toString
      )

      doc.select("h1").text() mustBe msgs("councilTaxLiabilityStartDate.heading")
      doc.select(".govuk-hint").text() must include(msgs("councilTaxLiabilityStartDate.hint"))
      doc.select(".govuk-hint span").text() mustBe msgs("councilTaxLiabilityStartDate.example")
      doc.select(".govuk-back-link").attr("href") mustBe "/back"
    }
  }
}
