package forms

import forms.behaviours.IntFieldBehaviours
import play.api.data.FormError

class PropertySplitOrMergeFormProviderSpec extends IntFieldBehaviours {

  val form = new PropertySplitOrMergeFormProvider()()

  ".value" - {

    val fieldName = "value"

    val minimum = merged
    val maximum = 1

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDataGenerator
    )

    behave like intField(
      form,
      fieldName,
      nonNumericError  = FormError(fieldName, "propertySplitOrMerge.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "propertySplitOrMerge.error.wholeNumber")
    )

    behave like intFieldWithRange(
      form,
      fieldName,
      minimum       = minimum,
      maximum       = maximum,
      expectedError = FormError(fieldName, "propertySplitOrMerge.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "propertySplitOrMerge.error.required")
    )
  }
}
