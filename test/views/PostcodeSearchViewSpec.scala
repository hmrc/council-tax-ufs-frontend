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
import forms.PostcodeSearchFormProvider
import models.NormalMode
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.html.PostcodeSearchView

class PostcodeSearchViewSpec extends SpecBase {

  val form: Form[String] = new PostcodeSearchFormProvider()()

  val application = applicationBuilder(userAnswers = None).build()
  val view         = application.injector.instanceOf[PostcodeSearchView]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  def applyView(form: Form[String]): HtmlFormat.Appendable =
    view(form, NormalMode)(fakeRequest, messages(application))

  def asDocument(html: HtmlFormat.Appendable): Document =
    Jsoup.parse(html.toString)

  "PostcodeSearchView" - {

    "must render the correct page title" in {
      val doc = asDocument(applyView(form))
      doc.title() must include(messages(application)("postcodeSearch.title"))
    }

    "must render the heading" in {
      val doc = asDocument(applyView(form))
      doc.select("h1.govuk-heading-xl").text() mustBe
        messages(application)("postcodeSearch.heading")
    }

    "must render the description" in {
      val doc = asDocument(applyView(form))
      doc.select("p.govuk-body").first().text() mustBe
        messages(application)("postcodeSearch.description")
    }

    "must render the input with correct label and hint" in {
      val doc = asDocument(applyView(form))

      doc.select("label[for=value]").text() mustBe messages(application)("postcodeSearch.label")
      doc.select("#value-hint").text() mustBe messages(application)("postcodeSearch.hint")
      doc.select("input#value").attr("autocomplete") mustBe "postal-code"
    }

    "must render a submit button" in {
      val doc = asDocument(applyView(form))
      doc.select("button.govuk-button").text() mustBe messages(application)("site.continue")
    }

    "must render the 'don't know your postcode' link" in {
      val doc = asDocument(applyView(form))
      val link = doc.select("a.govuk-link[href=#advance-search]")

      link.text() mustBe messages(application)("postcodeSearch.dontKnowPostcode")
    }

    "must not show an error summary when there are no errors" in {
      val doc = asDocument(applyView(form))
      doc.select(".govuk-error-summary").isEmpty mustBe true
    }

    "must show an error summary when the form has errors" in {
      val boundForm = form.bind(Map("value" -> ""))
      val doc = asDocument(applyView(boundForm))

      doc.select(".govuk-error-summary").isEmpty mustBe false
    }

    "must render a form with the correct action" in {
      val doc = asDocument(applyView(form))
      doc.select("form").attr("action") mustBe
        routes.PostcodeSearchController.onSubmit(NormalMode).url
    }
  }
}