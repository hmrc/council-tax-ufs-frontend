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

package services

import base.SpecBase
import connectors.BridgeIntegrationConnector
import models.{HistoricBandEntry, HistoricBandsViewModel, PropertyDetailResponse}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.Lang
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PropertyDetailServiceSpec extends SpecBase with MockitoSugar {

  implicit private val ec: ExecutionContext = ExecutionContext.global

  "PropertyDetailService.getHistoricBands" - {

    "must return completed API band periods and omit the current period" in {
      val connector = mock[BridgeIntegrationConnector]
      val response = Json.parse(
        """
          |{
          |  "results": {
          |    "records": [
          |      {
          |        "data": {
          |          "list": {
          |            "id": {"value": "1993"},
          |            "country": {"code": "E92000001"},
          |            "collection_authority": {"code": "Cardiff Council"}
          |          },
          |          "list_entry": {
          |            "valuation": {"value": "B"},
          |            "period": {"effective_from_date": "1993-04-01", "effective_to_date": "2000-01-01"},
          |            "addresses": {"full": "1 Test Street"},
          |            "administration": {"collection_authority_ref": "REF-123"},
          |            "property": {"workflow": {"improvement_ind": "N"}},
          |            "use": {"composite_ind": "N"}
          |          }
          |        }
          |      },
          |      {
          |        "data": {
          |          "list": {},
          |          "list_entry": {
          |            "valuation": {"value": "C"},
          |            "period": {"effective_from_date": "2000-01-01"},
          |            "addresses": {"full": "1 Test Street"}
          |          }
          |        }
          |      }
          |    ]
          |  }
          |}
          |""".stripMargin
      ).as[PropertyDetailResponse]

      when(connector.propertyDetail(eqTo("PROP-123"))(any[HeaderCarrier]))
        .thenReturn(Future.successful(Right(response)))

      val result = new PropertyDetailService(connector)
        .getHistoricBands("PROP-123")(HeaderCarrier(), Lang("en")).futureValue

      result mustBe Right(
        Some(
          HistoricBandsViewModel(
            "PROP-123",
            "1 Test Street",
            Seq(
              HistoricBandEntry(
                band = "B",
                effectiveFromDate = "1 April 1993",
                effectiveToDate = "1 January 2000",
                localAuthority = "Cardiff Council",
                councilRefNumber = "REF-123",
                improvementInd = "No",
                mixedUse = "No",
                courtCode = "None",
                valuationList = "1993"
              )
            )
          )
        )
      )
    }
  }
}
