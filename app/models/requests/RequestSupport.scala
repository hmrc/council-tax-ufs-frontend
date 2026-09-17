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

package models.requests

import javax.inject.Inject
import play.api.i18n._
import play.api.mvc.{Request, RequestHeader}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendHeaderCarrierProvider

/**
 * Provides implicit HeaderCarrier, Lang and Messages from a Request.
 * Also provides helpers for views to check login state and session id.
 */
class RequestSupport @Inject()(override val messagesApi: MessagesApi) extends I18nSupport {

  implicit def hc(implicit request: Request[_]): HeaderCarrier = RequestSupport.hc
}

object RequestSupport {

  implicit def hc(implicit request: Request[_]): HeaderCarrier = HcProvider.headerCarrier

  /**
   * Checking if user is logged in. Use it in views only.
   * SessionKeys.authToken is the HMRC platform constant for the auth cookie key.
   */
  def isLoggedIn(implicit rh: RequestHeader): Boolean =
    rh.session.get(SessionKeys.authToken).isDefined

  /**
   * Getting session id. Use it in views only.
   */
  def sessionId(implicit request: Request[_]): String =
    request.session.get(SessionKeys.sessionId).getOrElse("Unknown Session Id")

  /**
   * Delegates HeaderCarrier creation to platform code (FrontendHeaderCarrierProvider)
   */
  private object HcProvider extends FrontendHeaderCarrierProvider {
    def headerCarrier(implicit request: Request[_]): HeaderCarrier = super.hc
  }
}