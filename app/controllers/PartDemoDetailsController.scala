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
import forms.PartDemoDetailsFormProvider
import models.Mode
import navigation.Navigator
import pages.PartDemoDetailsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.PartDemoDetailsView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PartDemoDetailsController @Inject()(
                                override val messagesApi: MessagesApi,
                                sessionRepository: SessionRepository,
                                navigator: Navigator,
                                identify: IdentifierAction,
                                getData: DataRetrievalAction,
                                requireData: DataRequiredAction,
                                formProvider: PartDemoDetailsFormProvider,
                                val controllerComponents: MessagesControllerComponents,
                                view: PartDemoDetailsView
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  private def backLink(mode: Mode, answers: models.UserAnswers): Option[String] =
    Some(navigator.backLink(PartDemoDetailsPage, mode, Some(answers)).url)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(PartDemoDetailsPage).fold(form)(form.fill)

      Ok(view(preparedForm, mode, backLink(mode, request.userAnswers)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, backLink(mode, request.userAnswers)))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(PartDemoDetailsPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(PartDemoDetailsPage, mode, updatedAnswers))
      )
  }
}
