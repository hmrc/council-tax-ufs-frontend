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

/**
 * Maps directly to the API response structure from BridgeIntegrationConnector.
 * Used for JSON deserialisation only — not passed to views directly.
 * The service layer converts PostcodeSearchResult → SearchResultsViewModel.
 */
case class PostcodeSearchResult(results: Results)
object PostcodeSearchResult {
  implicit val format: OFormat[PostcodeSearchResult] = Json.format
}

case class Results(
  current_page:  Option[Int],
  page_size:     Option[Int],
  total_results: Option[Int],
  total_pages:   Option[Int],
  has_next:      Option[Boolean],
  has_previous:  Option[Boolean],
  records:       Seq[Record]
)
object Results {
  implicit val format: OFormat[Results] = Json.format
}

case class Record(
  list:       Option[ValuationList],
  list_entry: Option[ListEntry]
)
object Record {
  implicit val format: OFormat[Record] = Json.format
}

case class ValuationList(
  id:                   Option[Id],
  `class`:              Option[Classification],
  collection_authority: Option[CollectionAuthority]
)
object ValuationList {
  implicit val format: OFormat[ValuationList] = Json.format
}

case class Id(value: Option[String])
object Id {
  implicit val format: OFormat[Id] = Json.format
}

case class Classification(
  code:    Option[String],
  meaning: Option[String]
)
object Classification {
  implicit val format: OFormat[Classification] = Json.format
}

case class CollectionAuthority(code: Option[String])
object CollectionAuthority {
  implicit val format: OFormat[CollectionAuthority] = Json.format
}

case class ListEntry(
  valuation: Option[Valuation],
  property:  Option[Property]
)
object ListEntry {
  implicit val format: OFormat[ListEntry] = Json.format
}

case class Valuation(value: Option[String])
object Valuation {
  implicit val format: OFormat[Valuation] = Json.format
}

case class Property(
  id:      Option[Id],
  address: Option[Address]
)
object Property {
  implicit val format: OFormat[Property] = Json.format
}

case class Address(full: Option[String])
object Address {
  implicit val format: OFormat[Address] = Json.format
}

/**
 * Display model for one row in the search results table.
 * Extracted from Record by SearchResultsService.
 */
case class PropertyEntry(
  propertyId:     String,
  localAuthority: String,
  address:       String,
  band:          String,
)

/**
 * View model passed to SearchResultsView.
 *
 * Pagination comes directly from the API response — NOT computed locally.
 * This fixes the duplication bug: the API handles server-side pagination,
 * we just display what it returns.
 *
 * startIndex / endIndex are 1-based for "Showing X to Y of Z" display.
 */
case class SearchResultsViewModel(
  postcode:    String,
  entries:     Seq[PropertyEntry],
  currentPage: Int,
  totalPages:  Int,
  totalItems:  Int,
  pageSize:    Int
) {
  val startIndex: Int = if (totalItems == 0) 0 else (currentPage - 1) * pageSize + 1
  val endIndex:   Int = if (entries.isEmpty) startIndex - 1 else Math.min(startIndex + entries.size - 1, totalItems)
}