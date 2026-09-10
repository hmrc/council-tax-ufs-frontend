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

package controllers

import controllers.actions.{DataRetrievalAction, SessionIdentifierAction}
import models.{Mode, NormalMode}
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.SearchResultsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{UrlEncryptor, PaginationHelper}
import views.html.{NoResultsView, SearchResultsView}
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class SearchResultsController @Inject()(
  override val messagesApi:     play.api.i18n.MessagesApi,
  sessionIdentify:              SessionIdentifierAction,
  urlEncryptor:                 UrlEncryptor,
  getData:                      DataRetrievalAction,
  searchResultsService:         SearchResultsService,
  searchResultsView:            SearchResultsView,
  noResultsView:                NoResultsView,
  val controllerComponents:     MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def show(encodedPostcode: String, page: Int): Action[AnyContent] =
    (sessionIdentify andThen getData).async { implicit request =>

      implicit val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      val postcode = urlEncryptor.decrypt(encodedPostcode)
      val safePage = Math.max(1, page)

      searchResultsService.search(postcode, safePage).map {

        case Left(_) =>
          // API error — show no results page
          Redirect(routes.PostcodeSearchController.onPageLoad(NormalMode))
            .flashing("error" -> "searchResults.error")

        case Right(None) =>
          // No results for this postcode
          Ok(noResultsView(postcode))

        case Right(Some(viewModel)) =>
          // Cap page to valid range
          val cappedPage = Math.min(safePage, viewModel.totalPages)
          if (cappedPage != safePage) {
            Redirect(routes.SearchResultsController.show(encodedPostcode, cappedPage))
          } else {
            val pagination = PaginationHelper.buildPagination(
              currentPage  = viewModel.currentPage,
              totalPages   = viewModel.totalPages,
              buildPageUrl = p => routes.SearchResultsController.show(encodedPostcode, p).url
            )
            Ok(searchResultsView(viewModel, encodedPostcode, pagination))
          }
      }
    }
}