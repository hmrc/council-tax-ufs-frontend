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

package views

import forms.WhyBandIsWrongFormProvider
import models.NormalMode
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest

class WhyBandIsWrongViewSpec extends AnyWordSpec with Matchers {
  private val app: Application = new GuiceApplicationBuilder().build()
  private val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  private val request = FakeRequest()
  private implicit val messages: Messages = messagesApi.preferred(request)
  private val view = app.injector.instanceOf[views.html.WhyBandIsWrongView]
  private val form = new WhyBandIsWrongFormProvider()()

  "WhyBandIsWrongView" should {
    "render the evidence guidance and GOV.UK character-count textarea" in {
      val html = view.render(form, NormalMode, request, messages).body

      html should include("Tell us why you think the Council Tax band is wrong?")
      html should include("Your band will only be reviewed if you provide supporting evidence")
      html should include("What evidence can you include?")
      html should include("govuk-character-count")
      html should include("data-maxlength=\"1000\"")
      html should include("govuk-js-character-count")
      html should include("data-i18n.characters-under-limit.other='You have %{count} characters remaining'")
      html should include("You have 1,000 characters remaining")
    }

    "provide Welsh runtime counter messages to GOV.UK Frontend" in {
      val welshMessages = messagesApi.preferred(Seq(Lang("cy")))
      val html = view.render(form, NormalMode, request, welshMessages).body

      html should include("data-i18n.characters-under-limit.other='Mae gennych %{count} o cymeriad ar ôl'")
      html should include("data-i18n.characters-over-limit.other='Mae gennych %{count} o nodau yn ormod'")
    }
  }
}