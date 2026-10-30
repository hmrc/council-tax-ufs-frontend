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

import controllers.actions.*
import forms.CouncilTaxResponsibleFormProvider

import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.{CouncilTaxResponsiblePage, PropertyAddressPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.UrlEncryptor
import views.html.CouncilTaxResponsibleView

import scala.concurrent.{ExecutionContext, Future}

class CouncilTaxResponsibleController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       sessionIdentify:          SessionIdentifierAction,
                                       getData: DataRetrievalAction,
                                       formProvider: CouncilTaxResponsibleFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: CouncilTaxResponsibleView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (sessionIdentify andThen getData) {
    implicit request =>

      val preparedForm = request.userAnswers.flatMap(_.get(CouncilTaxResponsiblePage)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      val address = request.userAnswers
        .flatMap(_.get(PropertyAddressPage))
        .getOrElse("")

      Ok(view(preparedForm, mode, address, navigator.backLink(CouncilTaxResponsiblePage, mode, request.userAnswers).url))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (sessionIdentify andThen getData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          val address = request.userAnswers.flatMap(_.get(PropertyAddressPage)).getOrElse("")
          Future.successful(BadRequest(view(formWithErrors, mode, address, navigator.backLink(CouncilTaxResponsiblePage, mode, request.userAnswers).url))),

        value =>
          for {
            answers        <- Future.successful(
              request.userAnswers.getOrElse(
                models.UserAnswers(request.userId)
              )
            )
            updatedAnswers <- Future.fromTry(answers.set(CouncilTaxResponsiblePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CouncilTaxResponsiblePage, mode, updatedAnswers))
      )
  }
}