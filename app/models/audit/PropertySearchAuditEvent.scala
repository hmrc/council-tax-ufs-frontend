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

package models.audit

import models.AdvancedPropertySearch
import play.api.libs.json.{Json, OWrites}

/**
 * Detail for the `PropertySearch` explicit audit event, sent when the user presses Search.
 * Address and postcode fields are PII: only the names of the ones the user filled in are
 * recorded (`redactedFields`), never their values. The one exception is the first three characters
 * of the postcode (`postcodePrefix` in the criteria), which shows broadly where people search
 * without identifying anyone.
 */
final case class PropertySearchAuditEvent(
                                           userId: String,
                                           searchType: String,
                                           searchPage: String,
                                           searchCriteria: Map[String, String],
                                           redactedFields: Seq[String]
                                         )

object PropertySearchAuditEvent {

  val AuditType: String = "PropertySearch"

  val Standard: String = "standard"
  val Advanced: String = "advanced"

  implicit val writes: OWrites[PropertySearchAuditEvent] = Json.writes[PropertySearchAuditEvent]

  val PostcodePrefixKey: String = "postcodePrefix"

  /** Recorded for a dropdown filter the user left on its default, so every filter always appears in the event. */
  val AnyValue: String = "any"

  /**
   * First three characters of the postcode with spaces removed, e.g. "NG9 3NB" gives "NG9",
   * "M4 4AY" gives "M44" and "SW1A 1AA" gives "SW1". Nothing is returned for a blank postcode.
   */
  def postcodePrefix(postcode: String): Option[String] =
    Some(postcode.filterNot(_.isWhitespace).toUpperCase.take(3)).filter(_.nonEmpty)

  def standard(userId: String, searchPage: String, postcode: String): PropertySearchAuditEvent =
    PropertySearchAuditEvent(
      userId, Standard, searchPage,
      postcodePrefix(postcode).map(PostcodePrefixKey -> _).toMap,
      Seq("postcode")
    )

  def advanced(userId: String, searchPage: String, search: AdvancedPropertySearch): PropertySearchAuditEvent = {
    def provided(fields: (String, String)*): Seq[(String, String)] =
      fields.collect { case (name, value) if value.trim.nonEmpty => name -> value.trim }

    def filter(name: String, value: String): (String, String) =
      name -> Some(value.trim).filter(_.nonEmpty).getOrElse(AnyValue)

    // The dropdown filters are always present; "any" means the user left them on the default.
    val criteria = Seq(
      filter("councilTaxBand", search.councilTaxBand),
      filter("bandStatus", search.bandStatus),
      filter("localAuthority", search.localAuthority),
      filter("courtCode", search.courtCode),
      filter("propertyUse", search.propertyUse)
    ) ++ provided("localAuthorityReferenceNumber" -> search.localAuthorityReferenceNumber) ++
      postcodePrefix(search.postcode).map(PostcodePrefixKey -> _)

    val redacted = provided(
      "propertyNameOrNumber" -> search.propertyNameOrNumber,
      "street"               -> search.street,
      "townOrCity"           -> search.townOrCity,
      "postcode"             -> search.postcode
    ).map(_._1)

    PropertySearchAuditEvent(userId, Advanced, searchPage, criteria.toMap, redacted)
  }
}