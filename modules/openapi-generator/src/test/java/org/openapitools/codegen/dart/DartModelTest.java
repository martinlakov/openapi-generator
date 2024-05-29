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

package org.openapitools.codegen.dart;

import static org.openapitools.codegen.TestUtils.createCodegenModelWrapper;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.*;
import org.openapitools.codegen.*;
import org.openapitools.codegen.languages.DartClientCodegen;

import org.testng.annotations.DataProvider;


import java.util.*;

@SuppressWarnings("static-method")
public class DartModelTest {

    @Test(description = "convert a simple php model")
    public void simpleModelTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("id", new IntegerSchema())
                .addProperties("name", new StringSchema())
                .addProperties("createdAt", new DateTimeSchema())
                .addProperties("defaultItem", new IntegerSchema()._default(1))
                .addProperties("number", new NumberSchema())
                .addProperties("decimal", new StringSchema().format("number"))
                .addRequiredItem("id")
                .addRequiredItem("name");
        final DefaultCodegen codegen = new DartClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 6);

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "id");
        assertEquals(property1.dataType, "int");
        assertEquals(property1.name, "id");
        Assert.assertNull(property1.defaultValue);
        assertEquals(property1.baseType, "int");
        assertTrue(property1.required);
        assertTrue(property1.isPrimitiveType);
        Assert.assertFalse(property1.isContainer);

        final CodegenProperty property2 = cm.vars.get(1);
        assertEquals(property2.baseName, "name");
        assertEquals(property2.dataType, "String");
        assertEquals(property2.name, "name");
        Assert.assertNull(property2.defaultValue);
        assertEquals(property2.baseType, "String");
        assertTrue(property2.required);
        assertTrue(property2.isPrimitiveType);
        Assert.assertFalse(property2.isContainer);

        final CodegenProperty property3 = cm.vars.get(2);
        assertEquals(property3.baseName, "createdAt");
        assertEquals(property3.complexType, "DateTime");
        assertEquals(property3.dataType, "DateTime");
        assertEquals(property3.name, "createdAt");
        Assert.assertNull(property3.defaultValue);
        assertEquals(property3.baseType, "DateTime");
        Assert.assertFalse(property3.required);
        Assert.assertFalse(property3.isContainer);

        final CodegenProperty property4 = cm.vars.get(3);
        assertEquals(property4.baseName, "defaultItem");
        assertEquals(property4.dataType, "int");
        assertEquals(property4.defaultValue, "1");
        assertEquals(property4.baseType, "int");
        Assert.assertFalse(property4.required);
        Assert.assertFalse(property4.isContainer);

        final CodegenProperty property5 = cm.vars.get(4);
        assertEquals(property5.baseName, "number");
        assertEquals(property5.dataType, "num");
        assertEquals(property5.baseType, "num");

        final CodegenProperty property6 = cm.vars.get(5);
        assertEquals(property6.baseName, "decimal");
        assertEquals(property6.dataType, "double");
        assertEquals(property6.baseType, "double");
    }

    @Test(description = "convert a model with list property")
    public void listPropertyTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("id", new IntegerSchema())
                .addProperties("urls", new ArraySchema().items(new StringSchema()))
                .addRequiredItem("id");

        final DefaultCodegen codegen = new DartClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 2);

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "id");
        assertEquals(property1.dataType, "int");
        assertEquals(property1.name, "id");
        Assert.assertNull(property1.defaultValue);
        assertEquals(property1.baseType, "int");
        assertTrue(property1.required);
        assertTrue(property1.isPrimitiveType);
        Assert.assertFalse(property1.isContainer);

        final CodegenProperty property2 = cm.vars.get(1);
        assertEquals(property2.baseName, "urls");
        assertEquals(property2.dataType, "List<String>");
        assertEquals(property2.name, "urls");
        assertEquals(property2.baseType, "List");
        assertEquals(property2.containerType, "array");
        Assert.assertFalse(property2.required);
        assertTrue(property2.isPrimitiveType);
        assertTrue(property2.isContainer);
    }

    @Test(description = "convert a model with set property")
    public void setPropertyTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("id", new IntegerSchema())
                .addProperties("urls", new ArraySchema().items(new StringSchema()).uniqueItems(true))
                .addRequiredItem("id");

        final DefaultCodegen codegen = new DartClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 2);

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "id");
        assertEquals(property1.dataType, "int");
        assertEquals(property1.name, "id");
        Assert.assertNull(property1.defaultValue);
        assertEquals(property1.baseType, "int");
        assertTrue(property1.required);
        assertTrue(property1.isPrimitiveType);
        Assert.assertFalse(property1.isContainer);

        final CodegenProperty property2 = cm.vars.get(1);
        assertEquals(property2.baseName, "urls");
        assertEquals(property2.dataType, "Set<String>");
        assertEquals(property2.name, "urls");
        assertEquals(property2.baseType, "Set");
        assertEquals(property2.containerType, "set");
        Assert.assertFalse(property2.required);
        assertTrue(property2.isPrimitiveType);
        assertTrue(property2.isContainer);
    }

    @Test(description = "convert a model with a map property")
    public void mapPropertyTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("translations", new MapSchema()
                        .additionalProperties(new StringSchema()))
                .addRequiredItem("id");
        final DefaultCodegen codegen = new DartClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "translations");
        assertEquals(property1.dataType, "Map<String, String>");
        assertEquals(property1.name, "translations");
        assertEquals(property1.baseType, "Map");
        assertEquals(property1.containerType, "map");
        Assert.assertFalse(property1.required);
        assertTrue(property1.isContainer);
        assertTrue(property1.isPrimitiveType);
    }

    @Test(description = "convert a model with complex property")
    public void complexPropertyTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("children", new Schema().$ref("#/definitions/Children"));
        final DefaultCodegen codegen = new DartClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "children");
        assertEquals(property1.dataType, "Children");
        assertEquals(property1.name, "children");
        assertEquals(property1.baseType, "Children");
        Assert.assertFalse(property1.required);
        Assert.assertFalse(property1.isContainer);
    }

    @Test(description = "convert a model with complex list property")
    public void complexListProperty() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("children", new ArraySchema()
                        .items(new Schema().$ref("#/definitions/Children")));
        final DefaultCodegen codegen = new DartClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "children");
        assertEquals(property1.dataType, "List<Children>");
        assertEquals(property1.name, "children");
        assertEquals(property1.baseType, "List");
        assertEquals(property1.containerType, "array");
        Assert.assertFalse(property1.required);
        assertTrue(property1.isContainer);
    }

    @Test(description = "convert a model with complex map property")
    public void complexMapSchema() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("children", new MapSchema()
                        .additionalProperties(new Schema().$ref("#/definitions/Children")));
        final DefaultCodegen codegen = new DartClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);
        // {{imports}} is not used in template
        //assertEquals(Sets.intersection(cm.imports, Sets.newHashSet("Children")).size(), 1);

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "children");
        assertEquals(property1.complexType, "Children");
        assertEquals(property1.dataType, "Map<String, Children>");
        assertEquals(property1.name, "children");
        assertEquals(property1.baseType, "Map");
        assertEquals(property1.containerType, "map");
        Assert.assertFalse(property1.required);
        assertTrue(property1.isContainer);
    }

    @Test(description = "convert an array model")
    public void arrayModelTest() {
        final Schema model = new ArraySchema()
                .items(new Schema().$ref("#/definitions/Children"))
                .description("an array model");
        final DefaultCodegen codegen = new DartClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(model.getDescription(), "an array model");

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertTrue(cm.isArray);
        assertEquals(cm.description, "an array model");
        assertEquals(cm.vars.size(), 0);
        // skip import test as import is not used by PHP codegen
    }

    @Test(description = "convert a map model")
    public void mapModelTest() {
        final Schema model = new Schema()
                .description("a map model")
                .additionalProperties(new Schema().$ref("#/definitions/Children"));
        final DefaultCodegen codegen = new DartClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a map model");
        assertEquals(cm.vars.size(), 0);
    }

    @DataProvider(name = "modelNames")
    public static Object[][] modelNames() {
        return new Object[][] {
            {"sample", "Sample"},
            {"sample_name", "SampleName"},
            {"sample__name", "SampleName"},
            {"/sample", "Sample"},
            {"\\sample", "\\Sample"},
            {"sample.name", "SampleName"},
            {"_sample", "Sample"},
            {"sample name", "SampleName"},
            {"List", "ModelList"},
            {"list", "ModelList"},
            {"File", "TestModelFile"},
            {"Client", "TestModelClient"},
            {"String", "ModelString"},
        };
    }

    @Test(dataProvider = "modelNames", description = "correctly generate model names")
    public void modelNameTest(String name, String expectedName) {
        OpenAPI openAPI = TestUtils.createOpenAPI();
        final Schema model = new Schema();
        final DefaultCodegen codegen = new DartClientCodegen();
        codegen.setOpenAPI(openAPI);
        codegen.typeMapping().put("File", "TestModelFile");
        codegen.typeMapping().put("Client", "TestModelClient");
        final CodegenModel cm = codegen.fromModel(name, model);

        assertEquals(cm.name, name);
        assertEquals(cm.classname, codegen.toModelName(expectedName));
    }

    @DataProvider(name = "varNames")
    public static Object[][] varNames() {
        return new Object[][] {
                {"Double", "double_"},
                {"double", "double_"},
                {"dynamic", "dynamic_"},
                {"String", "string"},
                {"string", "string"},
                {"hello", "hello"},
                {"FOO", "FOO"},
                {"FOO_BAR", "FOO_BAR"},
                {"FOO_BAR_BAZ_", "FOO_BAR_BAZ_"},
                {"123hello", "n123hello"},
                {"_hello", "hello"},
                {"_double", "double_"},
                {"_123hello", "n123hello"},
                {"_5FOO", "n5fOO"},
                 {"_FOO", "FOO"},
                {"_$foo", "dollarFoo"},
                {"_$_foo_", "dollarFoo"},
                {"$special[property.name]", "dollarSpecialLeftSquareBracketPropertyPeriodNameRightSquareBracket"},
                {"foo bar", "fooBar"},
        };
    }

    @Test(dataProvider = "varNames", description = "test variable names are correctly escaped")
    public void convertVarName(String name, String expectedName) {
        final DefaultCodegen codegen = new DartClientCodegen();
        assertEquals(codegen.toVarName(name), expectedName);
    }

    private static class EnumVarName {
        final String name;
        final String expected;
        final String dataType;

        EnumVarName(String name, String expected, String dataType) {
            this.name = name;
            this.expected = expected;
            this.dataType = dataType;
        }
    }

    @DataProvider(name = "enumVarNames")
    public static Object[] enumVarNames() {
        return new Object[] {
                new EnumVarName("", "empty", "String"),
                new EnumVarName("Double", "double_", "String"),
                new EnumVarName("double", "double_", "String"),
                new EnumVarName("dynamic", "dynamic_", "String"),
                new EnumVarName("String", "string", "String"),
                new EnumVarName("string", "string", "String"),
                new EnumVarName("hello", "hello", "String"),
                new EnumVarName("FOO", "FOO", "String"),
                new EnumVarName("FOO_BAR", "FOO_BAR", "String"),
                new EnumVarName("FOO_BAR_BAZ_", "FOO_BAR_BAZ_", "String"),
                new EnumVarName("123hello", "n123hello", "String"),
                new EnumVarName("_hello", "hello", "String"),
                new EnumVarName("_double", "double_", "String"),
                new EnumVarName("_123hello", "n123hello", "String"),
                new EnumVarName("_5FOO", "n5fOO", "String"),
                new EnumVarName("_FOO", "FOO", "String"),
                new EnumVarName("_$foo", "dollarFoo", "String"),
                new EnumVarName("_$_foo_", "dollarFoo", "String"),
                new EnumVarName("$special[property.name]", "dollarSpecialLeftSquareBracketPropertyPeriodNameRightSquareBracket", "String"),
                new EnumVarName("$", "dollar", "String"),
                new EnumVarName(">=", "greaterThanEqual", "String"),
                new EnumVarName("foo bar", "fooBar", "String"),
                new EnumVarName("1", "number1", "int"),
                new EnumVarName("2", "number2", "int"),
                new EnumVarName("-1", "numberNegative1", "int"),
                new EnumVarName("-99", "numberNegative99", "int"),
                new EnumVarName("1", "number1", "double"),
                new EnumVarName("1.1", "number1Period1", "double"),
                new EnumVarName("-1.2", "numberNegative1Period2", "double"),
        };
    }

    @Test(dataProvider = "enumVarNames", description = "test enum names are correctly escaped")
    public void convertEnumVarNames(EnumVarName enumVar) {
        final DefaultCodegen codegen = new DartClientCodegen();
        assertEquals(codegen.toEnumVarName(enumVar.name, enumVar.dataType), enumVar.expected);
    }

    @Test(description = "model names support `--model-name-prefix` and `--model-name-suffix`")
    public void modelPrefixSuffixTest() {
        final DefaultCodegen codegen = new DartClientCodegen();
        codegen.setModelNamePrefix("model");
        codegen.setModelNameSuffix("type");

        assertEquals(codegen.toModelName("hello_test"), "ModelHelloTestType");
    }

    @Test(description = "support normal enum values")
    public void testEnumValues() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("testStringEnum", new StringSchema()._enum(Arrays.asList("foo", "bar")))
                .addProperties("testIntEnum", new IntegerSchema().addEnumItem(1).addEnumItem(2));
        final DefaultCodegen codegen = new DartClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);
        codegen.postProcessModels(createCodegenModelWrapper(cm));

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "testStringEnum");
        assertEquals(property1.dataType, "String");
        assertEquals(property1.baseType, "String");
        assertEquals(property1.datatypeWithEnum, "SampleTestStringEnumEnum");
        assertEquals(property1.name, "testStringEnum");
        assertTrue(property1.isEnum);
        assertEquals(property1.allowableValues.size(), 2);
        assertEquals(((List<String>) property1.allowableValues.get("values")).size(), 2);
        List<Map<String, Object>> enumVars1 = (List<Map<String, Object>>) property1.allowableValues.get("enumVars");
        assertEquals(enumVars1.size(), 2);

        assertEquals(enumVars1.get(0).get("name"), "foo");
        assertEquals(enumVars1.get(0).get("value"), "'foo'");
        assertEquals(enumVars1.get(0).get("isString"), true);

        assertEquals(enumVars1.get(1).get("name"), "bar");
        assertEquals(enumVars1.get(1).get("value"), "'bar'");
        assertEquals(enumVars1.get(1).get("isString"), true);

        final CodegenProperty property2 = cm.vars.get(1);
        assertEquals(property2.baseName, "testIntEnum");
        assertEquals(property2.dataType, "int");
        assertEquals(property2.baseType, "int");
        assertEquals(property2.datatypeWithEnum, "SampleTestIntEnumEnum");
        assertEquals(property2.name, "testIntEnum");
        assertTrue(property2.isEnum);
        assertEquals(property2.allowableValues.size(), 2);
        assertEquals(((List<String>) property2.allowableValues.get("values")).size(), 2);
        List<Map<String, Object>> enumVars2 = (List<Map<String, Object>>) property2.allowableValues.get("enumVars");
        assertEquals(enumVars2.size(), 2);

        assertEquals(enumVars2.get(0).get("name"), "number1");
        assertEquals(enumVars2.get(0).get("value"), "1");
        assertEquals(enumVars2.get(0).get("isString"), false);

        assertEquals(enumVars2.get(1).get("name"), "number2");
        assertEquals(enumVars2.get(1).get("value"), "2");
        assertEquals(enumVars2.get(1).get("isString"), false);
    }

    @Test(description = "support for x-enum-values extension")
    public void testXEnumValuesExtension() {
        final Map<String, Object> enumValue1 = new HashMap<>();
        enumValue1.put("identifier", "foo");
        enumValue1.put("numericValue", 1);
        enumValue1.put("description", "the foo");
        final Map<String, Object> enumValue2 = new HashMap<>();
        enumValue2.put("identifier", "bar");
        enumValue2.put("numericValue", 2);
        enumValue2.put("description", "the bar");

        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("testIntEnum", new IntegerSchema().addEnumItem(1).addEnumItem(2)
                        .extensions(Collections.singletonMap("x-enum-values", Arrays.asList(enumValue1, enumValue2))));
        final DartClientCodegen codegen = new DartClientCodegen();
        codegen.setUseEnumExtension(true);
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);
        codegen.postProcessModels(createCodegenModelWrapper(cm));

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "testIntEnum");
        assertEquals(property1.dataType, "int");
        assertEquals(property1.baseType, "int");
        assertEquals(property1.datatypeWithEnum, "SampleTestIntEnumEnum");
        assertEquals(property1.name, "testIntEnum");
        assertTrue(property1.isEnum);
        assertEquals(property1.allowableValues.size(), 2);
        assertEquals(((List<String>) property1.allowableValues.get("values")).size(), 2);
        List<Map<String, Object>> enumVars = (List<Map<String, Object>>) property1.allowableValues.get("enumVars");
        assertEquals(enumVars.size(), 2);

        assertEquals(enumVars.get(0).get("name"), "foo");
        assertEquals(enumVars.get(0).get("value"), "1");
        assertEquals(enumVars.get(0).get("isString"), false);
        assertEquals(enumVars.get(0).get("description"), "the foo");

        assertEquals(enumVars.get(1).get("name"), "bar");
        assertEquals(enumVars.get(1).get("value"), "2");
        assertEquals(enumVars.get(1).get("isString"), false);
        assertEquals(enumVars.get(1).get("description"), "the bar");
    }

    // datetime (or primitive type) not yet supported in HTTP request body
    @Test(description = "returns DateTime when using `--model-name-prefix`")
    public void dateTest() {
        final OpenAPI openAPI = TestUtils.parseFlattenSpec("src/test/resources/2_0/datePropertyTest.json");
        final DefaultCodegen codegen = new DartClientCodegen();
        codegen.setModelNamePrefix("foo");
        codegen.setOpenAPI(openAPI);

        final String path = "/tests/dateResponse";
        final Operation p = openAPI.getPaths().get(path).getPost();
        final CodegenOperation op = codegen.fromOperation(path, "post", p, null);

        assertEquals(op.returnType, "DateTime");
        assertEquals(op.bodyParam.dataType, "DateTime");
    }
}
