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
import forms.CouncilTaxLiabilityStartDateFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.CouncilTaxLiabilityStartDatePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CouncilTaxLiabilityStartDateView

import scala.concurrent.{ExecutionContext, Future}

class CouncilTaxLiabilityStartDateController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        formProvider: CouncilTaxLiabilityStartDateFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CouncilTaxLiabilityStartDateView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) {
    implicit request =>

      val form = formProvider()

      val preparedForm = request.userAnswers.flatMap(_.get(CouncilTaxLiabilityStartDatePage)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, navigator.backLink(CouncilTaxLiabilityStartDatePage, mode, request.userAnswers).url))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val form = formProvider()

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, navigator.backLink(CouncilTaxLiabilityStartDatePage, mode, request.userAnswers).url))),

        value => {
            val answers = request.userAnswers.getOrElse(models.UserAnswers(request.userId))
          for {
            updatedAnswers <- Future.fromTry(answers.set(CouncilTaxLiabilityStartDatePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CouncilTaxLiabilityStartDatePage, mode, updatedAnswers))
        }
      )
  }
}