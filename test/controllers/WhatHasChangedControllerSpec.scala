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
import models.deletionOptions
import pages.{WhatHasChangedPage, deletionOptionsPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository

import scala.concurrent.Future

class WhatHasChangedControllerSpec extends SpecBase with MockitoSugar {

  private def appWithRepo(userAnswers: Option[models.UserAnswers]) = {
    val sessionRepository = mock[SessionRepository]
    when(sessionRepository.set(any())) thenReturn Future.successful(true)
    applicationBuilder(userAnswers = userAnswers).overrides(bind[SessionRepository].toInstance(sessionRepository)).build()
  }

  "WhatHasChangedController" - {

    "must render the question" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.WhatHasChangedController.onPageLoad(NormalMode).url)).value

        status(result) mustBe OK
        contentAsString(result) must include("What has changed?")
        contentAsString(result) must include("Provide as much detail as possible.")
        contentAsString(result) must include(routes.ScenarioOtherController.onPageLoad().url)
      }
    }

    "must use the local area wording and error when local area changes was chosen" in {
      val answers     = emptyUserAnswers.set(deletionOptionsPage, deletionOptions.Affected).success.value
      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {
        val page = route(application, FakeRequest(GET, routes.WhatHasChangedController.onPageLoad(NormalMode).url)).value
        contentAsString(page) must include("What permanent changes have taken place in your local area?")
        contentAsString(page) must include("a new supermarket or motorway has been built")
        contentAsString(page) must include(routes.ReviewOverviewController.formalReview().url)

        val blank = route(application, FakeRequest(POST, routes.WhatHasChangedController.onSubmit(NormalMode).url).withFormUrlEncodedBody("value" -> "")).value
        status(blank) mustBe BAD_REQUEST
        contentAsString(blank) must include("Enter details of the changes")
      }
    }

    "must show the saved answer in check mode" in {
      val answers     = emptyUserAnswers.set(WhatHasChangedPage, "Split into two flats").success.value
      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.WhatHasChangedController.onPageLoad(CheckMode).url)).value

        status(result) mustBe OK
        contentAsString(result) must include("Split into two flats")
      }
    }

    "must continue to When did this change happen after a valid answer" in {
      val application = appWithRepo(Some(emptyUserAnswers))

      running(application) {
        val request = FakeRequest(POST, routes.WhatHasChangedController.onSubmit(NormalMode).url).withFormUrlEncodedBody("value" -> "Split into two flats")
        val result  = route(application, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.WhenDidChangeHappenController.onPageLoad(NormalMode).url
      }
    }

    "must return a Bad Request with the error message when nothing is entered" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(POST, routes.WhatHasChangedController.onSubmit(NormalMode).url).withFormUrlEncodedBody("value" -> "")
        val result  = route(application, request).value

        status(result) mustBe BAD_REQUEST
        contentAsString(result) must include("Enter what has changed")
      }
    }

    "must return a Bad Request when the answer is longer than 5000 characters" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(POST, routes.WhatHasChangedController.onSubmit(NormalMode).url).withFormUrlEncodedBody("value" -> ("a" * 5001))
        val result  = route(application, request).value

        status(result) mustBe BAD_REQUEST
        contentAsString(result) must include("Enter what has changed")
      }
    }
  }
}
