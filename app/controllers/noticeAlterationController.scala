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

import controllers.actions._
import forms.noticeAlterationFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.noticeAlterationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.noticeAlterationView

import scala.concurrent.{ExecutionContext, Future}

class noticeAlterationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       sessionIdentify:          SessionIdentifierAction,   // ← session only, no auth
                                       getData: DataRetrievalAction,
                                       formProvider: noticeAlterationFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: noticeAlterationView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (sessionIdentify andThen getData) {
    implicit request =>

      val preparedForm = request.userAnswers.flatMap(_.get(noticeAlterationPage)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, navigator.backLink(noticeAlterationPage, mode, request.userAnswers).url))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (sessionIdentify andThen getData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, navigator.backLink(noticeAlterationPage, mode, request.userAnswers).url))),

        value =>
          for {
            answers        <- Future.successful(
              request.userAnswers.getOrElse(
                models.UserAnswers(request.userId)
              )
            )
            updatedAnswers <- Future.fromTry(answers.set(noticeAlterationPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(noticeAlterationPage, mode, updatedAnswers))
      )
  }
}