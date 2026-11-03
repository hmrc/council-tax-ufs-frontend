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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock.*
import config.FrontendAppConfig
import models.SearchQuery
import org.mockito.Mockito.when
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}
import uk.gov.hmrc.play.bootstrap.http.ErrorResponse

import scala.concurrent.ExecutionContext.Implicits.global

class BridgeIntegrationConnectorSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with MockitoSugar
    with WireMockSupport
    with HttpClientV2Support {

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  private lazy val appConfig = {
    val config = mock[FrontendAppConfig]
    when(config.bridgeIntegration).thenReturn(wireMockUrl)
    config
  }

  private lazy val connector = new BridgeIntegrationConnector(httpClientV2, appConfig)

  private val validSearchJson =
    """{"results":{"current_page":1,"page_size":20,"total_results":1,"total_pages":1,"records":[
      |{"list":{"country":{"code":"E92000001","label":"England"}},
      |"list_entry":{"valuation":{"value":"C"},"property":{"id":{"value":"P1"},"address":{"full":"1 Test Street"}}}}]}}""".stripMargin

  private val validDetailJson =
    """{"results":{"records":[{"data":{"list":{},"list_entry":{"valuation":{"value":"C"}}}}]}}"""

  "postcodeSearch" - {

    "must normalise the postcode, send only the supplied query params and return the result on 200" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/search"))
          .withQueryParam("postcode", equalTo("CF141AA"))
          .withQueryParam("listType", equalTo("CVE"))
          .withQueryParam("street", equalTo("High Street & Co"))
          .willReturn(aResponse().withStatus(OK).withBody(validSearchJson))
      )

      val query  = SearchQuery(" cf14 1aa ", "CVE", street = Some("High Street & Co"))
      val result = connector.postcodeSearch(query).futureValue

      result.map(_.results.total_results) mustBe Right(Some(1))
      wireMockServer.verify(getRequestedFor(urlPathEqualTo("/bridge-integration/search")).withQueryParam("town", absent()))
    }

    "must send every optional query param when present" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/search")).willReturn(aResponse().withStatus(OK).withBody(validSearchJson))
      )

      val query = SearchQuery(
        "CF141AA", "CVE", Some("2"), Some("10"), Some("1"), Some("Road"), Some("Cardiff"), Some("D"),
        Some("Current"), Some("6815"), Some("REF"), Some("R"), Some("C")
      )
      connector.postcodeSearch(query).futureValue.isRight mustBe true

      wireMockServer.verify(
        getRequestedFor(urlPathEqualTo("/bridge-integration/search"))
          .withQueryParam("page", equalTo("2"))
          .withQueryParam("page_size", equalTo("10"))
          .withQueryParam("propertyName", equalTo("1"))
          .withQueryParam("town", equalTo("Cardiff"))
          .withQueryParam("councilTaxBand", equalTo("D"))
          .withQueryParam("bandStatus", equalTo("Current"))
          .withQueryParam("localAuthority", equalTo("6815"))
          .withQueryParam("laReferenceNumber", equalTo("REF"))
          .withQueryParam("courtCode", equalTo("R"))
          .withQueryParam("propertyUse", equalTo("C"))
      )
    }

    "must return a BAD_REQUEST error when a 200 body fails validation" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/search")).willReturn(aResponse().withStatus(OK).withBody("""{"foo":"bar"}"""))
      )

      val result = connector.postcodeSearch(SearchQuery("CF141AA", "CVE")).futureValue

      result.left.map(_.statusCode) mustBe Left(BAD_REQUEST)
      result.left.map(_.message.startsWith("Json Validation Error")) mustBe Left(true)
    }

    "must return the parsed result for a 404 with a valid body" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/search")).willReturn(aResponse().withStatus(NOT_FOUND).withBody(validSearchJson))
      )

      connector.postcodeSearch(SearchQuery("CF141AA", "CVE")).futureValue.isRight mustBe true
    }

    "must return a NOT_FOUND error for a 404 with an unrecognised body" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/search")).willReturn(aResponse().withStatus(NOT_FOUND).withBody("""{"message":"nope"}"""))
      )

      connector.postcodeSearch(SearchQuery("CF141AA", "CVE")).futureValue mustBe
        Left(ErrorResponse(NOT_FOUND, """{"message":"nope"}"""))
    }

    "must return a BAD_REQUEST error for a 400" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/search")).willReturn(aResponse().withStatus(BAD_REQUEST).withBody("bad"))
      )

      connector.postcodeSearch(SearchQuery("CF141AA", "CVE")).futureValue mustBe Left(ErrorResponse(BAD_REQUEST, "bad"))
    }

    "must pass through other upstream statuses" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/search")).willReturn(aResponse().withStatus(BAD_GATEWAY).withBody("down"))
      )

      connector.postcodeSearch(SearchQuery("CF141AA", "CVE")).futureValue mustBe Left(ErrorResponse(BAD_GATEWAY, "down"))
    }

    "must recover from a failed connection with an INTERNAL_SERVER_ERROR" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/search")).willReturn(aResponse().withFault(com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER))
      )

      connector.postcodeSearch(SearchQuery("CF141AA", "CVE")).futureValue mustBe
        Left(ErrorResponse(INTERNAL_SERVER_ERROR, "Call to Bridge postcode search failed"))
    }
  }

  "propertyDetail" - {

    "must return the parsed response on 200" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/explore/P1/CVW,CVE")).willReturn(aResponse().withStatus(OK).withBody(validDetailJson))
      )

      connector.propertyDetail("P1").futureValue.isRight mustBe true
    }

    "must return a BAD_REQUEST error when a 200 body fails validation" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/explore/P1/CVW,CVE")).willReturn(aResponse().withStatus(OK).withBody("""{"foo":"bar"}"""))
      )

      connector.propertyDetail("P1").futureValue.left.map(_.statusCode) mustBe Left(BAD_REQUEST)
    }

    "must return a NOT_FOUND error for a 404" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/explore/P1/CVW,CVE")).willReturn(aResponse().withStatus(NOT_FOUND).withBody("missing"))
      )

      connector.propertyDetail("P1").futureValue mustBe Left(ErrorResponse(NOT_FOUND, "missing"))
    }

    "must return a BAD_REQUEST error for a 400" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/explore/P1/CVW,CVE")).willReturn(aResponse().withStatus(BAD_REQUEST).withBody("bad"))
      )

      connector.propertyDetail("P1").futureValue mustBe Left(ErrorResponse(BAD_REQUEST, "bad"))
    }

    "must pass through other upstream statuses" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/explore/P1/CVW,CVE")).willReturn(aResponse().withStatus(SERVICE_UNAVAILABLE).withBody("down"))
      )

      connector.propertyDetail("P1").futureValue mustBe Left(ErrorResponse(SERVICE_UNAVAILABLE, "down"))
    }

    "must recover from a failed connection with an INTERNAL_SERVER_ERROR" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo("/bridge-integration/explore/P1/CVW,CVE")).willReturn(aResponse().withFault(com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER))
      )

      connector.propertyDetail("P1").futureValue mustBe
        Left(ErrorResponse(INTERNAL_SERVER_ERROR, "Call to bridge-integration property detail failed"))
    }
  }
}
