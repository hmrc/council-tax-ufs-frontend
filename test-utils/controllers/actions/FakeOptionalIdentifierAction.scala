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

import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Fake for tests — simulates a user who is NOT logged in.
 * internalId = None
 */
class FakeOptionalIdentifierAction @Inject()(
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends ActionBuilder[OptionalIdentifierRequest, AnyContent] {

  override def invokeBlock[A](
    request: Request[A],
    block:   OptionalIdentifierRequest[A] => Future[Result]
  ): Future[Result] =
    block(OptionalIdentifierRequest(request, internalId = None))
}

/**
 * Fake for tests — simulates a user who IS logged in.
 * internalId = Some("test-user-id")
 */
class FakeOptionalIdentifierActionWithId @Inject()(
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends ActionBuilder[OptionalIdentifierRequest, AnyContent] {

  override def invokeBlock[A](
    request: Request[A],
    block:   OptionalIdentifierRequest[A] => Future[Result]
  ): Future[Result] =
    block(OptionalIdentifierRequest(request, internalId = Some("test-user-id")))
}