#!/bin/bash

# Variables
CDF_INSTANCE_ID=schema-validation
CDF_REGION=europe-west2

## Get OAuth token and CDAP endpoint
export AUTH_TOKEN=$(gcloud auth print-access-token)
export CDAP_ENDPOINT=$(gcloud beta data-fusion instances describe \
    --location=${CDF_REGION} \
    --format="value(apiEndpoint)" \
  ${CDF_INSTANCE_ID})

print_artifacts () {
  ## Print all artifacts (USER and SYSTEM)
  curl -s -X GET -H "Authorization: Bearer ${AUTH_TOKEN}" \
     "${CDAP_ENDPOINT}/v3/namespaces/default/artifacts" \
     | jq '.[] | .name + ", " + .version + ", " + .scope'

  echo ${CDAP_ENDPOINT}
}

upload_artifacts () {

  curl -w"\n" -X POST -H "Authorization: Bearer ${AUTH_TOKEN}" \
       "${CDAP_ENDPOINT}/v3/namespaces/default/artifacts/schema-validator" \
       -H "Artifact-Extends: system:cdap-data-pipeline[6.8.0, 7.0.0-SNAPSHOT)/system:cdap-data-streams[6.8.0, 7.0.0-SNAPSHOT)" \
       --data-binary @../target/schema-validator-1.2.0-SNAPSHOT.jar
}

print_artifacts
upload_artifacts