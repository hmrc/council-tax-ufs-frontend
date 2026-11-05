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

/**
 * Creates the reference shown to the user when a request is sent: 12 characters such as HDJ2123FK7BN, which is
 * displayed in groups of four (HDJ2-123F-K7BN).
 *
 * The 28-character alphabet leaves out vowels, so no word can be spelt, and 0, 1 so nothing is mistaken for O or I.
 * That gives 28^12 (about 2.3 x 10^17) combinations, which keeps the chance of any duplicate among 20 million
 * references below 0.1%. The frontend can't guarantee uniqueness across instances, so the receiving service must
 * treat the reference as a unique key and report a duplicate, and the caller should retry with a new reference.
 */
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

  /** The form shown to users and quoted on calls and in emails, for example HDJ2-123F-K7BN. */
  def display(reference: String): String = reference.grouped(4).mkString("-")
}
