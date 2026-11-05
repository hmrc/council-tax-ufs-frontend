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
import forms.WhichBandFormProvider
import models.{WhichBand, NormalMode}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.html.WhichBandView
import views.BandCountry

class WhichBandViewSpec extends SpecBase {

  val form: Form[WhichBand] = new WhichBandFormProvider()()

  val application = applicationBuilder(userAnswers = None).build()
  val view        = application.injector.instanceOf[WhichBandView]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  // Test both England and Wales scenarios
  val englandCountry = BandCountry.England
  val walesCountry   = BandCountry.Wales
  val currentBand    = "D"

  def applyView(form: Form[WhichBand], country: BandCountry = englandCountry): HtmlFormat.Appendable =
    view(form, NormalMode, country, currentBand)(fakeRequest, messages(application))

  def asDocument(html: HtmlFormat.Appendable): Document =
    Jsoup.parse(html.toString)

  "WhichBandView" - {

    "must render the correct page title" in {
      val doc = asDocument(applyView(form))
      doc.title() must include(messages(application)("whichBand.title"))
    }

    "must render the heading" in {
      val doc = asDocument(applyView(form))
      doc.select("h1.govuk-heading-l").text() mustBe
        messages(application)("whichBand.heading")
    }

    "must render England hint text for England" in {
      val doc = asDocument(applyView(form, englandCountry))
      doc.select("p.govuk-hint").first().text() must include(
        messages(application)("whichBand.hint.england")
      )
    }

    "must render Wales hint text for Wales" in {
      val doc = asDocument(applyView(form, walesCountry))
      doc.select("p.govuk-hint").first().text() must include(
        messages(application)("whichBand.hint.wales")
      )
    }

    "must render England link for England" in {
      val doc = asDocument(applyView(form, englandCountry))
      doc.select("p.govuk-hint a.govuk-link").first().text() mustBe
        messages(application)("whichBand.link.england")
    }

    "must render Wales link for Wales" in {
      val doc = asDocument(applyView(form, walesCountry))
      doc.select("p.govuk-hint a.govuk-link").first().text() mustBe
        messages(application)("whichBand.link.wales")
    }

    "must render 9 radio buttons for England (A to H plus not sure)" in {
      val doc = asDocument(applyView(form, englandCountry))
      doc.select(".govuk-radios__item").size() mustBe 9
    }

    "must render 10 radio buttons for Wales (A to I plus not sure)" in {
      val doc = asDocument(applyView(form, walesCountry))
      doc.select(".govuk-radios__item").size() mustBe 10
    }

    "must render the band details expand component" in {
      val doc = asDocument(applyView(form))
      doc.select(".govuk-details").isEmpty mustBe false
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
      val boundForm = form.bind(Map("value" -> ""))
      val doc       = asDocument(applyView(boundForm))
      doc.select(".govuk-error-summary").isEmpty mustBe false
    }

    "must render a form with the correct action" in {
      val doc = asDocument(applyView(form))
      doc.select("form").attr("action") mustBe
        routes.WhichBandController.onSubmit(NormalMode).url
    }
  }
}