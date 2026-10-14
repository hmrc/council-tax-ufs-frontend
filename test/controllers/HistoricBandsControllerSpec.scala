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
import models.{HistoricBandEntry, HistoricBandFullViewModel, HistoricBandsViewModel}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.i18n.Lang
import services.PropertyDetailService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class HistoricBandsControllerSpec extends SpecBase with MockitoSugar {

  "HistoricBandsController" - {

    "must display historic bands for the requested property" in {
      val propertyId = "PROP-123"
      val service = mock[PropertyDetailService]
      val viewModel = HistoricBandsViewModel(
        propertyId,
        "19, St David Crescent, Cardiff, CF5 4GP",
        Seq(
          HistoricBandEntry("B", "1 April 1993", "1 January 2000", "Cardiff Council", "REF-123", "No", "No", "None", "1993"),
          HistoricBandEntry("C", "1 January 2024", "1 February 2025", "Cardiff Council", "REF-123", "Yes", "No", "None", "2024")
        )
      )

      when(service.getHistoricBands(eqTo(propertyId))(any[HeaderCarrier], any[Lang]))
        .thenReturn(Future.successful(Right(Some(viewModel))))

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[PropertyDetailService].toInstance(service))
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.HistoricBandsController.onPageLoad(propertyId).url)
        val result = route(application, request).value
        val html = contentAsString(result)

        status(result) mustEqual OK
      html.split("class=\"govuk-back-link\"", -1).length - 1 mustEqual 1
        html must include("Cardiff, CF5 4GP")
        html must include("Council Tax band")
        html must include("What does my property information mean?")
        html must include("The band the property was in.")
        html must include("1 April 1993")
        html must include("1 January 2024")
        html must include(s"href=\"${routes.HistoricBandsController.onBandPageLoad(propertyId.toLowerCase, 0).url}\"")
        html must include(s"href=\"${routes.PropertyDetailController.onPageLoad(propertyId.toLowerCase).url}\"")
      }
    }

    "must display the selected historic band full details" in {
      val propertyId = "PROP-123"
      val bandIndex = 1
      val band = HistoricBandEntry("C", "1 January 2024", "1 February 2025", "Cardiff Council", "REF-123", "Yes", "No", "None", "2024")
      val service = mock[PropertyDetailService]
      val viewModel = HistoricBandFullViewModel(
        propertyId,
        "19, St David Crescent, Cardiff, CF5 4GP",
        band
      )

      when(service.getHistoricBand(eqTo(propertyId), eqTo(bandIndex))(any[HeaderCarrier], any[Lang]))
        .thenReturn(Future.successful(Right(Some(viewModel))))

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[PropertyDetailService].toInstance(service))
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.HistoricBandsController.onBandPageLoad(propertyId, bandIndex).url)
        val result = route(application, request).value
        val html = contentAsString(result)

        status(result) mustEqual OK
        html must include("<meta name=\"description\" content=\"View historic Council Tax band details for this property, including when the band applied.\">")
      html.split("class=\"govuk-back-link\"", -1).length - 1 mustEqual 1
        html must include("19, St David Crescent, Cardiff, CF5 4GP")
        html must include("<th scope=\"row\" class=\"govuk-table__header\">Council Tax band</th>")
        html must include("<td class=\"govuk-table__cell\">C</td>")
        html must include("Local Authority")
        html must include("Cardiff Council")
        html must include("REF-123")
        html must include("Improvement indicator")
        html must include("1 January 2024")
        html must include("1 February 2025")
        html must include("Valuation list")
        html must include("2024")
        html must include("What does my property information mean?")
        html must include("Challenge your previous Council Tax band")
        html must include("href=\"tel:03000501501\"")
        html must include("href=\"mailto:ctinboxvo@hmrc.gov.uk\"")
        html must include("Print this page")
        html must include(s"href=\"${routes.HistoricBandsController.onPageLoad(propertyId.toLowerCase).url}\"")
      }
    }
  }
}
