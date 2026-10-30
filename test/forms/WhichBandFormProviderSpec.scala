package forms

import forms.behaviours.OptionFieldBehaviours
import models.WhichBand
import play.api.data.FormError

class WhichBandFormProviderSpec extends OptionFieldBehaviours {

  val form = new WhichBandFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "whichBand.error.required"

    behave like optionsField[WhichBand](
      form,
      fieldName,
      validValues  = WhichBand.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
