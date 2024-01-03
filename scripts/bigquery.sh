#!/bin/bash

download-schema () {
	#bq show --schema --format=json playpen-e3d014:test_source_data.Integers > /Users/mayur.shankar/Documents/schema-validator/src/test/resources/bq-schemas/Integers-Schema.json
	
	bq show --schema --format=json playpen-e3d014:test_source_data.Integers > /Users/mayur.shankar/Documents/schema-validator/src/test/resources/schemas/bigquery/test_source_data/Integers.json
}

download-schema
