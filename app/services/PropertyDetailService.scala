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

import connectors.BridgeIntegrationConnector
import models._
import play.api.Logging
import play.api.i18n.Lang
import uk.gov.hmrc.http.HeaderCarrier
import utils.DateTimeFormats

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PropertyDetailService @Inject() (connector: BridgeIntegrationConnector)(implicit ec: ExecutionContext)
    extends Logging {

  def getPropertyDetail(propertyId: String)(implicit
      hc: HeaderCarrier,
      lang: Lang
  ): Future[Either[String, Option[PropertyDetailViewModel]]] =
    connector.propertyDetail(propertyId).map {
      case Left(error) =>
        logger.warn(s"[PropertyDetailService] API error for propertyId=$propertyId: ${error.message}")
        Left(error.message)

      case Right(result) =>
        result.results.records.headOption.map(_.data) match {
          case None =>
            Right(None)

          case Some(detail) =>
            val entry = detail.list_entry
            val list = detail.list

            val address = entry.property.flatMap(_.address).flatMap(_.full).getOrElse("Address not available")
            val band = entry.valuation.flatMap(_.value).getOrElse("Not available")
            val effectiveFrom = entry.period
              .flatMap(_.effective_from_date)
              .map(formatDate)
              .getOrElse("Not available")

            val localCouncil = list.collection_authority.flatMap(_.code).getOrElse("Not available")
            val localCouncilCode = list.collection_authority.flatMap(_.code).getOrElse("")
            val councilRef = entry.administration.flatMap(_.collection_authority_ref).getOrElse("Not available")
            val improvement = entry.property.flatMap(_.workflow).flatMap(_.improvement_ind).map(indicatorToYesNo).getOrElse("Not available")
            val mixedUse = entry.use.flatMap(_.composite_ind).map(indicatorToYesNo).getOrElse("Not available")
            val courtCode = "None"
            val country = list.country.flatMap(_.code).getOrElse("W92000004")

            Right(
              Some(
                PropertyDetailViewModel(
                  propertyId = propertyId,
                  address = address,
                  band = band,
                  effectiveFromDate = effectiveFrom,
                  localCouncil = localCouncil,
                  localCouncilCode = localCouncilCode,
                  councilRefNumber = councilRef,
                  improvementInd = improvement,
                  mixedUse = mixedUse,
                  courtCode = courtCode,
                  country = country
                )
              )
            )
        }
    }

  private def formatDate(raw: String)(implicit lang: Lang): String =
    try {
      val dateOnly = raw.take(8)
      LocalDate.parse(dateOnly, DateTimeFormatter.BASIC_ISO_DATE)
        .format(DateTimeFormats.dateTimeFormat())
    } catch {
      case _: Exception => raw
    }

  private def indicatorToYesNo(value: String): String =
    if (value.trim.equalsIgnoreCase("Y") || value.trim.equalsIgnoreCase("yes")) "Yes" else "No"
}