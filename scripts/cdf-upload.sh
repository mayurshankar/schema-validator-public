#!/bin/bash

CDF_INSTANCE_ID=

CDF_REGION=us-west1## Get OAuth token and CDAP endpoint

export AUTH_TOKEN=$(gcloud auth print-access-token)

export CDAP_ENDPOINT=$(gcloud beta data-fusion instances describe \
    --location=${CDF_REGION} \
    --format="value(apiEndpoint)" \

  ${CDF_INSTANCE_ID})## Print all artifacts (USER and SYSTEM)
curl -s -X GET -H "Authorization: Bearer ${AUTH_TOKEN}" \
   "${CDAP_ENDPOINT}/v3/namespaces/default/artifacts" \
   | jq '.[] | .name + ", " + .version + ", " + .scope'