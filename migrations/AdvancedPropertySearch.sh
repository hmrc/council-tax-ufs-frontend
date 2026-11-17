#!/bin/bash

echo ""
echo "Applying migration AdvancedPropertySearch"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /advancedPropertySearch                        controllers.AdvancedPropertySearchController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /advancedPropertySearch                        controllers.AdvancedPropertySearchController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAdvancedPropertySearch                  controllers.AdvancedPropertySearchController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAdvancedPropertySearch                  controllers.AdvancedPropertySearchController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "advancedPropertySearch.title = advancedPropertySearch" >> ../conf/messages.en
echo "advancedPropertySearch.heading = advancedPropertySearch" >> ../conf/messages.en
echo "advancedPropertySearch.Property name or number = Property name or number" >> ../conf/messages.en
echo "advancedPropertySearch.Street = Street" >> ../conf/messages.en
echo "advancedPropertySearch.checkYourAnswersLabel = AdvancedPropertySearch" >> ../conf/messages.en
echo "advancedPropertySearch.error.Property name or number.required = Enter Property name or number" >> ../conf/messages.en
echo "advancedPropertySearch.error.Street.required = Enter Street" >> ../conf/messages.en
echo "advancedPropertySearch.error.Property name or number.length = Property name or number must be 100 characters or less" >> ../conf/messages.en
echo "advancedPropertySearch.error.Street.length = Street must be 100 characters or less" >> ../conf/messages.en
echo "advancedPropertySearch.Property name or number.change.hidden = Property name or number" >> ../conf/messages.en
echo "advancedPropertySearch.Street.change.hidden = Street" >> ../conf/messages.en

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAdvancedPropertySearch: Arbitrary[AdvancedPropertySearch] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        Property name or number <- arbitrary[String]";\
    print "        Street <- arbitrary[String]";\
    print "      } yield AdvancedPropertySearch(Property name or number, Street)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Migration AdvancedPropertySearch completed"
