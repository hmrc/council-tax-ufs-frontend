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
import models.{NormalMode, ReasonForReview, UserAnswers, deletionOptions, noticeAlteration, scenarioResponsible}
import pages.{ReasonForReviewPage, deletionOptionsPage, noticeAlterationPage, scenarioResponsiblePage}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*

class ReviewOverviewControllerSpec extends SpecBase {

  "ReviewOverviewController" - {
    "must render the formal review page" in {
      val application = new GuiceApplicationBuilder().build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.ReviewOverviewController.formalReview().url).withSession("sessionId" -> userAnswersId)).value

        status(result) mustBe OK
        contentAsString(result) must include("Asking for a formal review")
      }
    }

    "must render the informal review page" in {
      val application = new GuiceApplicationBuilder().build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.ReviewOverviewController.informalReview().url).withSession("sessionId" -> userAnswersId)).value

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
        redirectLocation(result).value must include("council-tax-liability-start-date")
      }
    }

    "must continue from the formal review for local area changes to what has changed" in {
      val answers = UserAnswers(userAnswersId)
        .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
        .set(deletionOptionsPage, deletionOptions.Affected).success.value
      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.ReviewOverviewController.continueToLogin("deletion").url)
          .withSession("sessionId" -> userAnswersId)
        val result = route(application, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.WhatHasChangedController.onPageLoad(NormalMode).url
      }
    }

    "must send the formal review back to the deletion options for local area changes" in {
      val answers = UserAnswers(userAnswersId)
        .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
        .set(deletionOptionsPage, deletionOptions.Affected).success.value
      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.ReviewOverviewController.formalReview().url).withSession("sessionId" -> userAnswersId)).value

        val html = contentAsString(result)
        html must include(routes.deletionOptionsController.onPageLoad(NormalMode).url)
        html must include("changes made to your home or significant changes made in the local area")
        html must include("all or part of your home has been demolished")
        html must include("There are 4 possible decisions that can be made:")
        html must include("your home is deleted from the Council Tax list")
        html must include(routes.ReviewOverviewController.continueToLogin("deletion").url)
        html must not include "less than 6 months"
      }
    }

    "must show the demolished formal review in English and Welsh, and continue to the reason" in {
      val application = applicationBuilder(userAnswers = Some(demolishedAnswers(None))).build()

      running(application) {
        val english = contentAsString(route(application, FakeRequest(GET, routes.ReviewOverviewController.formalReview().url)).value)
        english must include("property has been demolished completely and cannot be lived in")
        english must include("section 80/81 Notice of Demolition")
        english must include(routes.ReviewOverviewController.continueToLogin("demolished").url)

        val welsh = contentAsString(route(application,
          FakeRequest(GET, routes.ReviewOverviewController.formalReview().url).withCookies(play.api.mvc.Cookie("PLAY_LANG", "cy"))).value)
        welsh must not include "reviewOverview.demolished"

        val result = route(application, FakeRequest(GET, routes.ReviewOverviewController.continueToLogin("demolished").url)).value
        redirectLocation(result).value mustEqual routes.UninhabitableDetailsController.onPageLoad(NormalMode).url
      }
    }

    "must show the major works or renovations formal review in English and Welsh, and continue to the start date" in {
      val application = applicationBuilder(userAnswers = Some(renoAnswers)).build()

      running(application) {
        val english = contentAsString(route(application, FakeRequest(GET, routes.ReviewOverviewController.formalReview().url)).value)
        english must include("property is having major works or renovations done and cannot be lived in")
        english must include("A Scheme of Works document")
        english must include(routes.ReviewOverviewController.continueToLogin("reno").url)

        val welsh = contentAsString(route(application,
          FakeRequest(GET, routes.ReviewOverviewController.formalReview().url).withCookies(play.api.mvc.Cookie("PLAY_LANG", "cy"))).value)
        welsh must not include "reviewOverview.reno"

        val result = route(application, FakeRequest(GET, routes.ReviewOverviewController.continueToLogin("reno").url)).value
        redirectLocation(result).value mustEqual routes.RenoStartDateController.onPageLoad(NormalMode).url
      }
    }

    "must show the part demolished formal review in English and Welsh, and continue to the demolition date" in {
      val application = applicationBuilder(userAnswers = Some(partDemoAnswers)).build()

      running(application) {
        val english = contentAsString(route(application, FakeRequest(GET, routes.ReviewOverviewController.formalReview().url)).value)
        english must include("property has been partially demolished and cannot be lived in")
        english must include("Valuation Tribunal Wales")
        english must include(routes.ReviewOverviewController.continueToLogin("partdemo").url)

        val welsh = contentAsString(route(application,
          FakeRequest(GET, routes.ReviewOverviewController.formalReview().url).withCookies(play.api.mvc.Cookie("PLAY_LANG", "cy"))).value)
        welsh must not include "reviewOverview.partdemo"

        val result = route(application, FakeRequest(GET, routes.ReviewOverviewController.continueToLogin("partdemo").url)).value
        redirectLocation(result).value mustEqual routes.PartDemoDateController.onPageLoad(NormalMode).url
      }
    }

    "must show the derelict formal review, going back to the property choice and on to the uninhabitable details" in {
      val application = applicationBuilder(userAnswers = Some(derelictAnswers(None))).build()

      running(application) {
        val html = contentAsString(route(application, FakeRequest(GET, routes.ReviewOverviewController.formalReview().url)
          .withSession("sessionId" -> userAnswersId)).value)

        html must include("your property is derelict and cannot be lived in")
        html must include("Important")
        html must include("Photo evidence")
        html must include("There are 2 possible outcomes:")
        html must include("A Schedule of Condition document")
        html must include(routes.PropertyUninhabitableController.onPageLoad(NormalMode).url)
        html must include(routes.ReviewOverviewController.continueToLogin("derelict").url)
        html must not include "less than 6 months"
        html must not include "your home is deleted from the Council Tax list"
      }
    }

    "must show the derelict formal review in Welsh without missing text" in {
      val application = applicationBuilder(userAnswers = Some(derelictAnswers(None))).build()

      running(application) {
        val request = FakeRequest(GET, routes.ReviewOverviewController.formalReview().url)
          .withSession("sessionId" -> userAnswersId).withCookies(play.api.mvc.Cookie("PLAY_LANG", "cy"))
        val html = contentAsString(route(application, request).value)

        html must not include "reviewOverview.derelict"
      }
    }

    "must continue from the derelict formal review to the first uninhabitable question" in {
      val application = applicationBuilder(userAnswers = Some(derelictAnswers(None))).build()

      running(application) {
        val request = FakeRequest(GET, routes.ReviewOverviewController.continueToLogin("derelict").url)
          .withSession("sessionId" -> userAnswersId)
        val result = route(application, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.UninhabitableDetailsController.onPageLoad(NormalMode).url
      }
    }

    "must keep the original formal review wording and three decisions for the band challenge" in {
      val answers = UserAnswers(userAnswersId).set(scenarioResponsiblePage, scenarioResponsible.Lessthan).success.value
      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.ReviewOverviewController.formalReview().url).withSession("sessionId" -> userAnswersId)).value
        val html = contentAsString(result)

        html must include("less than 6 months")
        html must include("There are 3 possible decisions that can be made:")
        html must not include "your home is deleted from the Council Tax list"
      }
    }

    "must continue from the formal review with a Notice of Alteration to the notice date" in {
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
        redirectLocation(result).value must include("date-notice")
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
        redirectLocation(result).value must include("why-band-is-wrong")
      }
    }
  }
}