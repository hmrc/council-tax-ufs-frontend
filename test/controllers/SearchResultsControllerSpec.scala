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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SearchResultsService
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.play.bootstrap.http.ErrorResponse
import utils.UrlEncryptor

import scala.concurrent.Future

class SearchResultsControllerSpec extends SpecBase with MockitoSugar {

  private val encodedPostcode = "encrypted-postcode"
  private val decodedPostcode = "CF14 1AA"

  "SearchResults Controller" - {

    "must render a generic internal server error for an upstream failure" in {
      val searchResultsService = mock[SearchResultsService]
      val urlEncryptor         = mock[UrlEncryptor]

      when(urlEncryptor.decrypt(encodedPostcode)).thenReturn(decodedPostcode)
      when(searchResultsService.search(any[String], any[Int])(any[HeaderCarrier]))
        .thenReturn(Future.successful(Left(ErrorResponse(SERVICE_UNAVAILABLE, "upstream detail"))))

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[SearchResultsService].toInstance(searchResultsService),
          bind[UrlEncryptor].toInstance(urlEncryptor)
        )
        .build()

      running(application) {
        val request = FakeRequest(
          GET,
          routes.SearchResultsController.show(encodedPostcode, 1).url
        ).withSession(SessionKeys.sessionId -> "test-session")

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
        contentAsString(result) must include("Sorry, there is a problem with the service")
        contentAsString(result) must not include "upstream detail"
      }
    }

    "must render no results for an upstream not-found response" in {
      val searchResultsService = mock[SearchResultsService]
      val urlEncryptor         = mock[UrlEncryptor]

      when(urlEncryptor.decrypt(encodedPostcode)).thenReturn(decodedPostcode)
      when(searchResultsService.search(any[String], any[Int])(any[HeaderCarrier]))
        .thenReturn(Future.successful(Left(ErrorResponse(NOT_FOUND, "not found"))))

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[SearchResultsService].toInstance(searchResultsService),
          bind[UrlEncryptor].toInstance(urlEncryptor)
        )
        .build()

      running(application) {
        val request = FakeRequest(
          GET,
          routes.SearchResultsController.show(encodedPostcode, 1).url
        ).withSession(SessionKeys.sessionId -> "test-session")

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) must include("No results for")
      }
    }
  }
}