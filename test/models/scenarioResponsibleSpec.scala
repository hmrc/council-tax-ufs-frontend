package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class scenarioResponsibleSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "scenarioResponsible" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(scenarioResponsible.values.toSeq)

      forAll(gen) {
        scenarioResponsible =>

          JsString(scenarioResponsible.toString).validate[scenarioResponsible].asOpt.value mustEqual scenarioResponsible
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!scenarioResponsible.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[scenarioResponsible] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(scenarioResponsible.values.toSeq)

      forAll(gen) {
        scenarioResponsible =>

          Json.toJson(scenarioResponsible) mustEqual JsString(scenarioResponsible.toString)
      }
    }
  }
}
