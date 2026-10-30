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
import pages.{CouncilTaxLiabilityStartDatePage, PostcodeSearchPage, WhyBandIsWrongPage}
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
        contentAsString(result) mustEqual view(Seq.empty, routes.WhyBandIsWrongController.onPageLoad(models.NormalMode).url)(request, messages(application)).toString
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
        .set(PostcodeSearchPage, "CF14 1AA").success.value
        .set(CouncilTaxLiabilityStartDatePage, LocalDate.of(2025, 8, 12)).success.value
        .set(WhyBandIsWrongPage, "My evidence explains why the band is wrong.").success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)
        val result = route(application, request).value
        val html = contentAsString(result)

        status(result) mustEqual OK
        html must include("Property details")
        html must include("Challenge details")
        html must include("Postcode used to find your home")
        html must include("Date you became responsible for Council Tax")
        html must include("Why you think the Council Tax band is wrong")
        html.indexOf("Property details") must be < html.indexOf("Challenge details")
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