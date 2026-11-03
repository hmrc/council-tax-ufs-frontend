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

package services

import models.audit.PropertySearchAuditEvent
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.verify
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext.Implicits.global

class AuditServiceSpec extends AnyFreeSpec with Matchers with MockitoSugar {

  "auditPropertySearch" - {

    "must send a PropertySearch explicit audit event with the event as its detail" in {
      val auditConnector = mock[AuditConnector]
      val event          = PropertySearchAuditEvent.standard("session-1", "/council-tax-ufs/postcode-search")

      new AuditService(auditConnector).auditPropertySearch(event)(HeaderCarrier())

      verify(auditConnector).sendExplicitAudit(eqTo("PropertySearch"), eqTo(event))(any(), any(), any())
    }
  }
}