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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class ReferenceGeneratorSpec extends AnyFreeSpec with Matchers {

  private val generator = new ReferenceGenerator()

  "ReferenceGenerator" - {
    "must produce 12 characters using only the unambiguous alphabet" in {
      Seq.fill(2000)(generator.generate()).foreach(_ must fullyMatch regex ReferenceGenerator.pattern)
    }

    "must display the reference in groups of four" in {
      ReferenceGenerator.display("HDJ2123FK7BN") mustBe "HDJ2-123F-K7BN"
    }

    "must not repeat itself in a realistic batch" in {
      val batch = Seq.fill(5000)(generator.generate())
      batch.distinct.size mustBe batch.size
    }
  }
}
