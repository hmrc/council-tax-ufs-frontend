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
import models.CouncilTaxResponsible
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
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]
        status(result) mustEqual OK
        contentAsString(result) mustEqual view(Seq.empty, routes.WhichBandController.onPageLoad(models.NormalMode).url)(request, messages(application)).toString
      }
    }

    "must show the saved explanation and a Change link to the editable page" in {
      val answer = "The band does not reflect the evidence for my home."
      val userAnswers = emptyUserAnswers.set(WhyBandIsWrongPage, answer).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)
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
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)
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
          contentAsString(route(application, FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)).value)
        }
      }

      page(emptyUserAnswers.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value) must include("Authority to Act")
      page(emptyUserAnswers.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.Yes).success.value) must not include "Authority to Act"
      page(emptyUserAnswers) must include("Print this page")
    }

    "must offer a Confirm and send button that posts to the submit route" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val html = contentAsString(route(application, FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)).value)
        html must include("Confirm and send")
        html must include(routes.CheckYourAnswersController.onSubmit().url)
      }
    }

    "must submit and redirect to the request sent page" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val result = route(application, FakeRequest(POST, routes.CheckYourAnswersController.onSubmit().url)).value
        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.RequestSentController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}