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
import org.mockito.ArgumentMatchers.{any, eq => eqTo}

import base.SpecBase
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.i18n.Lang
import services.PropertyDetailService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.ErrorResponse

import scala.concurrent.Future

class PropertyDetailControllerSpec extends SpecBase with MockitoSugar {

  private val propertyId = "property-id"

  "PropertyDetail Controller" - {

    "must redirect uppercase property IDs to the lowercase ID-only URL" in {
      val uppercasePropertyId = "DF8082EA-EA20-4B1B-A149-73156D39C5FD"
      val lowercasePropertyId = uppercasePropertyId.toLowerCase

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.PropertyDetailController.onPageLoad(uppercasePropertyId).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          routes.PropertyDetailController.onPageLoad(lowercasePropertyId).url
      }
    }

    "must render a generic internal server error for an upstream failure" in {
      val propertyDetailService = mock[PropertyDetailService]

      when(propertyDetailService.getPropertyDetail(eqTo(propertyId))(
        any[HeaderCarrier],
        any[Lang]
      )).thenReturn(Future.successful(Left(ErrorResponse(SERVICE_UNAVAILABLE, "upstream detail"))))

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[PropertyDetailService].toInstance(propertyDetailService))
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.PropertyDetailController.onPageLoad(propertyId).url)
        val result  = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
        contentAsString(result) must include("Sorry, there is a problem with the service")
        contentAsString(result) must not include "upstream detail"
      }
    }

    "must redirect to journey recovery for an upstream not-found response" in {
      val propertyDetailService = mock[PropertyDetailService]

      when(propertyDetailService.getPropertyDetail(eqTo(propertyId))(
        any[HeaderCarrier],
        any[Lang]
      )).thenReturn(Future.successful(Left(ErrorResponse(NOT_FOUND, "not found"))))

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[PropertyDetailService].toInstance(propertyDetailService))
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.PropertyDetailController.onPageLoad(propertyId).url)
        val result  = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}