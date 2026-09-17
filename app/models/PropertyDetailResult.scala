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

package models

import play.api.libs.json.{Json, OFormat}

case class PropertyDetailResponse(results: PropertyDetailResults)
object PropertyDetailResponse {
 implicit val format: OFormat[PropertyDetailResponse] = Json.format
}
case class PropertyDetailResults(records: Seq[PropertyDetailRecord])
object PropertyDetailResults {
 implicit val format: OFormat[PropertyDetailResults] = Json.format
}
case class PropertyDetailRecord(data: PropertyDetailResult)
object PropertyDetailRecord {
 implicit val format: OFormat[PropertyDetailRecord] = Json.format
}

case class PropertyDetailResult(
  list:       PropertyDetailList,
  list_entry: PropertyDetailListEntry
)

object PropertyDetailResult {
  implicit val format: OFormat[PropertyDetailResult] = Json.format
}

case class PropertyDetailCountry(code: Option[String])
object PropertyDetailCountry {
   implicit val format: OFormat[PropertyDetailCountry] = Json.format
}

case class PropertyDetailList(
  id:                   Option[PropertyDetailId],
  classification:       Option[PropertyDetailClassification],
  collection_authority: Option[PropertyDetailCollectionAuthority],
  country:              Option[PropertyDetailCountry]

)

object PropertyDetailList {
  implicit val format: OFormat[PropertyDetailList] = Json.format
}

case class PropertyDetailId(value: Option[String])
object PropertyDetailId {
  implicit val format: OFormat[PropertyDetailId] = Json.format

}

case class PropertyDetailClassification(
  code:    Option[String],
  meaning: Option[String]
)

object PropertyDetailClassification {
  implicit val format: OFormat[PropertyDetailClassification] = Json.format
}

case class PropertyDetailCollectionAuthority(
  code: Option[String]
)

object PropertyDetailCollectionAuthority {
  implicit val format: OFormat[PropertyDetailCollectionAuthority] = Json.format
}

case class PropertyDetailListEntry(
  valuation:      Option[PropertyDetailValuation],
  period:         Option[PropertyDetailPeriod],
  administration: Option[PropertyDetailAdministration],
  addresses:      Option[PropertyDetailAddresses],
  use:            Option[PropertyDetailUse],
  property:       Option[PropertyDetailProperty]

)

object PropertyDetailListEntry {
  implicit val format: OFormat[PropertyDetailListEntry] = Json.format
}

case class PropertyDetailValuation(value: Option[String])

object PropertyDetailValuation {
  implicit val format: OFormat[PropertyDetailValuation] = Json.format
}

case class PropertyDetailPeriod(
  effective_from_date: Option[String],
  effective_to_date:   Option[String]
)

object PropertyDetailPeriod {
  implicit val format: OFormat[PropertyDetailPeriod] = Json.format
}

case class PropertyDetailAdministration(
  alteration_date:          Option[String],
  alteration_seq_no:        Option[String],
  entry_seq_no:             Option[String],
  collection_authority_ref: Option[String]
)

object PropertyDetailAdministration {
  implicit val format: OFormat[PropertyDetailAdministration] = Json.format
}

case class PropertyDetailAddresses(
  full: Option[String]
)

object PropertyDetailAddresses {
  implicit val format: OFormat[PropertyDetailAddresses] = Json.format
}

case class PropertyDetailUse(
  description:     Option[String],
  composite_ind:   Option[String],
  part_exempt_ind: Option[String]
)

object PropertyDetailUse {
  implicit val format: OFormat[PropertyDetailUse] = Json.format
}

case class PropertyDetailWorkflow(
  improvement_ind: Option[String]
)

object PropertyDetailWorkflow {
  implicit val format: OFormat[PropertyDetailWorkflow] = Json.format
}

case class PropertyDetailProperty(
  id:              Option[PropertyDetailId],
  address:         Option[PropertyDetailAddresses],
  workflow:        Option[PropertyDetailWorkflow]
)

object PropertyDetailProperty {
  implicit val format: OFormat[PropertyDetailProperty] = Json.format
}

// View model — display model passed to PropertyDetailView

case class PropertyDetailViewModel(
  propertyId:        String,
  address:           String,
  band:              String,
  effectiveFromDate: String,
  localCouncil:      String,
  localCouncilCode:  String,
  councilRefNumber:  String,
  improvementInd:    String,
  mixedUse:          String,
  courtCode:         String,
  country:           String
)
 