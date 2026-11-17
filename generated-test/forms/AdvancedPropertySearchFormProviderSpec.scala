package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class AdvancedPropertySearchFormProviderSpec extends StringFieldBehaviours {

  val form = new AdvancedPropertySearchFormProvider()()

  ".Property name or number" - {

    val fieldName = "Property name or number"
    val requiredKey = "advancedPropertySearch.error.Property name or number.required"
    val lengthKey = "advancedPropertySearch.error.Property name or number.length"
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

  ".Street" - {

    val fieldName = "Street"
    val requiredKey = "advancedPropertySearch.error.Street.required"
    val lengthKey = "advancedPropertySearch.error.Street.length"
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
