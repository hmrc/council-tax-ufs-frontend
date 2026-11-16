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
import forms.UninhabitableDetailsPlanPlanningApplicationFormProvider
import models.NormalMode
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.html.UninhabitableDetailsPlanPlanningApplicationView

class UninhabitableDetailsPlanPlanningApplicationViewSpec extends SpecBase {

  private val application = applicationBuilder(userAnswers = None).build()
  private val view        = application.injector.instanceOf[UninhabitableDetailsPlanPlanningApplicationView]
  private implicit val msgs: play.api.i18n.Messages = messages(application)

  private implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  private def form: Form[String] = new UninhabitableDetailsPlanPlanningApplicationFormProvider()()

  private def applyView(form: Form[String]): HtmlFormat.Appendable =
    view(form, NormalMode, Some("/back"))(fakeRequest, msgs)

  private def asDocument(html: HtmlFormat.Appendable): Document = Jsoup.parse(html.toString)

  "UninhabitableDetailsPlanPlanningApplicationView" - {

    "must render the correct page title" in {
      asDocument(applyView(form)).title() must include(msgs("uninhabitableDetailsPlanPlanningApplication.title"))
    }

    "must render the heading" in {
      asDocument(applyView(form)).select("h1").text() mustBe msgs("uninhabitableDetailsPlanPlanningApplication.heading")
    }

    "must render the hint text as a hint, linked to the textarea" in {
      val doc = asDocument(applyView(form))

      doc.select("#uninhabitable-details-plan-planning-application-hint").text() mustBe msgs("uninhabitableDetailsPlanPlanningApplication.hint")
      doc.select("#uninhabitable-details-plan-planning-application-hint").hasClass("govuk-hint") mustBe true
      doc.select("textarea#uninhabitable-details-plan-planning-application").attr("aria-describedby") mustBe "uninhabitable-details-plan-planning-application-hint uninhabitable-details-plan-planning-application-info"
    }

    "must render a character counted textarea limited to 5,000 characters" in {
      val doc = asDocument(applyView(form))

      doc.select("textarea#uninhabitable-details-plan-planning-application").isEmpty mustBe false
      doc.select(".govuk-character-count").attr("data-maxlength") mustBe "5000"
    }

    "must render the back link" in {
      asDocument(applyView(form)).select(".govuk-back-link").attr("href") mustBe "/back"
    }

    "must render a submit button" in {
      asDocument(applyView(form)).select("button.govuk-button").text() mustBe msgs("site.continue")
    }

    "must not show an error summary when there are no errors" in {
      asDocument(applyView(form)).select(".govuk-error-summary").isEmpty mustBe true
    }

    "must show the optional hint in the heading" in {
      asDocument(applyView(form)).select("h1").text() must endWith(" (" + "optional" + ")")
    }

    "must show the maximum length error when the answer is too long" in {
      val doc = asDocument(applyView(form.bind(Map("value" -> ("a" * 5001)))))

      doc.select(".govuk-error-summary").text() must include(msgs("uninhabitableDetailsPlanPlanningApplication.error.maxLength"))
    }

    "must render a form with the correct action" in {
      asDocument(applyView(form)).select("form").attr("action") mustBe
        routes.UninhabitableDetailsPlanPlanningApplicationController.onSubmit(NormalMode).url
    }
  }
}
