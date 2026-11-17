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
import forms.PropertyUninhabitableFormProvider
import models.{PropertyUninhabitable, NormalMode}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.html.PropertyUninhabitableView

class PropertyUninhabitableViewSpec extends SpecBase {

  val form: Form[PropertyUninhabitable] = new PropertyUninhabitableFormProvider()()

  val application = applicationBuilder(userAnswers = None).build()
  val view        = application.injector.instanceOf[PropertyUninhabitableView]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  def applyView(form: Form[PropertyUninhabitable]): HtmlFormat.Appendable =
    view(form, NormalMode)(fakeRequest, messages(application))

  def asDocument(html: HtmlFormat.Appendable): Document =
    Jsoup.parse(html.toString)

  "PropertyUninhabitableView" - {

    "must render the correct page title" in {
      val doc = asDocument(applyView(form))
      doc.title() must include(messages(application)("propertyUninhabitable.title"))
    }

    "must render the heading" in {
      val doc = asDocument(applyView(form))
      doc.select("h1.govuk-fieldset__heading").text() mustBe
        messages(application)("propertyUninhabitable.heading")
    }

    "must render four radio buttons" in {
      val doc = asDocument(applyView(form))
      doc.select(".govuk-radios__item").size() mustBe 4
    }

    "must render the first radio option" in {
      val doc = asDocument(applyView(form))
      doc.select("label[for=value_0]").text() mustBe
        messages(application)("propertyUninhabitable.derelict")
    }

    "must render the second radio option" in {
      val doc = asDocument(applyView(form))
      doc.select("label[for=value_1]").text() mustBe
        messages(application)("propertyUninhabitable.demolished")
    }

    "must render the third radio option" in {
      val doc = asDocument(applyView(form))
      doc.select("label[for=value_2]").text() mustBe
        messages(application)("propertyUninhabitable.reno")
    }

    "must render the fourth radio option" in {
      val doc = asDocument(applyView(form))
      doc.select("label[for=value_3]").text() mustBe
        messages(application)("propertyUninhabitable.partdemo")
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
        routes.PropertyUninhabitableController.onSubmit(NormalMode).url
    }
  }
}