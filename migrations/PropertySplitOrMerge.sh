#!/bin/bash

echo ""
echo "Applying migration PropertySplitOrMerge"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /propertySplitOrMerge                  controllers.PropertySplitOrMergeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /propertySplitOrMerge                  controllers.PropertySplitOrMergeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePropertySplitOrMerge                        controllers.PropertySplitOrMergeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePropertySplitOrMerge                        controllers.PropertySplitOrMergeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "propertySplitOrMerge.title = PropertySplitOrMerge" >> ../conf/messages.en
echo "propertySplitOrMerge.heading = PropertySplitOrMerge" >> ../conf/messages.en
echo "propertySplitOrMerge.checkYourAnswersLabel = PropertySplitOrMerge" >> ../conf/messages.en
echo "propertySplitOrMerge.error.nonNumeric = Enter your propertySplitOrMerge using numbers" >> ../conf/messages.en
echo "propertySplitOrMerge.error.required = Enter your propertySplitOrMerge" >> ../conf/messages.en
echo "propertySplitOrMerge.error.wholeNumber = Enter your propertySplitOrMerge using whole numbers" >> ../conf/messages.en
echo "propertySplitOrMerge.error.outOfRange = PropertySplitOrMerge must be between {0} and {1}" >> ../conf/messages.en
echo "propertySplitOrMerge.change.hidden = PropertySplitOrMerge" >> ../conf/messages.en

echo "Migration PropertySplitOrMerge completed"
