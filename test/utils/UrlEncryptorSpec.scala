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

package utils

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.crypto.{Crypted, PlainText}

class UrlEncryptorSpec extends AnyWordSpec with Matchers {

  private val app: Application = new GuiceApplicationBuilder().build()
  private val encryptor = app.injector.instanceOf[UrlEncryptor]

  "UrlEncryptor" should {
    "encrypt and decrypt a plain text value successfully" in {
      val plainText = "postcode=AA1 1AA"

      val encrypted = encryptor.encrypt(plainText)
      val decrypted = encryptor.decrypt(encrypted)

      encrypted should not be plainText
      decrypted shouldBe plainText
    }

    "decrypt a legacy non-url-safe encrypted payload using the fallback path" in {
      val plainText = "legacy-value"

      val encryptedValue = app.injector.instanceOf[uk.gov.hmrc.play.bootstrap.frontend.filters.crypto.ApplicationCrypto]
        .QueryParameterCrypto
        .encrypt(PlainText(plainText))
        .value

      val decrypted = encryptor.decrypt(encryptedValue)

      decrypted shouldBe plainText
    }

    "throw an exception for invalid ciphertext" in {
      val invalidCipherText = "not-valid-base64!!!"

      an[Exception] should be thrownBy {
        encryptor.decrypt(invalidCipherText)
      }
    }
  }
}