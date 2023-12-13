package schema.validator;

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Macro;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.cdap.api.data.format.StructuredRecord;
import io.cdap.cdap.api.data.schema.Schema;
import io.cdap.cdap.api.plugin.PluginConfig;
import io.cdap.cdap.etl.api.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * Schema Validator Plugin
 * @author mayur.shankar
 */
@Plugin(type = Transform.PLUGIN_TYPE)
@Name("SchemaValidator") // <- The name of the plugin should match the name of the docs and widget json files.
@Description("Schema Validator Plugin")
public class SchemaValidatorPlugin extends Transform<StructuredRecord, StructuredRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(SchemaValidatorPlugin.class);
    private final Config config;
    private Schema outputSchema;

    // Create list of records that will be dynamically updated
    // For valid records
    private static final ArrayList<Object> validRecordList = new ArrayList<>();

    // For invalid records
    private static final ArrayList<Object> invalidRecordList = new ArrayList<>();

    // Record error message
    private static String errorMsg = "";

    public SchemaValidatorPlugin(Config config) {
        this.config = config;
    }

    /**
     * This function is called when the pipeline is published. You should use this for validating the config and setting
     * additional parameters in pipelineConfigurer.getStageConfigurer(). Those parameters will be stored and will be made
     * available to your plugin during runtime via the TransformContext. Any errors thrown here will stop the pipeline
     * from being published.
     * Used to retrieve schema from local storage.
     * @param pipelineConfigurer Configures an ETL Pipeline. Allows adding datasets and streams and storing parameters
     * @throws IllegalArgumentException If the config is invalid.
     */
    @Override
    public void configurePipeline(PipelineConfigurer pipelineConfigurer) throws IllegalArgumentException {
        super.configurePipeline(pipelineConfigurer);
        /* It's usually a good idea to validate the configuration at this point.
         * It will stop the pipeline from being published if this throws an error.
         */

        //Schema inputSchema = pipelineConfigurer.getStageConfigurer().getInputSchema();
        //config.validate(inputSchema);

        // GCS WIP
        /*
        Storage storage = StorageOptions.newBuilder().setProjectId("playpen-223970").build().getService();
        Blob blob = storage.get(BlobId.of("schema-bk", "int-schema.json"));

        String jsonSchemaString = new String(blob.getContent());
        */

        Schema oschema;

        try {
            BufferedReader br = new BufferedReader(new FileReader(config.schemaPath));

            String jsonSchemaString = br.lines().collect(Collectors.joining());

            // Removes all whitespace
            jsonSchemaString = jsonSchemaString.replaceAll("\\s", "");

            // Remove first two lines
            jsonSchemaString = jsonSchemaString.replaceAll("\\[\\{\"name\":\"etlSchemaBody\",\"schema\":","");

            // Remove last two characters
            jsonSchemaString = jsonSchemaString.substring(0, jsonSchemaString.length() - 2);

            System.out.println("jsonschema:" + jsonSchemaString);

            // Finally parses schema
            oschema = Schema.parseJson(jsonSchemaString);
            outputSchema = oschema;

        } catch (IOException e) {
            throw new RuntimeException("Error" + e);
        }

        // Sets output schema
        pipelineConfigurer.getStageConfigurer().setOutputSchema(oschema);
    }

    /**
     * This function is called when the pipeline has started. The values configured in here will be made available to the
     * transform function. Use this for initializing costly objects and opening connections that will be reused.
     * Used to set the output schema.
     * @param context Context for a pipeline stage, providing access to information about the stage, metrics, and plugins.
     * @throws Exception If there are any issues before starting the pipeline.
     */
    @Override
    public void initialize(TransformContext context) throws Exception {
        super.initialize(context);
        //outputSchema = Schema.parseJson(config.schema);
        Schema inputSchema = Schema.parseJson(config.schema);
        //System.out.println(config.schema);

        outputSchema = context.getOutputSchema();
        //System.out.println(outputSchema);

        // Use only for testing framework
        //outputSchema = getOutputSchema(config, inputSchema);

    }

    /**
     * This is the method that is called for every record in the pipeline and allows you to make any transformations
     * you need and emit one or more records to the next stage.
     * Validates data types.
     * @param input The record that is coming into the plugin
     * @param emitter An emitter allowing you to emit one or more records to the next stage
     * @throws Exception
     */
    @Override
    public void transform(StructuredRecord input, Emitter<StructuredRecord> emitter) throws Exception {
        // Get all the fields that are in the output schema
        List<Schema.Field> fields = outputSchema.getFields();

        // Create a builder for creating the output records
        StructuredRecord.Builder builder = StructuredRecord.builder(outputSchema);
        // Create a builder for creating the error records
        StructuredRecord.Builder error = StructuredRecord.builder(input.getSchema());

        // Clear lists and error messages after each transformed row
        validRecordList.clear();
        invalidRecordList.clear();
        errorMsg = "";

        // Create schema list to store data types
        ArrayList<String> inputSchema = new ArrayList<>();

        int i = 0;
        for (Schema.Field fd : fields) {
            if (fd.getSchema().getLogicalType() == null) {
                inputSchema.add(fd.getSchema().toString().toLowerCase().replace("\"", ""));
            }
            else {
                inputSchema.add(fd.getSchema().getLogicalType().toString().toLowerCase().replace("\"", ""));
            }
            System.out.println("Logical type:" + fd.getSchema().getLogicalType());
            System.out.println("Type:" + fd.getSchema().getType());
            LOG.info(fd.getSchema().toString());
            System.out.println("Input schema:" + inputSchema.get(i));
            i++;
        }

        // Schema list iterator
        int iterator = 0;

        // Add all the values to the builder
        for (Schema.Field field : fields) {

            String name = field.getName();

            if (input.get(name) != null) {

            // Comparing fields for schema validation
            /*
            1. Establish a list of fields and data types from GCS schema bucket
            2. Use a for loop to compare each field of the raw data to schema data types
               Can use built-in Java functions for thi
            3. Records that pass the validation should be emitted
            */

                // Validates numbers
                if (inputSchema.get(iterator).matches("int|float|double|long")) {
                    System.out.println("int herestart");
                    System.out.println(inputSchema.get(iterator));
                    numberTryParse(input.get(name), inputSchema.get(iterator));
                }

                // Validates strings
                else if (inputSchema.get(iterator).equals("string")) {
                    stringTryParse(input.get(name));
                }

                // Validates booleans
                else if (inputSchema.get(iterator).equals("boolean")) {
                    booleanTryParse(input.get(name));
                }

                // Validates byte arrays
                else if (inputSchema.get(iterator).equals("bytes")) {
                    System.out.println("has reached");
                    byteTryParse(input.get(name));
                }

                // Validates simple dates
                else if (inputSchema.get(iterator).equals("date")) {
                    simpleDateTryParse(input.get(name));
                    System.out.println("here1");
                }

                // Validates timestamps
                else if (inputSchema.get(iterator).matches("timestamp_micros|timestamp_millis")) {
                    LOG.info("timestamp reached");
                    System.out.println("timestamp reached");
                    timestampTryParse(input.get(name), inputSchema.get(iterator));

                    System.out.println("done");
                }

                else if (inputSchema.get(iterator).matches("time_micros|time_millis")) {
                    timeTryParse(input.get(name), inputSchema.get(iterator));
                }

                System.out.println("Current record " + validRecordList.get(iterator));
                iterator++;
            }
        }

        int result = setRecords();

        int rt = 0;
        // No errors
        if (result == 1) {
            while (rt < fields.size()) {
                System.out.println("Success" + fields.get(rt).getName() + "|" + validRecordList.get(rt));
                builder.set(fields.get(rt).getName(), validRecordList.get(rt));
                rt++;
            }
        }
        else if (result == 2) {
            while (rt < fields.size()) {
                System.out.println("Invalid" + fields.get(rt).getName() + "|" + validRecordList.get(rt));
                System.out.println(fields.get(rt).getSchema());
                error.set(fields.get(rt).getName(), validRecordList.get(rt).toString());
                rt++;
            }
        }

        // If you wanted to make additional changes to the output record, this might be a good place to do it.

        if (!invalidRecordList.isEmpty()) {
            InvalidEntry<StructuredRecord> invalidEntry = new InvalidEntry<>(1, errorMsg, error.build());
            emitter.emitError(invalidEntry);
        }

        else {
            // Finally, build and emit the record.
            emitter.emit(builder.build());
        }
    }

    /** Sets a custom output schema for testing framework
     * @param config config
     * @param inputSchema input schema
     * @return returns field names and record values
     */
    private static Schema getOutputSchema(Config config, Schema inputSchema) {
        List<Schema.Field> fields = new ArrayList<>();

        fields.add(Schema.Field.of("name", Schema.of(Schema.Type.STRING)));
        fields.add(Schema.Field.of("age", Schema.of(Schema.Type.INT)));
        //fields.add(Schema.Field.of("date", Schema.of(Schema.Type.STRING)));

        return Schema.recordOf(inputSchema.getRecordName(), fields);
    }

    /** Determines whether to emit a success or error record
     */
    public static int setRecords() {

        if (invalidRecordList.isEmpty()) {
            System.out.println("empty");
            return 1;
        }

        else {
            System.out.println("If outputted, all good");
            return 2;
        }
    }

    /**
     * Parsing method for numbers
     * @param recordValue Record value
     * @param recordType Record datatype
     */
    public static void numberTryParse (String recordValue, String recordType) {

        System.out.println("Record type: " + recordType);
        switch (recordType) {
            case "int":
                try {
                    Integer intValue = Integer.parseInt(recordValue);
                    validRecordList.add(intValue);
                    System.out.println("Int: " + intValue);

                } catch (Exception e) {
                    invalidRecordList.add(recordValue);
                    validRecordList.add(recordValue);

                    errorMsg = errorMsg + recordValue + " doesn't match schema type (INT)\n";
                    System.out.println(errorMsg);
                    System.out.print("Exception:" + e);

                }
                break;

            case "float":
                try {
                    Float floatValue = Float.parseFloat(recordValue);
                    validRecordList.add(floatValue);

                    System.out.println("Float: " + floatValue);
                }
                catch (Exception e) {
                    invalidRecordList.add(recordValue);
                    validRecordList.add(recordValue);

                    errorMsg = errorMsg + recordValue + "doesn't match schema type (FLOAT)\n";

                    System.out.print("Exception:" + e);
                }
                break;

            case "double":
                try {
                    Double doubleValue = Double.parseDouble(recordValue);
                    validRecordList.add(doubleValue);

                }
                catch (Exception e) {
                    invalidRecordList.add(recordValue);
                    validRecordList.add(recordValue);

                    errorMsg = errorMsg + recordValue + " doesn't match schema type (DOUBLE)\n";

                    System.out.print("Exception:" + e);
                }
                break;

            case "long":
                try {
                    Long longValue = Long.parseLong(recordValue);
                    System.out.println("Long: " + longValue);
                    validRecordList.add(longValue);

                }
                catch (Exception e) {
                    invalidRecordList.add(recordValue);
                    validRecordList.add(recordValue);

                    errorMsg = errorMsg + recordValue + " doesn't match schema type (LONG)\n";
                    System.out.println(errorMsg);

                    System.out.print("Exception:" + e);
                }
                break;
        }
    }

    /** Parsing method for strings
     * @param recordValue Record value
     */
    public static void stringTryParse (String recordValue) {

        validRecordList.add(recordValue);
    }

    /** Parsing method for byte array
     * @param recordValue Record value
     */
    public static void byteTryParse (String recordValue) {
        byte[] byteValue = recordValue.getBytes();
        validRecordList.add(recordValue);
    }

    /** Parsing method for simple date
     * @param recordValue Record value
     */
    public static void simpleDateTryParse (String recordValue) {

        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            formatter.setLenient(false);
            Date date = formatter.parse(recordValue);

            ZonedDateTime zonedDateTime = ZonedDateTime.from(date.toInstant().atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)));

            // Calculate number of days since epoch
            Long daysLong = ChronoUnit.DAYS.between(Instant.EPOCH, zonedDateTime);
            Integer daysInt = daysLong.intValue();

            validRecordList.add(daysInt);
            System.out.println(zonedDateTime);
        }
        catch (DateTimeParseException e) {
            LOG.warn("Date Parse Exception (DATETIME PARSE): " + e);
            invalidRecordList.add(recordValue);
            validRecordList.add(recordValue);

            errorMsg = errorMsg + recordValue + " doesn't match schema type (DATE)\n";

        }
        catch (ParseException e) {
            LOG.warn("Date Parse Exception (PARSE): " + e);
            invalidRecordList.add(recordValue);
            validRecordList.add(recordValue);

            errorMsg = errorMsg + recordValue + " doesn't match schema type (DATE)\n";
        }
    }

    /** Parsing method for timestamps
     * @param recordValue Record value
     * @param recordType Record type
     */
    public static void timestampTryParse (String recordValue, String recordType) {

        // Trim any whitespace
        recordValue = recordValue.trim();

        switch (recordType) {
            case "timestamp_millis":

                try {

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");
                    LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(recordValue));

                    Timestamp timestamp = Timestamp.valueOf(localDateTime);

                    Long millisLong = ChronoUnit.MILLIS.between(Instant.EPOCH, timestamp.toInstant());

                    validRecordList.add(millisLong);
                    LOG.info("Timestamp millis: " + millisLong);

                }
                catch (DateTimeParseException e) {
                    LOG.warn("Timestamp Parse Millis Exception (DATETIME PARSE): " + e);
                    invalidRecordList.add(recordValue);
                    validRecordList.add(recordValue);

                    errorMsg = errorMsg + recordValue + " doesn't match schema type (TIMESTAMP_MILLIS)\n";
                }

                break;

            case "timestamp_micros":

                try {
                    System.out.println(recordValue);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSSSSS");
                    LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(recordValue));

                    Timestamp timestamp = Timestamp.valueOf(localDateTime);

                    Long microsLong = ChronoUnit.MICROS.between(Instant.EPOCH, timestamp.toInstant());

                    System.out.println(timestamp);
                    validRecordList.add(microsLong);
                    LOG.info("Timestamp micros: " + microsLong);
                }
                catch (DateTimeParseException e) {
                    LOG.warn("Timestamp Micros Parse Exception (DATETIME PARSE): " + e);
                    invalidRecordList.add(recordValue);
                    validRecordList.add(recordValue);

                    errorMsg = errorMsg + recordValue + " doesn't match schema type (TIMESTAMP_MICROS)\n";
                }
                break;
        }
    }

    /** Parsing method for time
     * @param recordValue Record value
     * @param recordType Record Type
     */
    public static void timeTryParse (String recordValue, String recordType) {

        switch (recordType) {
            case "time_micros":
                try {
                    LocalTime localTime = LocalTime.parse(recordValue);

                    long timeValueMicros = localTime.toNanoOfDay() / 1000;

                    System.out.println("Result" + timeValueMicros);
                    validRecordList.add(timeValueMicros);
                    System.out.println("Result2" + timeValueMicros);
                    //System.out.println(validRecordList.get(3));
                    LOG.info("Time micros: " + timeValueMicros);
                }
                catch (DateTimeParseException e) {
                    LOG.warn("Time Micros Parse Exception: " + e);
                    invalidRecordList.add(recordValue);
                    validRecordList.add(recordValue);

                    errorMsg = errorMsg + recordValue + " doesn't match schema type (TIME_MICROS)\n";
                    System.out.println(errorMsg);
                }
                break;

            case "time_millis":
                try {
                    LocalTime localTime = LocalTime.parse(recordValue);

                    long timeValueMillis = localTime.toNanoOfDay() / 1000000;

                    validRecordList.add(timeValueMillis);
                    LOG.info("Time millis: " + timeValueMillis);
                }
                catch (DateTimeParseException e) {
                    LOG.warn("Time Millis Parse Exception: " + e + "\n");
                    invalidRecordList.add(recordValue);
                    validRecordList.add(recordValue);

                    errorMsg = errorMsg + recordValue + " doesn't match schema type (TIME_MILLIS)\n";
                }
                break;
        }
    }


    /** Parsing method for booleans
     * @param recordValue Record value
     */
    public static void booleanTryParse (String recordValue) {

        recordValue = recordValue.toLowerCase();

        if (recordValue.equals("true") || recordValue.equals("false")) {
            Boolean booleanValue = Boolean.valueOf(recordValue);
            System.out.println("Boolean parsed as: " + booleanValue);
            validRecordList.add(booleanValue);
        }

        else {
            validRecordList.add(recordValue);
            invalidRecordList.add(recordValue);

            errorMsg = errorMsg + recordValue + " doesn't match schema type (BOOLEAN)\n";
        }
    }

    /**
     * This function will be called at the end of the pipeline. You can use it to clean up any variables or connections.
     */
    @Override
    public void destroy() {
        // No Op
    }

    /**
     * Your plugin's configuration class. The fields here will correspond to the fields in the UI for configuring the
     * plugin.
     */
    public static class Config extends PluginConfig {
        @Name("myOption")
        @Description("This option is required for this transform.")
        @Macro // <- Macro means that the value will be substituted at runtime by the user.
        private final String schemaPath;

        @Name("myOptionalOption")
        @Description("And this option is not.")
        @Macro
        @Nullable // <- Indicates that the config param is optional
        private final Integer myOptionalOption;

        @Name("schema")
        @Description("Specifies the schema of the records outputted from this plugin.")
        private final String schema;

        public Config(String schemaPath, Integer myOptionalOption, String schema) {
            this.schemaPath = schemaPath;
            this.myOptionalOption = myOptionalOption;
            this.schema = schema;
        }

        private void validate(Schema inputSchema) throws IllegalArgumentException {
            // It's usually a good idea to check the schema. Sometimes users edit
            // the JSON config directly and make mistakes.
            try {
                Schema.parseJson(schema);
            } catch (IOException e) {
                throw new IllegalArgumentException("Output schema cannot be parsed.", e);
            }
            // This method should be used to validate that the configuration is valid.
            if (schemaPath == null || schemaPath.isEmpty()) {
                throw new IllegalArgumentException("myOption is a required field.");
            }
            // You can use the containsMacro() function to determine if you can validate at deploy time or runtime.
            // If your plugin depends on fields from the input schema being present or the right type, use inputSchema
        }
    }
}


