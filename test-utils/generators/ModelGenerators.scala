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

package generators

import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryPropertyUninhabitable: Arbitrary[PropertyUninhabitable] =
    Arbitrary {
      Gen.oneOf(PropertyUninhabitable.values.toSeq)
    }

  implicit lazy val arbitraryCouncilTaxPayerDetails: Arbitrary[CouncilTaxPayerDetails] =
    Arbitrary {
      for {
        firstName <- arbitrary[String]
        lastName <- arbitrary[String]
        email     <- arbitrary[String]
      } yield CouncilTaxPayerDetails(firstName, lastName, email)
    }

  implicit lazy val arbitraryContactDetails: Arbitrary[ContactDetails] =
    Arbitrary {
      for {
        firstName    <- arbitrary[String]
        lastName     <- arbitrary[String]
        email        <- arbitrary[Option[String]]
        phoneNumber  <- arbitrary[Option[String]]
        addressLine1 <- arbitrary[Option[String]]
        addressLine2 <- arbitrary[Option[String]]
        townOrCity   <- arbitrary[Option[String]]
        county       <- arbitrary[Option[String]]
        postcode     <- arbitrary[Option[String]]
      } yield ContactDetails(
        firstName    = firstName,
        lastName     = lastName,
        email        = email,
        phoneNumber  = phoneNumber,
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        townOrCity   = townOrCity,
        county       = county,
        postcode     = postcode,
        value        = Set.empty
      )
    }

  implicit lazy val arbitraryWhichBand: Arbitrary[WhichBand] =
    Arbitrary {
      Gen.oneOf(WhichBand.values.toSeq)
    }

  implicit lazy val arbitrarynoticeAlteration: Arbitrary[noticeAlteration] =
    Arbitrary {
      Gen.oneOf(noticeAlteration.values.toSeq)
    }

  implicit lazy val arbitrarydeletionOptions: Arbitrary[deletionOptions] =
    Arbitrary {
      Gen.oneOf(deletionOptions.values.toSeq)
    }

  implicit lazy val arbitraryscenarioResponsible: Arbitrary[scenarioResponsible] =
    Arbitrary {
      Gen.oneOf(scenarioResponsible.values.toSeq)
    }

  implicit lazy val arbitraryReasonForReview: Arbitrary[ReasonForReview] =
    Arbitrary {
      Gen.oneOf(ReasonForReview.values.toSeq)
    }

  implicit lazy val arbitraryRequestCouncilTaxBandReview: Arbitrary[RequestCouncilTaxBandReview] =
    Arbitrary {
      Gen.oneOf(RequestCouncilTaxBandReview.values)
    }

  implicit lazy val arbitraryCouncilTaxResponsible: Arbitrary[CouncilTaxResponsible] =
    Arbitrary {
      Gen.oneOf(CouncilTaxResponsible.values.toSeq)
    }

  implicit lazy val arbitraryAdvancedPropertySearch: Arbitrary[AdvancedPropertySearch] =
    Arbitrary {
      for {
        propertyNameOrNumber <- arbitrary[String]
        street <- arbitrary[String]
        townOrCity <- arbitrary[String]
        postcode <- arbitrary[String]
        councilTaxBand <- arbitrary[String]
        bandStatus <- arbitrary[String]
        localAuthority <- arbitrary[String]
        localAuthorityReferenceNumber <- arbitrary[String]
        courtCode <- arbitrary[String]
        propertyUse <- arbitrary[String]
      } yield AdvancedPropertySearch(
        propertyNameOrNumber,
        street,
        townOrCity,
        postcode,
        councilTaxBand,
        bandStatus,
        localAuthority,
        localAuthorityReferenceNumber,
        courtCode,
        propertyUse
      )
    }
}
