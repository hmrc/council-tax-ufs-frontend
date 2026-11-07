#!/bin/bash

echo ""
echo "Applying migration CouncilTaxPayerDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /councilTaxPayerDetails                        controllers.CouncilTaxPayerDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /councilTaxPayerDetails                        controllers.CouncilTaxPayerDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeCouncilTaxPayerDetails                  controllers.CouncilTaxPayerDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeCouncilTaxPayerDetails                  controllers.CouncilTaxPayerDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "councilTaxPayerDetails.title = councilTaxPayerDetails" >> ../conf/messages.en
echo "councilTaxPayerDetails.heading = councilTaxPayerDetails" >> ../conf/messages.en
echo "councilTaxPayerDetails.firstName = firstName" >> ../conf/messages.en
echo "councilTaxPayerDetails.lastName = lastName" >> ../conf/messages.en
echo "councilTaxPayerDetails.checkYourAnswersLabel = CouncilTaxPayerDetails" >> ../conf/messages.en
echo "councilTaxPayerDetails.error.firstName.required = Enter firstName" >> ../conf/messages.en
echo "councilTaxPayerDetails.error.lastName.required = Enter lastName" >> ../conf/messages.en
echo "councilTaxPayerDetails.error.firstName.length = firstName must be 100 characters or less" >> ../conf/messages.en
echo "councilTaxPayerDetails.error.lastName.length = lastName must be 100 characters or less" >> ../conf/messages.en
echo "councilTaxPayerDetails.firstName.change.hidden = firstName" >> ../conf/messages.en
echo "councilTaxPayerDetails.lastName.change.hidden = lastName" >> ../conf/messages.en

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCouncilTaxPayerDetails: Arbitrary[CouncilTaxPayerDetails] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        firstName <- arbitrary[String]";\
    print "        lastName <- arbitrary[String]";\
    print "      } yield CouncilTaxPayerDetails(firstName, lastName)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Migration CouncilTaxPayerDetails completed"
