package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class CouncilTaxPayerDetailsFormProviderSpec extends StringFieldBehaviours {

  val form = new CouncilTaxPayerDetailsFormProvider()()

  ".firstName" - {

    val fieldName = "firstName"
    val requiredKey = "councilTaxPayerDetails.error.firstName.required"
    val lengthKey = "councilTaxPayerDetails.error.firstName.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".lastName" - {

    val fieldName = "lastName"
    val requiredKey = "councilTaxPayerDetails.error.lastName.required"
    val lengthKey = "councilTaxPayerDetails.error.lastName.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
