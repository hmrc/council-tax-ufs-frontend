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

import play.api.Logging
import uk.gov.hmrc.play.bootstrap.frontend.filters.crypto.ApplicationCrypto
import uk.gov.hmrc.crypto.{Crypted, PlainText}
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}

@Singleton()
class UrlEncryptor @Inject() (appCrypto: ApplicationCrypto) extends Logging {

  def encrypt(plainText: String): String = {
    val encryptedData    = appCrypto.QueryParameterCrypto.encrypt(PlainText(plainText))
    val byteData         = Base64.getDecoder.decode(encryptedData.value)
    val base64Urlencoded = new String(Base64.getUrlEncoder.withoutPadding().encode(byteData), StandardCharsets.UTF_8)
    base64Urlencoded
  }

  def decrypt(ciphertext: String): String =
    Try {
      val byteData          = Base64.getUrlDecoder.decode(ciphertext)
      val base64EncodedData = new String(Base64.getEncoder.encode(byteData), StandardCharsets.UTF_8)
      appCrypto.QueryParameterCrypto.decrypt(Crypted(base64EncodedData)).value
    } match {
      case Success(value)     => value
      case Failure(exception) =>
        logger.info(s"Unable to decrypt parameter in base64url. Fallback to base64. Parameter: $ciphertext", exception)
        appCrypto.QueryParameterCrypto.decrypt(Crypted(ciphertext)).value
    }

}
