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

package views.components

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.data.{Form, FormError}
import play.api.data.Forms.{single, text}
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import views.html.components.CharacterCountTextarea

class CharacterCountTextareaSpec extends AnyWordSpec with Matchers {
  private val app: Application = new GuiceApplicationBuilder().build()
  private val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  private implicit val messages: Messages = messagesApi.preferred(FakeRequest())
  private val component = app.injector.instanceOf[CharacterCountTextarea]
  private val form = Form(single("value" -> text))

  private def render(f: Form[String], m: Messages = messages, maxLength: Option[Int] = Some(200)) =
    component(f("value"), "my-id", "My label", maxLength)(m).body

  "CharacterCountTextarea" should {
    "render a GOV.UK character count textarea using the supplied id, label and limit" in {
      val html = render(form)

      html should include("govuk-character-count")
      html should include("data-maxlength=\"200\"")
      // no native maxlength, users need to be able to go over and be told by how much
      html should not include " maxlength=\"200\""
      html should include("id=\"my-id\"")
      html should include("name=\"value\"")
      html should include("aria-describedby=\"my-id-info\"")
      html should include("id=\"my-id-info\"")
      html should include("My label")
      html should include("You have 200 characters remaining")
    }

    "reduce the remaining count by the length of the existing answer" in {
      render(form.bind(Map("value" -> "hello"))) should include("You have 195 characters remaining")
    }

    "render the existing answer and error message" in {
      val html = render(form.bind(Map("value" -> "hello")).withError(FormError("value", "error.required")))

      html should include(">hello</textarea>")
      html should include("govuk-error-message")
    }

    "render a plain textarea with no counter or limit when no maximum is configured" in {
      val html = render(form, maxLength = None)

      html should include("id=\"my-id\"")
      html should include("My label")
      html should not include "govuk-character-count"
      html should not include "maxlength"
      html should not include "characters remaining"
    }

    "provide English runtime counter messages" in {
      val html = render(form)

      html should include("data-i18n.characters-under-limit.other='You have %{count} characters remaining'")
      html should include("data-i18n.characters-over-limit.other='You have %{count} characters too many'")
    }

    "provide Welsh runtime counter messages" in {
      val html = render(form, messagesApi.preferred(Seq(Lang("cy"))))

      html should include("data-i18n.characters-under-limit.other='Mae gennych %{count} o cymeriad ar ôl'")
      html should include("data-i18n.characters-over-limit.other='Mae gennych %{count} o gymeriadau yn ormod'")
    }
  }
}
