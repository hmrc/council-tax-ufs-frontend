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
import pages.{WhenDidChangeHappenPage, deletionOptionsPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository

import java.time.LocalDate
import scala.concurrent.Future

class WhenDidChangeHappenControllerSpec extends SpecBase with MockitoSugar {

  private def anotherReason = emptyUserAnswers.set(deletionOptionsPage, deletionOptions.AnotherReason).success.value

  private val validDate = LocalDate.of(2025, 4, 27)

  private def dateBody(day: String, month: String, year: String) =
    Seq("value.day" -> day, "value.month" -> month, "value.year" -> year)

  "WhenDidChangeHappenController" - {

    "must render the question in English with English labels" in {
      val application = applicationBuilder(userAnswers = Some(anotherReason)).build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.WhenDidChangeHappenController.onPageLoad(NormalMode).url)).value
        val html   = contentAsString(result)

        status(result) mustBe OK
        html must include("When did this change happen?")
        html must include("For example, if the change happened on 27 April 2025, enter 27 4 2025.")
        html must include("Day")
        html must include("Continue")
        html must not include "Diwrnod"
        html must not include "Parhau"
        html must include(routes.WhatHasChangedController.onPageLoad(NormalMode).url)
      }
    }

    "must use the local area wording and errors when local area changes was chosen" in {
      val answers     = emptyUserAnswers.set(deletionOptionsPage, deletionOptions.Affected).success.value
      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {
        val page = route(application, FakeRequest(GET, routes.WhenDidChangeHappenController.onPageLoad(NormalMode).url)).value
        contentAsString(page) must include("When did these permanent changes take place?")
        contentAsString(page) must include("if the changes in your local area happened on 27 April 2025")

        def post(fields: Seq[(String, String)]) =
          route(application, FakeRequest(POST, routes.WhenDidChangeHappenController.onSubmit(NormalMode).url).withFormUrlEncodedBody(fields*)).value

        val blank = post(dateBody("", "", ""))
        status(blank) mustBe BAD_REQUEST
        contentAsString(blank) must include("Enter the date the changes took place")

        val tooEarly = post(dateBody("31", "3", "1993"))
        contentAsString(tooEarly) must include("must be between 1 April 1993 and today")
      }
    }

    "must show the saved date in check mode" in {
      val answers     = emptyUserAnswers.set(WhenDidChangeHappenPage, validDate).success.value
      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.WhenDidChangeHappenController.onPageLoad(CheckMode).url)).value

        status(result) mustBe OK
        contentAsString(result) must include("2025")
      }
    }

    "must continue to Contact Details after a valid date" in {
      val sessionRepository = mock[SessionRepository]
      when(sessionRepository.set(any())) thenReturn Future.successful(true)
      val application = applicationBuilder(userAnswers = Some(anotherReason))
        .overrides(bind[SessionRepository].toInstance(sessionRepository)).build()

      running(application) {
        val request = FakeRequest(POST, routes.WhenDidChangeHappenController.onSubmit(NormalMode).url)
          .withFormUrlEncodedBody(dateBody("27", "4", "2025")*)
        val result = route(application, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ContactDetailsController.onPageLoad(NormalMode).url
      }
    }

    "must show the matching error for each invalid date" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val future      = LocalDate.now().plusDays(2)

      running(application) {
        def post(fields: Seq[(String, String)]) =
          route(application, FakeRequest(POST, routes.WhenDidChangeHappenController.onSubmit(NormalMode).url).withFormUrlEncodedBody(fields*)).value

        val blank = post(dateBody("", "", ""))
        status(blank) mustBe BAD_REQUEST
        contentAsString(blank) must include("Enter the date the change happened")

        val partial = post(dateBody("27", "", "2025"))
        status(partial) mustBe BAD_REQUEST
        contentAsString(partial) must include("Enter the date the change happened")

        val inFuture = post(dateBody(future.getDayOfMonth.toString, future.getMonthValue.toString, future.getYear.toString))
        status(inFuture) mustBe BAD_REQUEST
        contentAsString(inFuture) must include("The date must be today or in the past")

        val tooEarly = post(dateBody("31", "3", "1993"))
        status(tooEarly) mustBe BAD_REQUEST
        contentAsString(tooEarly) must include("must be between 1 April 1993 and today")
      }
    }
  }
}
