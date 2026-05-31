## How to test http4k unreleased changes

#### Step 1 - (in http4k) Make changes and deploy to local maven repository:

```bash
./gradlew publishToMavenLocal
```


#### Step 2 - (in this repo) Update this project to use the local version

```bash
./sync-libs.sh
```

This will copy the local version of http4k to the libs directory so they can be deployed to heroku


#### Step 3 - Make sure any changes to this project are committed!


#### Step 4 - Deploy to heroku:

```bash
./deploy_adhoc.sh
```


#### Step 5 - Run tests

Relying Party:

```bash
CONFORMANCE_API_TOKEN="<redacted>" \                    
CONFORMANCE_PLAN_ID="6aJ57GEWsAPJk" \
CONFORMANCE_RELYING_PARTY_BASE_URI="https://http4k-oidc.herokuapp.com" \
./gradlew :test-coordinator:conformanceTestsRelyingParty
```

Auth server:

```bash
CONFORMANCE_API_TOKEN="<redacted>" \ 
CONFORMANCE_PLAN_ID="kC7xtqyd0M3Hf" \
CONFORMANCE_RELYING_PARTY_BASE_URI="https://http4k-oidc.herokuapp.com" \
./gradlew :test-coordinator:conformanceTestsAuthServer
```