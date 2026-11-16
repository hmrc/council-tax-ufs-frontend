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

package models

enum ReviewType(val messageKey: String, val continueRoute: String) {
  case Formal   extends ReviewType("formal", "formal")
  case Informal extends ReviewType("informal", "informal")
  // formal review wording for changes to the property or local area
  case Deletion extends ReviewType("deletion", "deletion")
  // formal review wording for a property that is derelict and cannot be lived in
  case Derelict extends ReviewType("derelict", "derelict")
  // formal review wording for a property that has been fully demolished
  case Demolished extends ReviewType("demolished", "demolished")
  // formal review wording for a property having major works or renovations done
  case Reno extends ReviewType("reno", "reno")
  // formal review wording for a property that has been partly demolished
  case PartDemo extends ReviewType("partdemo", "partdemo")
}
