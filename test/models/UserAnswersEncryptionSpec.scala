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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.crypto.SymmetricCryptoFactory

import java.security.SecureRandom
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64

class UserAnswersEncryptionSpec extends AnyFreeSpec with Matchers {

  private def newKey: String = {
    val bytes = new Array[Byte](32)
    new SecureRandom().nextBytes(bytes)
    Base64.getEncoder.encodeToString(bytes)
  }

  private val crypto  = SymmetricCryptoFactory.aesGcmCrypto(newKey)
  private val format  = UserAnswers.encryptedFormat(crypto)
  private val answers = UserAnswers(
    id          = "id",
    data        = Json.obj("postcodeSearch" -> "M4 4AY", "propertyAddress" -> "23, Cheetham, Manchester"),
    lastUpdated = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  )

  "encryptedFormat" - {

    "must store data as ciphertext that does not contain the plain answers" in {
      val json = format.writes(answers)

      (json \ "_id").as[String] mustBe "id"
      (json \ "data").get mustBe a[JsString]
      Json.stringify(json) must not include "M4 4AY"
      Json.stringify(json) must not include "Cheetham"
    }

    "must read back what it writes" in {
      format.reads(format.writes(answers)).get mustBe answers
    }

    "must read records written before encryption was enabled" in {
      format.reads(UserAnswers.format.writes(answers)).get mustBe answers
    }

    "must fail to read data encrypted with a different key" in {
      val otherFormat = UserAnswers.encryptedFormat(SymmetricCryptoFactory.aesGcmCrypto(newKey))
      format.reads(otherFormat.writes(answers)).isError mustBe true
    }
  }
}