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

import helpers.ViewBaseSpec
import play.twirl.api.Html
import views.html.templates.FullWidthLayout


class FullWidthLayoutSpec extends ViewBaseSpec  {

  val injectedView: FullWidthLayout = inject[FullWidthLayout]

  "produce the same output for apply() and render()" in {
    val htmlApply = injectedView.apply(Html("Test")).body
    val htmlRender = injectedView.render(Html("Test")).body
    val htmlF = injectedView.f(Html("Test")).body
    htmlApply mustBe htmlRender
    htmlF must not be empty
  }

}
