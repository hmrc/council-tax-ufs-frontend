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

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.DomesticDateFormProvider
import models.Mode
import navigation.Navigator
import pages.DomesticDatePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DomesticDateView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DomesticDateController @Inject()(
                                override val messagesApi: MessagesApi,
                                sessionRepository: SessionRepository,
                                navigator: Navigator,
                                identify: IdentifierAction,
                                getData: DataRetrievalAction,
                                requireData: DataRequiredAction,
                                formProvider: DomesticDateFormProvider,
                                val controllerComponents: MessagesControllerComponents,
                                view: DomesticDateView
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def backLink(mode: Mode, answers: models.UserAnswers): Option[String] =
    Some(navigator.backLink(DomesticDatePage, mode, Some(answers)).url)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val form = formProvider()

      val preparedForm = request.userAnswers.get(DomesticDatePage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, backLink(mode, request.userAnswers)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, backLink(mode, request.userAnswers)))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DomesticDatePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(DomesticDatePage, mode, updatedAnswers))
      )
  }
}
