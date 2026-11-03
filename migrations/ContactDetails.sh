#!/bin/bash

echo ""
echo "Applying migration ContactDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /contactDetails                        controllers.ContactDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /contactDetails                        controllers.ContactDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeContactDetails                  controllers.ContactDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeContactDetails                  controllers.ContactDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "contactDetails.title = contactDetails" >> ../conf/messages.en
echo "contactDetails.heading = contactDetails" >> ../conf/messages.en
echo "contactDetails.firstName = firstName" >> ../conf/messages.en
echo "contactDetails.lastName = lastName" >> ../conf/messages.en
echo "contactDetails.checkYourAnswersLabel = ContactDetails" >> ../conf/messages.en
echo "contactDetails.error.firstName.required = Enter firstName" >> ../conf/messages.en
echo "contactDetails.error.lastName.required = Enter lastName" >> ../conf/messages.en
echo "contactDetails.error.firstName.length = firstName must be 100 characters or less" >> ../conf/messages.en
echo "contactDetails.error.lastName.length = lastName must be 100 characters or less" >> ../conf/messages.en
echo "contactDetails.firstName.change.hidden = firstName" >> ../conf/messages.en
echo "contactDetails.lastName.change.hidden = lastName" >> ../conf/messages.en

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryContactDetails: Arbitrary[ContactDetails] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        firstName <- arbitrary[String]";\
    print "        lastName <- arbitrary[String]";\
    print "      } yield ContactDetails(firstName, lastName)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Migration ContactDetails completed"
