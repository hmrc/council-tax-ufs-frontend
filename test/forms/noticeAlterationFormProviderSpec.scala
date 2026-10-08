package forms

import forms.behaviours.OptionFieldBehaviours
import models.noticeAlteration
import play.api.data.FormError

class noticeAlterationFormProviderSpec extends OptionFieldBehaviours {

  val form = new noticeAlterationFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "noticeAlteration.error.required"

    behave like optionsField[noticeAlteration](
      form,
      fieldName,
      validValues  = noticeAlteration.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
