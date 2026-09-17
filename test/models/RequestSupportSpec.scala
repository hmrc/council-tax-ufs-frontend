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

package models.requests

import base.SpecBase
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.SessionKeys

class RequestSupportSpec extends SpecBase {

  "RequestSupport" - {

    "isLoggedIn" - {

      "must return true when authToken session key is present" in {
        implicit val request: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest().withSession(SessionKeys.authToken -> "Bearer some-token")
        RequestSupport.isLoggedIn mustBe true
      }

      "must return false when authToken session key is absent" in {
        implicit val request: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest()
        RequestSupport.isLoggedIn mustBe false
      }

      "must return false when a different session key is present but not authToken" in {
        implicit val request: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest().withSession("someOtherKey" -> "someValue")
        RequestSupport.isLoggedIn mustBe false
      }

      "must return false when session is empty" in {
        implicit val request: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest().withSession()
        RequestSupport.isLoggedIn mustBe false
      }
    }

    "sessionId" - {

      "must return the session id when present" in {
        implicit val request: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest().withSession(SessionKeys.sessionId -> "session-abc-123")
        RequestSupport.sessionId mustBe "session-abc-123"
      }

      "must return 'Unknown Session Id' when session id is absent" in {
        implicit val request: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest()
        RequestSupport.sessionId mustBe "Unknown Session Id"
      }
    }
  }
}