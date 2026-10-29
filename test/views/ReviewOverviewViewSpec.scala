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

package views

import models.ReviewType
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest

class ReviewOverviewViewSpec extends AnyWordSpec with Matchers {

  private val app: Application = new GuiceApplicationBuilder().build()
  private val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  private val request = FakeRequest()
  private implicit val messages: Messages = messagesApi.preferred(request)
  private val template = app.injector.instanceOf[views.html.ReviewOverviewView]

  "ReviewOverviewView" should {
    "render the formal review content and shared decision table" in {
      val html = template.render(ReviewType.Formal, "/back-link", request, messages).body

      html should include("Asking for a formal review")
      html should include("less than 6 months")
      html should include("<strong>less than 6 months</strong>")
      html should include("for <strong>less than 6 months</strong>.</td>")
      html should include("Up to 16 weeks")
      html should include("Evidence required")
      html should include("addresses of similar homes in a lower band")
      html should include("Valuation Tribunal Wales")
      html should include("review/continue")
      html should not include "javascript:history.back()"
      html should include(">Back</a>")
    }

    "render the informal review content" in {
      val html = template.render(ReviewType.Informal, "/back-link", request, messages).body

      html should include("Asking for an informal review")
      html should include("more than 6 months")
      html should include("Up to 48 weeks")
      html should include("supporting evidence")
      html should include("The decision is final")
    }

    "use the existing formal review continuation" in {
      val html = template.render(ReviewType.Formal, "/back-link", request, messages).body

      html should include("Asking for a formal review")
      html should include(controllers.routes.ReviewOverviewController.continueToLogin("formal").url)
    }
  }
}