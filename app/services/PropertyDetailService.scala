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
import utils.{DateTimeFormats, ExternalLink}

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
        val allRecords = sortByEffectiveFrom(response.results.records.map(_.data))
        // Latest record is the current band; the rest are historic, most recent first
        val records = allRecords.drop(1)
        if (allRecords.isEmpty) {
          Right(None)
        } else {
          val address = allRecords.iterator
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
            val localAuthority = record.list.collection_authority.flatMap(ca => ca.label.orElse(ca.code)).getOrElse("Not available")
            val councilRefNumber = entry.property.flatMap(_.collection_authority_ref).getOrElse("Not available")
            val improvementInd = entry.property.flatMap(_.workflow).flatMap(_.improvement_ind).map(indicatorToYesNo).getOrElse("Not available")
            val mixedUse = entry.use.flatMap(_.composite_ind).map(PropertyDetailService.compositeToYesNo).getOrElse("Not available")
            val valuationList = record.list.id.flatMap(_.value).getOrElse("Not available")
            for {
              band <- entry.valuation.flatMap(_.value)
              effectiveFrom <- period.flatMap(_.effective_from_date)
            } yield HistoricBandEntry(
              band = band,
              effectiveFromDate = formatDate(effectiveFrom),
              effectiveToDate = period.flatMap(_.effective_to_date).filter(_.nonEmpty).map(formatDate).getOrElse("Not available"),
              localAuthority = localAuthority,
              councilRefNumber = councilRefNumber,
              improvementInd = improvementInd,
              mixedUse = mixedUse,
              courtCode = entry.administration.flatMap(_.judicially_ordered_by).getOrElse("None"),
              valuationList = valuationList
            )
          }

          Right(Some(HistoricBandsViewModel(propertyId, address, bands)))
        }
    }

  /** The current (latest effective_from_date) record's list entry id and list type, never a historic band. */
  def getLatestListEntry(propertyId: String)(implicit hc: HeaderCarrier): Future[Either[String, LatestListEntry]] =
    connector.propertyDetail(propertyId).map {
      case Left(error) => Left(s"Could not retrieve property $propertyId: status ${error.statusCode}")
      case Right(response) =>
        sortByEffectiveFrom(response.results.records.map(_.data)).headOption match {
          case None => Left(s"No records found for property $propertyId")
          case Some(latest) =>
            for {
              id       <- latest.list_entry.id.flatMap(_.value).filter(_.trim.nonEmpty)
                            .toRight(s"No list entry id found for property $propertyId")
              listType <- listTypeOf(latest).toRight(s"Could not tell whether property $propertyId is on the CVE or CVW list")
            } yield LatestListEntry(id, listType)
        }
    }

  // CVE (England) or CVW (Wales): the list's own class code, else the country code (E92... / W92...)
  private def listTypeOf(record: PropertyDetailResult): Option[String] =
    record.list.`class`.flatMap(_.code).map(_.trim.toUpperCase).filter(Set("CVE", "CVW"))
      .orElse(record.list.country.flatMap(_.code).map(_.trim.toUpperCase).collect {
        case c if c.startsWith("E") => "CVE"
        case c if c.startsWith("W") => "CVW"
      })

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
        val sorted = sortByEffectiveFrom(result.results.records.map(_.data))
        sorted.headOption match {
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

            val localCouncil = list.collection_authority.flatMap(ca => ca.label.orElse(ca.code)).getOrElse("Not available")
            val localCouncilCode = list.collection_authority.flatMap(_.code).getOrElse("")
            val localCouncilLink = list.collection_authority.flatMap(_.pointer).flatMap(ExternalLink.absolute)
            val councilRef = entry.property.flatMap(_.collection_authority_ref).getOrElse("Not available")
            val improvement = entry.property.flatMap(_.workflow).flatMap(_.improvement_ind).map(indicatorToYesNo).getOrElse("Not available")
            val mixedUse = entry.use.flatMap(_.composite_ind).map(PropertyDetailService.compositeToYesNo).getOrElse("Not available")
            val courtCode = entry.administration.flatMap(_.judicially_ordered_by).getOrElse("None")
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
                  localCouncilLink = localCouncilLink,
                  councilRefNumber = councilRef,
                  improvementInd = improvement,
                  mixedUse = mixedUse,
                  courtCode = courtCode,
                  country = country,
                  hasHistoricBands = sorted.size > 1
                )
              )
            )
        }
    }

  /** Records ordered by effective_from_date, latest first. Records without a date go last. */
  private def sortByEffectiveFrom(records: Seq[PropertyDetailResult]): Seq[PropertyDetailResult] =
    records.sortBy(r => r.list_entry.period.flatMap(_.effective_from_date).map(_.filter(_.isDigit).take(8)).getOrElse(""))(
      Ordering[String].reverse
    )

  private def formatDate(raw: String)(implicit lang: Lang): String =
    DateTimeFormats.formatDate(raw).getOrElse(raw)

  private def indicatorToYesNo(value: String): String =
    if (value.trim.equalsIgnoreCase("Y") || value.trim.equalsIgnoreCase("yes")) "Yes" else "No"
}

object PropertyDetailService {

  // Mixed use comes from use.composite_ind: "C" (composite) is mixed use, "N" is domestic use only.
  def compositeToYesNo(value: String): String =
    value.trim.toUpperCase(java.util.Locale.ROOT) match {
      case "C" => "Yes"
      case "N" => "No"
      case _ => "Not available"
    }
}