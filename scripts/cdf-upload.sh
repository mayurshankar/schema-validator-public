#!/bin/bash



print_artifacts () {
  ## Print all artifacts (USER and SYSTEM)
  curl -s -X GET -H "Authorization: Bearer ${AUTH_TOKEN}" \
     "${CDAP_ENDPOINT}/v3/namespaces/default/artifacts" \
     | jq '.[] | .name + ", " + .version + ", " + .scope'

  echo ${CDAP_ENDPOINT}
}

upload_artifacts () {
# Variables
CDF_INSTANCE_ID=schema-validation
CDF_REGION=europe-west2

## Get OAuth token and CDAP endpoint
export AUTH_TOKEN=$(gcloud auth print-access-token)
export CDAP_ENDPOINT=$(gcloud beta data-fusion instances describe \
    --location=${CDF_REGION} \
    --format="value(apiEndpoint)" \
  ${CDF_INSTANCE_ID})

  curl -w"\n" -X POST -H "Authorization: Bearer ${AUTH_TOKEN}" \
       "${CDAP_ENDPOINT}/v3/namespaces/default/artifacts/schema-validator" \
       -H "Artifact-Extends: system:cdap-data-pipeline[6.8.0, 7.0.0-SNAPSHOT)/system:cdap-data-streams[6.8.0, 7.0.0-SNAPSHOT)" \
       --data-binary @../target/schema-validator-1.2.0-SNAPSHOT.jar

}

upload_cdap () {
  curl -w"\n" -X POST "localhost:11015/v3/namespaces/Test/artifacts/schema-validator" \
         -H "Artifact-Extends: system:cdap-data-pipeline[6.8.0, 7.0.0-SNAPSHOT)/system:cdap-data-streams[6.8.0, 7.0.0-SNAPSHOT)" \
         --data-binary @../target/schema-validator-1.2.0-SNAPSHOT.jar

  curl -w"\n" -X PUT "localhost:11015/v3/namespaces/Test/artifacts/schema-validator/versions/1.2.0-SNAPSHOT/properties" -d
          "{\n  \"outputs\": [{\n    \"name\": \"schema\",\n    \"widget-type\": \"schema\",\n    \"label\": \"schema\"\n  }],\n  \"metadata\": {\"spec-version\": \"1.0\"},\n  \"configuration-groups\": [{\n    \"label\": \"Schema Validator\",\n    \"properties\": [{\n      \"widget-type\": \"textbox\",\n      \"name\": \"field\",\n      \"label\": \"Path name to schema\",\n      \"widget-attributes\": {\n        \"default\": \"Format: \\\"/path/schema.json\\\"\",\n        \"width\": \"large\"\n      }\n    }]\n  }],\n  \"emit-errors\": true\n}"
}

retrieve-properties () {
  curl -s -X GET "localhost:11015/v3/namespaces/default/artifacts/wrangler/4.9.2/properties??"
}



#print_artifacts
#upload_artifacts
upload_cdap
#retrieve-properties