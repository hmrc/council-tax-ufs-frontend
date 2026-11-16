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

package messages

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import scala.io.Source

// a key missing from messages.cy shows as the raw key on a Welsh page, so every English key needs a Welsh line
// (English text is fine until the translation arrives). Welsh may have extra keys, such as its plural forms.
class MessagesParitySpec extends AnyFreeSpec with Matchers {

  private def keys(file: String): Set[String] = {
    val source = Source.fromFile(s"conf/$file", "UTF-8")
    try source.getLines().filter(line => line.contains("=") && !line.trim.startsWith("#")).map(_.split("=", 2).head.trim).toSet
    finally source.close()
  }

  "messages.cy" - {
    "must have every key that messages.en has" in {
      (keys("messages.en") -- keys("messages.cy")).toSeq.sorted mustBe empty
    }
  }
}
