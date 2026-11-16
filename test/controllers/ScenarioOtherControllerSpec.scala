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
import models.NormalMode
import play.api.test.FakeRequest
import play.api.test.Helpers.*

class ScenarioOtherControllerSpec extends SpecBase {

  "ScenarioOtherController" - {

    "must render the page for a user who is not signed in, with Continue going to What has changed" in {
      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.ScenarioOtherController.onPageLoad().url)).value
        val html   = contentAsString(result)

        status(result) mustBe OK
        html must include("for some other reason")
        html must include("You can change your selection if this option is not right for you.")
        html must include(routes.WhatHasChangedController.onPageLoad(NormalMode).url)
        html must include(routes.deletionOptionsController.onPageLoad(NormalMode).url)
        html must include("govuk-grid-column-full")
        html must not include "govuk-grid-column-two-thirds"
      }
    }
  }
}
