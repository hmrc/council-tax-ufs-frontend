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
 * recorded (`redactedFields`), never their values.
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

  def standard(userId: String, searchPage: String): PropertySearchAuditEvent =
    PropertySearchAuditEvent(userId, Standard, searchPage, Map.empty, Seq("postcode"))

  def advanced(userId: String, searchPage: String, search: AdvancedPropertySearch): PropertySearchAuditEvent = {
    def provided(fields: (String, String)*): Seq[(String, String)] =
      fields.collect { case (name, value) if value.trim.nonEmpty => name -> value.trim }

    val criteria = provided(
      "councilTaxBand"                -> search.councilTaxBand,
      "bandStatus"                    -> search.bandStatus,
      "localAuthority"                -> search.localAuthority,
      "localAuthorityReferenceNumber" -> search.localAuthorityReferenceNumber,
      "courtCode"                     -> search.courtCode,
      "propertyUse"                   -> search.propertyUse
    )

    val redacted = provided(
      "propertyNameOrNumber" -> search.propertyNameOrNumber,
      "street"               -> search.street,
      "townOrCity"           -> search.townOrCity,
      "postcode"             -> search.postcode
    ).map(_._1)

    PropertySearchAuditEvent(userId, Advanced, searchPage, criteria.toMap, redacted)
  }
}