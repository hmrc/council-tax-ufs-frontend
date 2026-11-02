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
import play.api.i18n.Lang
import play.api.test.FakeRequest
import play.api.test.Helpers.*

class LanguageSwitchControllerSpec extends SpecBase {

  "LanguageSwitchController" - {

    "must fall back to the index page" in {
      val application = applicationBuilder().build()

      running(application) {
        val controller = application.injector.instanceOf[LanguageSwitchController]
        controller.fallbackURL mustEqual routes.IndexController.onPageLoad().url
      }
    }

    "must expose the English and Welsh languages" in {
      val application = applicationBuilder().build()

      running(application) {
        val controller = application.injector.instanceOf[LanguageSwitchController]
        controller.languageMap mustEqual Map("en" -> Lang("en"), "cy" -> Lang("cy"))
      }
    }

    "must redirect to the referer and set the Welsh language cookie when switching to cy" in {
      val application = applicationBuilder().build()

      running(application) {
        val request = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage("cy").url)
          .withHeaders("Referer" -> "/council-tax-ufs/somewhere")

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual "/council-tax-ufs/somewhere"
        cookies(result).get("PLAY_LANG").map(_.value) mustBe Some("cy")
      }
    }

    "must redirect to the fallback URL when there is no referer" in {
      val application = applicationBuilder().build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage("en").url)).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad().url
      }
    }
  }
}
