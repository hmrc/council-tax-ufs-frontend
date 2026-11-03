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

import models.UserAnswers
import play.api.mvc.RequestHeader
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import utils.UrlEncryptor

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
 * Moves answers captured before sign-in (keyed by the mdtp sessionId) onto the
 * signed-in user's record (keyed by internalId), so the journey carries on seamlessly.
 * Pre-login answers win over older stored answers; the session record is removed afterwards.
 *
 * The pre-login sessionId travels through the login continue URL as `k`, encrypted so the
 * raw session identifier never appears in URLs, logs or referrers.
 */
@Singleton
class UserAnswersMigrationService @Inject()(
                                             sessionRepository: SessionRepository,
                                             urlEncryptor: UrlEncryptor
                                           )(implicit ec: ExecutionContext) {

  val preLoginSessionParam: String = UserAnswersMigrationService.PreLoginSessionParam

  def encryptSessionId(sessionId: String): String =
    urlEncryptor.encrypt(sessionId)

  /** The current sessionId plus the decrypted `k` param, if present and valid. */
  def preLoginSessionIds(request: RequestHeader)(implicit hc: HeaderCarrier): Seq[String] =
    request.getQueryString(preLoginSessionParam).flatMap(k => Try(urlEncryptor.decrypt(k)).toOption).toSeq ++
      hc.sessionId.map(_.value)

  def migrate(fromIds: Seq[String], toId: String): Future[Unit] =
    fromIds.distinct.filterNot(_ == toId).foldLeft(Future.unit) {
      (acc, fromId) => acc.flatMap(_ => migrateOne(fromId, toId))
    }

  private def migrateOne(fromId: String, toId: String): Future[Unit] =
    sessionRepository.get(fromId).flatMap {
      case None =>
        Future.unit
      case Some(sessionAnswers) =>
        for {
          existing <- sessionRepository.get(toId)
          merged    = existing.fold(UserAnswers(toId))(identity)
                        .copy(data = existing.fold(sessionAnswers.data)(_.data deepMerge sessionAnswers.data))
          _        <- sessionRepository.set(merged)
          _        <- sessionRepository.clear(fromId)
        } yield ()
    }
}

object UserAnswersMigrationService {
  val PreLoginSessionParam = "k"
}