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

/** The page a "search again" form was submitted from. The form posts to the postcode search route, so the
  * request path alone cannot tell the audit which page the user started the search on.
  */
enum SearchOrigin(val queryValue: String, val path: String) {
  case SearchResults extends SearchOrigin("search-results", "/search-results")
  case NoResults     extends SearchOrigin("search-no-results", "/search-no-results")
}

object SearchOrigin {

  val QueryKey: String = "origin"

  /** Only known values are accepted, so arbitrary input never reaches the audit event. */
  def fromQuery(value: Option[String]): Option[SearchOrigin] =
    value.flatMap(v => SearchOrigin.values.find(_.queryValue == v))
}
