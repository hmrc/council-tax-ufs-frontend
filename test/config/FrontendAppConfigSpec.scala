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

package config

import base.SpecBase
import play.api.i18n.Lang
import play.api.test.FakeRequest
import play.api.test.Helpers._

class FrontendAppConfigSpec extends SpecBase {

  private def withConfig(test: FrontendAppConfig => Unit): Unit = {
    val application = applicationBuilder().build()
    running(application) {
      test(application.injector.instanceOf[FrontendAppConfig])
    }
  }

  "FrontendAppConfig" - {

    "signInContinueUrl" - {

      "must return the plain absolute url when there is no session id" in withConfig { config =>
        config.signInContinueUrl("/council-tax-ufs/start", None) mustBe s"${config.host.stripSuffix("/")}/council-tax-ufs/start"
      }

      "must append the encoded session id with ? when the uri has no query string" in withConfig { config =>
        config.signInContinueUrl("/council-tax-ufs/start", Some("a b+c")) mustBe
          s"${config.host.stripSuffix("/")}/council-tax-ufs/start?k=a+b%2Bc"
      }

      "must append the encoded session id with & when the uri already has a query string" in withConfig { config =>
        config.signInContinueUrl("/council-tax-ufs/start?x=1", Some("abc")) mustBe
          s"${config.host.stripSuffix("/")}/council-tax-ufs/start?x=1&k=abc"
      }
    }

    "feedbackUrl must carry the service name and the current page as the back url" in withConfig { config =>
      val url = config.feedbackUrl(FakeRequest(GET, "/council-tax-ufs/page"))

      url must include("/contact/beta-feedback?service=council-tax-ufs-frontend")
      url must endWith(s"backUrl=${config.host}/council-tax-ufs/page")
    }

    "exitSurveyUrl must end with the service feedback path" in withConfig { config =>
      config.exitSurveyUrl must endWith("/feedback/council-tax-ufs-frontend")
    }

    "must expose the english and welsh languages" in withConfig { config =>
      config.languageMap mustBe Map("en" -> Lang("en"), "cy" -> Lang("cy"))
    }

    "must expose the timeout, countdown and cache ttl settings" in withConfig { config =>
      config.timeout must be > 0
      config.countdown must be > 0
      config.cacheTtl must be > 0L
      config.loginUrl must not be empty
      config.loginContinueUrl must not be empty
      config.signOutUrl must not be empty
      config.bridgeIntegration must not be empty
    }
  }
}
