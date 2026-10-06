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

import models.{PropertyEntry, SearchResultsViewModel}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import utils.PaginationHelper

class SearchResultsViewSpec extends AnyWordSpec with Matchers {

  private val app: Application = new GuiceApplicationBuilder().build()
  private val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  private val request = FakeRequest()
  private implicit val messages: Messages = messagesApi.preferred(request)
  private val template = app.injector.instanceOf[views.html.SearchResultsView]

  "SearchResultsView" should {
    "show Wales-specific band guidance when results are from Wales" in {
      val viewModel = SearchResultsViewModel(
        postcode = "CF14 1AA",
        entries = Seq(PropertyEntry("PROP-123", "Cardiff Council", "1 Test Street", "C")),
        currentPage = 1,
        totalPages = 1,
        totalItems = 1,
        pageSize = 20,
        countries = Seq(models.SearchCountry.Wales)
      )
      val pagination = PaginationHelper.buildPagination(1, 1, _ => "/search-results?page=1")

      val html = template.render(viewModel, "postcode-token", pagination, request, messages).body

      html should include("every home in Wales")
      html should include("1 April 2003")
      html should include("highest band is &#x27;I&#x27;")
    }

    "use a lowercase property ID without search parameters in property links" in {
      val propertyId = "PROP-123"
      val currentPage = 3
      val viewModel = SearchResultsViewModel(
        postcode = "CF14 1AA",
        entries = Seq(PropertyEntry(propertyId, "Cardiff Council", "1 Test Street", "C")),
        currentPage = currentPage,
        totalPages = 5,
        totalItems = 100,
        pageSize = 20
      )
      val pagination = PaginationHelper.buildPagination(currentPage, 5, page => s"/search-results?page=$page")

      val html = template.render(viewModel, "postcode-token", pagination, request, messages).body
      val propertyUrl = controllers.routes.PropertyDetailController
        .onPageLoad(propertyId.toLowerCase)
        .url

      html should include(s"href=\"${propertyUrl.replace("&", "&amp;")}\"")
      propertyUrl should not include "?"
    }
  }
}
