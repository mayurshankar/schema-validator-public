#!/bin/bash

download-schema () {
	#bq show --schema --format=json playpen-e3d014:test_source_data.Integers > /Users/mayur.shankar/Documents/schema-validator/src/test/resources/bq-schemas/Integers-Schema.json
	
	bq show --schema --format=json playpen-e3d014:test_source_data.Integers > /Users/mayur.shankar/Documents/schema-validator/src/test/resources/schemas/bigquery/test_source_data/Integers.json
}

upload-schema () {
  bq load --source_format=NEWLINE_DELIMITED_JSON playpen-e3d014:data_schemas.Schema1 /Users/mayur.shankar/Documents/schema-validator/src/test/resources/schemas/bigquery/test_source_data/Integers.jsonl mode:STRING,type:STRING,name:STRING

}

#download-schema
upload-schema
