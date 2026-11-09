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

class UploadedEvidenceSpec extends AnyFreeSpec with Matchers {

  "displaySize" - {
    "must show small files in KB, at least 1 KB, and large files in MB" in {
      UploadedEvidence("a.pdf", "application/pdf", 10).displaySize mustBe "1 KB"
      UploadedEvidence("a.pdf", "application/pdf", 2048).displaySize mustBe "2 KB"
      UploadedEvidence("a.pdf", "application/pdf", 3L * 1024 * 1024).displaySize mustBe "3.0 MB"
    }
  }

  "isAllowedType" - {
    "must accept JPG, JPEG, PNG and PDF files with a matching content type, ignoring case" in {
      UploadedEvidence.isAllowedType("a.jpg", Some("image/jpeg")) mustBe true
      UploadedEvidence.isAllowedType("a.JPEG", Some("image/jpeg")) mustBe true
      UploadedEvidence.isAllowedType("a.png", Some("image/png")) mustBe true
      UploadedEvidence.isAllowedType("a.b.PDF", Some("application/pdf")) mustBe true
    }

    "must reject other extensions, missing extensions and mismatched or missing content types" in {
      UploadedEvidence.isAllowedType("a.exe", Some("application/pdf")) mustBe false
      UploadedEvidence.isAllowedType("a", Some("application/pdf")) mustBe false
      UploadedEvidence.isAllowedType("a.pdf", Some("image/png")) mustBe false
      UploadedEvidence.isAllowedType("a.pdf", None) mustBe false
    }
  }
}
