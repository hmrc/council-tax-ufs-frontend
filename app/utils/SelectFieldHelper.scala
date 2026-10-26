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

package utils

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.html.components.{Select, SelectItem, Label, Text, Hint}

object SelectFieldHelper {

	def select(
							form: Form[_],
							messagePrefix: String,
							fieldKey: String,
							options: Seq[(String, String)]
						)(implicit messages: Messages): Select =
		Select(
			id = fieldKey,
			name = fieldKey,
			items = options.map { case (v, label) => SelectItem(value = Some(v), text = label) },
			label = Label(content = Text(messages(s"$messagePrefix.$fieldKey"))),
			hint = if (messages.isDefinedAt(s"$messagePrefix.$fieldKey.hint"))
				Some(Hint(content = Text(messages(s"$messagePrefix.$fieldKey.hint"))))
			else None,
			value = form(fieldKey).value
		)
}