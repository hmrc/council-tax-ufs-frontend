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

import models.PropertyDetailViewModel
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest

class PropertyDetailViewSpec extends AnyWordSpec with Matchers {

  private val app: Application = new GuiceApplicationBuilder().build()
  private val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  private val request = FakeRequest()
  private implicit val messages: Messages = messagesApi.preferred(request)

  private val template = app.injector.instanceOf[views.html.PropertyDetailView]

  private val viewModel = PropertyDetailViewModel(
    propertyId = "PROP-123",
    address = "1 Test Street",
    band = "C",
    effectiveFromDate = "1 January 2024",
    localCouncil = "Test Council",
    localCouncilCode = "E07000001",
    councilRefNumber = "ABC123",
    improvementInd = "No",
    mixedUse = "No",
    courtCode = "V",
    country = "E92000001"
  )

  "PropertyDetailView" should {
    "render the property address and main details" in {
      val html = template.render(viewModel, request, messages).body

      html should include("1 Test Street")
      html should include("govuk-table")
      html should include("Test Council")
      html should include("ABC123")
      html should include("1 January 2024")
      html should include("C")
    }

    "render the continue button" in {
      val html = template.render(viewModel, request, messages).body

      html should include("id=\"continue\"")
    }

    "render a local council link" in {
      val html = template.render(viewModel, request, messages).body

      html should include("class=\"govuk-link\"")
      html should include("Test Council")
    }

    "render the court code details section" in {
      val html = template.render(viewModel, request, messages).body

      html should include("govuk-details")
      html should include("V")
    }
  }
}