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
import forms.WhichBandFormProvider

import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.{PropertyCountryPage, WhichBandPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.BandCountry
import views.html.WhichBandView

import scala.concurrent.{ExecutionContext, Future}

class WhichBandController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       formProvider: WhichBandFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: WhichBandView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) {
    implicit request =>

      val preparedForm = request.userAnswers.flatMap(_.get(WhichBandPage)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val country = request.userAnswers
        .flatMap(_.get(PropertyCountryPage))
        .map(BandCountry.fromCode)
        .getOrElse(BandCountry.England)

      Ok(view(preparedForm, mode, country))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val country = request.userAnswers
        .flatMap(_.get(PropertyCountryPage))
        .map(BandCountry.fromCode)
        .getOrElse(BandCountry.England)

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, country))),

        value => {
          val answers = request.userAnswers.getOrElse(models.UserAnswers(request.userId))
          for {
            updatedAnswers <- Future.fromTry(answers.set(WhichBandPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhichBandPage, mode, updatedAnswers))
        }
      )
  }
}