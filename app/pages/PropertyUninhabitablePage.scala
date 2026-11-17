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

import models.{PropertyUninhabitable, UserAnswers}
import play.api.libs.json.JsPath
import queries.Settable

import scala.util.Try

case object PropertyUninhabitablePage extends QuestionPage[PropertyUninhabitable] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "propertyUninhabitable"

  // what each choice asks that the others do not
  // (a def: ChangesPages refers back to this page, so a val would read it before it exists)
  private def ownQuestions: Map[PropertyUninhabitable, Seq[Settable[?]]] = Map(
    PropertyUninhabitable.Derelict   -> ChangesPages.derelictOnly,
    PropertyUninhabitable.Demolished -> ChangesPages.demolishedOnly,
    PropertyUninhabitable.Reno       -> ChangesPages.renoOnly,
    PropertyUninhabitable.PartDemo   -> ChangesPages.partDemoOnly
  )

  // only these two ask why the property cannot be lived in
  private val asksReason: Set[PropertyUninhabitable] = Set(PropertyUninhabitable.Derelict, PropertyUninhabitable.Demolished)

  // choosing one drops the questions of the others. Every choice ends with the same evidence questions,
  // so those are kept.
  override def cleanup(value: Option[PropertyUninhabitable], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(choice) =>
        val others = ownQuestions.removed(choice).values.flatten.toSeq
        userAnswers.removeAll(if (asksReason(choice)) others else UninhabitableDetailsPage +: others)
      case None => super.cleanup(value, userAnswers)
    }
}
