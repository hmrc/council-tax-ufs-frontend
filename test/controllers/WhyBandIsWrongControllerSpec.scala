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
import models.{CheckMode, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.WhyBandIsWrongPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository

import scala.concurrent.Future

class WhyBandIsWrongControllerSpec extends SpecBase with MockitoSugar {

  "WhyBandIsWrongController" - {
    "must render for an authenticated user with no existing answers" in {
      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.WhyBandIsWrongController.onPageLoad(NormalMode).url)).value

        status(result) mustBe OK
        contentAsString(result) must include("Tell us why you think the Council Tax band is wrong?")
      }
    }

    "must render the saved answer in Check mode" in {
      val answer = "My evidence shows the band is wrong."
      val userAnswers = emptyUserAnswers.set(WhyBandIsWrongPage, answer).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.WhyBandIsWrongController.onPageLoad(CheckMode).url)
        val result = route(application, request).value

        status(result) mustBe OK
        contentAsString(result) must include(answer)
        contentAsString(result) must include("change-why-band-is-wrong")
      }
    }

    "must continue to Check Your Answers after a valid Normal-mode answer" in {
      val sessionRepository = mock[SessionRepository]
      when(sessionRepository.set(any())) thenReturn Future.successful(true)
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SessionRepository].toInstance(sessionRepository))
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.WhyBandIsWrongController.onSubmit(NormalMode).url)
          .withFormUrlEncodedBody("value" -> "My evidence shows the band is wrong.")
        val result = route(application, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.CheckYourAnswersController.onPageLoad().url
      }
    }

    "must save the answer when no answer document exists yet" in {
      val sessionRepository = mock[SessionRepository]
      when(sessionRepository.set(any())) thenReturn Future.successful(true)
      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[SessionRepository].toInstance(sessionRepository))
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.WhyBandIsWrongController.onSubmit(NormalMode).url)
          .withFormUrlEncodedBody("value" -> "My evidence shows the band is wrong.")
        val result = route(application, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.CheckYourAnswersController.onPageLoad().url
      }
    }
  }
}