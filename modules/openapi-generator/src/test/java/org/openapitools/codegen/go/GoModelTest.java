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

package org.openapitools.codegen.go;

import com.google.common.collect.Sets;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.parser.util.SchemaTypeUtil;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.DefaultCodegen;
import org.openapitools.codegen.TestUtils;
import org.openapitools.codegen.languages.GoClientCodegen;

import org.testng.annotations.DataProvider;


import java.io.File;

@SuppressWarnings("static-method")
public class GoModelTest {

    @Test(description = "convert a simple Go model")
    public void simpleModelTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperty("id", new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT))
                .addProperty("name", new StringSchema())
                .addProperty("createdAt", new DateTimeSchema())
                .addRequiredItem("id")
                .addRequiredItem("name");
        final DefaultCodegen codegen = new GoClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 3);
        assertEquals(cm.imports.size(), 1);

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "id");
        assertEquals(property1.dataType, "int64");
        assertEquals(property1.name, "Id");
        Assert.assertNull(property1.defaultValue);
        assertEquals(property1.baseType, "int64");
        assertTrue(property1.required);
        assertTrue(property1.isPrimitiveType);

        final CodegenProperty property2 = cm.vars.get(1);
        assertEquals(property2.baseName, "name");
        assertEquals(property2.dataType, "string");
        assertEquals(property2.name, "Name");
        Assert.assertNull(property2.defaultValue);
        assertEquals(property2.baseType, "string");
        assertTrue(property2.required);
        assertTrue(property2.isPrimitiveType);

        final CodegenProperty property3 = cm.vars.get(2);
        assertEquals(property3.baseName, "createdAt");
        assertEquals(property3.complexType, "time.Time");
        assertEquals(property3.dataType, "time.Time");
        assertEquals(property3.name, "CreatedAt");
        Assert.assertNull(property3.defaultValue);
        assertEquals(property3.baseType, "time.Time");
        Assert.assertFalse(property3.required);
    }

    @Test(description = "convert a model with list property")
    public void listPropertyTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperty("id", new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT))
                .addProperty("urls", new ArraySchema()
                        .items(new StringSchema()))
                .addRequiredItem("id");
        final DefaultCodegen codegen = new GoClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 2);

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "id");
        assertEquals(property1.dataType, "int64");
        assertEquals(property1.name, "Id");
        Assert.assertNull(property1.defaultValue);
        assertEquals(property1.baseType, "int64");
        assertTrue(property1.required);
        assertTrue(property1.isPrimitiveType);

        final CodegenProperty property2 = cm.vars.get(1);
        assertEquals(property2.baseName, "urls");
        assertEquals(property2.dataType, "[]string");
        assertEquals(property2.name, "Urls");
        assertEquals(property2.baseType, "array");
        assertEquals(property2.containerType, "array");
        Assert.assertFalse(property2.required);
        assertTrue(property2.isPrimitiveType);
    }

    @Test(description = "convert a model with a map property")
    public void mapPropertyTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperty("translations", new MapSchema()
                        .additionalProperties(new StringSchema()))
                .addRequiredItem("id");
        final DefaultCodegen codegen = new GoClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "translations");
        assertEquals(property1.dataType, "map[string]string");
        assertEquals(property1.name, "Translations");
        assertEquals(property1.baseType, "map");
        assertEquals(property1.containerType, "map");
        Assert.assertFalse(property1.required);
        assertTrue(property1.isContainer);
        assertTrue(property1.isPrimitiveType);
    }

    @Test(description = "convert a model with complex property")
    public void complexPropertyTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperty("children", new Schema().$ref("#/definitions/Children"));
        final DefaultCodegen codegen = new GoClientCodegen();
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
        assertEquals(property1.name, "Children");
        assertEquals(property1.baseType, "Children");
        Assert.assertFalse(property1.required);
    }

    @Test(description = "convert a model with complex list property")
    public void complexListProperty() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperty("children", new ArraySchema()
                        .items(new Schema().$ref("#/definitions/Children")));
        final DefaultCodegen codegen = new GoClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "children");
        assertEquals(property1.dataType, "[]Children");
        assertEquals(property1.name, "Children");
        assertEquals(property1.baseType, "array");
        assertEquals(property1.containerType, "array");
        Assert.assertFalse(property1.required);
        assertTrue(property1.isContainer);
    }

    @Test(description = "convert a model with complex map property")
    public void complexMapProperty() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperty("children", new MapSchema()
                        .additionalProperties(new Schema().$ref("#/definitions/Children")));
        final DefaultCodegen codegen = new GoClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);
        assertEquals(Sets.intersection(cm.imports, Sets.newHashSet("Children")).size(), 1);

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "children");
        assertEquals(property1.complexType, "Children");
        assertEquals(property1.dataType, "map[string]Children");
        assertEquals(property1.name, "Children");
        assertEquals(property1.baseType, "map");
        assertEquals(property1.containerType, "map");
        Assert.assertFalse(property1.required);
        assertTrue(property1.isContainer);
    }

    @Test(description = "convert an array model")
    public void arrayModelTest() {
        final Schema model = new ArraySchema()
                .items(new Schema().$ref("#/definitions/Children"))
                .description("an array model");
        final DefaultCodegen codegen = new GoClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "an array model");
        assertEquals(cm.vars.size(), 0);
        assertEquals(cm.imports.size(), 1);
    }

    @Test(description = "convert a map model")
    public void mapModelTest() {
        final Schema model = new Schema()
                .additionalProperties(new Schema().$ref("#/definitions/Children"))
                .description("a map model");
        final DefaultCodegen codegen = new GoClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a map model");
        assertEquals(cm.vars.size(), 0);
        assertEquals(cm.imports.size(), 1);
        assertEquals(Sets.intersection(cm.imports, Sets.newHashSet("Children")).size(), 1);
    }

    @Test(description = "convert file type and file schema models")
    public void filePropertyTest() {
        final DefaultCodegen codegen = new GoClientCodegen();
        final Schema model1 = new Schema().type("file");
        assertEquals(codegen.getSchemaType(model1), "*os.File");
        assertEquals(codegen.getTypeDeclaration(model1), "*os.File");

        final Schema model2 = new Schema().$ref("#/definitions/File");
        assertEquals(codegen.getSchemaType(model2), "File");
        assertEquals(codegen.getTypeDeclaration(model2), "File");

        final Schema model3 = new Schema().$ref("#/components/schemas/File");
        assertEquals(codegen.getSchemaType(model3), "File");
        assertEquals(codegen.getTypeDeclaration(model3), "File");
    }

    @DataProvider(name = "modelNames")
    public static Object[][] primeNumbers() {
        return new Object[][] {
            {"sample", "Sample"},
            {"sample_name", "SampleName"},
            {"sample__name", "SampleName"},
            {"/sample", "Sample"},
            {"\\sample", "Sample"},
            {"sample.name", "SampleName"},
            {"_sample", "Sample"},
        };
    }

    @Test(dataProvider = "modelNames", description = "avoid inner class")
    public void modelNameTest(String name, String expectedName) {
        final Schema model = new Schema();
        final DefaultCodegen codegen = new GoClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema(name, model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel(name, model);

        assertEquals(cm.name, name);
        assertEquals(cm.classname, expectedName);
    }

    @DataProvider(name = "modelMappedNames")
    public static Object[][] mappedNames() {
        return new Object[][] {
            {"mapped", "Remapped", "model_remapped.go"},
            {"mapped_underscore", "RemappedUnderscore", "model_remapped_underscore.go"},
        };
    }

    @Test(dataProvider = "modelMappedNames", description = "map model names")
    public void modelNameMappingsTest(String name, String expectedName, String expectedFilename) {
        final Schema model = new Schema();
        final DefaultCodegen codegen = new GoClientCodegen();
        codegen.modelNameMapping().put(name, expectedName);
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema(name, model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel(name, model);

        final String fn = codegen.modelFilename("model.mustache", name, "");
        assertEquals(fn, File.separator + expectedFilename);

        assertEquals(cm.name, name);
        assertEquals(cm.classname, expectedName);
    }
}
