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
import config.FrontendAppConfig
import models.{UserAnswers, noticeAlteration, scenarioResponsible}
import pages.{noticeAlterationPage, scenarioResponsiblePage}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*

class ReviewOverviewControllerSpec extends SpecBase {

  "ReviewOverviewController" - {
    "must render the formal review page" in {
      val application = new GuiceApplicationBuilder().build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.ReviewOverviewController.formalReview().url)).value

        status(result) mustBe OK
        contentAsString(result) must include("Asking for a formal review")
      }
    }

    "must render the informal review page" in {
      val application = new GuiceApplicationBuilder().build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.ReviewOverviewController.informalReview().url)).value

        status(result) mustBe OK
        contentAsString(result) must include("Asking for an informal review")
      }
    }

    "must continue from formal review login to the liability start date page" in {
      val answers = UserAnswers(userAnswersId)
        .set(scenarioResponsiblePage, scenarioResponsible.Lessthan).success.value
      val application = applicationBuilder(userAnswers = Some(answers)).build()
      val appConfig = application.injector.instanceOf[FrontendAppConfig]

      running(application) {
        val request = FakeRequest(GET, routes.ReviewOverviewController.continueToLogin("formal").url)
          .withSession("sessionId" -> userAnswersId)
        val result = route(application, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value must startWith(appConfig.loginUrl)
        redirectLocation(result).value must include("council-tax-liability-start-date")
      }
    }

    "must continue from the formal review with a Notice of Alteration to why the band is wrong" in {
      val answers = UserAnswers(userAnswersId)
        .set(scenarioResponsiblePage, scenarioResponsible.Morethan).success.value
        .set(noticeAlterationPage, noticeAlteration.Yes).success.value
      val application = applicationBuilder(userAnswers = Some(answers)).build()
      val appConfig = application.injector.instanceOf[FrontendAppConfig]

      running(application) {
        val request = FakeRequest(GET, routes.ReviewOverviewController.continueToLogin("formal").url)
          .withSession("sessionId" -> userAnswersId)
        val result = route(application, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value must startWith(appConfig.loginUrl)
        redirectLocation(result).value must include("why-band-is-wrong")
      }
    }

    "must continue from informal review to why the band is wrong" in {
      val application = new GuiceApplicationBuilder().build()
      val appConfig = application.injector.instanceOf[FrontendAppConfig]

      running(application) {
        val request = FakeRequest(GET, routes.ReviewOverviewController.continueToLogin("informal").url)
          .withSession("sessionId" -> userAnswersId)
        val result = route(application, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value must startWith(appConfig.loginUrl)
        redirectLocation(result).value must include("why-band-is-wrong")
      }
    }
  }
}