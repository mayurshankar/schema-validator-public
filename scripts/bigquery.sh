#!/bin/bash

project_id="playpen-fbeec3"

download-schema () {
	#bq show --schema --format=json playpen-e3d014:test_source_data.Integers > /Users/mayur.shankar/Documents/schema-validator/src/test/resources/bq-schemas/Integers-Schema.json
	
	bq show --schema --format=json $project_id:test_source_data.Integers > /Users/mayur.shankar/Documents/schema-validator/src/test/resources/schemas/bigquery/test_source_data/Integers.json
}

upload-schema () {
  bq load --source_format=NEWLINE_DELIMITED_JSON $project_id:data_schemas.Schema1 /Users/mayur.shankar/Documents/schema-validator/src/test/resources/schemas/bigquery/test_source_data/Integers.jsonl mode:STRING,type:STRING,name:STRING

}

upload-data () {
  bq load --autodetect --source_format=CSV $project_id:input_dataset.ingested_data ../src/test/resources/local-source/source-int.csv
}

#download-schema
#upload-schema
upload-data
