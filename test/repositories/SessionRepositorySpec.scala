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

package repositories

import models.UserAnswers
import org.mongodb.scala.MongoClient
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json

import java.time.{Clock, Instant, ZoneOffset}
import scala.concurrent.ExecutionContext

class SessionRepositorySpec extends AnyWordSpec with Matchers with ScalaFutures {

  private implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  private val clock: Clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC)

  private val app = new GuiceApplicationBuilder()
    .configure(
      "mongodb.uri" -> "mongodb://localhost:27017/test-session-repository",
      "cache.ttl"  -> "3600"
    )
    .build()

  private val repository = app.injector.instanceOf[SessionRepository]

  private val userAnswers = UserAnswers(
    id = "user-1",
    data = Json.obj(
      "postcode" -> "AA1 1AA",
      "band"     -> "C"
    ),
    lastUpdated = Instant.now(clock)
  )

  "SessionRepository" should {
    "set and retrieve user answers" in {
      val setResult = repository.set(userAnswers).futureValue

      setResult shouldBe true

      val stored = repository.get(userAnswers.id).futureValue

      stored should not be None
      stored.map(_.id) shouldBe Some(userAnswers.id)
      stored.map(_.data) shouldBe Some(userAnswers.data)
    }

    "keep the session alive and update lastUpdated" in {
      repository.set(userAnswers).futureValue

      val before = repository.get(userAnswers.id).futureValue
      val previousUpdated = before.map(_.lastUpdated)

      val keepAliveResult = repository.keepAlive(userAnswers.id).futureValue

      keepAliveResult shouldBe true

      val after = repository.get(userAnswers.id).futureValue
      after.map(_.lastUpdated) should not be previousUpdated
    }

    "clear a user's answers" in {
      repository.set(userAnswers).futureValue

      val clearResult = repository.clear(userAnswers.id).futureValue
      clearResult shouldBe true

      repository.get(userAnswers.id).futureValue shouldBe None
    }
  }
}