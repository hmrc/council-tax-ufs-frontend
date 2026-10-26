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
import forms.CouncilTaxResponsibleFormProvider
import models.{CouncilTaxResponsible, NormalMode}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.html.CouncilTaxResponsibleView

class CouncilTaxResponsibleViewSpec extends SpecBase {

  val form: Form[CouncilTaxResponsible] = new CouncilTaxResponsibleFormProvider()()

  val application = applicationBuilder(userAnswers = None).build()
  val view        = application.injector.instanceOf[CouncilTaxResponsibleView]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val testAddress = "19, Somerby Court, Bramcote, Nottingham, NG9 3NB"

  def applyView(form: Form[CouncilTaxResponsible]): HtmlFormat.Appendable =
    view(form, NormalMode, testAddress)(fakeRequest, messages(application))

  def asDocument(html: HtmlFormat.Appendable): Document =
    Jsoup.parse(html.toString)

  "CouncilTaxResponsibleView" - {

    "must render the correct page title" in {
      val doc = asDocument(applyView(form))
      doc.title() must include(messages(application)("councilTaxResponsible.title"))
    }

    "must render the address caption" in {
      val doc = asDocument(applyView(form))
      doc.select("span.govuk-caption-l").text() mustBe testAddress
    }

    "must render the heading" in {
      val doc = asDocument(applyView(form))
      doc.select("h1.govuk-heading-l").text() must include(
        messages(application)("councilTaxResponsible.heading")
      )
    }

    "must render the hint text" in {
      val doc = asDocument(applyView(form))
      doc.select(".govuk-hint").first().text() mustBe
        messages(application)("councilTaxResponsible.hint")
    }

    "must render the warning text" in {
      val doc = asDocument(applyView(form))
      doc.select(".govuk-warning-text__text").text() must include(
        messages(application)("councilTaxResponsible.warning")
      )
    }

    "must render three radio buttons" in {
      val doc = asDocument(applyView(form))
      doc.select(".govuk-radios__item").size() mustBe 3
    }

    "must render the yes radio button" in {
      val doc = asDocument(applyView(form))
      doc.select("label[for=value_0]").text() mustBe
        messages(application)("councilTaxResponsible.yes")
    }

    "must render the no radio button" in {
      val doc = asDocument(applyView(form))
      doc.select("label[for=value_1]").text() mustBe
        messages(application)("councilTaxResponsible.no")
    }


    "must render the actingFor radio button with hint" in {
      val doc = asDocument(applyView(form))
      doc.select("label[for=value_2]").text() mustBe
        messages(application)("councilTaxResponsible.actingFor")
      doc.select("#value_2-item-hint").text() mustBe
        messages(application)("councilTaxResponsible.actingFor.hint")
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
        routes.CouncilTaxResponsibleController.onSubmit(NormalMode).url
    }
  }
}