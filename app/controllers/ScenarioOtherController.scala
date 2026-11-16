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

import controllers.actions.{DataRetrievalAction, SessionIdentifierAction}
import models.NormalMode
import navigation.Navigator
import pages.scenarioOtherPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ScenarioOtherView

import javax.inject.{Inject, Singleton}

// information page shown before sign-in, so it only needs a session. Continue goes to a page that does require sign-in
@Singleton
class ScenarioOtherController @Inject()(
  override val messagesApi: MessagesApi,
  navigator: Navigator,
  sessionIdentify: SessionIdentifierAction,
  getData: DataRetrievalAction,
  val controllerComponents: MessagesControllerComponents,
  view: ScenarioOtherView
) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (sessionIdentify andThen getData) { implicit request =>
    Ok(view(
      backLinkUrl = navigator.backLink(scenarioOtherPage, NormalMode, request.userAnswers).url,
      continueUrl = routes.WhatHasChangedController.onPageLoad(NormalMode).url
    ))
  }
}
