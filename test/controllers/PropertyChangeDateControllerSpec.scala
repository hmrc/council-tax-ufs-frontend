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
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{PropertyMergedDatePage, PropertySplitDatePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository

import java.time.LocalDate
import scala.concurrent.Future

class PropertyChangeDateControllerSpec extends SpecBase with MockitoSugar {

  private val onwardRoute = Call("GET", "/foo")

  private case class Journey(
    name: String,
    pageUrl: String,
    checkUrl: String,
    submitUrl: String,
    backUrl: String,
    heading: String,
    requiredError: String,
    setSaved: LocalDate => models.UserAnswers
  )

  private lazy val journeys = Seq(
    Journey(
      "merged",
      routes.PropertyChangeDateController.onPageLoadMerged(NormalMode).url,
      routes.PropertyChangeDateController.onPageLoadMerged(CheckMode).url,
      routes.PropertyChangeDateController.onSubmitMerged(NormalMode).url,
      routes.PropertyChangeDetailsController.onPageLoadMerged(NormalMode).url,
      "When was the work to merge your properties completed?",
      "Enter the date the work to merge your properties was completed",
      date => emptyUserAnswers.set(PropertyMergedDatePage, date).success.value
    ),
    Journey(
      "split",
      routes.PropertyChangeDateController.onPageLoadSplit(NormalMode).url,
      routes.PropertyChangeDateController.onPageLoadSplit(CheckMode).url,
      routes.PropertyChangeDateController.onSubmitSplit(NormalMode).url,
      routes.PropertyChangeDetailsController.onPageLoadSplit(NormalMode).url,
      "When was the work to split your home completed?",
      "Enter the date the work to split your home was completed",
      date => emptyUserAnswers.set(PropertySplitDatePage, date).success.value
    )
  )

  "PropertyChangeDateController" - {
    journeys.foreach { j =>
      j.name - {

        "must render the question with Back to the details page" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

          running(application) {
            val result = route(application, FakeRequest(GET, j.pageUrl)).value

            status(result) mustBe OK
            contentAsString(result) must include(j.heading)
            contentAsString(result) must include("For example, if the work was completed on 27 April 2025, enter 27 4 2025.")
            contentAsString(result) must include(s"""${j.backUrl}" class="govuk-back-link""")
          }
        }

        "must show the saved date" in {
          val application = applicationBuilder(userAnswers = Some(j.setSaved(LocalDate.of(2024, 3, 15)))).build()

          running(application) {
            val result = route(application, FakeRequest(GET, j.checkUrl)).value

            status(result) mustBe OK
            contentAsString(result) must include("value=\"2024\"")
          }
        }

        "must save a valid date and redirect to the next page" in {
          val sessionRepository = mock[SessionRepository]
          when(sessionRepository.set(any())) thenReturn Future.successful(true)
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[SessionRepository].toInstance(sessionRepository)
            )
            .build()

          running(application) {
            val request = FakeRequest(POST, j.submitUrl)
              .withFormUrlEncodedBody("value.day" -> "15", "value.month" -> "3", "value.year" -> "2024")
            val result = route(application, request).value

            status(result) mustBe SEE_OTHER
            redirectLocation(result).value mustBe onwardRoute.url
          }
        }

        "must return Bad Request when the date is missing" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

          running(application) {
            val request = FakeRequest(POST, j.submitUrl)
              .withFormUrlEncodedBody("value.day" -> "", "value.month" -> "", "value.year" -> "")
            val result = route(application, request).value

            status(result) mustBe BAD_REQUEST
            contentAsString(result) must include(j.requiredError)
          }
        }
      }
    }
  }
}
