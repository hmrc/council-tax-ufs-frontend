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
import forms.DateNoticeFormProvider
import models.NormalMode
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.html.DateNoticeView
import java.time.LocalDate

class DateNoticeViewSpec extends SpecBase {

  val application = applicationBuilder(userAnswers = None).build()
  val view        = application.injector.instanceOf[DateNoticeView]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  def form: Form[LocalDate] = {
    implicit val msgs: play.api.i18n.Messages = messages(application)
    new DateNoticeFormProvider()()
  }

  def applyView(form: Form[LocalDate]): HtmlFormat.Appendable =
    view(form, NormalMode)(fakeRequest, messages(application))

  def asDocument(html: HtmlFormat.Appendable): Document =
    Jsoup.parse(html.toString)

  "DateNoticeView" - {

    "must render the correct page title" in {
      val doc = asDocument(applyView(form))
      doc.title() must include(messages(application)("dateNotice.title"))
    }

    "must render the heading" in {
      val doc = asDocument(applyView(form))
      doc.select("legend").text() must include(messages(application)("dateNotice.heading"))
    }

    "must render the hint text" in {
      val doc = asDocument(applyView(form))
      doc.select(".govuk-hint").text() must include(messages(application)("dateNotice.hint"))
    }

    "must render day, month and year inputs" in {
      val doc = asDocument(applyView(form))
      doc.select("input#value\\.day").isEmpty mustBe false
      doc.select("input#value\\.month").isEmpty mustBe false
      doc.select("input#value\\.year").isEmpty mustBe false
    }

    "must render a submit button" in {
      val doc = asDocument(applyView(form))
      doc.select("button.govuk-button").text() mustBe
        messages(application)("site.continue")
    }

    "must not show an error summary when there are no errors" in {
      val doc = asDocument(applyView(form))
      doc.select(".govuk-error-summary").isEmpty mustBe true
    }

    "must show an error summary when the form has errors" in {
      val boundForm = form.bind(Map("value.day" -> "", "value.month" -> "", "value.year" -> ""))
      val doc       = asDocument(applyView(boundForm))
      doc.select(".govuk-error-summary").isEmpty mustBe false
    }

    "must render a form with the correct action" in {
      val doc = asDocument(applyView(form))
      doc.select("form").attr("action") mustBe
        routes.DateNoticeController.onSubmit(NormalMode).url
    }
  }
}