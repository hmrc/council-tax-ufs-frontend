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

import base.SpecBase
import controllers.routes
import forms.UninhabitableDateLastOccupiedOtherFormProvider
import models.NormalMode
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.html.UninhabitableDateLastOccupiedOtherView

import java.time.LocalDate

class UninhabitableDateLastOccupiedOtherViewSpec extends SpecBase {

  private val application =
    applicationBuilder(userAnswers = None).build()

  private val view =
    application.injector.instanceOf[UninhabitableDateLastOccupiedOtherView]

  private implicit val msgs: play.api.i18n.Messages =
    messages(application)

  private implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest()

  private def form: Form[LocalDate] =
    new UninhabitableDateLastOccupiedOtherFormProvider()()

  private def applyView(form: Form[LocalDate]): HtmlFormat.Appendable =
    view(form, NormalMode, Some("/back"))(fakeRequest, msgs)

  private def asDocument(html: HtmlFormat.Appendable): Document =
    Jsoup.parse(html.toString)

  "UninhabitableDateLastOccupiedOtherView" - {

    "must render the correct page title" in {
      asDocument(applyView(form)).title() must include(
        msgs("uninhabitableDateLastOccupiedOther.title")
      )
    }

    "must render the heading" in {
      asDocument(applyView(form))
        .select("h1")
        .text() mustBe
        msgs("uninhabitableDateLastOccupiedOther.heading")
    }

    "must render the hint text" in {
      asDocument(applyView(form))
        .select(".govuk-hint")
        .text() must include(
        msgs("uninhabitableDateLastOccupiedOther.hint")
      )
    }

    "must render day, month and year inputs" in {
      val doc = asDocument(applyView(form))

      doc.select("input#value\\.day").isEmpty mustBe false
      doc.select("input#value\\.month").isEmpty mustBe false
      doc.select("input#value\\.year").isEmpty mustBe false
    }

    "must render the back link" in {
      asDocument(applyView(form))
        .select(".govuk-back-link")
        .attr("href") mustBe "/back"
    }

    "must render a submit button" in {
      asDocument(applyView(form))
        .select("button.govuk-button")
        .text() mustBe msgs("site.continue")
    }

    "must not show an error summary when there are no errors" in {
      asDocument(applyView(form))
        .select(".govuk-error-summary")
        .isEmpty mustBe true
    }

    "must show the required error and link it to the day input when the date is missing" in {

      val formWithErrors =
        form.bind(
          Map(
            "value.day"   -> "",
            "value.month" -> "",
            "value.year"  -> ""
          )
        )

      val doc = asDocument(applyView(formWithErrors))

      doc.select(".govuk-error-summary")
        .text() must include(
        msgs("uninhabitableDateLastOccupiedOther.error.required.all")
      )

      doc.select(".govuk-error-summary a")
        .attr("href") mustBe "#value.day"
    }

    "must render a form with the correct action" in {
      asDocument(applyView(form))
        .select("form")
        .attr("action") mustBe
        routes.UninhabitableDateLastOccupiedOtherController
          .onSubmit(NormalMode)
          .url
    }
  }
}
