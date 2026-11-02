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
import models.*
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.ErrorResponse

import scala.concurrent.{ExecutionContext, Future}

class SearchResultsServiceSpec extends SpecBase with MockitoSugar {

  private given ExecutionContext = ExecutionContext.global
  private given HeaderCarrier    = HeaderCarrier()

  private def record(
    id: Option[String] = Some("P1"),
    address: Option[String] = Some("1 Test Street"),
    band: Option[String] = Some("C"),
    authority: Option[CollectionAuthority] = Some(CollectionAuthority(Some("CODE"), Some("Cardiff Council"), Some("http://la"))),
    country: Option[String] = Some("England")
  ): Record =
    Record(
      list = Some(ValuationList(None, None, authority, country.map(c => Country(None, Some(c))))),
      list_entry = Some(ListEntry(Some(Valuation(band)), Some(Property(Some(Id(id)), Some(Address(address))))))
    )

  private def results(
    records: Seq[Record],
    current: Option[Int] = Some(1),
    pageSize: Option[Int] = Some(20),
    total: Option[Int] = Some(1),
    pages: Option[Int] = Some(1)
  ): PostcodeSearchResult =
    PostcodeSearchResult(Results(current, pageSize, total, pages, None, None, Some("2026-01-01"), records))

  private def search(query: SearchQuery, response: Either[ErrorResponse, PostcodeSearchResult]) = {
    val connector = mock[BridgeIntegrationConnector]
    when(connector.postcodeSearch(any[SearchQuery])(any[HeaderCarrier])).thenReturn(Future.successful(response))
    new SearchResultsService(connector).search(query).futureValue
  }

  private def searchModel(query: SearchQuery, response: PostcodeSearchResult): SearchResultsViewModel =
    search(query, Right(response)) match {
      case Right(Some(model)) => model
      case other              => fail(s"Expected a view model but got $other")
    }

  private val query = SearchQuery("CF14 1AA", "CVE")

  "SearchResultsService.search" - {

    "must return the connector error unchanged" in {
      val error = ErrorResponse(503, "down")
      search(query, Left(error)) mustBe Left(error)
    }

    "must return None when there are no records and no total results" in {
      search(query, Right(results(Seq.empty, total = None))) mustBe Right(None)
      search(query, Right(results(Seq.empty, total = Some(0)))) mustBe Right(None)
    }

    "must map records to a view model with pagination from the API" in {
      val response = results(Seq(record()), current = Some(2), pageSize = Some(10), total = Some(11), pages = Some(2))

      val model = searchModel(query, response)

      model.postcode mustBe "CF14 1AA"
      model.entries mustBe Seq(PropertyEntry("P1", "Cardiff Council", "1 Test Street", "C", Some("http://la")))
      model.currentPage mustBe 2
      model.totalPages mustBe 2
      model.totalItems mustBe 11
      model.pageSize mustBe 10
      model.lastUpdatedAt mustBe Some("2026-01-01")
      model.countries mustBe Seq(SearchCountry.England)
    }

    "must use the authority code when there is no label and fall back to Unknown values" in {
      val response = results(Seq(
        record(authority = Some(CollectionAuthority(Some("CODE")))),
        record(id = None, band = None, authority = None)
      ), total = Some(2))

      val entries = searchModel(query, response).entries

      entries.map(_.localAuthority) mustBe Seq("CODE", "Unknown")
      entries.map(_.band) mustBe Seq("C", "Unknown")
      entries.map(_.propertyId) mustBe Seq("P1", "")
      entries.map(_.localAuthorityLink) mustBe Seq(None, None)
    }

    "must skip records without an address" in {
      val response = results(Seq(record(address = None), record()), total = Some(2))

      searchModel(query, response).entries.map(_.address) mustBe Seq("1 Test Street")
    }

    "must compute pagination defaults when the API omits them" in {
      val response = results(Seq(record()), current = None, pageSize = Some(0), total = None, pages = None)
      val model    = searchModel(query.copy(page = Some("3")), response)

      model.pageSize mustBe 20
      model.totalItems mustBe 1
      model.totalPages mustBe 1
      model.currentPage mustBe 3
    }

    "must derive total pages from the total results and page size" in {
      val response = results(Seq(record()), pageSize = Some(10), total = Some(25), pages = Some(0))

      searchModel(query, response).totalPages mustBe 3
    }

    "must default the current page to 1 when the query page is not a number" in {
      val response = results(Seq(record()), current = None)

      searchModel(query.copy(page = Some("abc")), response).currentPage mustBe 1
    }

    "must collect distinct countries and default to England when none are recognised" in {
      val mixed = results(Seq(record(country = Some("Wales")), record(country = Some("Wales")), record(country = Some("England"))), total = Some(3))
      searchModel(query, mixed).countries mustBe Seq(SearchCountry.Wales, SearchCountry.England)

      val unknown = results(Seq(record(country = Some("Narnia")), record(country = None)), total = Some(2))
      searchModel(query, unknown).countries mustBe Seq(SearchCountry.England)
    }
  }
}
