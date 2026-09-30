package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class CouncilTaxResponsibleSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "CouncilTaxResponsible" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(CouncilTaxResponsible.values.toSeq)

      forAll(gen) {
        councilTaxResponsible =>

          JsString(councilTaxResponsible.toString).validate[CouncilTaxResponsible].asOpt.value mustEqual councilTaxResponsible
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!CouncilTaxResponsible.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[CouncilTaxResponsible] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(CouncilTaxResponsible.values.toSeq)

      forAll(gen) {
        councilTaxResponsible =>

          Json.toJson(councilTaxResponsible) mustEqual JsString(councilTaxResponsible.toString)
      }
    }
  }
}
