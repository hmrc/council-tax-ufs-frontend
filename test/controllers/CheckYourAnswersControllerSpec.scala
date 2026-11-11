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

package controllers

import base.SpecBase
import models.{CouncilTaxResponsible, Journey, ReasonForReview}
import pages.{CouncilTaxLiabilityStartDatePage, CouncilTaxResponsiblePage, PropertyAddressPage, WhyBandIsWrongPage}
import java.time.LocalDate
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(Journey.Challenge).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]
        status(result) mustEqual OK
        contentAsString(result) mustEqual view(Seq.empty, routes.ContactDetailsController.onPageLoad(models.NormalMode).url)(request, messages(application)).toString
      }
    }

    "must serve the page at the review-details URL for the journey" in {
      routes.CheckYourAnswersController.onPageLoad(Journey.Challenge).url must endWith ("/challenge/review-details")
      routes.CheckYourAnswersController.onPageLoad(Journey.Deletion).url must endWith ("/deletion/review-details")
    }

    "must redirect to the page for the journey the user chose when the URL names the other one" in {
      val challenge = emptyUserAnswers.set(pages.ReasonForReviewPage, ReasonForReview.Bandiswrong).success.value
      val application = applicationBuilder(userAnswers = Some(challenge)).build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(Journey.Deletion).url)).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad(Journey.Challenge).url
      }
    }

    "must not submit when the URL names the other journey" in {
      val deletion = emptyUserAnswers.set(pages.ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
      val application = applicationBuilder(userAnswers = Some(deletion)).build()

      running(application) {
        val result = route(application, FakeRequest(POST, routes.CheckYourAnswersController.onSubmit(Journey.Challenge).url)).value

        redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad(Journey.Deletion).url
      }
    }

    "must reject an unknown journey in the URL" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      running(application) {
        status(route(application, FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(Journey.Challenge).url.replace("challenge", "other"))).value) mustEqual BAD_REQUEST
      }
    }

    "must show the saved explanation and a Change link to the editable page" in {
      val answer = "The band does not reflect the evidence for my home."
      val userAnswers = emptyUserAnswers.set(WhyBandIsWrongPage, answer).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(Journey.Challenge).url)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) must include(answer)
        contentAsString(result) must include(routes.WhyBandIsWrongController.onPageLoad(models.CheckMode).url)
      }
    }

    "must group only saved answers under their categories" in {
      val userAnswers = emptyUserAnswers
        .set(PropertyAddressPage, "35 Somerby Court, Nottingham").success.value
        .set(CouncilTaxLiabilityStartDatePage, LocalDate.of(2025, 8, 12)).success.value
        .set(WhyBandIsWrongPage, "My evidence explains why the band is wrong.").success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(Journey.Challenge).url)
        val result = route(application, request).value
        val html = contentAsString(result)

        status(result) mustEqual OK
        html must include("Challenge details")
        html must include("Address")
        html must include("35 Somerby Court, Nottingham")
        html must include("When did you become responsible for paying Council Tax for your home?")
        html must include("Tell us why you think the Council Tax band is wrong?")
        html.indexOf("Address") must be < html.indexOf("Challenge details")
      }
    }

    "must show the Authority to Act notice only when acting for someone" in {
      def page(answers: models.UserAnswers) = {
        val application = applicationBuilder(userAnswers = Some(answers)).build()
        running(application) {
          contentAsString(route(application, FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(Journey.Challenge).url)).value)
        }
      }

      page(emptyUserAnswers.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value) must include("Authority to Act")
      page(emptyUserAnswers.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.Yes).success.value) must not include "Authority to Act"
      page(emptyUserAnswers) must include("Print this page")
    }

    "must show the contact details, and the payer details only when acting for someone" in {
      val base = emptyUserAnswers
        .set(pages.ContactDetailsPage, models.ContactDetails("Jo", "Bloggs", Some("jo@example.com"), None, None, None, None, None, None, Set("email"))).success.value
        .set(pages.CouncilTaxPayerDetailsPage, models.CouncilTaxPayerDetails("Pat", "Payer", "pat@example.com")).success.value
      def page(answers: models.UserAnswers) = {
        val application = applicationBuilder(userAnswers = Some(answers)).build()
        running(application) {
          contentAsString(route(application, FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(Journey.Challenge).url)).value)
        }
      }

      val direct = page(base)
      direct must include("Jo Bloggs")
      direct must include("jo@example.com")
      direct must include("Contact details")
      direct must include("Preferred contact methods")
      direct must include("Preferred contact")
      direct must include("Email")
      direct must not include "Contact details for the property"
      direct must not include "Pat Payer"

      val acting = page(base.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value)
      acting must include("Pat Payer")
      acting must include("Contact details for the property&#x27;s Council Tax payer")
      acting must include(routes.CouncilTaxPayerDetailsController.onPageLoad(models.CheckMode).url)
    }

    "must offer a Confirm and send button that posts to the submit route" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val html = contentAsString(route(application, FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(Journey.Challenge).url)).value)
        html must include("Confirm and send")
        html must include(routes.CheckYourAnswersController.onSubmit(Journey.Challenge).url)
      }
    }

    "must submit and redirect to the request sent page" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val result = route(application, FakeRequest(POST, routes.CheckYourAnswersController.onSubmit(Journey.Challenge).url)).value
        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.RequestSentController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(Journey.Challenge).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}