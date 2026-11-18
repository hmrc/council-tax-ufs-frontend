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

package pages

import models.{UserAnswers, deletionOptions}
import play.api.libs.json.JsPath

import scala.util.Try

case object deletionOptionsPage extends QuestionPage[deletionOptions] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "deletionOptions"

  // each option has its own follow-up questions, so choosing one drops the others'
  override def cleanup(value: Option[deletionOptions], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(deletionOptions.AnotherReason) | Some(deletionOptions.Affected) =>
        userAnswers.removeAll(ChangesPages.demolishedJourney ++ ChangesPages.changeOfUse ++ ChangesPages.splitOrMerge ++ ChangesPages.addressChanged ++ ChangesPages.evidence)
      case Some(deletionOptions.Demolished) =>
        userAnswers.removeAll(ChangesPages.changeDetails ++ ChangesPages.changeOfUse ++ ChangesPages.splitOrMerge ++ ChangesPages.addressChanged)
      // change of use and split or merge both end with the address question, so it is kept when switching between them
      case Some(deletionOptions.Changed) =>
        userAnswers.removeAll(ChangesPages.changeDetails ++ ChangesPages.demolishedJourney ++ ChangesPages.splitOrMerge ++ ChangesPages.evidence)
      case Some(deletionOptions.Split) =>
        userAnswers.removeAll(ChangesPages.changeDetails ++ ChangesPages.demolishedJourney ++ ChangesPages.changeOfUse ++ ChangesPages.evidence)
      case None => super.cleanup(value, userAnswers)
    }
}
