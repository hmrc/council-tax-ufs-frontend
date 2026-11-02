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

package controllers

import base.SpecBase
import forms.AdvancedPropertySearchFormProvider
import models.audit.PropertySearchAuditEvent
import models.{AdvancedPropertySearch, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{never, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.{AdvancedPropertySearchPage, PostcodeSearchPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository
import services.AuditService
import uk.gov.hmrc.http.SessionKeys
import utils.UrlEncryptor
import views.html.AdvancedPropertySearchView

import scala.concurrent.Future

class AdvancedPropertySearchControllerSpec extends SpecBase with MockitoSugar {

  private val form  = new AdvancedPropertySearchFormProvider()()
  private val route_ = routes.AdvancedPropertySearchController.onPageLoad(NormalMode).url
  private val submitRoute = routes.AdvancedPropertySearchController.onSubmit(NormalMode).url
  private val session = SessionKeys.sessionId -> "session-1"

  private val validSearch = AdvancedPropertySearch("1", "High Street", "Cardiff", "CF14 1AA", "D", "Current", "6815", "REF1", "R", "C")

  private val validBody = Seq(
    "propertyNameOrNumber" -> "1",
    "street"               -> "High Street",
    "townOrCity"           -> "Cardiff",
    "postcode"             -> "cf14 1aa",
    "councilTaxBand"       -> "D",
    "bandStatus"           -> "Current",
    "localAuthority"       -> "6815",
    "laReferenceNumber"    -> "REF1",
    "courtCode"            -> "R",
    "propertyUse"          -> "C"
  )

  "AdvancedPropertySearch Controller" - {

    "must return OK and the correct view for a GET" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, route_).withSession(session)
        val result  = route(application, request).value
        val view    = application.injector.instanceOf[AdvancedPropertySearchView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must return OK and an empty form for a GET when there are no user answers" in {
      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, route_).withSession(session)
        val result  = route(application, request).value
        val view    = application.injector.instanceOf[AdvancedPropertySearchView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view on a GET when the question has previously been answered" in {
      val userAnswers = UserAnswers(userAnswersId).set(AdvancedPropertySearchPage, validSearch).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, route_).withSession(session)
        val result  = route(application, request).value
        val view    = application.injector.instanceOf[AdvancedPropertySearchView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validSearch), NormalMode)(request, messages(application)).toString
      }
    }

    "must save the answers, clear the postcode answer, audit and redirect to search results for valid data" in {
      val mockSessionRepository = mock[SessionRepository]
      val mockAuditService      = mock[AuditService]
      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

      val existing    = UserAnswers(userAnswersId).set(PostcodeSearchPage, "OLD 1AA").success.value
      val application = applicationBuilder(userAnswers = Some(existing))
        .overrides(
          bind[SessionRepository].toInstance(mockSessionRepository),
          bind[AuditService].toInstance(mockAuditService)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, submitRoute).withSession(session).withFormUrlEncodedBody(validBody*)
        val result  = route(application, request).value

        val encryptor = application.injector.instanceOf[UrlEncryptor]

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          routes.SearchResultsController.show(encryptor.encrypt("CF14 1AA"), 1).url

        val captor = org.mockito.ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(captor.capture())
        captor.getValue.get(PostcodeSearchPage) mustBe None
        captor.getValue.get(AdvancedPropertySearchPage) mustBe Some(validSearch)

        verify(mockAuditService).auditPropertySearch(
          eqTo(PropertySearchAuditEvent.advanced("session-1", submitRoute, validSearch))
        )(any())
      }
    }

    "must create new user answers when none exist" in {
      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      running(application) {
        val request = FakeRequest(POST, submitRoute).withSession(session).withFormUrlEncodedBody(validBody*)

        status(route(application, request).value) mustEqual SEE_OTHER
        verify(mockSessionRepository).set(any())
      }
    }

    "must return a Bad Request, show errors and not audit when invalid data is submitted" in {
      val mockAuditService = mock[AuditService]
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[AuditService].toInstance(mockAuditService))
        .build()

      running(application) {
        val request   = FakeRequest(POST, submitRoute).withSession(session).withFormUrlEncodedBody("postcode" -> "")
        val boundForm = form.bind(Map("postcode" -> ""))
        val view      = application.injector.instanceOf[AdvancedPropertySearchView]
        val result    = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
        verify(mockAuditService, never()).auditPropertySearch(any())(any())
      }
    }
  }
}
