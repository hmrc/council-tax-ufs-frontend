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

import com.typesafe.config.ConfigFactory
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Configuration

class ServiceSpec extends AnyFreeSpec with Matchers {

  "Service" - {

    "must build its base url from protocol, host and port" in {
      Service("localhost", "9514", "http").baseUrl mustBe "http://localhost:9514"
    }

    "must use the base url as its string form" in {
      Service("auth.protected.mdtp", "443", "https").toString mustBe "https://auth.protected.mdtp:443"
    }

    "must implicitly convert to its base url" in {
      val url: String = Service("localhost", "8500", "http")
      url mustBe "http://localhost:8500"
    }

    "must load from configuration" in {
      val configuration = Configuration(ConfigFactory.parseString(
        """
          |microservice.services.auth {
          |  protocol = https
          |  host     = auth.protected.mdtp
          |  port     = 443
          |}
          |""".stripMargin
      ))

      configuration.get[Service]("microservice.services.auth") mustBe Service("auth.protected.mdtp", "443", "https")
    }

    "must fail to load when a required key is missing" in {
      val configuration = Configuration(ConfigFactory.parseString(
        "microservice.services.auth { host = localhost, port = 8500 }"
      ))

      an[Exception] must be thrownBy configuration.get[Service]("microservice.services.auth")
    }
  }
}
