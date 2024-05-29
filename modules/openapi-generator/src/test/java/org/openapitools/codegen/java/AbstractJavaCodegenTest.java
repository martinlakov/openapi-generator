/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
 * Copyright 2018 SmartBear Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openapitools.codegen.java;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.*;

import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.core.models.ParseOptions;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

import java.util.stream.Collectors;

import org.mockito.Answers;
import org.mockito.Mockito;
import org.openapitools.codegen.*;
import org.openapitools.codegen.languages.AbstractJavaCodegen;
import org.openapitools.codegen.utils.ModelUtils;

import org.testng.annotations.BeforeMethod;


import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractJavaCodegenTest {

    static final Map<String, OpenAPI> FLATTENED_SPEC = Map.of(
        "3_0/petstore", TestUtils.parseFlattenSpec("src/test/resources/3_0/petstore.yaml"),
        "3_0/mapSchemas", TestUtils.parseFlattenSpec("src/test/resources/3_0/mapSchemas.yaml"),
        "3_0/spring/date-time-parameter-types-for-testing", TestUtils.parseFlattenSpec("src/test/resources/3_0/spring/date-time-parameter-types-for-testing.yml")
    );
    
    private AbstractJavaCodegen codegen;
    
    /**
     * In TEST-NG, test class (and its fields) is only constructed once (vs. for every test in Jupiter),
     * using @BeforeMethod to have a fresh codegen mock for each test
     */
    @BeforeMethod void mockAbstractCodegen() {
        codegen = Mockito.mock(
            AbstractJavaCodegen.class, Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS).useConstructor()
        );        
    }

    @Test
    public void toEnumVarNameShouldNotShortenUnderScore() {
        assertEquals(codegen.toEnumVarName("_", "String"), "UNDERSCORE");
        assertEquals(codegen.toEnumVarName("__", "String"), "__");
        assertEquals(codegen.toEnumVarName("_,.", "String"), "__");
    }

    /**
     * As of Java 9, '_' is a keyword, and may not be used as an identifier.
     */
    @Test
    public void toEnumVarNameShouldNotResultInSingleUnderscore() {
        assertEquals(codegen.toEnumVarName(" ", "String"), "SPACE");
        assertEquals(codegen.toEnumVarName("==", "String"), "u");
    }

    @Test
    public void toEnumVarNameAddUnderscoresIfValueIsPascalCase() {
        assertEquals(codegen.toEnumVarName("OnlyCamelCase", "String"), "ONLY_CAMEL_CASE");
        assertEquals(codegen.toEnumVarName("WithNumber1", "String"), "WITH_NUMBER1");
        assertEquals(codegen.toEnumVarName("_LeadingUnderscore", "String"), "_LEADING_UNDERSCORE");
    }

    @Test
    public void toVarNameShouldAvoidOverloadingGetClassMethod() {
        assertEquals(codegen.toVarName("class"), "propertyClass");
        assertEquals(codegen.toVarName("_class"), "propertyClass");
        assertEquals(codegen.toVarName("__class"), "propertyClass");
    }

    @Test
    public void toModelNameShouldNotUseProvidedMapping() {
        codegen.importMapping().put("json_myclass", "com.test.MyClass");
        assertEquals(codegen.toModelName("json_myclass"), "JsonMyclass");
    }

    @Test
    public void toModelNameUsesPascalCase() {
        assertEquals(codegen.toModelName("json_anotherclass"), "JsonAnotherclass");
    }

    @Test
    public void testPreprocessOpenApiIncludeAllMediaTypesInAcceptHeader() {
        final OpenAPI openAPI = FLATTENED_SPEC.get("3_0/petstore");
        codegen.preprocessOpenAPI(openAPI);

        assertEquals(codegen.getArtifactVersion(), openAPI.getInfo().getVersion());

        Object xAccepts = openAPI.getPaths().get("/pet").getPost().getExtensions().get("x-accepts");
        assertTrue(xAccepts instanceof String[]);
        assertTrue(List.of((String[]) xAccepts).containsAll(List.of("application/json", "application/xml")));
    }

    @Test
    public void testPreprocessOpenAPINumVersion() {
        final OpenAPI openAPIOtherNumVersion = TestUtils.parseFlattenSpec("src/test/resources/2_0/duplicateOperationIds.yaml");
        
        codegen.preprocessOpenAPI(openAPIOtherNumVersion);

        assertEquals(codegen.getArtifactVersion(), openAPIOtherNumVersion.getInfo().getVersion());
    }

    @Test
    public void convertVarName() {
        assertEquals(codegen.toVarName("name"), "name");
        assertEquals(codegen.toVarName("$name"), "$name");
        assertEquals(codegen.toVarName("nam$$e"), "nam$$e");
        assertEquals(codegen.toVarName("user-name"), "userName");
        assertEquals(codegen.toVarName("user_name"), "userName");
        assertEquals(codegen.toVarName("user|name"), "userName");
        assertEquals(codegen.toVarName("uSername"), "uSername");
        assertEquals(codegen.toVarName("USERname"), "usERname");
        assertEquals(codegen.toVarName("USERNAME"), "USERNAME");
        assertEquals(codegen.toVarName("USER123NAME"), "USER123NAME");
        assertEquals(codegen.toVarName("1"), "_1");
        assertEquals(codegen.toVarName("1a"), "_1a");
        assertEquals(codegen.toVarName("1A"), "_1A");
        assertEquals(codegen.toVarName("1AAAA"), "_1AAAA");
        assertEquals(codegen.toVarName("1AAaa"), "_1aAaa");
    }

    @Test
    public void convertVarNameWithCaml() {
        codegen.setCamelCaseDollarSign(true);

        assertEquals(codegen.toVarName("$name"), "$Name");
        assertEquals(codegen.toVarName("1AAaa"), "_1AAaa");
    }
    
    @Test
    public void convertModelName() {
        assertEquals(codegen.toModelName("name"), "Name");
        assertEquals(codegen.toModelName("$name"), "Name");
        assertEquals(codegen.toModelName("nam#e"), "Name");
        assertEquals(codegen.toModelName("$another-fake?"), "AnotherFake");
        assertEquals(codegen.toModelName("1a"), "Model1a");
        assertEquals(codegen.toModelName("1A"), "Model1A");
        assertEquals(codegen.toModelName("AAAb"), "AAAb");
        assertEquals(codegen.toModelName("aBB"), "ABB");
        assertEquals(codegen.toModelName("AaBBa"), "AaBBa");
        assertEquals(codegen.toModelName("A_B"), "AB");
        assertEquals(codegen.toModelName("A-B"), "AB");
        assertEquals(codegen.toModelName("Aa_Bb"), "AaBb");
        assertEquals(codegen.toModelName("Aa-Bb"), "AaBb");
        assertEquals(codegen.toModelName("Aa_bb"), "AaBb");
        assertEquals(codegen.toModelName("Aa-bb"), "AaBb");
    }

    @Test
    public void testInitialConfigValues() throws Exception {
        OpenAPI openAPI = TestUtils.createOpenAPI();

        codegen.processOpts();
        codegen.preprocessOpenAPI(openAPI);

        assertEquals(codegen.additionalProperties().get(CodegenConstants.HIDE_GENERATION_TIMESTAMP), Boolean.FALSE);
        Assert.assertFalse(codegen.isHideGenerationTimestamp());
        assertEquals(codegen.modelPackage(), "invalidPackageName");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.MODEL_PACKAGE), "invalidPackageName");
        assertEquals(codegen.apiPackage(), "invalidPackageName");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.API_PACKAGE), "invalidPackageName");
        assertEquals(codegen.getInvokerPackage(), "org.openapitools");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.INVOKER_PACKAGE), "org.openapitools");
        assertEquals(codegen.additionalProperties().get(AbstractJavaCodegen.BOOLEAN_GETTER_PREFIX), "get");
        assertEquals(codegen.getArtifactVersion(), openAPI.getInfo().getVersion());
        assertEquals(codegen.additionalProperties().get(CodegenConstants.ARTIFACT_VERSION), openAPI.getInfo().getVersion());
    }

    @Test
    public void testSettersForConfigValues() throws Exception {
        OpenAPI openAPI = TestUtils.createOpenAPI();


        codegen.setHideGenerationTimestamp(true);
        codegen.setModelPackage("xyz.yyyyy.zzzzzzz.model");
        codegen.setApiPackage("xyz.yyyyy.zzzzzzz.api");
        codegen.setInvokerPackage("xyz.yyyyy.zzzzzzz.invoker");
        codegen.setBooleanGetterPrefix("is");
        codegen.setArtifactVersion("0.9.0-SNAPSHOT");

        codegen.processOpts();
        codegen.preprocessOpenAPI(openAPI);

        assertEquals(codegen.additionalProperties().get(CodegenConstants.HIDE_GENERATION_TIMESTAMP), Boolean.TRUE);
        assertTrue(codegen.isHideGenerationTimestamp());
        assertEquals(codegen.modelPackage(), "xyz.yyyyy.zzzzzzz.model");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.MODEL_PACKAGE), "xyz.yyyyy.zzzzzzz.model");
        assertEquals(codegen.apiPackage(), "xyz.yyyyy.zzzzzzz.api");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.API_PACKAGE), "xyz.yyyyy.zzzzzzz.api");
        assertEquals(codegen.getInvokerPackage(), "xyz.yyyyy.zzzzzzz.invoker");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.INVOKER_PACKAGE), "xyz.yyyyy.zzzzzzz.invoker");
        assertEquals(codegen.additionalProperties().get(AbstractJavaCodegen.BOOLEAN_GETTER_PREFIX), "is");
        assertEquals(codegen.getArtifactVersion(), "0.9.0-SNAPSHOT");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.ARTIFACT_VERSION), "0.9.0-SNAPSHOT");
    }

    @Test
    public void testAdditionalPropertiesPutForConfigValues() throws Exception {
        codegen.additionalProperties().put(CodegenConstants.HIDE_GENERATION_TIMESTAMP, false);
        codegen.additionalProperties().put(CodegenConstants.MODEL_PACKAGE, "xyz.yyyyy.model.oooooo");
        codegen.additionalProperties().put(CodegenConstants.API_PACKAGE, "xyz.yyyyy.api.oooooo");
        codegen.additionalProperties().put(CodegenConstants.INVOKER_PACKAGE, "xyz.yyyyy.invoker.oooooo");
        codegen.additionalProperties().put(AbstractJavaCodegen.BOOLEAN_GETTER_PREFIX, "getBoolean");
        codegen.additionalProperties().put(CodegenConstants.ARTIFACT_VERSION, "0.8.0-SNAPSHOT");

        codegen.processOpts();
        codegen.preprocessOpenAPI(TestUtils.createOpenAPI());

        assertEquals(codegen.additionalProperties().get(CodegenConstants.HIDE_GENERATION_TIMESTAMP), Boolean.FALSE);
        Assert.assertFalse(codegen.isHideGenerationTimestamp());
        assertEquals(codegen.modelPackage(), "xyz.yyyyy.model.oooooo");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.MODEL_PACKAGE), "xyz.yyyyy.model.oooooo");
        assertEquals(codegen.apiPackage(), "xyz.yyyyy.api.oooooo");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.API_PACKAGE), "xyz.yyyyy.api.oooooo");
        assertEquals(codegen.getInvokerPackage(), "xyz.yyyyy.invoker.oooooo");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.INVOKER_PACKAGE), "xyz.yyyyy.invoker.oooooo");
        assertEquals(codegen.additionalProperties().get(AbstractJavaCodegen.BOOLEAN_GETTER_PREFIX), "getBoolean");
        assertEquals(codegen.getArtifactVersion(), "0.8.0-SNAPSHOT");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.ARTIFACT_VERSION), "0.8.0-SNAPSHOT");
    }

    @Test
    public void testAdditionalModelTypeAnnotationsSemiColon() {
        codegen.additionalProperties().put(AbstractJavaCodegen.ADDITIONAL_MODEL_TYPE_ANNOTATIONS, "@Foo;@Bar");

        codegen.processOpts();
        codegen.preprocessOpenAPI(TestUtils.createOpenAPI());

        assertThat(codegen.getAdditionalModelTypeAnnotations())
            .containsExactlyInAnyOrder("@Bar", "@Foo");
    }

    @Test
    public void testAdditionalModelTypeAnnotationsNewLineLinux() {
        codegen.additionalProperties().put(AbstractJavaCodegen.ADDITIONAL_MODEL_TYPE_ANNOTATIONS, "@Foo\n@Bar");

        codegen.processOpts();
        codegen.preprocessOpenAPI(TestUtils.createOpenAPI());

        assertThat(codegen.getAdditionalModelTypeAnnotations())
            .containsExactlyInAnyOrder("@Bar", "@Foo");
    }

    @Test
    public void testAdditionalModelTypeAnnotationsNewLineWindows() {
        codegen.additionalProperties().put(AbstractJavaCodegen.ADDITIONAL_MODEL_TYPE_ANNOTATIONS, "@Foo\r\n@Bar");

        codegen.processOpts();
        codegen.preprocessOpenAPI(TestUtils.createOpenAPI());

        assertThat(codegen.getAdditionalModelTypeAnnotations())
            .containsExactlyInAnyOrder("@Bar", "@Foo");
    }

    @Test
    public void testAdditionalModelTypeAnnotationsMixed() {
        codegen.additionalProperties().put(AbstractJavaCodegen.ADDITIONAL_MODEL_TYPE_ANNOTATIONS, " \t @Foo;\r\n@Bar  ;\n @Foobar  ");

        codegen.processOpts();
        codegen.preprocessOpenAPI(TestUtils.createOpenAPI());

        assertThat(codegen.getAdditionalModelTypeAnnotations())
            .containsExactlyInAnyOrder("@Bar", "@Foo", "@Foobar");
    }

    @Test
    public void testAdditionalModelTypeAnnotationsNoDuplicate() {
        codegen.additionalProperties().put(AbstractJavaCodegen.ADDITIONAL_MODEL_TYPE_ANNOTATIONS, "@Foo;@Bar;@Foo");

        codegen.processOpts();
        codegen.preprocessOpenAPI(TestUtils.createOpenAPI());

        assertThat(codegen.getAdditionalModelTypeAnnotations())
            .containsExactlyInAnyOrder("@Bar", "@Foo", "@Foo");
    }

    @Test
    public void toEnumValue() {
        assertEquals(codegen.toEnumValue("1", "Integer"), "1");
        assertEquals(codegen.toEnumValue("42", "Double"), "42");
        assertEquals(codegen.toEnumValue("1337", "Long"), "1337l");
        assertEquals(codegen.toEnumValue("3.14", "Float"), "3.14f");
        assertEquals(codegen.toEnumValue("schema.json", "URI"), "URI.create(\"schema.json\")");
    }

    @Test
    public void apiFileFolder() {
        codegen.setOutputDir("/User/open.api.tools");
        codegen.setSourceFolder("source.folder");
        codegen.setApiPackage("org.openapitools.codegen.api");
        assertEquals(codegen.apiFileFolder(), "/User/open.api.tools/source.folder/org/openapitools/codegen/api".replace('/', File.separatorChar));
    }

    @Test
    public void apiTestFileFolder() {
        codegen.setOutputDir("/User/open.api.tools");
        codegen.setTestFolder("test.folder");
        codegen.setApiPackage("org.openapitools.codegen.api");
        assertEquals(codegen.apiTestFileFolder(), "/User/open.api.tools/test.folder/org/openapitools/codegen/api".replace('/', File.separatorChar));
    }

    @Test
    public void modelTestFileFolder() {
        codegen.setOutputDir("/User/open.api.tools");
        codegen.setTestFolder("test.folder");
        codegen.setModelPackage("org.openapitools.codegen.model");
        assertEquals(codegen.modelTestFileFolder(), "/User/open.api.tools/test.folder/org/openapitools/codegen/model".replace('/', File.separatorChar));
    }

    @Test
    public void apiTestFileFolderDirect() {
        codegen.setOutputTestFolder("/User/open.api.tools");
        codegen.setTestFolder("test.folder");
        codegen.setApiPackage("org.openapitools.codegen.api");
        assertEquals(codegen.apiTestFileFolder(), "/User/open.api.tools/test.folder/org/openapitools/codegen/api".replace('/', File.separatorChar));
    }

    @Test
    public void modelTestFileFolderDirect() {
        codegen.setOutputTestFolder("/User/open.api.tools");
        codegen.setTestFolder("test.folder");
        codegen.setModelPackage("org.openapitools.codegen.model");
        assertEquals(codegen.modelTestFileFolder(), "/User/open.api.tools/test.folder/org/openapitools/codegen/model".replace('/', File.separatorChar));
    }

    @Test
    public void modelFileFolder() {
        codegen.setOutputDir("/User/open.api.tools");
        codegen.setSourceFolder("source.folder");
        codegen.setModelPackage("org.openapitools.codegen.model");
        assertEquals(codegen.modelFileFolder(), "/User/open.api.tools/source.folder/org/openapitools/codegen/model".replace('/', File.separatorChar));
    }

    @Test
    public void apiDocFileFolder() {
        codegen.setOutputDir("/User/open.api.tools");
        assertEquals(codegen.apiDocFileFolder(), "/User/open.api.tools/docs/".replace('/', File.separatorChar));
    }

    @Test(description = "tests if API version specification is used if no version is provided in additional properties")
    public void openApiVersionTest() {

        codegen.processOpts();
        codegen.preprocessOpenAPI(TestUtils.createOpenAPI());

        assertEquals(codegen.getArtifactVersion(), "1.0.7");
    }

    @Test(description = "tests if API version specification is used if no version is provided in additional properties with snapshot version")
    public void openApiSnapShotVersionTest() {
        codegen.additionalProperties().put("snapshotVersion", "true");

        codegen.processOpts();
        codegen.preprocessOpenAPI(TestUtils.createOpenAPI());

        assertEquals(codegen.getArtifactVersion(), "1.0.7-SNAPSHOT");
    }

    @Test(description = "tests if artifactVersion additional property is used")
    public void additionalPropertyArtifactVersionTest() {
        codegen.additionalProperties().put("artifactVersion", "1.1.1");

        codegen.processOpts();
        codegen.preprocessOpenAPI(TestUtils.createOpenAPI());

        assertEquals(codegen.getArtifactVersion(), "1.1.1");
    }

    @Test(description = "tests if artifactVersion additional property is used with snapshot parameter")
    public void additionalPropertyArtifactSnapShotVersionTest() {

        codegen.additionalProperties().put("artifactVersion", "1.1.1");
        codegen.additionalProperties().put("snapshotVersion", "true");

        codegen.processOpts();
        codegen.preprocessOpenAPI(TestUtils.createOpenAPI());

        assertEquals(codegen.getArtifactVersion(), "1.1.1-SNAPSHOT");
    }

    @Test(description = "tests if default version is used when neither OpenAPI version nor artifactVersion additional property has been provided")
    public void defaultVersionTest() {
        codegen.setArtifactVersion(null);
        OpenAPI api = TestUtils.createOpenAPI();
        api.getInfo().setVersion(null);
        
        codegen.processOpts();
        codegen.preprocessOpenAPI(api);

        assertEquals(codegen.getArtifactVersion(), "1.0.0");
    }

    @Test(description = "tests if default version with snapshot is used when neither OpenAPI version nor artifactVersion additional property has been provided")
    public void snapshotVersionTest() {

        codegen.additionalProperties().put("snapshotVersion", "true");
        OpenAPI api = TestUtils.createOpenAPI();
        api.getInfo().setVersion(null);
        
        codegen.processOpts();
        codegen.preprocessOpenAPI(api);

        assertEquals(codegen.getArtifactVersion(), "1.0.0-SNAPSHOT");
    }

    @Test(description = "tests if default version with snapshot is used when OpenAPI version has been provided")
    public void snapshotVersionOpenAPITest() {

        codegen.additionalProperties().put(CodegenConstants.SNAPSHOT_VERSION, "true");
        OpenAPI api = TestUtils.createOpenAPI();
        api.getInfo().setVersion("2.0");
        
        codegen.processOpts();
        codegen.preprocessOpenAPI(api);

        assertEquals(codegen.getArtifactVersion(), "2.0-SNAPSHOT");
    }

    @Test(description = "tests if setting an artifact version programmatically persists to additional properties, when openapi version is null")
    public void allowsProgrammaticallySettingArtifactVersionWithNullOpenApiVersion() {
        final String version = "9.8.7-rc1";
        codegen.setArtifactVersion(version);
        OpenAPI api = TestUtils.createOpenAPI();
        api.getInfo().setVersion(null);

        codegen.processOpts();
        codegen.preprocessOpenAPI(api);

        assertEquals(codegen.getArtifactVersion(), version);
        assertEquals(codegen.additionalProperties().get(CodegenConstants.ARTIFACT_VERSION), version);
    }

    @Test(description = "tests if setting an artifact version programmatically persists to additional properties, even when openapi version is specified")
    public void allowsProgrammaticallySettingArtifactVersionWithSpecifiedOpenApiVersion() {
        final String version = "9.8.7-rc1";
        codegen.setArtifactVersion(version);
        OpenAPI api = TestUtils.createOpenAPI();
        api.getInfo().setVersion("1.2.3-SNAPSHOT");

        codegen.processOpts();
        codegen.preprocessOpenAPI(api);

        assertEquals(codegen.getArtifactVersion(), version);
        assertEquals(codegen.additionalProperties().get(CodegenConstants.ARTIFACT_VERSION), version);
    }

    @Test(description = "tests if a null in addition properties artifactVersion results in default version")
    public void usesDefaultVersionWhenAdditionalPropertiesVersionIsNull() {
        final String version = "1.0.0";
        OpenAPI api = TestUtils.createOpenAPI();
        api.getInfo().setVersion(null);
        codegen.setArtifactVersion(null);
        codegen.additionalProperties().put(CodegenConstants.ARTIFACT_VERSION, null);

        codegen.processOpts();
        codegen.preprocessOpenAPI(api);

        assertEquals(codegen.getArtifactVersion(), version);
        assertEquals(codegen.additionalProperties().get(CodegenConstants.ARTIFACT_VERSION), version);
    }


    @Test(description = "tests if default version with snapshot is used when setArtifactVersion is used")
    public void snapshotVersionAlreadySnapshotTest() {

        codegen.additionalProperties().put(CodegenConstants.SNAPSHOT_VERSION, "true");
        codegen.setArtifactVersion("4.1.2-SNAPSHOT");

        codegen.processOpts();
        codegen.preprocessOpenAPI(TestUtils.createOpenAPI());
        
        assertEquals(codegen.getArtifactVersion(), "4.1.2-SNAPSHOT");
    }

    @Test
    public void toDefaultValueDateTimeLegacyTest() {
        codegen.setDateLibrary("legacy");
        String defaultValue;

        // Test default value for date format (DateSchema)
        DateSchema dateSchema = new DateSchema();

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        LocalDate defaultLocalDate = LocalDate.of(2021, 5, 23);
        Date date = Date.from(defaultLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(date.toString(), "Sun May 23 00:00:00 UTC 2021");

        dateSchema.setDefault(date);
        defaultValue = codegen.toDefaultValue(dateSchema);

        // dateLibrary <> java8
        assertEquals(defaultValue, "Sun May 23 00:00:00 UTC 2021");

        // Test default value for date format (DateTimeSchema)
        DateTimeSchema dateTimeSchema = new DateTimeSchema();

        OffsetDateTime defaultDateTime = OffsetDateTime.parse("1984-12-19T03:39:57-09:00");
        assertEquals(defaultDateTime.toString(), "1984-12-19T03:39:57-09:00");

        dateTimeSchema.setDefault(defaultDateTime);
        defaultValue = codegen.toDefaultValue(dateTimeSchema);

        // dateLibrary <> java8
        assertEquals(defaultValue, "1984-12-19T03:39:57-09:00");
    }

    @Test
    public void toDefaultValueTest() {
        codegen.setDateLibrary("java8");

        Schema<?> schema = new ObjectSchema().addProperty("id", new IntegerSchema().format("int32")).minItems(1);
        String defaultValue = codegen.toDefaultValue(schema);
        assertEquals(defaultValue, "null");

        // Create an alias to an array schema
        Schema<?> nestedArraySchema = new ArraySchema().items(new IntegerSchema().format("int32"));
        codegen.setOpenAPI(new OpenAPI().components(new Components().addSchemas("NestedArray", nestedArraySchema)));

        // Create an array schema with item type set to the array alias
        schema = new ArraySchema().items(new Schema<>().$ref("#/components/schemas/NestedArray"));

        ModelUtils.setGenerateAliasAsModel(false);
        defaultValue = codegen.toDefaultValue(codegen.fromProperty("", schema), schema);
        assertEquals(defaultValue, "new ArrayList<>()");

        ModelUtils.setGenerateAliasAsModel(true);
        defaultValue = codegen.toDefaultValue(codegen.fromProperty("", schema), schema);
        assertEquals(defaultValue, "new ArrayList<>()");

        // Create a map schema with additionalProperties type set to array alias
        schema = new MapSchema().additionalProperties(new Schema<>().$ref("#/components/schemas/NestedArray"));

        ModelUtils.setGenerateAliasAsModel(false);
        defaultValue = codegen.toDefaultValue(codegen.fromProperty("", schema), schema);
        assertEquals(defaultValue, "new HashMap<>()");

        ModelUtils.setGenerateAliasAsModel(true);
        defaultValue = codegen.toDefaultValue(codegen.fromProperty("", schema), schema);
        assertEquals(defaultValue, "new HashMap<>()");

        // Test default value for date format
        DateSchema dateSchema = new DateSchema();
        LocalDate defaultLocalDate = LocalDate.of(2019, 2, 15);
        Date date = Date.from(defaultLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        dateSchema.setDefault(date);
        defaultValue = codegen.toDefaultValue(codegen.fromProperty("", schema), dateSchema);
        assertEquals(defaultValue, "LocalDate.parse(\"" + defaultLocalDate + "\")");

        DateTimeSchema dateTimeSchema = new DateTimeSchema();
        OffsetDateTime defaultDateTime = OffsetDateTime.parse("1984-12-19T03:39:57-08:00");
        ZonedDateTime expectedDateTime = defaultDateTime.atZoneSameInstant(ZoneId.systemDefault());
        dateTimeSchema.setDefault(defaultDateTime);
        defaultValue = codegen.toDefaultValue(codegen.fromProperty("", schema), dateTimeSchema);
        assertTrue(defaultValue.startsWith("OffsetDateTime.parse(\"" + expectedDateTime));

        // Test default value for number without format
        NumberSchema numberSchema = new NumberSchema();
        Double doubleValue = 100.0;
        numberSchema.setDefault(doubleValue);
        defaultValue = codegen.toDefaultValue(codegen.fromProperty("", schema), numberSchema);
        assertEquals(defaultValue, "new BigDecimal(\"" + doubleValue + "\")");

        // Test default value for number with format set to double
        numberSchema.setFormat("double");
        defaultValue = codegen.toDefaultValue(codegen.fromProperty("", schema), numberSchema);
        assertEquals(defaultValue, doubleValue + "d");
    }

    @Test
    public void dateDefaultValueIsIsoDate() {
        final OpenAPI openAPI = FLATTENED_SPEC.get("3_0/spring/date-time-parameter-types-for-testing");
        codegen.setOpenAPI(openAPI);

        Set<String> imports = new HashSet<>();
        CodegenParameter parameter = codegen.fromParameter(openAPI.getPaths().get("/thingy/{date}").getGet().getParameters().get(2), imports);

        assertEquals(parameter.dataType, "Date");
        assertTrue(parameter.isDate);
        assertEquals(parameter.defaultValue, "1974-01-01");
        assertEquals(imports.size(), 1);
        assertEquals(imports.iterator().next(), "Date");

        Assert.assertNotNull(parameter.getSchema());
        assertEquals(parameter.getSchema().baseType, "Date");
    }

    @Test
    public void dateDefaultValueIsIsoDateTime() {
        final OpenAPI openAPI = FLATTENED_SPEC.get("3_0/spring/date-time-parameter-types-for-testing");
        codegen.setOpenAPI(openAPI);

        Set<String> imports = new HashSet<>();
        CodegenParameter parameter = codegen.fromParameter(openAPI.getPaths().get("/thingy/{date}").getGet().getParameters().get(1), imports);

        assertEquals(parameter.dataType, "Date");
        assertTrue(parameter.isDateTime);
        assertEquals(parameter.defaultValue, "1973-12-19T03:39:57-08:00");
        assertEquals(imports.size(), 1);
        assertEquals(imports.iterator().next(), "Date");

        Assert.assertNotNull(parameter.getSchema());
        assertEquals(parameter.getSchema().baseType, "Date");
    }

    @Test
    public void getTypeDeclarationGivenSchemaMappingTest() {
        codegen.schemaMapping().put("MyStringType", "com.example.foo");
        codegen.setOpenAPI(new OpenAPI().components(new Components().addSchemas("MyStringType", new StringSchema())));
        Schema<?> schema = new ArraySchema().items(new Schema<>().$ref("#/components/schemas/MyStringType"));
        
        String defaultValue = codegen.getTypeDeclaration(schema);

        assertEquals(defaultValue, "List<com.example.foo>");
    }

    @Test
    public void getTypeDeclarationTest() {

        Schema<?> schema = new ObjectSchema().addProperty("id", new IntegerSchema().format("int32")).minItems(1);
        String defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "Object");

        // Create an alias to an array schema
        Schema<?> nestedArraySchema = new ArraySchema().items(new IntegerSchema().format("int32"));
        codegen.setOpenAPI(new OpenAPI().components(new Components().addSchemas("NestedArray", nestedArraySchema)));

        // Create an array schema with item type set to the array alias
        schema = new ArraySchema().items(new Schema<>().$ref("#/components/schemas/NestedArray"));

        ModelUtils.setGenerateAliasAsModel(false);
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<List<Integer>>");

        ModelUtils.setGenerateAliasAsModel(true);
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<NestedArray>");

        // Create an array schema with item type set to the array alias
        schema = new ArraySchema().items(new Schema<>().$ref("#/components/schemas/NestedArray"));
        schema.setUniqueItems(true);

        ModelUtils.setGenerateAliasAsModel(false);
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "Set<List<Integer>>");

        ModelUtils.setGenerateAliasAsModel(true);
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "Set<NestedArray>");

        // Create a map schema with additionalProperties type set to array alias
        schema = new MapSchema().additionalProperties(new Schema<>().$ref("#/components/schemas/NestedArray"));

        ModelUtils.setGenerateAliasAsModel(false);
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "Map<String, List<Integer>>");

        ModelUtils.setGenerateAliasAsModel(true);
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "Map<String, NestedArray>");
    }

    @Test
    public void processOptsBooleanTrueFromString() {
        codegen.additionalProperties().put(CodegenConstants.SNAPSHOT_VERSION, "true");
        
        codegen.preprocessOpenAPI(FLATTENED_SPEC.get("3_0/petstore"));
        
        assertTrue((boolean) codegen.additionalProperties().get(CodegenConstants.SNAPSHOT_VERSION));
    }

    @Test
    public void processOptsBooleanTrueFromBoolean() {
        codegen.additionalProperties().put(CodegenConstants.SNAPSHOT_VERSION, true);
        
        codegen.preprocessOpenAPI(FLATTENED_SPEC.get("3_0/petstore"));

        assertTrue((boolean) codegen.additionalProperties().get(CodegenConstants.SNAPSHOT_VERSION));
    }

    @Test
    public void processOptsBooleanFalseFromString() {
        codegen.additionalProperties().put(CodegenConstants.SNAPSHOT_VERSION, "false");
        
        codegen.preprocessOpenAPI(FLATTENED_SPEC.get("3_0/petstore"));

        Assert.assertFalse((boolean) codegen.additionalProperties().get(CodegenConstants.SNAPSHOT_VERSION));
    }

    @Test
    public void processOptsBooleanFalseFromBoolean() {
        codegen.additionalProperties().put(CodegenConstants.SNAPSHOT_VERSION, false);
        
        codegen.preprocessOpenAPI(FLATTENED_SPEC.get("3_0/petstore"));
        
        Assert.assertFalse((boolean) codegen.additionalProperties().get(CodegenConstants.SNAPSHOT_VERSION));
    }

    @Test
    public void processOptsBooleanFalseFromGarbage() {
        codegen.additionalProperties().put(CodegenConstants.SNAPSHOT_VERSION, "blibb");
        
        codegen.preprocessOpenAPI(FLATTENED_SPEC.get("3_0/petstore"));

        Assert.assertFalse((boolean) codegen.additionalProperties().get(CodegenConstants.SNAPSHOT_VERSION));
    }

    @Test
    public void processOptsBooleanFalseFromNumeric() {
        codegen.additionalProperties().put(CodegenConstants.SNAPSHOT_VERSION, 42L);
        codegen.preprocessOpenAPI(FLATTENED_SPEC.get("3_0/petstore"));
        Assert.assertFalse((boolean) codegen.additionalProperties().get(CodegenConstants.SNAPSHOT_VERSION));
    }

    @Test
    public void nullDefaultValueForModelWithDynamicProperties() {
        final OpenAPI openAPI = FLATTENED_SPEC.get("3_0/mapSchemas");
        codegen.additionalProperties().put(CodegenConstants.GENERATE_ALIAS_AS_MODEL, true);
        codegen.setOpenAPI(openAPI);

        Schema<?> schema = openAPI.getComponents().getSchemas().get("ModelWithAdditionalProperties");
        CodegenModel cm = codegen.fromModel("ModelWithAdditionalProperties", schema);
        assertEquals(cm.vars.size(), 1, "Expected single declared var");
        assertEquals(cm.vars.get(0).name, "id");
        Assert.assertNull(cm.defaultValue, "Expected no defined default value in spec");

        String defaultValue = codegen.toDefaultValue(schema);
        assertEquals(defaultValue, "null");
    }

    @Test
    public void maplikeDefaultValueForModelWithStringToStringMapping() {
        final OpenAPI openAPI = FLATTENED_SPEC.get("3_0/mapSchemas");
        codegen.additionalProperties().put(CodegenConstants.GENERATE_ALIAS_AS_MODEL, true);
        codegen.setOpenAPI(openAPI);

        Schema<?> schema = openAPI.getComponents().getSchemas().get("ModelWithStringToStringMapping");
        CodegenModel cm = codegen.fromModel("ModelWithAdditionalProperties", schema);
        assertEquals(cm.vars.size(), 0, "Expected no declared vars");
        Assert.assertNull(cm.defaultValue, "Expected no defined default value in spec");

        String defaultValue = codegen.toDefaultValue(schema);
        assertEquals(defaultValue, "null", "Expected string-string map aliased model to default to null since nullable is not set to true");
    }

    @Test
    public void maplikeDefaultValueForModelWithStringToModelMapping() {
        final OpenAPI openAPI = FLATTENED_SPEC.get("3_0/mapSchemas");
        codegen.additionalProperties().put(CodegenConstants.GENERATE_ALIAS_AS_MODEL, true);
        codegen.setOpenAPI(openAPI);

        Schema<?> schema = openAPI.getComponents().getSchemas().get("ModelWithStringToModelMapping");
        CodegenModel cm = codegen.fromModel("ModelWithStringToModelMapping", schema);
        assertEquals(cm.vars.size(), 0, "Expected no declared vars");
        Assert.assertNull(cm.defaultValue, "Expected no defined default value in spec");

        String defaultValue = codegen.toDefaultValue(schema);
        assertEquals(defaultValue, "null", "Expected string-ref map aliased model to default to null since nullable is not set to tru");
    }

    @Test
    public void srcMainFolderShouldNotBeOperatingSystemSpecificPaths() {
        // it's not responsibility of the generator to fix OS-specific paths. This is left to template manager.
        // This path must be non-OS-specific for expectations in source outputs (e.g. gradle build files)
        assertEquals(codegen.getSourceFolder(), "src/main/java");
    }

    @Test
    public void srcTestFolderShouldNotBeOperatingSystemSpecificPaths() {
        // it's not responsibility of the generator to fix OS-specific paths. This is left to template manager.
        // This path must be non-OS-specific for expectations in source outputs (e.g. gradle build files)
        assertEquals(codegen.getTestFolder(), "src/test/java");
    }

    @Test
    public void testOneOfModelImports() {
        final OpenAPI openAPI = TestUtils.parseFlattenSpec("src/test/resources/3_0/oneOf_nonPrimitive.yaml");
        codegen.setOpenAPI(openAPI);
        codegen.preprocessOpenAPI(openAPI);

        Schema<?> schema = openAPI.getComponents().getSchemas().get("Example");
        CodegenModel cm = codegen.fromModel("Example", schema);
        assertEquals(cm.imports.size(), 3);
        assertTrue(cm.imports.contains("BigDecimal"));
        assertTrue(cm.imports.contains("Date"));
        assertTrue(cm.imports.contains("UUID"));
    }

    @Test
    public void arrayParameterDefaultValueDoesNotNeedBraces() {
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/issue_16223.yaml", null, parseOptions)
                .getOpenAPI();
        codegen.setOpenAPI(openAPI);

        Map<String, Schema<?>> schemas = openAPI.getPaths().get("/test").getGet().getParameters().stream()
                .collect(Collectors.toMap(
                        Parameter::getName,
                        p -> ModelUtils.getReferencedSchema(openAPI, p.getSchema())));
        assertEquals(codegen.toDefaultParameterValue(schemas.get("fileEnumWithDefault")), "A,B");
        assertEquals(codegen.toDefaultParameterValue(schemas.get("fileEnumWithDefaultEmpty")), "");
        assertEquals(codegen.toDefaultParameterValue(schemas.get("inlineEnumWithDefault")), "A,B");
        assertEquals(codegen.toDefaultParameterValue(schemas.get("inlineEnumWithDefaultEmpty")), "");
    }

    @Test
    public void ignoreBeanValidationAnnotationsTest() {
        codegen.additionalProperties().put("useBeanValidation", true);

        Schema<?> schema = new Schema<>().type("string").format("uuid").pattern("^[a-z]$").maxLength(36);
        String defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "UUID");

        schema = new Schema<>().type("string").format("uri").pattern("^[a-z]$").maxLength(36);
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "URI");

        schema = new Schema<>().type("string").format("byte").pattern("^[a-z]$").maxLength(36);
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "byte[]");

        schema = new Schema<>().type("string").format("binary").pattern("^[a-z]$").maxLength(36);
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "File");

        schema = new Schema<>().type("string")._enum(List.of("A","B")).pattern("^[a-z]$").maxLength(36);
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "String");
    }

    @Test
    public void ignoreBeanValidationAnnotationsContainerTest() {
        codegen.additionalProperties().put("useBeanValidation", true);

        Schema<?> schema = new ArraySchema().items(new Schema<>().type("string").format("uuid").pattern("^[a-z]$").maxLength(36));
        String defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<UUID>");

        schema = new ArraySchema().items(new Schema<>().type("string").format("uri").pattern("^[a-z]$").maxLength(36));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<URI>");

        schema = new ArraySchema().items(new Schema<>().type("string").format("byte").pattern("^[a-z]$").maxLength(36));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<byte[]>");

        schema = new ArraySchema().items(new Schema<>().type("string").format("binary").pattern("^[a-z]$").maxLength(36));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<File>");

        schema = new ArraySchema().items(new Schema<>().type("string")._enum(List.of("A","B")).pattern("^[a-z]$").maxLength(36));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<String>");
    }

    @Test
    public void AnnotationsContainerTest() {
        codegen.additionalProperties().put("useBeanValidation", true);

        // 1. string type
        Schema<?> schema = new ArraySchema().items(new Schema<>().type("string").pattern("^[a-z]$").minLength(0).maxLength(36));
        String defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@Pattern(regexp = \"^[a-z]$\")@Size(min = 0, max = 36)String>");

        schema = new ArraySchema().items(new Schema<>().type("string").pattern("^[a-z]$").minLength(0));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@Pattern(regexp = \"^[a-z]$\")@Size(min = 0)String>");

        schema = new ArraySchema().items(new Schema<>().type("string").pattern("^[a-z]$").maxLength(36));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@Pattern(regexp = \"^[a-z]$\")@Size(max = 36)String>");

        schema = new ArraySchema().items(new Schema<>().type("string").format("email"));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@Email String>");

        // 2. string type with number format
        schema = new ArraySchema().items(new Schema<>().type("string").format("number").minimum(BigDecimal.ZERO).maximum(BigDecimal.TEN).exclusiveMinimum(Boolean.TRUE).exclusiveMaximum(Boolean.TRUE));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@DecimalMin(value = \"0\", inclusive = false) @DecimalMax(value = \"10\", inclusive = false)BigDecimal>");

        schema = new ArraySchema().items(new Schema<>().type("string").format("number").minimum(BigDecimal.ZERO).exclusiveMinimum(Boolean.TRUE));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@DecimalMin( value = \"0\", inclusive = false)BigDecimal>");

        schema = new ArraySchema().items(new Schema<>().type("string").format("number").maximum(BigDecimal.TEN).exclusiveMaximum(Boolean.TRUE));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@DecimalMax( value = \"10\", inclusive = false)BigDecimal>");

        // 3. number type
        schema = new ArraySchema().items(new Schema<>().type("number").minimum(BigDecimal.ZERO).maximum(BigDecimal.TEN).exclusiveMinimum(Boolean.TRUE).exclusiveMaximum(Boolean.TRUE));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@DecimalMin(value = \"0\", inclusive = false) @DecimalMax(value = \"10\", inclusive = false)BigDecimal>");

        schema = new ArraySchema().items(new Schema<>().type("number").minimum(BigDecimal.ZERO).exclusiveMinimum(Boolean.TRUE));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@DecimalMin( value = \"0\", inclusive = false)BigDecimal>");

        schema = new ArraySchema().items(new Schema<>().type("number").maximum(BigDecimal.TEN).exclusiveMaximum(Boolean.TRUE));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@DecimalMax( value = \"10\", inclusive = false)BigDecimal>");

        schema = new ArraySchema().items(new Schema<>().type("number").minimum(BigDecimal.ZERO).maximum(BigDecimal.TEN));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@DecimalMin(value = \"0\", inclusive = true) @DecimalMax(value = \"10\", inclusive = true)BigDecimal>");

        schema = new ArraySchema().items(new Schema<>().type("number").minimum(BigDecimal.ZERO));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@DecimalMin( value = \"0\", inclusive = true)BigDecimal>");

        schema = new ArraySchema().items(new Schema<>().type("number").maximum(BigDecimal.TEN));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@DecimalMax( value = \"10\", inclusive = true)BigDecimal>");

        // 4. integer type with int64 format
        schema = new ArraySchema().items(new Schema<>().type("integer").format("int64").minimum(BigDecimal.ZERO).maximum(BigDecimal.TEN));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@Min(0L) @Max(10L)Long>");

        schema = new ArraySchema().items(new Schema<>().type("integer").format("int64").minimum(BigDecimal.ZERO));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@Min(0L)Long>");

        schema = new ArraySchema().items(new Schema<>().type("integer").format("int64").maximum(BigDecimal.TEN));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@Max(10L)Long>");

        // 5. integer type
        schema = new ArraySchema().items(new Schema<>().type("integer").minimum(BigDecimal.ZERO).maximum(BigDecimal.TEN));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@Min(0) @Max(10)Integer>");

        schema = new ArraySchema().items(new Schema<>().type("integer").minimum(BigDecimal.ZERO));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@Min(0)Integer>");

        schema = new ArraySchema().items(new Schema<>().type("integer").maximum(BigDecimal.TEN));
        defaultValue = codegen.getTypeDeclaration(schema);
        assertEquals(defaultValue, "List<@Max(10)Integer>");
    }
}
