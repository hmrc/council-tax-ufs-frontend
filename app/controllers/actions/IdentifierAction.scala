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
import config.FrontendAppConfig
import controllers.routes
import models.requests.IdentifierRequest
import play.api.mvc.Results._
import play.api.mvc._
import services.UserAnswersMigrationService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys, UnauthorizedException}
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

/**
 * Once pre-login answers have been migrated, `k` has done its job: redirect to the same GET URL
 * without it, so it never stays in the address bar, history, bookmarks or Referer headers.
 */
private[actions] object PreLoginParam {
  import UserAnswersMigrationService.PreLoginSessionParam

  def removeFromUrl(request: RequestHeader): Option[Result] =
    if (request.method == "GET" && request.queryString.contains(PreLoginSessionParam)) {
      Some(Redirect(request.path, request.queryString - PreLoginSessionParam))
    } else {
      None
    }
}

/**
 * All user answers live in one record per user:
 *  - signed in      -> keyed by internalId
 *  - not signed in  -> keyed by the mdtp sessionId
 * On sign-in, answers saved under the pre-login sessionId are merged into the internalId record
 * (see UserAnswersMigrationService).
 */
class AuthenticatedIdentifierAction @Inject()(
                                               override val authConnector: AuthConnector,
                                               config: FrontendAppConfig,
                                               migrationService: UserAnswersMigrationService,
                                               val parser: BodyParsers.Default
                                             )
                                             (implicit val executionContext: ExecutionContext) extends IdentifierAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    authorised().retrieve(Retrievals.internalId) {
      _.map {
        internalId =>
          migrationService.migrate(migrationService.preLoginSessionIds(request), internalId).map(_ => internalId)
      }.getOrElse(throw new UnauthorizedException("Unable to retrieve internal Id"))
    } flatMap {
      internalId => PreLoginParam.removeFromUrl(request).fold(block(IdentifierRequest(request, internalId)))(Future.successful)
    } recover {
      case _: NoActiveSession =>
        Redirect(config.loginUrl, Map("continue" -> Seq(config.signInContinueUrl(request.uri, hc.sessionId.map(id => migrationService.encryptSessionId(id.value))))))
      case _: AuthorisationException =>
        Redirect(routes.UnauthorisedController.onPageLoad())
    }
  }
}

/**
 * For pages that do not require sign-in. Uses internalId when the user is already signed in
 * (so public and authenticated pages share the same answers), otherwise the mdtp sessionId.
 */
class SessionIdentifierAction @Inject()(
                                         override val authConnector: AuthConnector,
                                         migrationService: UserAnswersMigrationService,
                                         val parser: BodyParsers.Default
                                       )
                                       (implicit val executionContext: ExecutionContext) extends IdentifierAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val sessionId = hc.sessionId.map(_.value)

    val userId: Future[Option[String]] =
      if (request.session.get(SessionKeys.authToken).isEmpty) {
        Future.successful(sessionId)
      } else {
        authorised().retrieve(Retrievals.internalId) {
          case Some(internalId) =>
            migrationService.migrate(migrationService.preLoginSessionIds(request), internalId).map(_ => Some(internalId))
          case None =>
            Future.successful(sessionId)
        } recover {
          case _: AuthorisationException => sessionId
        }
      }

    userId.flatMap {
      case Some(id) => PreLoginParam.removeFromUrl(request).fold(block(IdentifierRequest(request, id)))(Future.successful)
      case None     => Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad()))
    }
  }
}