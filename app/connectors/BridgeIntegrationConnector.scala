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

import play.api.Logging
import config.FrontendAppConfig
import play.api.http.Status.*
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import models.{PostcodeSearchResult ,PropertyDetailResult, PropertyDetailResponse}

import java.net.URI
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.play.bootstrap.http.ErrorResponse
import scala.util.control.NonFatal

class BridgeIntegrationConnector @Inject()(
  http:      HttpClientV2,
  appConfig: FrontendAppConfig
)(implicit ec: ExecutionContext) extends Logging {

  private def uri(path: String) = new URI(s"${appConfig.bridgeIntegration}/bridge-integration/$path")

  def postcodeSearch(postcode: String, page: Int = 1, pageSize: Int = 20)
    (implicit hc: HeaderCarrier): Future[Either[ErrorResponse, PostcodeSearchResult]] = {

    val normalisedPostcode = postcode.trim.toUpperCase.replaceAll("\\s+", "")
    val url = uri(s"postcode/$normalisedPostcode/CVW?page=$page").toURL
    logger.info("[BridgeIntegrationConnector][postcodeSearch] Calling postcode search API")

    http.get(url)
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case OK =>
            response.json.validate[PostcodeSearchResult] match {  
              case JsSuccess(result, _) => Right(result)
              case JsError(errors)      =>
                logger.info("[BridgeIntegrationConnector][postcodeSearch] JSON validation failed")
                Left(ErrorResponse(BAD_REQUEST, s"Json Validation Error: $errors"))
            }
          case NOT_FOUND =>
            response.json.validate[PostcodeSearchResult] match {  
              case JsSuccess(result, _) => Right(result)
              case JsError(_)           => Left(ErrorResponse(NOT_FOUND, response.body))
            }
          case BAD_REQUEST =>
            logger.info(s"[BridgeIntegrationConnector][postcodeSearch] Bad request status=${response.status}")
            Left(ErrorResponse(BAD_REQUEST, response.body))
          case status =>
            logger.error(s"[BridgeIntegrationConnector][postcodeSearch] Unexpected status=$status")
            Left(ErrorResponse(status, response.body))
        }
      }
      .recover {
        case NonFatal(ex) =>
          logger.error(s"[BridgeIntegrationConnector][postcodeSearch] Request failed: ${ex.getClass.getSimpleName}")
          Left(ErrorResponse(INTERNAL_SERVER_ERROR, "Call to Bridge postcode search failed"))
      }
  }


  def propertyDetail(propertyId: String)(implicit hc: HeaderCarrier): Future[Either[ErrorResponse, PropertyDetailResponse]] = {

    val url = uri(s"explore/$propertyId/CVW").toURL
    logger.info("[BridgeIntegrationConnector][propertyDetail] Calling property detail API")
    http.get(url)
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case OK =>
            response.json.validate[PropertyDetailResponse] match {
              case JsSuccess(result, _) =>
                Right(result)
              case JsError(errors) =>
                logger.warn("[BridgeIntegrationConnector][property detail] JSON validation failed")
                Left(ErrorResponse(BAD_REQUEST, s"Json Validation Error: $errors"))
            }
          case NOT_FOUND =>
            logger.warn("[BridgeIntegrationConnector][property detail] Not found")
            Left(ErrorResponse(NOT_FOUND, response.body))
          case BAD_REQUEST =>
            logger.warn("[BridgeIntegrationConnector][property detail] Bad request")
            Left(ErrorResponse(BAD_REQUEST, response.body))
          case status =>
            logger.error(s"[BridgeIntegrationConnector][property detail] Unexpected status=$status")
            Left(ErrorResponse(status, response.body))
        }
      }
      .recover {
        case NonFatal(ex) =>
          logger.error(s"[BridgeIntegrationConnector][property detail] Request failed: ${ex.getClass.getSimpleName}")
          Left(ErrorResponse(INTERNAL_SERVER_ERROR, "Call to bridge-integration property detail failed"))
      }
  }
 
}