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
import uk.gov.hmrc.play.bootstrap.http.ErrorResponse
import utils.DateTimeFormats

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PropertyDetailService @Inject() (connector: BridgeIntegrationConnector)(implicit ec: ExecutionContext)
    extends Logging {

  def getHistoricBands(propertyId: String)(implicit
      hc: HeaderCarrier,
      lang: Lang
  ): Future[Either[ErrorResponse, Option[HistoricBandsViewModel]]] =
    connector.propertyDetail(propertyId).map {
      case Left(error) =>
        logger.warn(s"[PropertyDetailService] Historic bands API error status=${error.statusCode}")
        Left(error)

      case Right(response) =>
        val records = response.results.records.map(_.data)
        if (records.isEmpty) {
          Right(None)
        } else {
          val address = records.iterator
            .flatMap { record =>
              Iterator(
                record.list_entry.addresses.flatMap(_.full),
                record.list_entry.property.flatMap(_.address).flatMap(_.full)
              ).flatten
            }
            .find(_.nonEmpty)
            .getOrElse("Address not available")

          val bands = records.flatMap { record =>
            val period = record.list_entry.period
            val entry = record.list_entry
            val localAuthority = record.list.collection_authority.flatMap(_.code).getOrElse("Not available")
            val councilRefNumber = entry.administration.flatMap(_.collection_authority_ref).getOrElse("Not available")
            val improvementInd = entry.property.flatMap(_.workflow).flatMap(_.improvement_ind).map(indicatorToYesNo).getOrElse("Not available")
            val mixedUse = entry.use.flatMap(_.composite_ind).map(indicatorToYesNo).getOrElse("Not available")
            val valuationList = record.list.id.flatMap(_.value).getOrElse("Not available")
            for {
              band <- entry.valuation.flatMap(_.value)
              effectiveFrom <- period.flatMap(_.effective_from_date)
              effectiveTo <- period.flatMap(_.effective_to_date).filter(_.nonEmpty)
            } yield HistoricBandEntry(
              band = band,
              effectiveFromDate = formatDate(effectiveFrom),
              effectiveToDate = formatDate(effectiveTo),
              localAuthority = localAuthority,
              councilRefNumber = councilRefNumber,
              improvementInd = improvementInd,
              mixedUse = mixedUse,
              courtCode = "None",
              valuationList = valuationList
            )
          }

          Right(Some(HistoricBandsViewModel(propertyId, address, bands)))
        }
    }

  def getHistoricBand(propertyId: String, bandIndex: Int)(implicit
      hc: HeaderCarrier,
      lang: Lang
  ): Future[Either[ErrorResponse, Option[HistoricBandFullViewModel]]] =
    getHistoricBands(propertyId).map {
      case Left(error) => Left(error)
      case Right(None) => Right(None)
      case Right(Some(history)) =>
        Right(history.bands.lift(bandIndex).map(HistoricBandFullViewModel(propertyId, history.address, _)))
    }

  def getPropertyDetail(propertyId: String)(implicit
      hc: HeaderCarrier,
      lang: Lang
  ): Future[Either[ErrorResponse, Option[PropertyDetailViewModel]]] =
    connector.propertyDetail(propertyId).map {
      case Left(error) =>
        logger.warn(s"[PropertyDetailService] API error status=${error.statusCode}")
        Left(error)

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
            val country = list.country
              .flatMap(country => country.label.orElse(country.code))
              .getOrElse("England")

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
      val date =
        if (raw.contains("-")) LocalDate.parse(raw.take(10), DateTimeFormatter.ISO_LOCAL_DATE)
        else LocalDate.parse(raw.take(8), DateTimeFormatter.BASIC_ISO_DATE)
      date
        .format(DateTimeFormats.dateTimeFormat())
    } catch {
      case _: Exception => raw
    }

  private def indicatorToYesNo(value: String): String =
    if (value.trim.equalsIgnoreCase("Y") || value.trim.equalsIgnoreCase("yes")) "Yes" else "No"
}