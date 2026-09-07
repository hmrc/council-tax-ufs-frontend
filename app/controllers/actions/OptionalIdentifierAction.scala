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

package controllers.actions

import com.google.inject.Inject
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

/**
 * Request wrapper that carries an optional internalId.
 *
 * internalId = Some(id) when the user is authenticated
 * internalId = None     when the user is not logged in (public pages)
 *
 * Used for pages that show public info to everyone but show additional
 * private info when the user is authenticated — e.g. PropertyDetailsPage.
 */
final case class OptionalIdentifierRequest[A](
  request:    Request[A],
  internalId: Option[String]
) extends WrappedRequest[A](request)

/**
 * ActionBuilder that attempts authentication but does NOT force login.
 *
 * Successful auth  → OptionalIdentifierRequest(request, Some(internalId))
 * NoActiveSession  → OptionalIdentifierRequest(request, None)
 * Other auth error → OptionalIdentifierRequest(request, None)
 *
 */
class OptionalIdentifierAction @Inject()(
  override val authConnector: AuthConnector,
  val parser:                 BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends ActionBuilder[OptionalIdentifierRequest, AnyContent]
    with AuthorisedFunctions {

  override def invokeBlock[A](
    request: Request[A],
    block:   OptionalIdentifierRequest[A] => Future[Result]
  ): Future[Result] = {

    implicit val hc: HeaderCarrier =
      HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    authorised()
      .retrieve(Retrievals.internalId) {
        case Some(internalId) =>
          block(OptionalIdentifierRequest(request, Some(internalId)))
        case None =>
          // Authenticated but no internalId — treat as unauthenticated
          block(OptionalIdentifierRequest(request, None))
      }
      .recoverWith {
        case _: NoActiveSession =>
          // Not logged in — continue as public user
          block(OptionalIdentifierRequest(request, None))
        case _: AuthorisationException =>
          // Any other auth failure — continue as public user
          block(OptionalIdentifierRequest(request, None))
      }
  }
}