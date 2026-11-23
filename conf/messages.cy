# grouped by journey stage, then by page, same layout as messages.en
# English placeholders still to be translated are listed in the last section of this file.

##############################################################################
# Global: service, common labels, errors and session
##############################################################################

# --- service ---
service.name = Adolygu eich band Treth Gyngor

# --- site ---
site.opensInNewTab = (yn agor tab newydd)
site.back          = Yn ôl
site.remove        = Dileu
site.change        = Newid
site.no            = Na
site.yes           = Ie
site.continue      = Parhau
site.start         = Dechrau nawr
site.startAgain    = Dechrau eto
site.signIn        = Mewngofnodi
site.govuk         = GOV.UK
site.warning       = Warning

# --- button ---
# Buttons
button.print    = Argraffu’r dudalen hon
button.continue = Parhau
button.search   = Chwilio

# --- date ---
date.day         = Dydd
date.month       = Mis
date.year        = Blwyddyn
date.error.day   = diwrnod
date.error.month = mis
date.error.year  = blwyddyn

# --- characterCount ---
characterCount.remaining        = Mae gennych {0} cymeriad ar ôl
characterCount.underLimit.one   = Mae gennych {0} nod yn weddill
characterCount.underLimit.other = Mae gennych {0} o cymeriad ar ôl
characterCount.atLimit          = Nid oes unrhyw nodau yn weddill
characterCount.overLimit.one    = Mae gennych {0} cymeriad yn ormod
characterCount.overLimit.other  = Mae gennych {0} o gymeriadau yn ormod
characterCount.underLimit.zero  = Nid oes unrhyw nodau yn weddill
characterCount.underLimit.two   = Mae gennych {0} nod yn weddill
characterCount.underLimit.few   = Mae gennych {0} o cymeriad ar ôl
characterCount.underLimit.many  = Mae gennych {0} o cymeriad ar ôl
characterCount.overLimit.zero   = Nid oes cymeriadau dros y terfyn
characterCount.overLimit.two    = Mae gennych {0} gymeriad yn ormod
characterCount.overLimit.few    = Mae gennych {0} o gymeriadau yn ormod
characterCount.overLimit.many   = Mae gennych {0} o gymeriadau yn ormod

# --- error ---
error.title.prefix  = Gwall:
error.prefix        = Gwall
error.summary.title = Mae problem wedi codi
error.title         = Mae’n ddrwg gennym, mae problem gyda’r gwasanaeth hwn.
error.heading       = Mae’n ddrwg gennym, mae problem gyda’r gwasanaeth hwn.
error.guidance      = Rhowch gynnig arall arni yn ddiweddarach.

# --- timeout ---
timeout.title     = Rydych ar fin cael eich allgofnodi
timeout.message   = Am resymau diogelwch, byddwch yn cael eich allgofnodi o''r gwasanaeth hwn ymhen
timeout.keepAlive = Aros wedi mewngofnodi
timeout.signOut   = Allgofnodi

# --- signedOut ---
signedOut.title    = Er eich diogelwch, gwnaethom eich allgofnodi
signedOut.heading  = Er eich diogelwch, gwnaethom eich allgofnodi
signedOut.guidance = Ni wnaethom gadw eich atebion.

# --- unauthorised ---
unauthorised.title   = Ni allwch gael mynediad at y gwasanaeth hwn gyda''r cyfrif hwn
unauthorised.heading = Ni allwch gael mynediad at y gwasanaeth hwn gyda''r cyfrif hwn

# --- journeyRecovery ---
journeyRecovery.continue.title      = Mae''n ddrwg gennym, mae problem gyda''r gwasanaeth
journeyRecovery.continue.heading    = Mae''n ddrwg gennym, mae problem gyda''r gwasanaeth
journeyRecovery.continue.guidance   = [Ychwanegwch gynnwys i esbonio sut i barhau.]
journeyRecovery.startAgain.title    = Mae''n ddrwg gennym, mae problem gyda''r gwasanaeth
journeyRecovery.startAgain.heading  = Mae''n ddrwg gennym, mae problem gyda''r gwasanaeth
journeyRecovery.startAgain.guidance = [Ychwanegwch gynnwys i esbonio pam mae angen i''r defnyddiwr ddechrau eto.]

##############################################################################
# Start page
##############################################################################

# --- index ---
index.title    = council-tax-ufs-frontend
index.heading  = council-tax-ufs-frontend
index.guidance = Croeso i''ch blaenwedd newydd. Gweler y ffeil README am ganllaw i ddechrau.

##############################################################################
# 1. Find the property: postcode search, results and advanced search
##############################################################################

# --- postcodeSearch ---
postcodeSearch.title                 = Nodwch god post yr eiddo
postcodeSearch.heading               = Nodwch god post yr eiddo
postcodeSearch.description           = Gallwch chwilio am unrhyw gartref yng Nghymru a Lloegr drwy nodi''r cod post.
postcodeSearch.label                 = Cod post
postcodeSearch.hint                  = Er enghraifft CF5 4GP
postcodeSearch.checkYourAnswersLabel = Cod post a ddefnyddiwyd i ddod o hyd i''ch cartref
postcodeSearch.dontKnowPostcode      = Opsiynau chwilio eraill
postcodeSearch.error.required        = Nodwch god post, er enghraifft AA1 1AA
postcodeSearch.error.invalid         = Nodwch god post go iawn
postcodeSearch.error.length          = Nodwch god post go iawn
postcodeSearch.change.hidden         = Postcode Search

# --- searchResults ---
# Search Results
searchResults.title                                = Canlyniadau chwilio am {0}
searchResults.title.paged                          = Canlyniadau chwilio am {0} (tudalen {1} o {2})
searchResults.heading                              = Canlyniadau chwilio am {0}
searchResults.lastUpdated                          = Diweddarwyd ddiwethaf ar {0}
searchResults.showing                              = Yn dangos {0} - {1} o {2} canlyniad
searchResults.column.address                       = Cyfeiriad
searchResults.column.band                          = Band Treth Gyngor
searchResults.column.localAuthority                = Awdurdod lleol
searchResults.cannotFind                           = Ni allaf ddod o hyd i fy eiddo
searchResults.cannotFind.check                     = Gwiriwch fod y manylion rydych wedi''u rhoi yn gywir, neu chwiliwch am eiddo gan ddefnyddio''r
searchResults.cannotFind.otherOptions              = opsiynau chwilio eraill
searchResults.cannotFind.contactCouncil            = Cysylltwch â''ch cyngor lleol
searchResults.cannotFind.contactCouncilDescription = os na allwch ddod o hyd i’''ch eiddo o hyd. Byddant yn rhoi’''r wybodaeth sydd ei hangen ar y Swyddfa Brisio er mwyn neilltuo band Treth Gyngor.
searchResults.commercialUse                        = Ni ddylid defnyddio’''r data hwn at ddibenion masnachol
searchResults.searchAgain                          = Chwilio eto
searchResults.searchAgain.label                    = Rhowch god post
searchResults.searchAgain.button                   = Chwilio
searchResults.otherSearchOptions                   = Opsiynau chwilio eraill
searchResults.error                                = Roedd problem gyda''ch chwiliad. Rhowch gynnig arall arni.
searchResults.voa.england                          = Mae''r <a class="govuk-link" href="https://www.gov.uk/government/organisations/valuation-office-agency">Swyddfa Brisio (VO)</a> yn rhoi pob cartref mewn grŵp o''r enw ''band'', yn seiliedig ar werth yr eiddo ar 1 Ebrill 1991.
searchResults.bands.england                        = Band ''A'' yw''r un isaf a Band ''H'' yw''r un uchaf.
searchResults.voa.wales                            = Mae''r <a class="govuk-link" href="https://www.gov.uk/government/organisations/valuation-office-agency">Swyddfa Brisio (VO)</a> yn rhoi pob cartref mewn grŵp o''r enw ''band'', yn seiliedig ar werth yr eiddo ar 1 Ebrill 2003.
searchResults.bands.wales                          = Band ''A'' yw''r un isaf a Band ''I'' yw''r un uchaf.

# --- noResults ---
# No Results
noResults.title                     = Dim canlyniadau ar gyfer {0}
noResults.heading                   = Dim canlyniadau ar gyfer {0}
noResults.description               = Doedd dim modd i ni ddod o hyd i unrhyw gyfeiriad ar gyfer y cod post hwnnw.
noResults.label                     = Nodwch y cod post
noResults.button                    = Chwilio eto
noResults.cannotFind.check          = Gwiriwch fod y manylion rydych chi wedi’u nodi yn gywir, neu chwiliwch am eiddo gan ddefnyddio’r
noResults.cannotFind.otherOptions   = opsiynau chwilio eraill
noResults.contactCouncil            = Cysylltwch â''ch cyngor lleol
noResults.contactCouncilDescription = os na allwch ddod o hyd i’ch eiddo o hyd. Byddant yn rhoi’r wybodaeth sydd ei hangen ar Swyddfa Brisio i bennu band Treth Gyngor.

# --- advancedPropertySearch ---
# Advance Search
advancedPropertySearch.title                               = Chwilio am eiddo gan ddefnyddio manylion eraill
advancedPropertySearch.heading                             = Chwilio am eiddo gan ddefnyddio manylion eraill
advancedPropertySearch.propertyNameOrNumber                = Enw neu rif yr eiddo (dewisol)
advancedPropertySearch.street                              = Stryd (dewisol)
advancedPropertySearch.townOrCity                          = Tref neu ddinas (dewisol)
advancedPropertySearch.postcode                            = Cod Post
advancedPropertySearch.checkYourAnswersLabel               = Advanced property search
advancedPropertySearch.error.propertyNameOrNumber.required = Enter property name or number
advancedPropertySearch.error.street.required               = Enter street
advancedPropertySearch.error.propertyNameOrNumber.length   = Mae''n rhaid i enw neu rif yr eiddo fod yn 80 o gymeriadau neu lai
advancedPropertySearch.error.street.length                 = Mae''n rhaid i''r stryd fod yn 80 o gymeriadau neu lai
advancedPropertySearch.propertyNameOrNumber.change.hidden  = property name or number
advancedPropertySearch.street.change.hidden                = street
advancedPropertySearch.showSearchFilters                   = Dangos hidlwyr chwilio
advancedPropertySearch.councilTaxBand                      = Band Treth Gyngor
advancedPropertySearch.bandStatus                          = Statws band
advancedPropertySearch.localAuthority                      = Awdurdod lleol (dewisol)
advancedPropertySearch.bandStatus.hint                     = Mae eitemau cyfredol a ddilëwyd yn gofnodion ar gyfer eiddo nad ydynt yn talu Treth Gyngor mwyach - er enghraifft, oherwydd eu bod wedi cael eu dymchwel.
advancedPropertySearch.laReferenceNumber                   = Cyfeirnod yr awdurdod lleol (dewisol)
advancedPropertySearch.laReferenceNumber.hint              = Cyfeirnod unigryw ar gyfer eich eiddo
advancedPropertySearch.courtCode                           = Cod y Llys
advancedPropertySearch.courtCode.hint                      = Bydd gan yr eiddo god y llys os yw band y Dreth Gyngor wedi cael ei newid (penderfyniad) neu ei gadarnhau (cadarnhad) gan Dribiwnlys Prisio neu Uchel Lys.
advancedPropertySearch.propertyUse                         = Defnyddio’r eiddo
advancedPropertySearch.propertyUse.hint                    = Er enghraifft, dim ond at ddefnydd domestig, neu at ddibenion domestig a busnes
advancedPropertySearch.change.hidden                       = advanced property search
advancedPropertySearch.error.postcode.required             = Nodwch god post, fel AA1 1AA
advancedPropertySearch.error.postcode.length               = Nodwch god post go iawn
advancedPropertySearch.error.postcode.invalid              = Nodwch god post go iawn
advancedPropertySearch.error.townOrCity.length             = Mae''n rhaid i''r dref fod yn 80 o gymeriadau neu lai
advancedPropertySearch.error.laReferenceNumber.length      = Local authority reference number must be 20 characters or fewer
advancedPropertySearch.localAuthority.default              = Dewiswch opsiwn
advancedPropertySearch.councilTaxBand.allBands             = Pob band
advancedPropertySearch.councilTaxBand.A                    = A
advancedPropertySearch.councilTaxBand.B                    = B
advancedPropertySearch.councilTaxBand.C                    = C
advancedPropertySearch.councilTaxBand.D                    = D
advancedPropertySearch.councilTaxBand.E                    = E
advancedPropertySearch.councilTaxBand.F                    = F
advancedPropertySearch.councilTaxBand.G                    = G
advancedPropertySearch.councilTaxBand.H                    = H
advancedPropertySearch.councilTaxBand.I                    = I
advancedPropertySearch.bandStatus.default                  = Dileadau cyfredol a chyfredol
advancedPropertySearch.bandStatus.Current                  = Cyfredol
advancedPropertySearch.bandStatus.Deleted                  = Cyfredol a ddilëwyd
advancedPropertySearch.courtCode.default                   = Popeth
advancedPropertySearch.courtCode.V                         = Penderfyniad y TP
advancedPropertySearch.courtCode.C                         = Cadarnhad y TP
advancedPropertySearch.courtCode.D                         = Penderfyniad yr UL
advancedPropertySearch.courtCode.H                         = Cadarnhad yr UL
advancedPropertySearch.courtCode.R                         = Ailbenderfyniad y TP wedi atgyfeiriad yr UL
advancedPropertySearch.courtCode.E                         = Gorchymyn caniatâd yr UL
advancedPropertySearch.propertyUse.default                 = Eiddo domestig yn unig a hefyd defnydd cymysg
advancedPropertySearch.propertyUse.C                       = Eiddo defnydd cymysg yn unig
advancedPropertySearch.propertyUse.N                       = Eiddo defnydd domestig yn unig

##############################################################################
# 2. Property details and historic bands
##############################################################################

# --- propertyDetail ---
propertyDetail.searchAnother                  = Chwilio am eiddo arall
propertyDetail.informationFor                 = Dyma''r wybodaeth ar gyfer:
propertyDetail.metaDescription                = Gweld band Treth Gyngor a gwybodaeth eiddo ar gyfer y cyfeiriad hwn.
propertyDetail.publicInformation              = Gwybodaeth gyhoeddus
propertyDetail.publicInformation.anybody      = Gall unrhyw un weld yr wybodaeth hon.
propertyDetail.band                           = Band Treth Gyngor
propertyDetail.band.hint.england              = bandiau Treth Gyngor yn Lloegr (yn seiliedig ar werthoedd 1 Ebrill 1991)
propertyDetail.band.hint.wales                = bandiau Treth Gyngor yng Nghymru (yn seiliedig ar werthoedd 1 Ebrill 2003)
propertyDetail.band.table.band                = Band
propertyDetail.band.table.value.england       = Gwerth ar 1 Ebrill 1991
propertyDetail.band.table.value.wales         = Gwerth ar 1 Ebrill 2003
propertyDetail.band.current                   = band presennol
propertyDetail.band.range.A                   = up to £40,000
propertyDetail.band.range.B                   = £40,001 to £52,000
propertyDetail.band.range.C                   = £52,001 to £68,000
propertyDetail.band.range.D                   = £68,001 to £88,000
propertyDetail.band.range.E                   = £88,001 to £120,000
propertyDetail.band.range.F                   = £120,001 to £160,000
propertyDetail.band.range.G                   = £160,001 to £320,000
propertyDetail.band.range.H                   = More than £320,000
propertyDetail.effectiveFrom                  = Dyddiad dod i rym
propertyDetail.effectiveFrom.hint             = Y dyddiad wnaeth y band presennol ddechrau.
propertyDetail.localCouncil                   = Cyngor Lleol
propertyDetail.localCouncil.hint              = Y Cyngor Lleol sy''n gosod swm y Dreth Gyngor yr ydych yn ei dalu bob blwyddyn, yn seiliedig ar y band.
propertyDetail.councilRef                     = Cyfeirnod y Cyngor Lleol
propertyDetail.councilRef.hint                = Cyfeirnod ar gyfer yr eiddo.
propertyDetail.improvement                    = Dangosydd gwelliant
propertyDetail.improvement.hint               = Os yw''r eiddo wedi cael ei wella neu ei ymestyn ers iddo gael ei roi mewn band Treth Gyngor, dangosir ''Oes''. Mae hyn yn golygu bydd y band yn cael ei adolygu, a gall gynyddu ar ôl i''r eiddo gael ei werthu.
propertyDetail.mixedUse                       = Eiddo defnydd cymysg
propertyDetail.mixedUse.hint                  = Eiddo sy''n cael ei ddefnyddio fel cartref a busnes. Er enghraifft, fflat uwchben siop.
propertyDetail.courtCode                      = Cod y Llys
propertyDetail.courtCode.hint                 = Os yw''r band Treth Gyngor wedi ei adolygu gan Dribiwnlys Prisio neu Uchel Lys, dangosir cod y llys.
propertyDetail.courtCode.none                 = Dim
propertyDetail.notAvailable                   = Ddim ar gael
propertyDetail.courtCode.expand               = Dyma''r codau
propertyDetail.courtCode.V                    = Penderfyniad gan Dribiwnlys Prisio
propertyDetail.courtCode.C                    = Cadarnhad gan Dribiwnlys Prisio
propertyDetail.courtCode.R                    = Ail benderfyniad y Tribiwnlys Prisio yn dilyn atgyfeiriad gan yr Uchel Lys
propertyDetail.courtCode.D                    = Penderfyniad gan Uchel Lys
propertyDetail.courtCode.H                    = Cadarnhad gan Uchel Lys
propertyDetail.courtCode.E                    = Gorchymyn cydsynio''r Uchel Lys
propertyDetail.beforeYouContinue              = Cyn i chi barhau
propertyDetail.beforeYouContinue.youCan       = Gallwch:
propertyDetail.beforeYouContinue.viewPrevious = weld bandiau Treth Gyngor blaenorol yr eiddo hwn
propertyDetail.beforeYouContinue.challenge    = rhoi gwybod i ni os ydych yn credu bod y band Treth Gyngor yn anghywir os ydych naill ai yn talu Treth Gyngor ar gyfer yr eiddo neu os oes gennych ganiatâd gan dalwr y Dreth Gyngor
propertyDetail.beforeYouContinue.remove       = gofyn i ni dynnu’r eiddo oddi ar restr y Dreth Gyngor os yw wedi’i ddymchwel, yn adfeiliedig, neu’n destun gwaith adnewyddu mawr
propertyDetail.beforeYouContinue.signIn       = Bydd angen cyfrif Porth y Llywodraeth neu gyfrif GOV.UK One Login arnoch i fewngofnodi.
# England
propertyDetail.band.expand.england            = Bandiau Treth Gyngor yn Lloegr
propertyDetail.band.range.england.A           = hyd at £40,000
propertyDetail.band.range.england.B           = £40,001 i £52,000
propertyDetail.band.range.england.C           = £52,001 i £68,000
propertyDetail.band.range.england.D           = £68,001 i £88,000
propertyDetail.band.range.england.E           = £88,001 i £120,000
propertyDetail.band.range.england.F           = £120,001 i £160,000
propertyDetail.band.range.england.G           = £160,001 i £320,000
propertyDetail.band.range.england.H           = mwy na £320,000
# Wales
propertyDetail.band.expand.wales              = Bandiau Treth Gyngor yng Nghymru
propertyDetail.band.range.wales.A             = hyd at £44,000
propertyDetail.band.range.wales.B             = £44,001 i £65,000
propertyDetail.band.range.wales.C             = £65,001 i £91,000
propertyDetail.band.range.wales.D             = £91,001 i £123,000
propertyDetail.band.range.wales.E             = £123,001 i £162,000
propertyDetail.band.range.wales.F             = £162,001 i £223,000
propertyDetail.band.range.wales.G             = £223,001 i £324,000
propertyDetail.band.range.wales.H             = £324,001 i £424,000
propertyDetail.band.range.wales.I             = mwy na £424,000

# --- historicBands ---
historicBands.title                            = Bandiau Treth Gyngor blaenorol
historicBands.informationFor                   = Bandiau Treth Gyngor blaenorol ar gyfer
historicBands.metaDescription                  = Gweld bandiau Treth Gyngor blaenorol yr eiddo hwn.
historicBands.fullTitle                        = Manylion band Treth Gyngor hanesyddol
historicBands.fullHeading                      = Band Treth Gyngor {0}
historicBands.band                             = Band Treth Gyngor
historicBands.effectiveFrom                    = Yn weithredol o
historicBands.effectiveTo                      = Hyd at
historicBands.none                             = Nid oes bandiau Treth Gyngor blaenorol ar gael ar gyfer yr eiddo hwn.
historicBands.details.summary                  = Beth mae gwybodaeth fy eiddo yn ei olygu?
historicBands.details.band                     = Band Treth Gyngor
historicBands.details.bandDescription          = Y band yr oedd yr eiddo ynddo. Mae hyn yn pennu faint o Dreth Gyngor sy’n cael ei thalu.
historicBands.details.effectiveFrom            = Yn weithredol o
historicBands.details.effectiveFromDescription = Y dyddiad y daeth y band Treth Gyngor i rym.

# --- historicBandFull ---
historicBandFull.localAuthority           = Awdurdod lleol
historicBandFull.metaDescription          = Gweld manylion hanesyddol band Treth Gyngor yr eiddo hwn, gan gynnwys pryd oedd y band yn berthnasol.
historicBandFull.valuationList            = Rhestr brisio
historicBandFull.valuationListDescription = Mae hyn yn nodi''r holl eiddo sydd wedi''u bandio at ddibenion Treth Gyngor. Crëwyd rhestrau bandiau Treth Gyngor ar gyfer Cymru a Lloegr yn 1993 ac eto yng Nghymru yn 2005.
historicBandFull.challenge.summary        = Herio band Treth Gyngor blaenorol
historicBandFull.challenge.telephone      = Ffôn
historicBandFull.challenge.england        = 03000 501 501 ar gyfer Lloegr
historicBandFull.challenge.wales          = 03000 505 505 ar gyfer Cymru
historicBandFull.challenge.hours          = Dydd Llun i ddydd Gwener, 9:00am i 4:30pm (ac eithrio gwyliau cyhoeddus)
historicBandFull.challenge.email          = E-bost
historicBandFull.challenge.emailAddress   = ctinboxvo@hmrc.gov.uk

##############################################################################
# 3. Responsibility for Council Tax
##############################################################################

# --- councilTaxResponsible ---
councilTaxResponsible.title                 = Ydych chi''n gyfrifol am y Dreth Gyngor ar yr eiddo hwn?
councilTaxResponsible.heading               = Ydych chi''n gyfrifol am y Dreth Gyngor ar yr eiddo hwn?
councilTaxResponsible.yes                   = Ydw
councilTaxResponsible.no                    = Nac ydw
councilTaxResponsible.actingFor             = Rydw i''n gweithredu dros rywun sy''n gyfrifol
councilTaxResponsible.hint                  = Dim ond pobl sy''n gyfrifol am y Dreth Gyngor ar eiddo sydd â chaniatâd i weld rhagor o fanylion. Mae hyn yn cynnwys pobl sy''n gweithredu dros rywun sydd yn gyfrifol.
councilTaxResponsible.actingFor.hint        = Bydd angen i chi gael eu caniatâd ysgrifenedig
councilTaxResponsible.warning               = Os nad ydych chi''n gyfrifol am y Dreth Gyngor ar yr eiddo hwn, dylech chi ddim barhau gyda''r gwasanaeth hwn.
councilTaxResponsible.checkYourAnswersLabel = Ydych chi''n gyfrifol am y Dreth Gyngor ar yr eiddo hwn?
councilTaxResponsible.error.required        = Dewiswch ''Ydw'' os ydych chi''n gyfriifol am y Dreth Gyngor ar yr eiddo hwn
councilTaxResponsible.change.hidden         = CouncilTaxResponsible

# --- noPermission ---
noPermission.title                 = Does gennych chi ddim caniatâd i weld rhagor o fanylion
noPermission.heading               = Does gennych chi ddim caniatâd i weld rhagor o fanylion
noPermission.body                  = Dim ond pobl sy’n gyfrifol am y Dreth Gyngor ar eiddo sydd â chaniatâd i weld rhagor o fanylion. Mae hyn yn cynnwys pobl sy’n gweithredu dros rywun sydd yn gyfrifol.
noPermission.backToPropertyDetails = Yn ôl at fanylion yr eiddo

##############################################################################
# 4. Request a review and reason for review
##############################################################################

# --- requestCouncilTaxBandReview ---
requestCouncilTaxBandReview.title                 = Gwneud cais am adolygiad o fand Treth Gyngor
requestCouncilTaxBandReview.heading               = Gwneud cais am adolygiad o fand Treth Gyngor
requestCouncilTaxBandReview.p1.1                  = Gallwch ofyn i''r Swyddfa Brisio (VO) am adolygiad o fand Treth Gyngor ar unrhyw adeg.
requestCouncilTaxBandReview.p1.2                  = Nid oes tâl am y gwasanaeth hwn.
requestCouncilTaxBandReview.p1.3                  = Bydd angen i chi ddweud wrthym pam rydych yn meddwl y dylid adolygu’r band. Efallai y byddwn yn gofyn i chi am ragor o fanylion, ac efallai y bydd angen i chi ddarparu tystiolaeth i gefnogi''ch cais.
requestCouncilTaxBandReview.subheading1           = Pryd y gallwch ofyn am adolygiad o fand Treth Gyngor
requestCouncilTaxBandReview.p2.1                  = Rydych yn gallu gofyn i ni adolygu eich band Treth Gyngor os:
requestCouncilTaxBandReview.subheading2.bullet1   = ydych wedi dod yn drethdalwr newydd o fewn y 6 mis diwethaf
requestCouncilTaxBandReview.subheading2.bullet2   = mae’r band Treth Gyngor wedi newid yn ystod y 6 mis diwethaf
requestCouncilTaxBandReview.subheading2.bullet3   = mae’r ardal leol wedi newid yn gorfforol
requestCouncilTaxBandReview.p2.2                  = Rydych yn gallu hefyd ofyn am adolygiad os yw rhywbeth wedi newid. Er enghraifft, mae’r eiddo:
requestCouncilTaxBandReview.subheading2.bullet4   = wedi''i ddymchwel
requestCouncilTaxBandReview.subheading2.bullet5   = yn adfeiliedig ac ni ellir byw ynddo
requestCouncilTaxBandReview.subheading2.bullet6   = yn mynd trwy waith adnewyddu neu atgyweirio mawr
requestCouncilTaxBandReview.subheading2.bullet7   = yn cael ei ddefnyddio''n rhannol at ddibenion busnes
requestCouncilTaxBandReview.subheading2.bullet8   = wedi''i rannu''n sawl eiddo neu wedi''i uno''n un eiddo
requestCouncilTaxBandReview.p2.3                  = Os byddwch yn gofyn am adolygiad am un o’r rhesymau hyn, ond dydych chi ddim yn cytuno â’r canlyniad, rydych yn gallu apelio yn erbyn y penderfyniad.
requestCouncilTaxBandReview.p2.4                  = Byddwn yn ystyried unrhyw welliannau wrth bennu’r band newydd, a allai fod yn uwch na’r un blaenorol. Fel arfer, bydd y band newydd yn dod i rym o’r dyddiad y daw’r eiddo yn addas i fyw ynddo eto.
requestCouncilTaxBandReview.subheading2           = Cysylltu â’ch Cyngor Lleol
requestCouncilTaxBandReview.p3.1                  = Dylech hefyd wirio gyda’ch cyngor lleol am gyngor a gwybodaeth am y Dreth Gyngor. Ewch i wefan eich cyngor lleol am ragor o fanylion.
requestCouncilTaxBandReview.subheading3           = Adolygu’r band am unrhyw reswm arall
requestCouncilTaxBandReview.p4.1                  = Os nad yw’r uchod yn berthnasol, byddwn ond yn adolygu eich band os byddwch yn darparu tystiolaeth ategol.
requestCouncilTaxBandReview.p4.2                  = Bydd eich cais am adolygiad band yn cael ei wrthod heb hyn.
requestCouncilTaxBandReview.readContent.label     = Rwyf wedi darllen y dudalen hon ac rwyf am barhau.
requestCouncilTaxBandReview.checkYourAnswersLabel = Gwneud cais am adolygiad o fand Treth Gyngor
requestCouncilTaxBandReview.error.required        = Dewiswch i gadarnhau eich bod wedi darllen y dudalen hon

# --- reasonForReview ---
reasonForReview.title                     = Rhesymau dros adolygu eich band Treth Gyngor
reasonForReview.heading                   = Pam ydych chi am adolygu eich band Treth Gyngor?
reasonForReview.subheading1               = Os ydych yn meddwl bod eich band yn anghywir
reasonForReview.p1                        = Os ydych yn meddwl bod eich band Treth Gyngor yn rhy uchel, neu os ydych yn anghytuno â newid diweddar i’ch band neu’ch dyddiad dod i rym, byddwn yn ei adolygu. Gallwch hefyd ofyn am adolygiad yn dilyn penderfyniad tribiwnlys neu lys ynghylch eiddo tebyg.
reasonForReview.subheading2               = Os yw newidiadau wedi effeithio ar eich eiddo
reasonForReview.p2                        = Gallwn adolygu eich band os yw’r eiddo wedi bod, neu’n mynd drwy, rai newidiadau penodol, gan gynnwys, ond heb fod yn gyfyngedig i:
reasonForReview.bullet1                   = dymchwel rhan o’r eiddo neu’r eiddo cyfan
reasonForReview.bullet2                   = gwaith adeiladu mawr sy’n golygu na ellir byw ynddo
reasonForReview.bullet3                   = newid i’r ffordd y mae’r adeilad yn cael ei ddefnyddio
reasonForReview.p3                        = Gallwn hefyd adolygu eich band os yw newidiadau yn eich ardal leol wedi effeithio ar eich eiddo.
reasonForReview.band-is-wrong             = Rwy''n credu bod fy mand yn anghywir
reasonForReview.changes-affected-property = Mae newidiadau wedi effeithio ar yr eiddo
reasonForReview.error.required            = Dewiswch pam eich bod am herio eich band Treth Gyngor

##############################################################################
# 5. Challenge journey: eligibility, review overview and details
##############################################################################

# --- scenarioResponsible ---
scenarioResponsible.title                 = Ers faint o amser rydych wedi bod yn gyfrifol am dalu Treth Gyngor ar yr eiddo?
scenarioResponsible.heading               = Ers faint o amser rydych wedi bod yn gyfrifol am dalu Treth Gyngor ar yr eiddo?
scenarioResponsible.less-than             = Llai na 6 mis
scenarioResponsible.more-than             = Mwy na 6 mis
scenarioResponsible.checkYourAnswersLabel = Ers faint o amser rydych wedi bod yn gyfrifol am dalu Treth Gyngor ar yr eiddo?
scenarioResponsible.error.required        = Dewiswch pa mor hir rydych wedi bod yn talu Treth Gyngor ar yr eiddo hwn

# --- noticeAlteration ---
noticeAlteration.title                 = Ydych chi wedi derbyn Hysbysiad o Addasiad yn ystod y chwe mis diwethaf?
noticeAlteration.heading               = Ydych chi wedi derbyn Hysbysiad o Addasiad yn ystod y chwe mis diwethaf?
noticeAlteration.hint                  = Mae Hysbysiad o Addasiad yn lythyr sy’n cael ei anfon gan Asiantaeth y Swyddfa Brisio os yw newid wedi bod i''r Rhestr Brisio.
noticeAlteration.yes                   = Ydw
noticeAlteration.no                    = Nac ydw
noticeAlteration.checkYourAnswersLabel = Ydych chi wedi derbyn Hysbysiad o Addasiad yn ystod y chwe mis diwethaf?
noticeAlteration.error.required        = Dewiswch a ydych wedi cael Hysbysiad o Addasiad o fewn y chwe mis diwethaf

# --- reviewOverview ---
# Review overview pages
reviewOverview.changeSelection        = Rydych yn gallu newid eich dewis os dyw’r opsiwn yma ddim yn iawn i chi.
reviewOverview.row.whatItIs           = Beth yw e
reviewOverview.row.whatFor            = Ar gyfer beth yw e
reviewOverview.row.duration           = Pa mor hir mae’n cymryd
reviewOverview.row.evidence           = Y dystiolaeth sydd angen
reviewOverview.row.examples           = Enghreifftiau o dystiolaeth
reviewOverview.row.decision           = Penderfyniad
reviewOverview.row.afterDecision      = Ar ôl y penderfyniad
reviewOverview.decision.intro         = Mae 3 phenderfyniad posib:
reviewOverview.decision.1             = mae eich cartref yn aros yn yr un band
reviewOverview.decision.2             = mae eich cartref yn symud i fand is
reviewOverview.decision.3             = mae eich cartref yn symud i fand uwch
reviewOverview.formal.title           = Gofyn am adolygiad ffurfiol
reviewOverview.formal.intro           = Rydych wedi dweud wrthym eich bod wedi bod yn gyfrifol am dalu Treth Gyngor ar eich cartref <strong>lam ai na 6 mis</strong>.
reviewOverview.formal.description     = MMae hyn yn golygu eich bod yn gallu gofyn am adolygiad ffurfiol o’ch band Treth Gyngor. Enw arall am hyn ydy ‘Her Ffurfiol’ neu ‘Gwneud Cynnig’.
reviewOverview.formal.table.heading   = Sut mae adolygiad ffurfiol yn gweithio
reviewOverview.formal.whatItIs        = Rydych yn gallu gofyn i Asiantaeth y Swyddfa Brisio (VO) adolygu’r band Treth Gyngor mae eich cartref ynddo.
reviewOverview.formal.whatFor         = Rydych yn gallu gofyn am adolygiad ffurfiol os ydych yn meddwl bod eich cartref yn y band Treth Gyngor anghywir.
reviewOverview.formal.when.label      = Pryd rydych yn gallu gofyn am adolygiad ffurfiol
reviewOverview.formal.when.value      = Os ydych wedi bod yn gyfrifol am y Dreth Gyngor ar eich cartref am <strong>lai na 6 mis</strong>.
reviewOverview.formal.duration        = Hyd at 16 wythnos
reviewOverview.formal.evidence        = Dydych chi ddim angen rhoi unrhyw dystiolaeth. Ond, bydd unrhyw wybodaeth rydych yn ei roi yn helpu’r VO i ddeall pam rydych yn meddwl y dylai’r cartref fod mewn band gwahanol.
reviewOverview.formal.examples.1      = cyfeiriadau cartrefi tebyg sydd mewn band is na’ch un chi.
reviewOverview.formal.examples.2      = y pris y gwerthwyd eich cartref neu gartrefi tebyg amdano rhwng 1 Ebrill 2001 a 31 Mawrth 2005.
reviewOverview.formal.examples.3      = gallwch gyflwyno tystiolaeth ysgrifenedig a lanlwytho hyd at 5 ffeil, gan gynnwys PDF, dogfennau a lluniau.
reviewOverview.formal.afterDecision   = Os nad ydych yn hapus gyda’r canlyniad, mae gennych 4 mis ar ôl dyddiad y penderfyniad i apelio i Dribiwnlys Prisio Cymru.
reviewOverview.informal.title         = Gofyn am adolygiad anffurfiol
reviewOverview.informal.intro         = Rydych wedi dweud wrthym eich bod wedi bod yn gyfrifol am dalu Treth Gyngor am eich cartref am <strong>fwy na 6 mis</strong>.
reviewOverview.informal.description   = Mae hyn yn meddwl eich bod yn gallu gofyn am adolygiad anffurfiol o’ch band Treth Gyngor. Enw arall am hyn ydy ‘Her Anffurfiol’ neu ‘Adolygiad Band’.
reviewOverview.informal.table.heading = Sut mae adolygiad anffurfiol yn gweithio
reviewOverview.informal.whatItIs      = Rydych yn gallu gofyn i Asiantaeth y Swyddfa Brisio (VO) i adolygu band Treth Gyngor eich cartref.
reviewOverview.informal.whatFor       = Rydych yn gallu gofyn am adolygiad anffurfiol os ydych yn meddwl bod eich cartref yn y band Treth Gyngor anghywir.
reviewOverview.informal.when.label    = Pryd rydych yngallu gofyn am adolygiad anffurfiol
reviewOverview.informal.when.value    = Rydych yn gallu gofyn am adolygiad band unrhyw bryd.
reviewOverview.informal.duration      = Hyd at 48 wythnos
reviewOverview.informal.evidence      = Bydd y VO ond yn adolygu’r band os ydych yn rhoi tystiolaeth i’w gefnogi
reviewOverview.informal.examples.1    = cyfeiriadau hyd at 5 cartref tebyg sydd mewn band is na’ch un chi. Dylai’r cartrefi fod yr un fath â’ch cartref chi o ran math, maint, oedran a steil.
reviewOverview.informal.examples.2    = gall y cartrefi fod naill ai yn yr un stryd neu ystâd, neu yn yr un pentref neu ardal os ydych yn byw yng nghefn gwlad.
reviewOverview.informal.examples.3    = y pris y gwerthwyd eich cartref neu gartrefi tebyg amdano rhwng 1 Ebrill 2001 a 31 Mawrth 2005.
reviewOverview.informal.afterDecision = Mae’r penderfyniad yn derfynol, a dydych chi ddim yn gallu gofyn am adolygiad arall.
reviewOverview.deletion.title = Gofyn am adolygiad ffurfiol
reviewOverview.deletion.description = Mae hyn yn golygu eich bod yn gallu gofyn am adolygiad ffurfiol o’ch band Treth Gyngor. Enw arall am hyn ydy ‘Her Ffurfiol’ neu ‘Gwneud Cynnig’.
reviewOverview.deletion.table.heading = Sut mae adolygiad ffurfiol yn gweithio
reviewOverview.deletion.whatItIs = Rydych yn gallu gofyn i Asiantaeth y Swyddfa Brisio (VO) adolygu’r band Treth Gyngor mae eich cartref ynddo.
reviewOverview.deletion.whatFor = Rydych yn gallu gofyn am adolygiad ffurfiol os ydych yn meddwl bod eich cartref yn y band Treth Gyngor anghywir.
reviewOverview.deletion.when.label = Pryd rydych yn gallu gofyn am adolygiad ffurfiol
reviewOverview.deletion.duration = Hyd at 16 wythnos
reviewOverview.deletion.evidence = Dydych chi ddim angen rhoi unrhyw dystiolaeth. Ond, bydd unrhyw wybodaeth rydych yn ei roi yn helpu’r VO i ddeall pam rydych yn meddwl y dylai’r cartref fod mewn band gwahanol.
reviewOverview.deletion.examples.1 = cyfeiriadau cartrefi tebyg sydd mewn band is na’ch un chi.
reviewOverview.deletion.examples.2 = y pris y gwerthwyd eich cartref neu gartrefi tebyg amdano rhwng 1 Ebrill 2001 a 31 Mawrth 2005.
reviewOverview.deletion.afterDecision = Os nad ydych yn hapus gyda’r canlyniad, mae gennych 4 mis ar ôl dyddiad y penderfyniad i apelio i Dribiwnlys Prisio Cymru.
reviewOverview.deletion.decision.1 = mae eich cartref yn aros yn yr un band
reviewOverview.deletion.decision.2 = mae eich cartref yn symud i fand is
reviewOverview.deletion.decision.3 = mae eich cartref yn symud i fand uwch

# --- councilTaxLiabilityStartDate ---
councilTaxLiabilityStartDate.title                 = Pryd gwnaethoch chi ddod yn gyfrifol am dalu Treth Gyngor am eich cartref?
councilTaxLiabilityStartDate.heading               = Pryd gwnaethoch chi ddod yn gyfrifol am dalu Treth Gyngor am eich cartref?
councilTaxLiabilityStartDate.hint                  = Rydych yn gyfrifol o’r dyddiad wnaethoch brynu, neu ddechrau rhentu, y cartref. Nodwch ddyddiad o fewn y 6 mis diwethaf.
councilTaxLiabilityStartDate.example               = Er enghraifft, os taw’r dyddiad oedd 27 Ebrill 2025, rhowch 27 4 2025
councilTaxLiabilityStartDate.checkYourAnswersLabel = Y dyddiad y daethoch yn gyfrifol am Dreth Gyngor
councilTaxLiabilityStartDate.error.required.all    = Nodwch y dyddiad y daethoch yn gyfrifol am dalu Treth Gyngor
councilTaxLiabilityStartDate.error.required.two    = Rhaid i''r dyddiad y daethoch yn gyfrifol am dalu Treth Gyngor gynnwys {0} a {1}
councilTaxLiabilityStartDate.error.required        = Rhaid i''r dyddiad y daethoch yn gyfrifol am dalu Treth Gyngor gynnwys {0}
councilTaxLiabilityStartDate.error.invalid         = Nodwch ddyddiad dilys y daethoch yn gyfrifol am dalu Treth Gyngor
councilTaxLiabilityStartDate.error.range           = Nodwch ddyddiad o fewn y 6 mis diwethaf
councilTaxLiabilityStartDate.change.hidden         = Y dyddiad y daethoch yn gyfrifol am dalu Treth Gyngor

# --- dateNotice ---
dateNotice.title                 = Pa ddyddiad sydd ar lythyr yr Hysbysiad o Addasiad?
dateNotice.heading               = Pa ddyddiad sydd ar lythyr yr Hysbysiad o Addasiad?
dateNotice.hint                  = Er enghraifft, os taw’r dyddiad ar lythyr yr Hysbysiad o Addasiad yw 27 Ebrill 2025, rhowch 27 4 2025.
dateNotice.checkYourAnswersLabel = DateNotice
dateNotice.error.required.all    = Nodwch y dyddiad sydd wedi''i nodi ar yr Hysbysiad o Addasiad
dateNotice.error.invalid         = Nodwch ddyddiad go iawn
dateNotice.error.future          = Mae''n rhaid i''r dyddiad fod heddiw neu yn y gorffennol
dateNotice.error.sixMonths       = Mae''n rhaid bod yr Hysbysiad o Addasiad wedi''i gyhoeddi o fewn y 6 mis diwethaf
dateNotice.error.required.two    = Rhaid i ddyddiad yr hysbysiad gynnwys {0} a {1}
dateNotice.error.required        = Rhaid i ddyddiad yr hysbysiad gynnwys {0}

# --- whyBandIsWrong ---
whyBandIsWrong.title                     = Dywedwch wrthym pam rydych yn meddwl bod y band Treth Gyngor yn anghywir
whyBandIsWrong.heading                   = Dywedwch wrthym pam rydych yn meddwl bod y band Treth Gyngor yn anghywir?
whyBandIsWrong.evidenceRequirement       = Bydd eich band ond yn cael adolygiad os ydych yn rhoi tystiolaeth i gefnogi hyn.
whyBandIsWrong.intro                     = Bydd y dystiolaeth rydych yn ei roi yn cael ei gwirio cyn bod achos yn cael ei agor. Dylech roi cymaint o fanylion â phosib.
whyBandIsWrong.evidenceDetails           = Pa dystiolaeth y gallwch ei chynnwys?
whyBandIsWrong.evidenceListTitle         = Gall tystiolaeth gynnwys:
whyBandIsWrong.evidenceExample.1.england = cyfeiriadau hyd at 5 cartref sy’n debyg i’ch cartref o ran math, maint, oedran, cynllun a steil, sydd mewn band is. Dylai’r cartrefi fod ar yr un stryd neu stad, neu yn yr un pentref neu ardal.
whyBandIsWrong.evidenceExample.1.wales   = cyfeiriadau hyd at 5 cartref sy’n debyg i’ch cartref o ran math, maint, oedran, cynllun a steil, sydd mewn band is. Dylai’r cartrefi fod ar yr un stryd neu stad, neu yn yr un pentref neu ardal.
whyBandIsWrong.evidenceExample.2.england = y pris y gwerthwyd eich cartref neu gartrefi tebyg rhwng 1 Ebrill 2001 a 31 Mawrth 2005.
whyBandIsWrong.evidenceExample.2.wales   = y pris y gwerthwyd eich cartref neu gartrefi tebyg rhwng 1 Ebrill 2001 a 31 Mawrth 2005.
whyBandIsWrong.provideEvidence           = Sut i roi eich tystiolaeth
whyBandIsWrong.textareaGuidance          = Rydych yn gallu teipio eich tystiolaeth yn y blwch testun. Mae gennych hyd at 5,000 o gymeriadau. Rydych yn gallu uwchlwytho hyd at 5 ffeil, gan gynnwys dogfennau PDF, dogfennau Word, a ffotograffau, ar y dudalen nesaf.
whyBandIsWrong.error.required            = Nodwch y rheswm pam rydych yn credu bod eich band Treth Gyngor yn anghywir
whyBandIsWrong.error.maxLength           = Mae’n rhaid i’r rheswm pam rydych yn credu bod eich band Treth Gyngor yn anghywir fod yn 5000 o gymeriadau neu lai
whyBandIsWrong.checkYourAnswersLabel     = Pam rydych yn credu bod y band Treth Gyngor yn anghywir
whyBandIsWrong.change.hidden             = Pam rydych yn credu bod y band Treth Gyngor yn anghywir

##############################################################################
# 6. Deletion journey
##############################################################################

# --- deletionOptions ---
deletionOptions.title                 = Beth sydd wedi newid?
deletionOptions.heading               = Beth sydd wedi newid?
deletionOptions.demolished            = Mae fy eiddo wedi’i ddymchwel, yn adfeiliedig, neu mae gwaith adnewyddu ac atgyweirio sylweddol yn cael eu gwneud i fy eiddo ac ni ellir byw ynddo
deletionOptions.split                 = Mae fy eiddo wedi’i rannu’n sawl eiddo neu wedi’i gyfuno i greu un eiddo mwy
deletionOptions.changed               = Mae’r ffordd y mae fy eiddo yn cael ei ddefnyddio wedi newid
deletionOptions.affected              = Mae newidiadau yn yr ardal leol wedi effeithio ar fy eiddo
deletionOptions.anotherReason         = Rheswm arall
deletionOptions.checkYourAnswersLabel = Beth sydd wedi newid?
deletionOptions.error.required        = Dewiswch yr hyn rydych am ei newid

# --- scenarioOther ---
scenarioOther.title           = Arall
scenarioOther.heading         = Arall
scenarioOther.intro           = Rydych wedi dweud wrthym eich bod chi eisiau adolygu eich band Treth Gyngor <strong>am reswm arall</strong>.
scenarioOther.changeSelection = Os nad yw’r opsiwn hwn yn gywir i chi, rydych yn gallu newid eich dewis.

# --- whatHasChanged ---
whatHasChanged.title                 = Beth sydd wedi newid?
whatHasChanged.heading               = Beth sydd wedi newid?
whatHasChanged.hint                  = Rhowch gymaint o wybodaeth â phosibl.
whatHasChanged.error.required        = Nodwch beth sydd wedi newid
whatHasChanged.error.maxLength       = Nodwch beth sydd wedi newid

# --- whenDidChangeHappen ---
whenDidChangeHappen.title              = Pryd ddigwyddodd y newid hwn?
whenDidChangeHappen.heading            = Pryd ddigwyddodd y newid hwn?
whenDidChangeHappen.hint               = Er enghraifft, os digwyddodd y newid ar 27 Ebrill 2025, rhowch 27 4 2025.
whenDidChangeHappen.error.required.all = Nodwch y dyddiad y digwyddodd y newid
whenDidChangeHappen.error.required.two = Nodwch y dyddiad y digwyddodd y newid
whenDidChangeHappen.error.required     = Nodwch y dyddiad y digwyddodd y newid
whenDidChangeHappen.error.invalid      = Nodwch ddyddiad go iawn
whenDidChangeHappen.error.future       = Mae’n rhaid i’r dyddiad fod yn heddiw neu yn y gorffennol

# --- localAreaChanges ---
localAreaChanges.title           = Pa newidiadau parhaol sydd wedi digwydd yn eich ardal lleol?
localAreaChanges.heading         = Pa newidiadau parhaol sydd wedi digwydd yn eich ardal lleol?
localAreaChanges.hint            = Er enghraifft, mae archfarchnad newydd neu draffordd wedi’i adeiladu, ac rydych yn credu ei bod wedi effeithio ar werth eich cartref. Rhowch gymaint o wybodaeth â phosibl.
localAreaChanges.error.required  = Nodwch fanylion y newidiadau

# --- localAreaChangesDate ---
localAreaChangesDate.error.required.all = Nodwch y dyddiad y digwyddodd y newidiadau
localAreaChangesDate.error.required.two = Nodwch y dyddiad y digwyddodd y newidiadau
localAreaChangesDate.error.required     = Nodwch y dyddiad y digwyddodd y newidiadau
localAreaChangesDate.error.invalid      = Nodwch ddyddiad go iawn
localAreaChangesDate.error.future       = Mae’n rhaid i’r dyddiad fod yn heddiw neu yn y gorffennol

##############################################################################
# 7. Evidence
##############################################################################

# --- hasEvidence ---
hasEvidence.title          = A oes gennych ddogfennau i’w huwchlwytho fel tystiolaeth?
hasEvidence.heading        = A oes gennych ddogfennau i’w huwchlwytho fel tystiolaeth?
hasEvidence.hint           = Gallai hyn gynnwys sgrinluniau o restrau eiddo, cofnodion gwerthu, neu ddogfennau eraill sy’n cefnogi eich cais.
hasEvidence.error.required = Dewiswch ‘Ie’ os oes gennych ddogfennau i’w huwchlwytho fel tystiolaeth

# --- continueWithoutEvidence ---
continueWithoutEvidence.title       = Parhau heb ychwanegu tystiolaeth?
continueWithoutEvidence.heading     = Parhau heb ychwanegu tystiolaeth?
continueWithoutEvidence.body        = Pan fyddwch yn darparu tystiolaeth, gall eich gweithiwr achos adolygu eich cais heb oedi, ac mae’n ein helpu i wneud penderfyniad yn gynt.
continueWithoutEvidence.continue    = Parhau heb dystiolaeth
continueWithoutEvidence.addEvidence = Ychwanegu tystiolaeth

# --- uploadEvidence ---
uploadEvidence.title             = Uwchlwytho tystiolaeth
uploadEvidence.heading           = Uwchlwytho tystiolaeth
uploadEvidence.hint              = Gallwch uwchlwytho ffeiliau JPG, PNG neu PDF, hyd at {0} ffeil i gyd. Rhaid i bob ffeil fod yn llai na 10MB.
uploadEvidence.label             = Uwchlwytho ffeil
uploadEvidence.error.required    = Dewiswch ffeil i’w huwchlwytho
uploadEvidence.error.tooBig      = Rhaid i’r ffeil a ddewiswyd fod yn llai na 10MB
uploadEvidence.error.invalidType = Rhaid i’r ffeil a ddewiswyd fod yn JPG, PNG neu PDF
uploadEvidence.upload            = Uwchlwytho ffeil
uploadEvidence.labelAnother      = Uwchlwytho ffeil arall
uploadEvidence.uploadedCount     = Rydych wedi uwchlwytho {0} o {1} ffeil
uploadEvidence.limitReached      = Rydych wedi uwchlwytho’r uchafswm o {0} ffeil. Dilëwch ffeil os ydych eisiau uwchlwytho un wahanol.
uploadEvidence.success.title     = Llwyddiant
uploadEvidence.success           = Mae {0} wedi’i huwchlwytho
uploadEvidence.error.tooMany     = Gallwch uwchlwytho hyd at 5 ffeil yn unig
uploadEvidence.error.duplicate   = Rydych eisoes wedi uwchlwytho ffeil gyda’r enw hwn

# --- evidence ---
evidence.checkYourAnswers.label = Dogfennau a uwchlwythwyd
evidence.checkYourAnswers.none  = Dim dogfennau wedi’u huwchlwytho
evidence.change.hidden          = dogfennau a uwchlwythwyd

# --- removeEvidence ---
removeEvidence.title          = Ydych chi’n siŵr eich bod eisiau dileu’r ffeil hon?
removeEvidence.heading        = Ydych chi’n siŵr eich bod eisiau dileu {0}?
removeEvidence.error.required = Dewiswch ‘Ie’ os ydych eisiau dileu’r ffeil hon

##############################################################################
# 8. Which band
##############################################################################

# --- whichBand ---
whichBand.title                 = Beth rydych chi’n meddwl y dylai’r band Treth Gyngor fod?
whichBand.heading               = Beth rydych chi’n meddwl y dylai’r band Treth Gyngor fod?
whichBand.hint.england          = Yn Lloegr, mae''r band Treth Gyngor yn seiliedig ar werth yr eiddo ar 1 Ebrill 1991. Band "A" yw''r un isaf a Band "H" yw''r un uchaf. Dysgwch fwy am
whichBand.link.england          = fandiau Treth Gyngor yn Lloegr
whichBand.hint.wales            = Yng Nghymru, mae''r band Treth Gyngor yn seiliedig ar werth yr eiddo ar 1 Ebrill 2003. Band "A" yw''r un isaf a Band "I" yw''r un uchaf. Dysgwch fwy am
whichBand.link.wales            = fandiau Treth Gyngor yng Nghymru
whichBand.bandA                 = Band A
whichBand.bandB                 = Band B
whichBand.bandC                 = Band C
whichBand.bandD                 = Band D
whichBand.bandE                 = Band E
whichBand.bandF                 = Band F
whichBand.bandG                 = Band G
whichBand.bandH                 = Band H
whichBand.bandI                 = Band I
whichBand.notSure               = Dwi ddim yn siŵr
whichBand.link.url              = https://www.gov.uk/guidance/understand-how-council-tax-bands-are-assessed.cy
whichBand.checkYourAnswersLabel = Beth rydych chi’n meddwl y dylai’r band Treth Gyngor fod?
whichBand.error.required        = Dewiswch pa fand yr ydych yn credu y dylai’ch Treth Gyngor fod ynddo

##############################################################################
# 9. Contact details
##############################################################################

# --- councilTaxPayerDetails ---
councilTaxPayerDetails.heading                       = Beth yw manylion cyswllt y person sy’n talu’r Dreth Gyngor ar gyfer yr eiddo hwn?
councilTaxPayerDetails.firstName                     = Enw cyntaf
councilTaxPayerDetails.lastName                      = Cyfenw
councilTaxPayerDetails.email                         = Cyfeiriad e-bost
councilTaxPayerDetails.error.firstName.required      = Nodwch eich enw cyntaf
councilTaxPayerDetails.error.firstName.invalid       = Mae’n rhaid i’r enw cyntaf gynnwys llythrennau, bylchau, cysylltnodau a chollnodau yn unig
councilTaxPayerDetails.error.firstName.length        = Mae’n rhaid i’r enw cyntaf fod yn 100 o gymeriadau neu lai
councilTaxPayerDetails.error.lastName.required       = Nodwch eich enw olaf
councilTaxPayerDetails.error.lastName.invalid        = Mae’n rhaid i’r enw olaf gynnwys llythrennau, bylchau, cysylltnodau a chollnodau yn unig
councilTaxPayerDetails.error.lastName.length         = Mae’n rhaid i’r enw olaf fod yn 100 o gymeriadau neu lai
councilTaxPayerDetails.error.email.required          = Nodwch gyfeiriad e-bost
councilTaxPayerDetails.error.email.invalid           = Nodwch gyfeiriad e-bost yn y fformat cywir, fel enw@enghraifft.com
councilTaxPayerDetails.checkYourAnswers.name         = Enw
councilTaxPayerDetails.checkYourAnswers.name.hidden  = enw y person sy''n talu''r Dreth Gyngor
councilTaxPayerDetails.checkYourAnswers.email        = Cyfeiriad e-bost
councilTaxPayerDetails.checkYourAnswers.email.hidden = cyfeiriad e-bost y person sy''n talu''r Dreth Gyngor

# --- contactDetails ---
contactDetails.heading                                = Beth yw eich manylion cyswllt?
contactDetails.intro                                  = Byddwn ond yn defnyddio''r manylion hyn i gysylltu â chi am eich adolygiad.
contactDetails.subheading1                            = Eich manylion
contactDetails.firstName                              = Enw cyntaf
contactDetails.lastName                               = Enw olaf
contactDetails.email                                  = Cyfeiriad e-bost
contactDetails.phoneNumber                            = Rhif ffôn
contactDetails.subheading2                            = Cyfeiriad ar gyfer gohebiaeth
contactDetails.addressLine1                           = Llinell cyfeiriad 1
contactDetails.addressLine2                           = Llinell cyfeiriad 2 (dewisol)
contactDetails.townOrCity                             = Tref neu ddinas
contactDetails.county                                 = Sir (dewisol)
contactDetails.postcode                               = Cod post
contactDetails.subheading3                            = Sut yr hoffech i ni gysylltu â chi?
contactDetails.subheading3.hint                       = Dewiswch yr holl opsiynau sy’n berthnasol i chi:
contactDetails.error.firstName.required               = Nodwch eich enw cyntaf
contactDetails.error.firstName.invalid                = Mae’n rhaid i’r enw cyntaf gynnwys llythrennau, bylchau, cysylltnodau a chollnodau yn unig
contactDetails.error.firstName.length                 = Mae’n rhaid i’r enw cyntaf fod yn 100 o gymeriadau neu lai
contactDetails.error.lastName.required                = Nodwch eich enw olaf
contactDetails.error.lastName.invalid                 = Mae’n rhaid i’r enw olaf gynnwys llythrennau, bylchau, cysylltnodau a chollnodau yn unig
contactDetails.error.lastName.length                  = Mae’n rhaid i’r enw olaf fod yn 100 o gymeriadau neu lai
contactDetails.error.email.required                   = Nodwch gyfeiriad e-bost
contactDetails.error.email.invalid                    = Nodwch gyfeiriad e-bost yn y fformat cywir, fel enw@enghraifft.com
contactDetails.error.phoneNumber.required             = Nodwch rif ffôn
contactDetails.error.phoneNumber.invalid              = Nodwch rif ffôn dilys
contactDetails.error.phoneNumber.tooShort             = Nodwch o leiaf 10 digid
contactDetails.error.phoneNumber.tooLong              = Nodwch 15 digid neu lai
contactDetails.error.addressLine1.required            = Nodwch linell 1 y cyfeiriad (enw’r adeilad a’r stryd gan amlaf)
contactDetails.error.addressLine1.length              = Mae’n rhaid i linell 1 y cyfeiriad fod yn 80 o gymeriadau neu lai
contactDetails.error.addressLine2.length              = Mae’n rhaid i linell 2 y cyfeiriad fod yn 80 o gymeriadau neu lai
contactDetails.error.townOrCity.required              = Nodwch dref neu ddinas
contactDetails.error.townOrCity.length                = Mae’n rhaid i’r dref neu’r ddinas fod yn 80 o gymeriadau neu lai
contactDetails.error.county.length                    = Mae’n rhaid i linell 2 y cyfeiriad fod yn 80 o gymeriadau neu lai
contactDetails.error.postcode.required                = Nodwch god post, megis AA1 1AA
contactDetails.error.postcode.tooShort                = Nodwch god post go iawn
contactDetails.error.postcode.invalid                 = Nodwch god post go iawn
contactDetails.error.value.required                   = Dewiswch sut yr hoffech i ni gysylltu â chi
contactDetails.checkYourAnswers.name                  = Enw
contactDetails.checkYourAnswers.name.hidden           = enw
contactDetails.checkYourAnswers.email                 = Cyfeiriad e-bost
contactDetails.checkYourAnswers.email.hidden          = cyfeiriad e-bost
contactDetails.checkYourAnswers.phoneNumber           = Rhif ffôn
contactDetails.checkYourAnswers.phoneNumber.hidden    = rhif ffôn
contactDetails.checkYourAnswers.address               = Cyfeiriad ar gyfer gohebiaeth
contactDetails.checkYourAnswers.address.hidden        = cyfeiriad ar gyfer gohebiaeth
contactDetails.checkYourAnswers.addressLine1          = Llinell cyfeiriad 1
contactDetails.checkYourAnswers.addressLine1.hidden   = llinell cyfeiriad 1
contactDetails.checkYourAnswers.addressLine2          = Llinell cyfeiriad 2
contactDetails.checkYourAnswers.addressLine2.hidden   = llinell cyfeiriad 2
contactDetails.checkYourAnswers.townOrCity            = Tref neu ddinas
contactDetails.checkYourAnswers.townOrCity.hidden     = tref neu ddinas
contactDetails.checkYourAnswers.county                = Sir
contactDetails.checkYourAnswers.county.hidden         = sir
contactDetails.checkYourAnswers.postcode              = Cod post
contactDetails.checkYourAnswers.postcode.hidden       = cod post
contactDetails.checkYourAnswers.contactMethods        = Cyswllt a ffefrir
contactDetails.checkYourAnswers.contactMethods.hidden = cyswllt a ffefrir
contactDetails.title                                  = Beth yw eich manylion cyswllt?

# --- contactMethod ---
contactMethod.email = E-bost
contactMethod.phone = Ffôn
contactMethod.post  = Neges destun

##############################################################################
# 10. Check your answers and submission
##############################################################################

# --- checkYourAnswers ---
checkYourAnswers.title                           = Gwiriwch eich atebion
checkYourAnswers.heading                         = Gwiriwch eich atebion
checkYourAnswers.section.propertyDetails         = Manylion yr eiddo
checkYourAnswers.address                         = Cyfeiriad
checkYourAnswers.send.heading                    = Anfon eich cais am adolygiad
checkYourAnswers.send.body                       = Drwy anfon eich cais am adolygiad, rydych yn cadarnhau bod y manylion hyn yn gywir hyd eithaf eich gwybodaeth.
checkYourAnswers.actingFor.heading               = Gweithredu ar ran rhywun sy’n gyfrifol
checkYourAnswers.actingFor.before                = Rhaid i chi anfon ffurflen
checkYourAnswers.actingFor.authorityToAct        = Awdurdod i Weithredu
checkYourAnswers.actingFor.authorityToAct.url    = https://www.gov.uk/guidance/give-authority-to-act-for-council-tax-challenges.cy
checkYourAnswers.actingFor.or                    = neu
checkYourAnswers.actingFor.letterOfAuthority     = Lythyr Awdurdod
checkYourAnswers.actingFor.letterOfAuthority.url = https://www.gov.uk/guidance/give-authority-to-act-for-council-tax-challenges.cy
checkYourAnswers.actingFor.after                 = at y VO o fewn 28 diwrnod cyn y byddwn yn adolygu eich cais.
checkYourAnswers.section.challengeDetails        = Manylion yr her
checkYourAnswers.confirmAndSend                  = Cadarnhau ac anfon
checkYourAnswers.section.councilTaxPayer         = Manylion cyswllt y person sy''n talu''r Dreth Gyngor ar gyfer yr eiddo
checkYourAnswers.section.contactDetails          = Manylion cyswllt
checkYourAnswers.section.preferredContact        = Dulliau cyswllt a ffefrir

# --- requestSent ---
requestSent.title           = Mae eich cais wedi’i anfon
requestSent.heading         = Mae eich cais wedi’i anfon
requestSent.referenceLabel  = Eich cyfeirnod
requestSent.emailSent       = Rydym wedi anfon e-bost atoch i gadarnhau ein bod wedi derbyn eich cais am adolygiad.
requestSent.whatHappensNext = Beth sy’n digwydd nesaf
requestSent.next.1          = Mae eich cais wedi’i anfon at y Swyddfa Brisio (VO).
requestSent.next.2          = Pan fydd eich cais wedi’i gofrestru, byddwch yn derbyn e-bost gyda chyfeirnod achos unigryw o fewn X diwrnod.
requestSent.next.3          = Os bydd angen gwybodaeth ychwanegol arnom, byddwn yn cysylltu â chi.
requestSent.next.4          = Byddwn yn cysylltu â chi drwy [lythyr/e-bost] o fewn X wythnos gyda phenderfyniad ynghylch eich cais.
requestSent.feedback        = Beth oeddech yn ei feddwl o’r gwasanaeth hwn?
requestSent.feedbackTime    = (mae’n cymryd 30 eiliad)

propertyUninhabitable.title = Dewiswch pa un sy’n berthnasol i’ch eiddo
propertyUninhabitable.heading = Dewiswch pa un sy’n berthnasol i’ch eiddo
propertyUninhabitable.derelict = Mae fy eiddo yn adfeiliedig neu mewn cyflwr gwael ac ni ellir byw ynddo
propertyUninhabitable.demolished = Mae fy eiddo wedi’i ddymchwel yn llwyr
propertyUninhabitable.reno = Mae gwaith adeiladu mawr neu waith adnewyddu mawr yn cael ei wneud i fy eiddo ac ni ellir byw ynddo
propertyUninhabitable.partdemo = Mae rhan o fy eiddo wedi’i dymchwel
propertyUninhabitable.checkYourAnswersLabel = Dewiswch pa un sy’n berthnasol i’ch eiddo
propertyUninhabitable.error.required = Dewiswch yr hyn sy’n berthnasol i’ch eiddo

# --- Deletion: derelict or cannot be lived in ---

# --- uninhabitableDetails ---
uninhabitableDetails.title                 = Pam rydych yn meddwl na ellir byw yn yr eiddo?
uninhabitableDetails.heading               = Pam rydych yn meddwl na ellir byw yn yr eiddo?
uninhabitableDetails.fireDamage            = Difrod a achoswyd gan dân
uninhabitableDetails.floodDamage           = Difrod a achoswyd gan lifogydd
uninhabitableDetails.other                 = Arall
uninhabitableDetails.checkYourAnswersLabel = Pam rydych yn meddwl na ellir byw yn yr eiddo
uninhabitableDetails.error.required        = Dewiswch pam yr ydych yn meddwl nad yw eich eiddo yn addas i fyw ynddo
uninhabitableDetails.change.hidden         = Pam rydych yn meddwl na ellir byw yn yr eiddo

# --- uninhabitableDate ---
uninhabitableDate.title                 = Pryd y daeth yr eiddo’n adfeiliedig neu’n anaddas i fyw ynddo?
uninhabitableDate.heading               = Pryd y daeth yr eiddo’n adfeiliedig neu’n anaddas i fyw ynddo?
uninhabitableDate.hint                  = Dyma’r dyddiad cynharaf pan nad oeddech yn gallu byw yn eich eiddo oherwydd cyflwr yr eiddo.
uninhabitableDate.example               = Er enghraifft, os taw 27 Ebrill 2025 oedd y dyddiad, nodwch 27 4 2025.
uninhabitableDate.checkYourAnswersLabel = Y dyddiad y daeth yr eiddo’n adfeiliedig neu’n anaddas i fyw ynddo
uninhabitableDate.error.required.all    = Nodwch y dyddiad pan nad oedd modd i chi fyw yn eich eiddo mwyach
uninhabitableDate.error.required.two    = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
uninhabitableDate.error.required        = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
uninhabitableDate.error.invalid         = Rhowch ddyddiad cywir
uninhabitableDate.error.minimum         = Rhaid i’r dyddiad fod ar ôl 1 Ebrill 1993
uninhabitableDate.error.future          = Rhaid i’r dyddiad fod yn ddyddiad heddiw neu’n ddyddiad yn y gorffennol
uninhabitableDate.change.hidden         = Y dyddiad y daeth yr eiddo’n adfeiliedig neu’n anaddas i fyw ynddo

# --- uninhabitableDateLastOccupied ---
uninhabitableDateLastOccupied.title                 = Pryd y bu rhywun yn byw yn yr eiddo ddiwethaf?
uninhabitableDateLastOccupied.heading               = Pryd y bu rhywun yn byw yn yr eiddo ddiwethaf?
uninhabitableDateLastOccupied.hint                  = Er enghraifft, os taw 27 Ebrill 2025 oedd y dyddiad, nodwch 27 4 2025.
uninhabitableDateLastOccupied.checkYourAnswersLabel = Y dyddiad y bu rhywun yn byw yn yr eiddo ddiwethaf
uninhabitableDateLastOccupied.error.required.all    = Rhowch y dyddiad pan oedd pobl yn byw yn yr eiddo ddiwethaf
uninhabitableDateLastOccupied.error.required.two    = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
uninhabitableDateLastOccupied.error.required        = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
uninhabitableDateLastOccupied.error.invalid         = Rhowch ddyddiad cywir
uninhabitableDateLastOccupied.error.future          = Rhaid i’r dyddiad fod yn ddyddiad heddiw neu’n ddyddiad yn y gorffennol
uninhabitableDateLastOccupied.change.hidden         = Y dyddiad y bu rhywun yn byw yn yr eiddo ddiwethaf

# --- uninhabitableDetailsPlan ---
uninhabitableDetailsPlan.title                 = Beth rydych yn bwriadu ei wneud gyda’r eiddo?
uninhabitableDetailsPlan.heading               = Beth rydych yn bwriadu ei wneud gyda’r eiddo?
uninhabitableDetailsPlan.hint                  = Er enghraifft, dywedwch wrthym os ydych yn bwriadu dymchwel, adnewyddu neu ailadeiladu’r eiddo.
uninhabitableDetailsPlan.checkYourAnswersLabel = Yr hyn rydych yn bwriadu ei wneud gyda’r eiddo
uninhabitableDetailsPlan.error.required        = Nodwch fanylion am yr eiddo
uninhabitableDetailsPlan.error.maxLength       = Nodwch 5,000 o gymeriadau neu lai
uninhabitableDetailsPlan.change.hidden         = Yr hyn rydych yn bwriadu ei wneud gyda’r eiddo

# --- uninhabitableDetailsPlanHabitable ---
uninhabitableDetailsPlanHabitable.title                 = Beth sydd angen i chi ei wneud i wneud yr eiddo yn addas i fyw ynddo? (dewisol)
uninhabitableDetailsPlanHabitable.heading               = Beth sydd angen i chi ei wneud i wneud yr eiddo yn addas i fyw ynddo? (dewisol)
uninhabitableDetailsPlanHabitable.hint                  = Er enghraifft, dywedwch wrthym am unrhyw waith mawr y bydd angen i chi ei wneud er mwyn gallu byw yn yr eiddo eto.
uninhabitableDetailsPlanHabitable.checkYourAnswersLabel = Yr hyn sydd angen i chi ei wneud i wneud yr eiddo yn addas i fyw ynddo
uninhabitableDetailsPlanHabitable.error.maxLength       = Nodwch 5,000 o gymeriadau neu lai
uninhabitableDetailsPlanHabitable.change.hidden         = Yr hyn sydd angen i chi ei wneud i wneud yr eiddo yn addas i fyw ynddo

# --- uninhabitableDetailsPlanPlanningApplication ---
uninhabitableDetailsPlanPlanningApplication.title                 = Ydych chi wedi gwneud unrhyw geisiadau cynllunio ar gyfer yr eiddo? (dewisol)
uninhabitableDetailsPlanPlanningApplication.heading               = Ydych chi wedi gwneud unrhyw geisiadau cynllunio ar gyfer yr eiddo? (dewisol)
uninhabitableDetailsPlanPlanningApplication.hint                  = Er enghraifft, dywedwch wrthym am unrhyw geisiadau, gan gynnwys cyfeirnodau cynllunio, yr ydych wedi’u cael gan eich cyngor lleol.
uninhabitableDetailsPlanPlanningApplication.checkYourAnswersLabel = Ceisiadau cynllunio ar gyfer yr eiddo
uninhabitableDetailsPlanPlanningApplication.error.maxLength       = Nodwch 5,000 o gymeriadau neu lai
uninhabitableDetailsPlanPlanningApplication.change.hidden         = Ceisiadau cynllunio ar gyfer yr eiddo

# --- uninhabitableDetailsOther ---
uninhabitableDetailsOther.title                 = Dywedwch wrthym am eich eiddo adfeiliedig a pham na ellir byw ynddo
uninhabitableDetailsOther.heading               = Dywedwch wrthym am eich eiddo adfeiliedig a pham na ellir byw ynddo
uninhabitableDetailsOther.hint                  = Bydd hyn yn ein helpu i wneud penderfyniad ynghylch eich cais i ddileu band Treth Gyngor eich eiddo.
uninhabitableDetailsOther.body                  = Rhowch fanylion am eich cynlluniau ar gyfer yr eiddo os ydych yn bwriadu ei ddymchwel, ei adnewyddu neu ei ailadeiladu.
uninhabitableDetailsOther.checkYourAnswersLabel = Am yr eiddo adfeiliedig a pham na ellir byw ynddo
uninhabitableDetailsOther.error.required        = Nodwch fanylion am yr eiddo adfeiliedig
uninhabitableDetailsOther.error.maxLength       = Nodwch 5,000 o gymeriadau neu lai
uninhabitableDetailsOther.change.hidden         = Am yr eiddo adfeiliedig a pham na ellir byw ynddo

# --- Deletion: fully demolished ---

# --- demolishedDate ---
demolishedDate.title                 = Pryd cafodd yr eiddo ei ddymchwel?
demolishedDate.heading               = Pryd cafodd yr eiddo ei ddymchwel?
demolishedDate.hint                  = Er enghraifft, os cafodd y gwaith i ddymchwel eich eiddo ei gwblhau ar 27 Ebrill 2026, rhowch 27 4 2026.
demolishedDate.error.required.two    = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
demolishedDate.error.required        = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
demolishedDate.error.invalid         = Rhowch ddyddiad cywir
demolishedDate.error.minimum         = Rhaid i’r dyddiad fod ar ôl 1 Ebrill 1993
demolishedDate.error.future          = Rhaid i’r dyddiad fod yn ddyddiad heddiw neu’n ddyddiad yn y gorffennol

# --- demolishedDetails ---
demolishedDetails.title                 = Rhowch wybodaeth i ni am eich eiddo sydd wedi’i ddymchwel yn llwyr
demolishedDetails.heading               = Rhowch wybodaeth i ni am eich eiddo sydd wedi’i ddymchwel yn llwyr
demolishedDetails.hint                  = Bydd hyn yn ein helpu i wneud penderfyniad.
demolishedDetails.error.maxLength       = Nodwch 5,000 o gymeriadau neu lai

# --- Deletion: major works or renovations ---

# --- renoStartDate ---
renoStartDate.title                 = Pryd y dechreuodd y gwaith mawr neu’r gwaith adnewyddu?
renoStartDate.heading               = Pryd y dechreuodd y gwaith mawr neu’r gwaith adnewyddu?
renoStartDate.hint                  = Dyma’r dyddiad y dechreuodd y gwaith mawr neu’r gwaith adnewyddu ar eich eiddo, ac nad oedd modd i chi fyw ynddo o ganlyniad.
renoStartDate.example               = Er enghraifft, os taw 27 Ebrill 2025 oedd y dyddiad, nodwch 27 4 2025.
renoStartDate.error.required.all    = Nodwch y dyddiad y dechreuodd y gwaith adnewyddu
renoStartDate.error.required.two    = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
renoStartDate.error.required        = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
renoStartDate.error.invalid         = Rhowch ddyddiad cywir
renoStartDate.error.future          = Rhaid i’r dyddiad fod yn ddyddiad heddiw neu’n ddyddiad yn y gorffennol

# --- renoCompletionDate ---
renoCompletionDate.title                 = Beth yw’r dyddiad y cafodd y gwaith ei gwblhau?
renoCompletionDate.heading               = Beth yw’r dyddiad y cafodd y gwaith ei gwblhau?
renoCompletionDate.hint                  = Er enghraifft, os taw 27 Ebrill 2025 oedd y dyddiad, nodwch 27 4 2025.
renoCompletionDate.error.required.all    = Nodwch y dyddiad cwblhau
renoCompletionDate.error.required.two    = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
renoCompletionDate.error.required        = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
renoCompletionDate.error.invalid         = Rhowch ddyddiad cywir
renoCompletionDate.error.future          = Rhaid i’r dyddiad fod yn ddyddiad heddiw neu’n ddyddiad yn y gorffennol
renoCompletionDate.error.beforeStart     = Mae’n rhaid i’r dyddiad cwblhau fod ar ôl y dyddiad dechrau

# --- renoDetails ---
renoDetails.title                 = Dywedwch wrthym am y gwaith mawr neu’r gwaith adnewyddu ar eich eiddo
renoDetails.heading               = Dywedwch wrthym am y gwaith mawr neu’r gwaith adnewyddu ar eich eiddo
renoDetails.hint                  = Er enghraifft, dywedwch wrthym am y newidiadau strwythurol mawr rydych yn eu gwneud i’r eiddo, a pham nad yw’n bosibl byw ynddo. Rhowch gymaint o fanylion â phosibl.
renoDetails.hint.more             = Bydd hyn yn ein helpu i wneud penderfyniad ar eich cais i ddileu band Treth Gyngor eich eiddo.
renoDetails.error.required        = Nodwch fanylion am y gwaith adnewyddu
renoDetails.error.maxLength       = Nodwch 5,000 o gymeriadau neu lai

# --- reviewOverview: major works or renovations ---
reviewOverview.reno.title           = Gofyn am adolygiad ffurfiol
reviewOverview.reno.intro           = Rydych wedi dweud wrthym fod <strong>gwaith mawr neu waith adnewyddu yn cael ei wneud ar eich eiddo ac nad oes modd byw ynddo</strong>.
reviewOverview.reno.description     = Mae hyn yn golygu eich bod yn gallu gofyn i’r Swyddfa Brisio dynnu eich eiddo oddi ar restr y Dreth Gyngor. Gelwir hyn yn ‘Adolygiad Ffurfiol’ neu ‘Gwneud Cynnig’.
reviewOverview.reno.table.heading   = Sut mae adolygiad ffurfiol yn gweithio
reviewOverview.reno.whatItIs        = Rydych yn gallu gofyn i’r Swyddfa Brisio dynnu eich eiddo oddi ar restr y Dreth Gyngor.
reviewOverview.reno.whatFor         = Bydd hyn yn dileu band Treth Gyngor eich eiddo, ac ni fydd angen i chi dalu Treth Gyngor i’ch awdurdod lleol.
reviewOverview.reno.important       = <p class="govuk-body">Pan fydd y newidiadau strwythurol a’r gwaith adnewyddu wedi’u cwblhau, bydd angen i chi wneud cais i ychwanegu’r eiddo yn ôl at Restr y Dreth Gyngor.</p><p class="govuk-body">Pan fydd eiddo’n cael ei ychwanegu’n ôl at Restr y Dreth Gyngor, mae’n bosibl y bydd yn cael yr un band ag o’r blaen, band is neu fand uwch. Bydd hyn yn dibynnu ar brisiad yr eiddo bryd hynny.</p>
reviewOverview.reno.when.value      = Dim ond ar ôl i’r gwaith strwythurol neu’r gwaith adnewyddu ddechrau y gellir cyflwyno’r cais.
reviewOverview.reno.duration        = Hyd at 16 wythnos
reviewOverview.reno.evidence        = <p class="govuk-body">Pan fyddwch yn gwneud cais i ni ddileu band Treth Gyngor eich eiddo, byddwn yn gofyn i chi ddarparu tystiolaeth.</p><p class="govuk-body">Pan fyddwch yn darparu tystiolaeth, gall eich gweithiwr achos adolygu eich cais heb oedi, ac mae’n ein helpu i wneud penderfyniad yn gynt.</p><p class="govuk-body">Os bydd angen tystiolaeth ychwanegol ar weithiwr achos, byddan nhw’n cysylltu â chi’n uniongyrchol i ofyn amdani. Gall hyn arafu’r broses adolygu.</p>
reviewOverview.reno.examples.intro  = <p class="govuk-body">Gallwch lanlwytho dogfennau neu luniau sy’n dangos <strong>bod gwaith mawr neu waith adnewyddu yn cael ei wneud ar eich eiddo</strong>.</p><p class="govuk-body">Gall y dystiolaeth gynnwys:</p>
reviewOverview.reno.examples.1      = anfonebau neu dderbynebau gan gontractwyr neu gyflenwyr sy’n cadarnhau pryd y dechreuodd y gwaith ar yr eiddo;
reviewOverview.reno.examples.2      = copi o unrhyw arolygon, cynlluniau pensaer neu ddogfennau caniatâd cynllunio;
reviewOverview.reno.examples.3      = ffotograffau o bob ystafell fewnol yn yr eiddo;
reviewOverview.reno.examples.4      = ffotograffau sy’n dangos y to a thu allan yr eiddo o bob ochr;
reviewOverview.reno.examples.5      = dogfen Cwmpas y Gwaith sy’n dangos y cynlluniau manwl ar gyfer y gwaith sy’n cael ei wneud
reviewOverview.reno.examples.outro  = <p class="govuk-body">Byddwch hefyd yn cael cyfle i roi unrhyw wybodaeth neu fanylion ychwanegol yr ydych yn meddwl sy’n bwysig.</p>
reviewOverview.reno.photoEvidence   = <p class="govuk-body">Dylai ffotograffau fod yn ffotograffau digidol gwreiddiol. Peidiwch â lanlwytho sgrinluniau na lluniau sydd wedi’u hychwanegu at ddogfen neu gyflwyniad.</p><p class="govuk-body">You can upload photos in JPG or PNG format. Each file must be smaller than 10MB.</p>
reviewOverview.reno.decision.intro  = Mae dau ganlyniad posibl:
reviewOverview.reno.afterDecision   = Mae gennych 4 mis o ddyddiad y penderfyniad i apelio i’r Tribiwnlys Prisio os nad ydych yn fodlon â’r canlyniad.

# --- Deletion: part of the property demolished ---

# --- partDemoDate ---
partDemoDate.title                 = Pryd cafodd rhan o’r eiddo ei dymchwel?
partDemoDate.heading               = Pryd cafodd rhan o’r eiddo ei dymchwel?
partDemoDate.hint                  = Er enghraifft, os mai 27 Ebrill 2025 oedd y dyddiad, rhowch 27 4 2025.
partDemoDate.error.required.all    = Nodwch y dyddiad dymchwel
partDemoDate.error.required.two    = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
partDemoDate.error.required        = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
partDemoDate.error.invalid         = Rhowch ddyddiad cywir
partDemoDate.error.future          = Rhaid i’r dyddiad fod yn ddyddiad heddiw neu’n ddyddiad yn y gorffennol

# --- partDemoDetails ---
partDemoDetails.title                 = Rhowch fanylion am y gwaith a ddechreuwyd ar yr eiddo a’r hyn y mae’r gwaith yn ei gynnwys
partDemoDetails.heading               = Rhowch fanylion am y gwaith a ddechreuwyd ar yr eiddo a’r hyn y mae’r gwaith yn ei gynnwys
partDemoDetails.error.required        = Nodwch fanylion y gwaith dymchwel
partDemoDetails.error.maxLength       = Nodwch 5,000 o gymeriadau neu lai

# --- Deletion: change of use, now used as a home ---

# --- changeOfUse ---
changeOfUse.title                 = Sut mae defnydd yr eiddo wedi newid?
changeOfUse.heading               = Sut mae defnydd yr eiddo wedi newid?
changeOfUse.domestic              = Mae rhan o’r eiddo neu’r eiddo cyfan bellach yn cael ei ddefnyddio fel cartref
changeOfUse.business              = Mae’r eiddo bellach yn cael ei ddefnyddio fel busnes
changeOfUse.error.required        = Dewiswch opsiwn

# --- domesticParts ---
domesticParts.title                 = Pa rannau o’r eiddo sy’n cael eu defnyddio fel cartref erbyn hyn?
domesticParts.heading               = Pa rannau o’r eiddo sy’n cael eu defnyddio fel cartref erbyn hyn?
domesticParts.hint                  = Er enghraifft, rydych wedi newid rhan o gaffi yn lle i fyw. Rhowch gymaint o wybodaeth â phosibl.
domesticParts.error.required        = Nodwch pa rannau sydd bellach yn cael eu defnyddio fel cartref
domesticParts.error.maxLength       = Nodwch 5,000 o gymeriadau neu lai

# --- domesticDate ---
domesticDate.title                 = Pryd ddaeth eich eiddo, neu ran o’ch eiddo, yn gartref i chi?
domesticDate.heading               = Pryd ddaeth eich eiddo, neu ran o’ch eiddo, yn gartref i chi?
domesticDate.hint                  = Er enghraifft, os daeth rhan o’ch eiddo yn gartref i chi ar 27 Ebrill 2025, rhowch 27 4 2025.
domesticDate.error.required.all    = Nodwch y dyddiad y digwyddodd y newid
domesticDate.error.required.two    = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
domesticDate.error.required        = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
domesticDate.error.invalid         = Rhowch ddyddiad cywir
domesticDate.error.future          = Rhaid i’r dyddiad fod yn ddyddiad heddiw neu’n ddyddiad yn y gorffennol

# --- addressChanged ---
addressChanged.title                 = Ydy cyfeiriad eich cartref wedi newid?
addressChanged.heading               = Ydy cyfeiriad eich cartref wedi newid?
addressChanged.hint                  = Rhowch fanylion unrhyw gyfeiriadau newydd isod
addressChanged.error.required        = Dewiswch a yw’r cyfeiriad wedi newid
addressChanged.error.maxLength       = Nodwch 5,000 o gymeriadau neu lai

# --- businessParts ---
businessParts.title                 = Pa rannau o’ch eiddo sy’n cael eu defnyddio ar gyfer busnes?
businessParts.heading               = Pa rannau o’ch eiddo sy’n cael eu defnyddio ar gyfer busnes?
businessParts.hint                  = Er enghraifft, rydych wedi trosi rhan o’r llawr gwaelod yn salon trin gwallt neu gaffi, ac rwyt ti’n dal i fyw yng ngweddill y cartref. Rhowch gymaint o wybodaeth â phosibl.
businessParts.error.required        = Nodwch pa rannau sy’n cael eu defnyddio at ddibenion busnes
businessParts.error.maxLength       = Nodwch 5,000 o gymeriadau neu lai

# --- businessDate ---
businessDate.title                 = Pryd ddechreuodd eich cartref, neu ran o’ch cartref, gael ei ddefnyddio at ddibenion busnes?
businessDate.heading               = Pryd ddechreuodd eich cartref, neu ran o’ch cartref, gael ei ddefnyddio at ddibenion busnes?
businessDate.hint                  = Er enghraifft, os wnaethoch ddechrau defnyddio rhan o’ch cartref at ddibenion busnes ar 27 Ebrill 2025, rhowch 27 4 2025.
businessDate.error.required.all    = Nodwch y dyddiad y digwyddodd y newid
businessDate.error.required.two    = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
businessDate.error.required        = Rhaid i’r dyddiad gynnwys dydd, mis a blwyddyn
businessDate.error.invalid         = Rhowch ddyddiad cywir
businessDate.error.future          = Rhaid i’r dyddiad fod yn ddyddiad heddiw neu’n ddyddiad yn y gorffennol

# --- propertySplitOrMerge ---
propertySplitOrMerge.title                 = Sut mae’r eiddo wedi’i rannu neu ei gyfuno?
propertySplitOrMerge.heading               = Sut mae’r eiddo wedi’i rannu neu ei gyfuno?
propertySplitOrMerge.merged                = Mae’r eiddo wedi’i gyfuno ag un eiddo arall neu fwy i greu eiddo mwy
propertySplitOrMerge.split                 = Mae’r eiddo wedi’i rannu’n ddau eiddo ar wahân neu fwy i greu eiddo llai
propertySplitOrMerge.checkYourAnswersLabel = Sut mae’r eiddo wedi’i rannu neu ei gyfuno?
propertySplitOrMerge.error.required        = Dewiswch opsiwn
propertySplitOrMerge.change.hidden         = sut mae’r eiddo wedi’i rannu neu ei gyfuno

# --- propertyMergedDetails ---
# title, heading and hint are the supplied translation; the rest are adapted from it and need confirming
propertyMergedDetails.title                 = Sut mae eich eiddo wedi eu huno?
propertyMergedDetails.heading               = Sut mae eich eiddo wedi eu huno?
propertyMergedDetails.hint                  = Er enghraifft, mae dau fflat wedi eu huno’n un cartref. Rhowch gymaint o wybodaeth â phosibl.
propertyMergedDetails.checkYourAnswersLabel = Sut mae eich eiddo wedi eu huno
propertyMergedDetails.error.required        = Nodwch fanylion y cyfuno eiddo
propertyMergedDetails.error.maxLength       = Nodwch 5,000 o gymeriadau neu lai
propertyMergedDetails.change.hidden         = sut mae eich eiddo wedi eu huno

# --- propertyMergedDate ---
# title, heading and hint are the supplied translation; the rest are adapted from it and need confirming
propertyMergedDate.title                 = Pryd cafodd y gwaith i uno eich eiddo ei gwblhau?
propertyMergedDate.heading               = Pryd cafodd y gwaith i uno eich eiddo ei gwblhau?
propertyMergedDate.hint                  = Er enghraifft, os cafodd y gwaith ei gwblhau ar 27 Ebrill 2025, nodwch 27 4 2025.
propertyMergedDate.checkYourAnswersLabel = Pryd cafodd y gwaith i uno eich eiddo ei gwblhau
propertyMergedDate.error.required.all    = Nodwch ddyddiad cwblhau’r cyfuno eiddo
propertyMergedDate.error.required.two    = Rhaid i’r dyddiad y cafodd y gwaith ei gwblhau gynnwys {0} a {1}
propertyMergedDate.error.required        = Rhaid i’r dyddiad y cafodd y gwaith ei gwblhau gynnwys {0}
propertyMergedDate.error.invalid         = Nodwch ddyddiad go iawn
propertyMergedDate.error.future          = Mae’n rhaid i’r dyddiad fod yn heddiw neu yn y gorffennol
propertyMergedDate.change.hidden         = pryd cafodd y gwaith i uno eich eiddo ei gwblhau

# --- propertySplitDetails ---
propertySplitDetails.title                  = Sut mae’r eiddo wedi’i rannu?
propertySplitDetails.heading                = Sut mae’r eiddo wedi’i rannu?
propertySplitDetails.hint                   = Er enghraifft, mae eich cartref wedi’i rannu’n ddwy fflat neu fwy. Rhowch gymaint o fanylion â phosibl.
propertySplitDetails.checkYourAnswersLabel  = Sut mae’r eiddo wedi’i rannu
propertySplitDetails.error.required         = Nodwch fanylion gwahanu’r eiddo
propertySplitDetails.error.maxLength        = Nodwch 5,000 o gymeriadau neu lai
propertySplitDetails.change.hidden          = sut mae’r eiddo wedi’i rannu

# --- propertySplitDate ---
propertySplitDate.title                 = Pryd cafodd y gwaith i rannu eich cartref ei gwblhau?
propertySplitDate.heading               = Pryd cafodd y gwaith i rannu eich cartref ei gwblhau?
propertySplitDate.hint                  = Er enghraifft, os cafodd y gwaith ei gwblhau ar 27 Ebrill 2025, rhowch 27 4 2025.
propertySplitDate.checkYourAnswersLabel = Pryd cafodd y gwaith i rannu eich cartref ei gwblhau
propertySplitDate.error.required.all    = Nodwch ddyddiad gwahanu’r eiddo
propertySplitDate.error.required.two    = Rhaid i’r dyddiad y cafodd y gwaith ei gwblhau gynnwys {0} a {1}
propertySplitDate.error.required        = Rhaid i’r dyddiad y cafodd y gwaith ei gwblhau gynnwys {0}
propertySplitDate.error.invalid         = Nodwch ddyddiad go iawn
propertySplitDate.error.future          = Mae’n rhaid i’r dyddiad fod yn heddiw neu yn y gorffennol
propertySplitDate.change.hidden         = pryd cafodd y gwaith i rannu eich cartref ei gwblhau

##############################################################################
# TODO translate: English text stays here until the Welsh is supplied, so a Welsh
# page never shows a raw message key. Move each line to its page section when translated.
##############################################################################
propertyDetail.band.expand                    = Council Tax bands England
propertyDetail.courtCode.meaning              = Court codes indicate the outcome of a tribunal or court review of the Council Tax band.
requestCouncilTaxBandReview.change.hidden         = RequestCouncilTaxBandReview
reasonForReview.checkYourAnswersLabel     = Why do you want to review your Council Tax band?
reasonForReview.change.hidden             = ReasonForReview
scenarioResponsible.change.hidden         = scenarioResponsible
noticeAlteration.change.hidden         = noticeAlteration
reviewOverview.row.important          = Important
reviewOverview.row.photoEvidence      = Photo evidence
reviewOverview.deletion.intro           = You have told us that there have been changes made to your home or significant changes made in the local area that affect the value of your home.
reviewOverview.deletion.when.value      = You can ask for a formal review if there have been changes made to your home or the local area. For example:<ul class="govuk-list govuk-list--bullet"><li>all or part of your home has been demolished</li><li>it has been split into new homes</li><li>it has been merged into a larger, single home</li><li>something in the local area has changed that has affected the value of your home</li></ul>
reviewOverview.deletion.decision.intro  = There are 4 possible decisions that can be made:
reviewOverview.deletion.decision.4      = your home is deleted from the Council Tax list
reviewOverview.derelict.title           = Asking for a formal review
reviewOverview.derelict.intro           = You have told us that <strong>your property is derelict and cannot be lived in</strong>.
reviewOverview.derelict.description     = This means that you can ask for your property to be removed from the Council Tax list. This is known as a ‘Formal Challenge’ or ‘Making a Proposal’
reviewOverview.derelict.table.heading   = How a formal review works
reviewOverview.derelict.whatItIs        = You can ask the Valuation Office to delete your property from the Council Tax list.
reviewOverview.derelict.whatFor         = This will delete your property’s Council Tax band, and you will not be required to pay Council Tax to your Local Council.
reviewOverview.derelict.important       = <p class="govuk-body">In the future, if you make improvements to the property and the property can be lived in, you will need to apply to have the property added to the Council Tax List.</p><p class="govuk-body">When a property comes back into the Council Tax list, it could come back in at the same, lower or higher band than before. This will depend on the property valuation at that time.</p>
reviewOverview.derelict.when.label      = When you can ask for a formal review
reviewOverview.derelict.when.value      = The request can only be submitted after the derelict property could not be lived in.
reviewOverview.derelict.duration        = Up to 16 weeks
reviewOverview.derelict.evidence        = <p class="govuk-body">When you make a request for us to delete your property’s Council Tax band, we ask you for evidence.</p><p class="govuk-body">When you provide evidence, your caseworker can review your request without delays, and it helps us make a decision sooner.</p><p class="govuk-body">If a caseworker requires additional evidence, they will contact you directly to ask for this. This can slow the review process down.</p>
reviewOverview.derelict.examples.intro  = <p class="govuk-body">You can upload documents or photos that show <strong>your property is having major works or renovations done</strong>.</p><p class="govuk-body">Evidence can include:</p>
reviewOverview.derelict.examples.1      = copy of any structural surveys or reports
reviewOverview.derelict.examples.2      = details of any planning applications made or planning permission documents
reviewOverview.derelict.examples.3      = photos of all internal rooms in the property
reviewOverview.derelict.examples.4      = photos showing the roof and outside of the property from all sides
reviewOverview.derelict.examples.5      = A Schedule of Condition document that includes detailed inspection report noting any defects and damage.
reviewOverview.derelict.examples.outro  = <p class="govuk-body">You will also be given the opportunity to tell us any additional information or details that you think is important.</p>
reviewOverview.derelict.photoEvidence   = <p class="govuk-body">Photographs should be original digital photos. Do not upload screenshots or photos that have been added to a document or presentation.</p><p class="govuk-body">You can upload photos in JPG or PNG format. Each file must be smaller than 10MB.</p>
reviewOverview.derelict.decision.intro  = There are 2 possible outcomes:
reviewOverview.derelict.decision.1      = your property will be removed from the Council Tax list
reviewOverview.derelict.decision.2      = your property will stay on the Council Tax list if we do not receive the evidence we need, or if your request does not meet the criteria
reviewOverview.derelict.afterDecision   = You have 4 months after the date of the decision to appeal to the Valuation Tribunal if you are not satisfied with the outcome
dateNotice.change.hidden         = DateNotice
whatHasChanged.checkYourAnswersLabel = Details of what has changed
whatHasChanged.change.hidden         = details of what has changed
whenDidChangeHappen.error.tooEarly        = The date your property was demolished or became uninhabitable must be between 1 April 1993 and today
whenDidChangeHappen.checkYourAnswersLabel = When the change happened
whenDidChangeHappen.change.hidden         = when the change happened
localAreaChanges.error.maxLength       = Details of the changes must be 5000 characters or fewer
localAreaChanges.checkYourAnswersLabel = Permanent changes in your local area
localAreaChanges.change.hidden         = permanent changes in your local area
localAreaChangesDate.title                 = When did these permanent changes take place?
localAreaChangesDate.heading               = When did these permanent changes take place?
localAreaChangesDate.hint                  = For example, if the changes in your local area happened on 27 April 2025, enter 27 4 2025.
localAreaChangesDate.error.tooEarly        = The date your property was demolished or became uninhabitable must be between 1 April 1993 and today
localAreaChangesDate.checkYourAnswersLabel = When the permanent changes took place
localAreaChangesDate.change.hidden         = when the permanent changes took place
deletionOptions.change.hidden         = deletionOptions
whichBand.change.hidden         = WhichBand
councilTaxPayerDetails.title                         = What are the contact details for the property’s Council Tax payer?
propertyUninhabitable.change.hidden = PropertyUninhabitable
demolishedDate.checkYourAnswersLabel = Date the property was demolished
demolishedDate.error.required.all    = Enter the date the property was demolished
demolishedDate.change.hidden         = Date the property was demolished
demolishedDetails.checkYourAnswersLabel = About the fully demolished property
demolishedDetails.error.required        = Enter details about the fully demolished property
demolishedDetails.change.hidden         = About the fully demolished property
reviewOverview.demolished.title           = Asking for a formal review
reviewOverview.demolished.intro           = You have told us that your <strong>property has been demolished completely and cannot be lived in</strong>.
reviewOverview.demolished.description     = This means that you can ask for your property to be removed from the Council Tax list. This is known as a ‘Formal Challenge’ or ‘Making a Proposal’
reviewOverview.demolished.table.heading   = How a formal review works
reviewOverview.demolished.whatItIs        = You can ask the Valuation Office to remove your demolished property from the Council Tax list.
reviewOverview.demolished.whatFor         = This will delete your property’s Council Tax band, and you will not be required to pay Council Tax to your Local Council.
reviewOverview.demolished.important       = <p class="govuk-body">If your later build a new property, you will need to apply to have this added to the Council Tax List.</p><p class="govuk-body">When a property comes back into the Council Tax list it will banded as a new property.</p><p class="govuk-body">When valuing the property, we will consider all improvements made. This means your Council Tax band may be the same, lower or a higher band than before.</p>
reviewOverview.demolished.when.label      = When you can ask for a formal review
reviewOverview.demolished.when.value      = The request can only be submitted after the demolition of the property has been completed.
reviewOverview.demolished.duration        = Up to 16 weeks
reviewOverview.demolished.evidence        = <p class="govuk-body">When you make a request for us to delete your property’s Council Tax band, we ask you for evidence.</p><p class="govuk-body">When you provide evidence, your caseworker can review your request without delays, and it helps us make a decision sooner.</p><p class="govuk-body">If a caseworker requires additional evidence, they will contact you directly to ask for this.</p>
reviewOverview.demolished.examples.intro  = <p class="govuk-body">You can upload documents or photos that show your property has been demolished.</p><p class="govuk-body">Evidence can include:</p>
reviewOverview.demolished.examples.1      = original digital photos of the demolished property, clearly showing the location of where the property was.
reviewOverview.demolished.examples.2      = dated copy of the contract or invoice from the company who completed the demolition.
reviewOverview.demolished.examples.3      = copy of the section 80/81 Notice of Demolition
reviewOverview.demolished.examples.4      = any other document or receipt that shows the date that the demolition was completed
reviewOverview.demolished.examples.outro  = <p class="govuk-body">You will also be given the opportunity to tell us any additional information or details that you think is important.</p>
reviewOverview.demolished.photoEvidence   = <p class="govuk-body">Photographs should be original digital photos. Do not upload screenshots or photos that have been added to a document or presentation.</p><p class="govuk-body">You can upload photos in JPG or PNG format. Each file must be smaller than 10MB.</p>
reviewOverview.demolished.decision.intro  = There are 2 possible outcomes:
reviewOverview.demolished.decision.1      = your property will be removed from the Council Tax list
reviewOverview.demolished.decision.2      = your property will stay on the Council Tax list if we do not receive the evidence we need, or if your request does not meet the criteria
reviewOverview.demolished.afterDecision   = You have 4 months after the date of the decision to appeal to the Valuation Tribunal if you are not satisfied with the outcome
renoStartDate.checkYourAnswersLabel = Date the major works or renovation started
renoStartDate.change.hidden         = Date the major works or renovation started
renoCompletionDate.checkYourAnswersLabel = Date the work completed
renoCompletionDate.change.hidden         = Date the work completed
renoDetails.checkYourAnswersLabel = About the major works or renovation to the property
renoDetails.change.hidden         = About the major works or renovation to the property
reviewOverview.reno.when.label      = When you can ask for a formal review
reviewOverview.reno.decision.1      = your property will be removed from the Council Tax list
reviewOverview.reno.decision.2      = your property will stay on the Council Tax list if we do not receive the evidence we need, or if your request does not meet the criteria
partDemoDate.checkYourAnswersLabel = Date part of the property was demolished
partDemoDate.change.hidden         = Date part of the property was demolished
partDemoDetails.checkYourAnswersLabel = Details of the demolition works
partDemoDetails.change.hidden         = Details of the demolition works
reviewOverview.partdemo.title           = Asking for a formal review
reviewOverview.partdemo.intro           = You have told us that your <strong>property has been partially demolished and cannot be lived in</strong>.
reviewOverview.partdemo.description     = This means that you can ask for your property to be removed from the Council Tax list. This is known as a ‘Formal Challenge’ or ‘Making a Proposal’
reviewOverview.partdemo.table.heading   = How a formal review works
reviewOverview.partdemo.whatItIs        = You can ask the Valuation Office to remove your partially demolished property from the Council Tax list.
reviewOverview.partdemo.whatFor         = This will delete your property’s Council Tax band, and you will not be required to pay Council Tax to your Local Council.
reviewOverview.partdemo.important       = <p class="govuk-body">If your later rebuild this property, you will need to apply to have this added to the Council Tax List.</p><p class="govuk-body">When the property comes back into the Council Tax list it will banded as a new property.</p><p class="govuk-body">When valuing the property, we will consider all improvements made. This means your Council Tax band may be the same, lower or a higher band than before.</p>
reviewOverview.partdemo.when.label      = When you can ask for a formal review
reviewOverview.partdemo.when.value      = The request can only be submitted after the demolition work has started.
reviewOverview.partdemo.duration        = Up to 16 weeks
reviewOverview.partdemo.evidence        = <p class="govuk-body">When you make a request for us to delete your property’s Council Tax band, we ask you for evidence.</p><p class="govuk-body">When you provide evidence, your caseworker can review your request without delays, and it helps us make a decision sooner.</p><p class="govuk-body">If a caseworker requires additional evidence, they will contact you directly to ask for this.</p>
reviewOverview.partdemo.examples.intro  = <p class="govuk-body">You can upload documents or photos that show that <strong>your property has been partially demolished</strong>.</p><p class="govuk-body">Evidence can include:</p>
reviewOverview.partdemo.examples.1      = the date that part of the property was demolished
reviewOverview.partdemo.examples.2      = original digital photos of the property, clearly showing which parts of the property have been demolished
reviewOverview.partdemo.examples.3      = dated copy of the contract or invoice from the company who completed the demolition.
reviewOverview.partdemo.examples.outro  = <p class="govuk-body">You will also be given the opportunity to tell us any additional information or details that you think is important.</p>
reviewOverview.partdemo.photoEvidence   = <p class="govuk-body">Photographs should be original digital photos. Do not upload screenshots or photos that have been added to a document or presentation.</p><p class="govuk-body">You can upload photos in JPG or PNG format. Each file must be smaller than 10MB.</p>
reviewOverview.partdemo.decision.intro  = There are 2 possible outcomes:
reviewOverview.partdemo.decision.1      = your property will be removed from the Council Tax list
reviewOverview.partdemo.decision.2      = your property will stay on the Council Tax list if we do not receive the evidence we need, or if your request does not meet the criteria
reviewOverview.partdemo.afterDecision   = You have 4 months after the date of the decision to appeal to the Valuation Tribunal Wales if you are not satisfied with the outcome.
changeOfUse.checkYourAnswersLabel = How the use of the property has changed
changeOfUse.change.hidden         = How the use of the property has changed
domesticParts.checkYourAnswersLabel = Parts of the property now used as a home
domesticParts.change.hidden         = Parts of the property now used as a home
domesticDate.checkYourAnswersLabel = Date the property became your home
domesticDate.change.hidden         = Date the property became your home
addressChanged.checkYourAnswersLabel = Whether the address of your home has changed
addressChanged.change.hidden         = Whether the address of your home has changed
domesticDate.error.minimum = Rhaid i’r dyddiad y cafodd eich eiddo ei ddymchwel neu y daeth yn anaddas i fyw ynddo fod rhwng 1 Ebrill 1993 a heddiw
businessDate.error.minimum = Rhaid i’r dyddiad y cafodd eich eiddo ei ddymchwel neu y daeth yn anaddas i fyw ynddo fod rhwng 1 Ebrill 1993 a heddiw
businessParts.checkYourAnswersLabel = Parts of the home used for business
businessParts.change.hidden         = Parts of the home used for business
businessDate.checkYourAnswersLabel = Date the home changed to business use
businessDate.change.hidden         = Date the home changed to business use
propertyMergedDate.error.minimum = Rhaid i’r dyddiad fod rhwng 1 Ebrill 1993 a heddiw
propertySplitDate.error.minimum = Rhaid i’r dyddiad fod rhwng 1 Ebrill 1993 a heddiw
checkYourAnswers.submissionError.title = Roedd problem wrth anfon eich cais
checkYourAnswers.submissionError.body = Nid yw eich cais wedi cael ei anfon ac mae eich atebion wedi cael eu cadw. Dewiswch ''Cadarnhau ac anfon'' i roi cynnig arall arni.
