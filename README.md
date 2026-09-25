# council-tax-ufs-frontend

Frontend for the Council Tax Unaligned Filing Service (UFS) — allows citizens
to check their Council Tax band and submit a challenge if the details are wrong.

## Running locally

### Prerequisites

- JDK 11+
- sbt 1.9+
- MongoDB (via `sm2 --start MONGO` or Docker)
- HMRC Service Manager (`sm2`)

### Start dependencies

```bash
sm2 --start UFS_ALL
```

### Start the service

```bash
sbt run
```

Then visit `http://localhost:60002/council-tax-ufs/postcode-search` (port from `conf/application.conf`).
The service root, `http://localhost:60002/council-tax-ufs`, redirects to this first journey page.

## Testing

```bash
sbt clean test                                                    # unit tests
sbt clean it/test                                                 # integration tests
sbt "scalafmtOnly filepath"                                       # format scala code
example: "scalafmtOnly app/models/PropertyDetailResult.scala "
sbt clean coverage test coverageReport                            # code coverage with test cases
```

## Adding new pages

This service uses `hmrc-frontend-scaffold.g8` scaffolds. To add a new page:

```bash
sbt
> g8Scaffold yesNoPage       # or stringPage, intPage, radioButtonPage, datePage, checkboxPage
> exit
./migrate.sh
```

## Feature flags

Toggle features via `application.conf`:

- `features.propertyDetailsJourney` — Release-2 property details Journey

## License

This code is open source software licensed under the Apache 2.0 License.