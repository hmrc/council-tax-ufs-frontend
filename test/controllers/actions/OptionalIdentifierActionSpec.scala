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

import base.SpecBase
import org.scalatest.EitherValues
import play.api.mvc.{BodyParsers, Results}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate 
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}

import scala.concurrent.Future

class OptionalIdentifierActionSpec extends SpecBase with EitherValues {

  implicit val ec: scala.concurrent.ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  class Harness(authConnector: AuthConnector, parser: BodyParsers.Default)(implicit ec: scala.concurrent.ExecutionContext)
      extends OptionalIdentifierAction(authConnector, parser) {

    def callTransform[A](request: play.api.mvc.Request[A]): Future[play.api.mvc.Result] =
      invokeBlock(request, (req: OptionalIdentifierRequest[A]) =>
        Future.successful(
          Results.Ok(req.internalId.getOrElse("none"))
        )
      )
  }

  "OptionalIdentifierAction" - {

    "when the user is authenticated with an internalId" - {
      "must set internalId to Some(id) on the request" in {
        val mockAuthConnector: AuthConnector = new AuthConnector {
          override def authorise[A](
            predicate:  Predicate,
            retrieval:  Retrieval[A]
          )(implicit hc: uk.gov.hmrc.http.HeaderCarrier,
            ec: scala.concurrent.ExecutionContext): Future[A] =
            Future.successful(Some("internalId-123").asInstanceOf[A])
        }

        val app    = applicationBuilder().build()
        val parser = app.injector.instanceOf[BodyParsers.Default]
        val action = new Harness(mockAuthConnector, parser)

        val result = action.callTransform(play.api.test.FakeRequest())
        status(result)          mustBe OK
        contentAsString(result) mustBe "internalId-123"
        app.stop()
      }
    }

    "when the user is authenticated but internalId is None" - {
      "must set internalId to None on the request" in {
        val mockAuthConnector: AuthConnector = new AuthConnector {
          override def authorise[A](
            predicate:  Predicate,
            retrieval:  Retrieval[A]
          )(implicit hc: uk.gov.hmrc.http.HeaderCarrier,
            ec: scala.concurrent.ExecutionContext): Future[A] =
            Future.successful(None.asInstanceOf[A])
        }

        val app    = applicationBuilder().build()
        val parser = app.injector.instanceOf[BodyParsers.Default]
        val action = new Harness(mockAuthConnector, parser)

        val result = action.callTransform(play.api.test.FakeRequest())
        status(result)          mustBe OK
        contentAsString(result) mustBe "none"
        app.stop()
      }
    }

    "when the user is not logged in (NoActiveSession)" - {
      "must set internalId to None and continue — no redirect" in {
        val mockAuthConnector: AuthConnector = new AuthConnector {
          override def authorise[A](
            predicate:  Predicate,
            retrieval:  Retrieval[A]
          )(implicit hc: uk.gov.hmrc.http.HeaderCarrier,
            ec: scala.concurrent.ExecutionContext): Future[A] =
            Future.failed(SessionRecordNotFound("no session"))
        }

        val app    = applicationBuilder().build()
        val parser = app.injector.instanceOf[BodyParsers.Default]
        val action = new Harness(mockAuthConnector, parser)

        val result = action.callTransform(play.api.test.FakeRequest())
        status(result)          mustBe OK
        contentAsString(result) mustBe "none"
        app.stop()
      }
    }

    "when there is any other AuthorisationException" - {
      "must set internalId to None and continue — no crash" in {
        val mockAuthConnector: AuthConnector = new AuthConnector {
          override def authorise[A](
            predicate:  Predicate,
            retrieval:  Retrieval[A]
          )(implicit hc: uk.gov.hmrc.http.HeaderCarrier,
            ec: scala.concurrent.ExecutionContext): Future[A] =
            Future.failed(InsufficientEnrolments("missing enrolment"))
        }

        val app    = applicationBuilder().build()
        val parser = app.injector.instanceOf[BodyParsers.Default]
        val action = new Harness(mockAuthConnector, parser)

        val result = action.callTransform(play.api.test.FakeRequest())
        status(result)          mustBe OK
        contentAsString(result) mustBe "none"
        app.stop()
      }
    }
  }
}