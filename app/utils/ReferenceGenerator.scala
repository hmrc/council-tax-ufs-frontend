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

import java.security.SecureRandom
import javax.inject.{Inject, Singleton}

// Reference shown to the user when a request is sent, 12 characters e.g. HDJ2123FK7BN, displayed as HDJ2-123F-K7BN.
// The alphabet has no vowels (so no words) and no 0/1 (so no mix-up with O/I). 28^12 keeps the chance of any
// duplicate across 20 million references under 0.1%, but we can't guarantee uniqueness across instances, so
// the receiving service has to treat it as a unique key and reject duplicates.
@Singleton
class ReferenceGenerator @Inject()() {

  private val random = new SecureRandom()

  def generate(): String =
    Seq.fill(ReferenceGenerator.length)(ReferenceGenerator.alphabet.charAt(random.nextInt(ReferenceGenerator.alphabet.length))).mkString
}

object ReferenceGenerator {
  val alphabet: String = "BCDFGHJKLMNPQRSTVWXZ23456789"
  val length: Int      = 12
  val pattern: String  = s"^[$alphabet]{$length}$$"

  // the form shown to users and quoted in emails, e.g. HDJ2-123F-K7BN
  def display(reference: String): String = reference.grouped(4).mkString("-")
}
