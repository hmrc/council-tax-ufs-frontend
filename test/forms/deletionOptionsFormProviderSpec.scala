package forms

import forms.behaviours.OptionFieldBehaviours
import models.deletionOptions
import play.api.data.FormError

class deletionOptionsFormProviderSpec extends OptionFieldBehaviours {

  val form = new deletionOptionsFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "deletionOptions.error.required"

    behave like optionsField[deletionOptions](
      form,
      fieldName,
      validValues  = deletionOptions.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
