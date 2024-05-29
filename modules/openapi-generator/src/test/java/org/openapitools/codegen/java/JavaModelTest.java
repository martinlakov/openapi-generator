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

import com.google.common.collect.Sets;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.parser.util.SchemaTypeUtil;
import org.openapitools.codegen.*;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.languages.features.DocumentationProviderFeatures;
import org.openapitools.codegen.languages.features.DocumentationProviderFeatures.AnnotationLibrary;

import org.testng.annotations.DataProvider;


import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class JavaModelTest {

    @Test(description = "convert a simple java model")
    public void simpleModelTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("id", new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT))
                .addProperties("name", new StringSchema()
                        .example("Tony"))
                .addProperties("createdAt", new DateTimeSchema())
                .addRequiredItem("id")
                .addRequiredItem("name");
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 3);

        final List<CodegenProperty> vars = cm.vars;

        final CodegenProperty property1 = vars.get(0);
        assertEquals(property1.baseName, "id");
        assertEquals(property1.nameInPascalCase, "Id");
        assertEquals(property1.nameInCamelCase, "id");
        assertEquals(property1.nameInSnakeCase, "ID");
        assertEquals(property1.getter, "getId");
        assertEquals(property1.setter, "setId");
        assertEquals(property1.dataType, "Long");
        assertEquals(property1.name, "id");
        Assert.assertNull(property1.defaultValue);
        assertEquals(property1.baseType, "Long");
        assertTrue(property1.required);
        Assert.assertFalse(property1.isContainer);

        final CodegenProperty property2 = vars.get(1);
        assertEquals(property2.baseName, "name");
        assertEquals(property2.nameInPascalCase, "Name");
        assertEquals(property2.nameInCamelCase, "name");
        assertEquals(property2.nameInSnakeCase, "NAME");
        assertEquals(property2.getter, "getName");
        assertEquals(property2.setter, "setName");
        assertEquals(property2.dataType, "String");
        assertEquals(property2.name, "name");
        Assert.assertNull(property2.defaultValue);
        assertEquals(property2.baseType, "String");
        assertEquals(property2.example, "Tony");
        assertTrue(property2.required);
        Assert.assertFalse(property2.isContainer);

        final CodegenProperty property3 = vars.get(2);
        assertEquals(property3.baseName, "createdAt");
        assertEquals(property3.nameInPascalCase, "CreatedAt");
        assertEquals(property3.nameInCamelCase, "createdAt");
        assertEquals(property3.nameInSnakeCase, "CREATED_AT");
        assertEquals(property3.getter, "getCreatedAt");
        assertEquals(property3.setter, "setCreatedAt");
        assertEquals(property3.dataType, "Date");
        assertEquals(property3.name, "createdAt");
        Assert.assertNull(property3.defaultValue);
        assertEquals(property3.baseType, "Date");
        Assert.assertFalse(property3.required);
        Assert.assertFalse(property3.isContainer);
    }

    @Test(description = "convert a model with list property")
    public void listPropertyTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("id", new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT))
                .addProperties("urls", new ArraySchema()
                        .items(new StringSchema()))
                .addRequiredItem("id");
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 2);

        final CodegenProperty property = cm.vars.get(1);
        assertEquals(property.baseName, "urls");
        assertEquals(property.getter, "getUrls");
        assertEquals(property.setter, "setUrls");
        assertEquals(property.dataType, "List<String>");
        assertEquals(property.name, "urls");
        assertEquals(property.defaultValue, "new ArrayList<>()");
        assertEquals(property.baseType, "List");
        assertEquals(property.containerType, "array");
        Assert.assertFalse(property.required);
        assertTrue(property.isContainer);
    }

    @Test(description = "convert a model with set property")
    public void setPropertyTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("id", new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT))
                .addProperties("urls", new ArraySchema()
                        .items(new StringSchema())
                        .uniqueItems(true))
                .addRequiredItem("id");
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 2);

        final CodegenProperty property = cm.vars.get(1);
        assertEquals(property.baseName, "urls");
        assertEquals(property.getter, "getUrls");
        assertEquals(property.setter, "setUrls");
        assertEquals(property.dataType, "Set<String>");
        assertEquals(property.name, "urls");
        assertEquals(property.defaultValue, "new LinkedHashSet<>()");
        assertEquals(property.baseType, "Set");
        assertEquals(property.containerType, "set");
        Assert.assertFalse(property.required);
        assertTrue(property.isContainer);
    }

    @Test(description = "convert a model with a map property")
    public void mapPropertyTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("translations", new MapSchema()
                        .additionalProperties(new StringSchema()))
                .addRequiredItem("id");
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "translations");
        assertEquals(property.getter, "getTranslations");
        assertEquals(property.setter, "setTranslations");
        assertEquals(property.dataType, "Map<String, String>");
        assertEquals(property.name, "translations");
        assertEquals(property.defaultValue, "new HashMap<>()");
        assertEquals(property.baseType, "Map");
        assertEquals(property.containerType, "map");
        Assert.assertFalse(property.required);
        assertTrue(property.isContainer);
    }

    @Test(description = "convert a model with a map with complex list property")
    public void mapWithListPropertyTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("translations", new MapSchema()
                        .additionalProperties(new ArraySchema().items(new Schema().$ref("Pet"))))
                .addRequiredItem("id");
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "translations");
        assertEquals(property.getter, "getTranslations");
        assertEquals(property.setter, "setTranslations");
        assertEquals(property.dataType, "Map<String, List<Pet>>");
        assertEquals(property.name, "translations");
        assertEquals(property.defaultValue, "new HashMap<>()");
        assertEquals(property.baseType, "Map");
        assertEquals(property.containerType, "map");
        Assert.assertFalse(property.required);
        assertTrue(property.isContainer);
    }

    @Test(description = "convert a model with a 2D list property")
    public void list2DPropertyTest() {
        final Schema model = new Schema()
                .name("sample")
                .addProperties("list2D", new ArraySchema().items(
                        new ArraySchema().items(new Schema().$ref("Pet"))));
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", model);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", model);

        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "list2D");
        assertEquals(property.getter, "getList2D");
        assertEquals(property.setter, "setList2D");
        assertEquals(property.dataType, "List<List<Pet>>");
        assertEquals(property.name, "list2D");
        assertEquals(property.defaultValue, "new ArrayList<>()");
        assertEquals(property.baseType, "List");
        assertEquals(property.containerType, "array");
        Assert.assertFalse(property.required);
        assertTrue(property.isContainer);
    }

    @Test(description = "convert a model with restricted characters")
    public void restrictedCharactersPropertiesTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("@Some:restricted%characters#to!handle+", new BooleanSchema());
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "@Some:restricted%characters#to!handle+");
        assertEquals(property.getter, "getAtSomeColonRestrictedPercentCharactersHashToExclamationHandlePlus");
        assertEquals(property.setter, "setAtSomeColonRestrictedPercentCharactersHashToExclamationHandlePlus");
        assertEquals(property.dataType, "Boolean");
        assertEquals(property.name, "atSomeColonRestrictedPercentCharactersHashToExclamationHandlePlus");
        Assert.assertNull(property.defaultValue);
        assertEquals(property.baseType, "Boolean");
        Assert.assertFalse(property.required);
        Assert.assertFalse(property.isContainer);
    }

    @Test(description = "convert a model with complex properties")
    public void complexPropertiesTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("children", new Schema().$ref("#/components/schemas/Children"));
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "children");
        assertEquals(property.getter, "getChildren");
        assertEquals(property.setter, "setChildren");
        assertEquals(property.dataType, "Children");
        assertEquals(property.name, "children");
        // "null" as default value for model
        assertEquals(property.defaultValue, "null");
        assertEquals(property.baseType, "Children");
        Assert.assertFalse(property.required);
        Assert.assertFalse(property.isContainer);
    }

    @Test(description = "convert a model with complex list property")
    public void complexListPropertyTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("children", new ArraySchema()
                        .items(new Schema().$ref("#/components/schemas/Children")));
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "children");
        assertEquals(property.complexType, "Children");
        assertEquals(property.getter, "getChildren");
        assertEquals(property.setter, "setChildren");
        assertEquals(property.dataType, "List<Children>");
        assertEquals(property.name, "children");
        assertEquals(property.defaultValue, "new ArrayList<>()");
        assertEquals(property.baseType, "List");
        assertEquals(property.containerType, "array");
        Assert.assertFalse(property.required);
        assertTrue(property.isContainer);
    }

    @Test(description = "convert a model with complex map property")
    public void complexMapPropertyTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("children", new MapSchema()
                        .additionalProperties(new Schema().$ref("#/components/schemas/Children")));
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);
        assertEquals(Sets.intersection(cm.imports, Sets.newHashSet("Map", "Children")).size(), 2);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "children");
        assertEquals(property.complexType, "Children");
        assertEquals(property.getter, "getChildren");
        assertEquals(property.setter, "setChildren");
        assertEquals(property.dataType, "Map<String, Children>");
        assertEquals(property.name, "children");
        assertEquals(property.defaultValue, "new HashMap<>()");
        assertEquals(property.baseType, "Map");
        assertEquals(property.containerType, "map");
        Assert.assertFalse(property.required);
        assertTrue(property.isContainer);
        assertTrue(property.isMap);
    }

    @Test(description = "convert a model with complex array property")
    public void complexArrayPropertyTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("children", new ArraySchema()
                        .items(new Schema().$ref("#/components/schemas/Children")));
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);
        assertEquals(Sets.intersection(cm.imports, Sets.newHashSet("List", "Children")).size(), 2);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "children");
        assertEquals(property.complexType, "Children");
        assertEquals(property.getter, "getChildren");
        assertEquals(property.setter, "setChildren");
        assertEquals(property.dataType, "List<Children>");
        assertEquals(property.name, "children");
        assertEquals(property.defaultValue, "new ArrayList<>()");
        assertEquals(property.baseType, "List");
        assertEquals(property.containerType, "array");
        Assert.assertFalse(property.required);
        assertTrue(property.isContainer);
        assertTrue(property.isArray);
    }

    @Test(description = "convert a model with complex set property")
    public void complexSetPropertyTest() {
        Schema set = new ArraySchema().items(new Schema().$ref("#/components/schemas/Children"));
        set.setUniqueItems(true); // set
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("children", set);
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);
        assertTrue(cm.imports.contains("Set"));

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "children");
        assertEquals(property.complexType, "Children");
        assertEquals(property.getter, "getChildren");
        assertEquals(property.setter, "setChildren");
        assertEquals(property.dataType, "Set<Children>");
        assertEquals(property.name, "children");
        assertEquals(property.defaultValue, "new LinkedHashSet<>()");
        assertEquals(property.baseType, "Set");
        assertEquals(property.containerType, "set");
        Assert.assertFalse(property.required);
        assertTrue(property.isContainer);
        assertTrue(property.getUniqueItemsBoolean());
    }
    @Test(description = "convert a model with an array property with item name")
    public void arrayModelWithItemNameTest() {
        final Schema propertySchema = new ArraySchema()
                .items(new Schema().$ref("#/components/schemas/Child"))
                .description("an array property");
        propertySchema.addExtension("x-item-name", "child");
        final Schema schema = new Schema()
                .type("object")
                .description("a sample model")
                .addProperties("children", propertySchema);


        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 1);
        assertEquals(Sets.intersection(cm.imports, Sets.newHashSet("List", "Child")).size(), 2);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "children");
        assertEquals(property.complexType, "Child");
        assertEquals(property.getter, "getChildren");
        assertEquals(property.setter, "setChildren");
        assertEquals(property.dataType, "List<Child>");
        assertEquals(property.name, "children");
        assertEquals(property.defaultValue, "new ArrayList<>()");
        assertEquals(property.baseType, "List");
        assertEquals(property.containerType, "array");
        Assert.assertFalse(property.required);
        assertTrue(property.isContainer);

        final CodegenProperty itemsProperty = property.items;
        assertEquals(itemsProperty.baseName, "child");
        assertEquals(itemsProperty.name, "child");
    }

    @Test(description = "convert an array model")
    public void arrayModelTest() {
        final Schema schema = new ArraySchema()
                .items(new Schema().name("elobjeto").$ref("#/components/schemas/Children"))
                .name("arraySchema")
                .description("an array model");
        final JavaClientCodegen codegen = new JavaClientCodegen();
        codegen.setAnnotationLibrary(AnnotationLibrary.SWAGGER1);
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "an array model");
        assertEquals(cm.vars.size(), 0);
        assertEquals(cm.parent, "ArrayList<Children>");
        assertEquals(cm.imports.size(), 4);
        assertEquals(Sets.intersection(cm.imports, Sets.newHashSet("ApiModel", "List", "ArrayList", "Children")).size(), 4);
    }

    @Test(description = "convert a set model")
    public void setModelTest() {
        final Schema schema = new ArraySchema()
                .items(new Schema().name("elobjeto").$ref("#/components/schemas/Children"))
                .uniqueItems(true)
                .name("arraySchema")
                .description("an array model");
        final JavaClientCodegen codegen = new JavaClientCodegen();
        codegen.setAnnotationLibrary(AnnotationLibrary.SWAGGER1);
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "an array model");
        assertEquals(cm.vars.size(), 0);
        assertEquals(cm.parent, "LinkedHashSet<Children>");
        assertEquals(cm.imports.size(), 4);
        assertEquals(Sets.intersection(cm.imports, Sets.newHashSet("ApiModel", "Set", "LinkedHashSet", "Children")).size(), 4);
    }

    @Test(description = "convert a map model")
    public void mapModelTest() {
        final Schema schema = new Schema()
                .description("a map model")
                .additionalProperties(new Schema().$ref("#/components/schemas/Children"));
        final JavaClientCodegen codegen = new JavaClientCodegen();
        codegen.setAnnotationLibrary(AnnotationLibrary.SWAGGER1);
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a map model");
        assertEquals(cm.vars.size(), 0);
        assertEquals(cm.parent, "HashMap<String, Children>");
        assertEquals(cm.imports.size(), 4);
        assertEquals(Sets.intersection(cm.imports, Sets.newHashSet("ApiModel", "Map", "HashMap", "Children")).size(), 4);
    }

    @Test(description = "convert a model with upper-case property names")
    public void upperCaseNamesTest() {
        final Schema schema = new Schema()
                .description("a model with upper-case property names")
                .addProperties("NAME", new StringSchema())
                .addRequiredItem("NAME");
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "NAME");
        assertEquals(property.getter, "getNAME");
        assertEquals(property.setter, "setNAME");
        assertEquals(property.dataType, "String");
        assertEquals(property.name, "NAME");
        Assert.assertNull(property.defaultValue);
        assertEquals(property.baseType, "String");
        assertTrue(property.required);
        Assert.assertFalse(property.isContainer);
    }

    @Test(description = "convert a model with upper-case property names and Numbers")
    public void upperCaseNamesNumbersTest() {
        final Schema schema = new Schema()
                .description("a model with upper-case property names and numbers")
                .addProperties("NAME1", new StringSchema())
                .addRequiredItem("NAME1");
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "NAME1");
        assertEquals(property.getter, "getNAME1");
        assertEquals(property.setter, "setNAME1");
        assertEquals(property.dataType, "String");
        assertEquals(property.name, "NAME1");
        Assert.assertNull(property.defaultValue);
        assertEquals(property.baseType, "String");
        assertTrue(property.required);
        Assert.assertFalse(property.isContainer);
    }

    @Test(description = "convert a model with a 2nd char upper-case property names")
    public void secondCharUpperCaseNamesTest() {
        final Schema schema = new Schema()
                .description("a model with a 2nd char upper-case property names")
                .addProperties("pId", new StringSchema())
                .addRequiredItem("pId");
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "pId");
        assertEquals(property.getter, "getpId");
        assertEquals(property.setter, "setpId");
        assertEquals(property.dataType, "String");
        assertEquals(property.name, "pId");
        Assert.assertNull(property.defaultValue);
        assertEquals(property.baseType, "String");
        assertTrue(property.required);
        Assert.assertFalse(property.isContainer);
    }

    @Test(description = "convert a model starting with two upper-case letter property names")
    public void firstTwoUpperCaseLetterNamesTest() {
        final Schema schema = new Schema()
                .description("a model with a property name starting with two upper-case letters")
                .addProperties("ATTName", new StringSchema())
                .addRequiredItem("ATTName");
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "ATTName");
        assertEquals(property.getter, "getAtTName");
        assertEquals(property.setter, "setAtTName");
        assertEquals(property.dataType, "String");
        assertEquals(property.name, "atTName");
        Assert.assertNull(property.defaultValue);
        assertEquals(property.baseType, "String");
        assertTrue(property.required);
        Assert.assertFalse(property.isContainer);
    }

    @Test(description = "convert a model with an all upper-case letter and one non letter property names")
    public void allUpperCaseOneNonLetterNamesTest() {
        final Schema schema = new Schema()
                .description("a model with a property name starting with two upper-case letters")
                .addProperties("ATT_NAME", new StringSchema())
                .addRequiredItem("ATT_NAME");
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "ATT_NAME");
        assertEquals(property.getter, "getATTNAME");
        assertEquals(property.setter, "setATTNAME");
        assertEquals(property.dataType, "String");
        assertEquals(property.name, "ATT_NAME");
        Assert.assertNull(property.defaultValue);
        assertEquals(property.baseType, "String");
        assertTrue(property.required);
        Assert.assertFalse(property.isContainer);
    }

    @Test(description = "convert hyphens per issue 503")
    public void hyphensTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("created-at", new DateTimeSchema());
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "created-at");
        assertEquals(property.getter, "getCreatedAt");
        assertEquals(property.setter, "setCreatedAt");
        assertEquals(property.name, "createdAt");
    }

    @Test(description = "convert query[password] to queryPassword")
    public void squareBracketsTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("query[password]", new StringSchema());
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "query[password]");
        assertEquals(property.getter, "getQueryPassword");
        assertEquals(property.setter, "setQueryPassword");
        assertEquals(property.name, "queryPassword");
    }

    @Test(description = "properly escape names per 567")
    public void escapeNamesTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties("created-at", new DateTimeSchema());
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("with.dots", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("with.dots", schema);

        assertEquals(cm.classname, "WithDots");
    }

    @Test(description = "convert a model with binary data")
    public void binaryDataTest() {
        final Schema schema = new Schema()
                .description("model with binary")
                .addProperties("inputBinaryData", new ByteArraySchema());
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "inputBinaryData");
        assertEquals(property.getter, "getInputBinaryData");
        assertEquals(property.setter, "setInputBinaryData");
        assertEquals(property.dataType, "byte[]");
        assertEquals(property.name, "inputBinaryData");
        Assert.assertNull(property.defaultValue);
        assertEquals(property.baseType, "byte[]");
        Assert.assertFalse(property.required);
        Assert.assertFalse(property.isContainer);
    }

    @Test(description = "translate an invalid param name")
    public void invalidParamNameTest() {
        final Schema schema = new Schema()
                .description("a model with a 2nd char upper-case property names")
                .addProperties("_", new StringSchema());
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.vars.size(), 1);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, "_");
        assertEquals(property.getter, "getU");
        assertEquals(property.setter, "setU");
        assertEquals(property.dataType, "String");
        assertEquals(property.name, "u");
        Assert.assertNull(property.defaultValue);
        assertEquals(property.baseType, "String");
        Assert.assertFalse(property.isContainer);
    }

    @Test(description = "convert a parameter")
    public void convertParameterTest() {
        OpenAPI openAPI = TestUtils.createOpenAPI();
        final Parameter parameter = new QueryParameter()
                .description("this is a description")
                .name("limit")
                .required(true);
        final DefaultCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        final CodegenParameter cm = codegen.fromParameter(parameter, null);

        Assert.assertNull(cm.allowableValues);
        assertEquals(cm.description, "this is a description");
    }

    @Test(description = "types used by inner properties should be imported")
    public void mapWithAnListOfBigDecimalTest() {
        Schema decimal = new StringSchema();
        decimal.setFormat("number");

        Schema schema1 = new Schema()
                .description("model with Map<String, List<BigDecimal>>")
                .addProperties("map", new MapSchema()
                        .additionalProperties(new ArraySchema().items(decimal)));
        OpenAPI openAPI1 = TestUtils.createOpenAPIWithOneSchema("sample", schema1);
        JavaClientCodegen codegen1 = new JavaClientCodegen();
        codegen1.setOpenAPI(openAPI1);
        final CodegenModel cm1 = codegen1.fromModel("sample", schema1);
        assertEquals(cm1.vars.get(0).dataType, "Map<String, List<BigDecimal>>");
        assertTrue(cm1.imports.contains("BigDecimal"));

        Schema schema2 = new Schema()
                .description("model with Map<String, Map<String, List<BigDecimal>>>")
                .addProperties("map", new MapSchema()
                        .additionalProperties(new MapSchema()
                                .additionalProperties(new ArraySchema().items(decimal))));
        OpenAPI openAPI2 = TestUtils.createOpenAPIWithOneSchema("sample", schema2);
        JavaClientCodegen codegen2 = new JavaClientCodegen();
        codegen2.setOpenAPI(openAPI2);
        final CodegenModel cm2 = codegen2.fromModel("sample", schema2);
        assertEquals(cm2.vars.get(0).dataType, "Map<String, Map<String, List<BigDecimal>>>");
        assertTrue(cm2.imports.contains("BigDecimal"));
    }

    @DataProvider(name = "modelNames")
    public static Object[][] primeNumbers() {
        return new Object[][]{
                {"sample", "Sample"},
                {"sample_name", "SampleName"},
                {"sample__name", "SampleName"},
                {"/sample", "Sample"},
                {"\\sample", "Sample"},
                {"sample.name", "SampleName"},
                {"_sample", "Sample"},
                {"Sample", "Sample"},
        };
    }

    @Test(dataProvider = "modelNames", description = "avoid inner class")
    public void modelNameTest(String name, String expectedName) {
        final Schema schema = new Schema();
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema(name, schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel(name, schema);

        assertEquals(cm.name, name);
        assertEquals(cm.classname, expectedName);
    }

    @DataProvider(name = "classProperties")
    public static Object[][] classProperties() {
        return new Object[][]{
                {"class", "getPropertyClass", "setPropertyClass", "propertyClass"},
                {"_class", "getPropertyClass", "setPropertyClass", "propertyClass"},
                {"__class", "getPropertyClass", "setPropertyClass", "propertyClass"}
        };
    }

    @Test(dataProvider = "classProperties", description = "handle 'class' properties")
    public void classPropertyTest(String baseName, String getter, String setter, String name) {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperties(baseName, new StringSchema());
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        final CodegenProperty property = cm.vars.get(0);
        assertEquals(property.baseName, baseName);
        assertEquals(property.getter, getter);
        assertEquals(property.setter, setter);
        assertEquals(property.name, name);
    }


    @Test(description = "test models with xml")
    public void modelWithXmlTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .xml(new XML()
                        .prefix("my")
                        .namespace("xmlNamespace")
                        .name("customXmlName"))
                .addProperties("id", new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT))
                .addProperties("name", new StringSchema()
                        .example("Tony")
                        .xml(new XML()
                                .attribute(true)
                                .prefix("my")
                                .name("myName")))
                .addProperties("createdAt", new DateTimeSchema()
                        .xml(new XML()
                                .prefix("my")
                                .namespace("myNamespace")
                                .name("myCreatedAt")))
                .addRequiredItem("id")
                .addRequiredItem("name");
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.xmlPrefix, "my");
        assertEquals(cm.xmlName, "customXmlName");
        assertEquals(cm.xmlNamespace, "xmlNamespace");
        assertEquals(cm.vars.size(), 3);

        final List<CodegenProperty> vars = cm.vars;

        final CodegenProperty property2 = vars.get(1);
        assertEquals(property2.baseName, "name");
        assertEquals(property2.getter, "getName");
        assertEquals(property2.setter, "setName");
        assertEquals(property2.dataType, "String");
        assertEquals(property2.name, "name");
        Assert.assertNull(property2.defaultValue);
        assertEquals(property2.baseType, "String");
        assertEquals(property2.example, "Tony");
        assertTrue(property2.required);
        Assert.assertFalse(property2.isContainer);
        assertTrue(property2.isXmlAttribute);
        assertEquals(property2.xmlName, "myName");
        Assert.assertNull(property2.xmlNamespace);

        final CodegenProperty property3 = vars.get(2);
        assertEquals(property3.baseName, "createdAt");
        assertEquals(property3.getter, "getCreatedAt");
        assertEquals(property3.setter, "setCreatedAt");
        assertEquals(property3.dataType, "Date");
        assertEquals(property3.name, "createdAt");
        Assert.assertNull(property3.defaultValue);
        assertEquals(property3.baseType, "Date");
        Assert.assertFalse(property3.required);
        Assert.assertFalse(property3.isContainer);
        Assert.assertFalse(property3.isXmlAttribute);
        assertEquals(property3.xmlName, "myCreatedAt");
        assertEquals(property3.xmlNamespace, "myNamespace");
        assertEquals(property3.xmlPrefix, "my");
    }

    @Test(description = "test models with wrapped xml")
    public void modelWithWrappedXmlTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .xml(new XML()
                        .prefix("my")
                        .namespace("xmlNamespace")
                        .name("customXmlName"))
                .addProperties("id", new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT))
                .addProperties("array", new ArraySchema()
                        .items(new StringSchema()
                                .xml(new XML()
                                        .name("i")))
                        .xml(new XML()
                                .prefix("my")
                                .wrapped(true)
                                .namespace("myNamespace")
                                .name("xmlArray")))
                .addRequiredItem("id");
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.xmlPrefix, "my");
        assertEquals(cm.xmlName, "customXmlName");
        assertEquals(cm.xmlNamespace, "xmlNamespace");
        assertEquals(cm.vars.size(), 2);

        final List<CodegenProperty> vars = cm.vars;

        final CodegenProperty property2 = vars.get(1);
        assertEquals(property2.baseName, "array");
        assertEquals(property2.getter, "getArray");
        assertEquals(property2.setter, "setArray");
        assertEquals(property2.dataType, "List<String>");
        assertEquals(property2.name, "array");
        assertEquals(property2.defaultValue, "new ArrayList<>()");
        assertEquals(property2.baseType, "List");
        assertTrue(property2.isContainer);
        assertTrue(property2.isXmlWrapped);
        assertEquals(property2.xmlName, "xmlArray");
        Assert.assertNotNull(property2.xmlNamespace);
        Assert.assertNotNull(property2.items);
        CodegenProperty items = property2.items;
        assertEquals(items.xmlName, "i");
        assertEquals(items.baseName, "array");
    }

    @Test(description = "convert a boolean parameter")
    public void booleanPropertyTest() {
        OpenAPI openAPI = TestUtils.createOpenAPI();
        final BooleanSchema property = new BooleanSchema();
        final JavaClientCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        codegen.setBooleanGetterPrefix("is");
        final CodegenProperty cp = codegen.fromProperty("property", property);

        assertEquals(cp.baseName, "property");
        assertEquals(cp.dataType, "Boolean");
        assertEquals(cp.name, "property");
        assertEquals(cp.baseType, "Boolean");
        Assert.assertFalse(cp.isContainer);
        assertTrue(cp.isBoolean);
        assertEquals(cp.getter, "isProperty");
    }

    @Test(description = "convert an integer property")
    public void integerPropertyTest() {
        OpenAPI openAPI = TestUtils.createOpenAPI();
        final IntegerSchema property = new IntegerSchema();
        final DefaultCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        final CodegenProperty cp = codegen.fromProperty("property", property);

        assertEquals(cp.baseName, "property");
        assertEquals(cp.dataType, "Integer");
        assertEquals(cp.name, "property");
        assertEquals(cp.baseType, "Integer");
        Assert.assertFalse(cp.isContainer);
        assertTrue(cp.isInteger);
        Assert.assertFalse(cp.isLong);
        assertEquals(cp.getter, "getProperty");
    }

    @Test(description = "convert a long property")
    public void longPropertyTest() {
        OpenAPI openAPI = TestUtils.createOpenAPI();
        final IntegerSchema property = new IntegerSchema().format("int64");
        final DefaultCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        final CodegenProperty cp = codegen.fromProperty("property", property);

        assertEquals(cp.baseName, "property");
        assertEquals(cp.nameInPascalCase, "Property");
        assertEquals(cp.nameInCamelCase, "property");
        assertEquals(cp.nameInSnakeCase, "PROPERTY");
        assertEquals(cp.dataType, "Long");
        assertEquals(cp.name, "property");
        assertEquals(cp.baseType, "Long");
        Assert.assertFalse(cp.isContainer);
        assertTrue(cp.isLong);
        Assert.assertFalse(cp.isInteger);
        assertEquals(cp.getter, "getProperty");
    }

    @Test(description = "convert an integer property in a referenced schema")
    public void integerPropertyInReferencedSchemaTest() {
        final IntegerSchema longProperty = new IntegerSchema().format("int32");
        final Schema testSchema = new ObjectSchema()
                .addProperties("Integer1", new Schema<>().$ref("#/components/schemas/IntegerProperty"))
                .addProperties("Integer2", new IntegerSchema().format("int32"));
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("IntegerProperty", longProperty);
        final DefaultCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("test", testSchema);

        assertEquals(cm.vars.size(), 2);

        CodegenProperty cp1 = cm.vars.get(0);
        assertEquals(cp1.baseName, "Integer1");
        assertEquals(cp1.nameInPascalCase, "Integer1");
        assertEquals(cp1.nameInCamelCase, "integer1");
        assertEquals(cp1.nameInSnakeCase, "INTEGER1");
        assertEquals(cp1.dataType, "Integer");
        assertEquals(cp1.name, "integer1");
        assertEquals(cp1.baseType, "Integer");
        assertEquals(cp1.getter, "getInteger1");

        CodegenProperty cp2 = cm.vars.get(1);
        assertEquals(cp2.baseName, "Integer2");
        assertEquals(cp2.nameInPascalCase, "Integer2");
        assertEquals(cp2.nameInCamelCase, "integer2");
        assertEquals(cp2.nameInSnakeCase, "INTEGER2");
        assertEquals(cp2.dataType, "Integer");
        assertEquals(cp2.name, "integer2");
        assertEquals(cp2.baseType, "Integer");
        assertEquals(cp2.getter, "getInteger2");
    }

    @Test(description = "convert a long property in a referenced schema")
    public void longPropertyInReferencedSchemaTest() {
        final IntegerSchema longProperty = new IntegerSchema().format("int64");
        final Schema TestSchema = new ObjectSchema()
                .addProperties("Long1", new Schema<>().$ref("#/components/schemas/LongProperty"))
                .addProperties("Long2", new IntegerSchema().format("int64"));
        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("LongProperty", longProperty);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("test", TestSchema);

        assertEquals(cm.vars.size(), 2);

        CodegenProperty cp1 = cm.vars.get(0);
        assertEquals(cp1.baseName, "Long1");
        assertEquals(cp1.dataType, "Long");
        assertEquals(cp1.name, "long1");
        assertEquals(cp1.baseType, "Long");
        assertEquals(cp1.getter, "getLong1");

        CodegenProperty cp2 = cm.vars.get(1);
        assertEquals(cp2.baseName, "Long2");
        assertEquals(cp2.dataType, "Long");
        assertEquals(cp2.name, "long2");
        assertEquals(cp2.baseType, "Long");
        assertEquals(cp2.getter, "getLong2");
    }

    @Test(description = "convert string property")
    public void stringPropertyTest() {
        OpenAPI openAPI = TestUtils.createOpenAPI();
        final Schema property = new StringSchema().maxLength(10).minLength(3).pattern("^[A-Z]+$");
        final DefaultCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        final CodegenProperty cp = codegen.fromProperty("somePropertyWithMinMaxAndPattern", property);

        assertEquals(cp.baseName, "somePropertyWithMinMaxAndPattern");
        assertEquals(cp.nameInPascalCase, "SomePropertyWithMinMaxAndPattern");
        assertEquals(cp.nameInCamelCase, "somePropertyWithMinMaxAndPattern");
        assertEquals(cp.nameInSnakeCase, "SOME_PROPERTY_WITH_MIN_MAX_AND_PATTERN");
        assertEquals(cp.dataType, "String");
        assertEquals(cp.name, "somePropertyWithMinMaxAndPattern");
        assertEquals(cp.baseType, "String");
        Assert.assertFalse(cp.isContainer);
        Assert.assertFalse(cp.isLong);
        Assert.assertFalse(cp.isInteger);
        assertTrue(cp.isString);
        assertEquals(cp.getter, "getSomePropertyWithMinMaxAndPattern");
        assertEquals(cp.minLength, Integer.valueOf(3));
        assertEquals(cp.maxLength, Integer.valueOf(10));
        assertEquals(cp.pattern, "^[A-Z]+$");
    }
    
    @Test(description = "convert string property with password format")
    public void stringPropertyPasswordFormatTest() {
        OpenAPI openAPI = TestUtils.createOpenAPI();
        final Schema property = new StringSchema().format("password");
        final DefaultCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        
        final CodegenProperty cp = codegen.fromProperty("somePropertyWithPasswordFormat", property);
        assertEquals(cp.isPassword, true);
    }

    @Test(description = "convert string property in an object")
    public void stringPropertyInObjectTest() {
        final Schema property = new StringSchema().maxLength(10).minLength(3).pattern("^[A-Z]+$");
        final Schema myObject = new ObjectSchema().addProperties("somePropertyWithMinMaxAndPattern", property);

        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("myObject", myObject);
        codegen.setOpenAPI(openAPI);
        CodegenModel cm = codegen.fromModel("myObject", myObject);

        assertEquals(cm.getVars().size(), 1);
        CodegenProperty cp = cm.getVars().get(0);
        assertEquals(cp.baseName, "somePropertyWithMinMaxAndPattern");
        assertEquals(cp.nameInPascalCase, "SomePropertyWithMinMaxAndPattern");
        assertEquals(cp.nameInCamelCase, "somePropertyWithMinMaxAndPattern");
        assertEquals(cp.nameInSnakeCase, "SOME_PROPERTY_WITH_MIN_MAX_AND_PATTERN");
        assertEquals(cp.dataType, "String");
        assertEquals(cp.name, "somePropertyWithMinMaxAndPattern");
        assertEquals(cp.baseType, "String");
        Assert.assertFalse(cp.isContainer);
        Assert.assertFalse(cp.isLong);
        Assert.assertFalse(cp.isInteger);
        assertTrue(cp.isString);
        assertEquals(cp.getter, "getSomePropertyWithMinMaxAndPattern");
        assertEquals(cp.minLength, Integer.valueOf(3));
        assertEquals(cp.maxLength, Integer.valueOf(10));
        assertEquals(cp.pattern, "^[A-Z]+$");
    }

    @Test(description = "convert referenced string property in an object")
    public void stringPropertyReferencedInObjectTest() {
        final Schema property = new StringSchema().maxLength(10).minLength(3).pattern("^[A-Z]+$");
        final Schema myObject = new ObjectSchema().addProperties("somePropertyWithMinMaxAndPattern", new ObjectSchema().$ref("refObj"));

        final DefaultCodegen codegen = new JavaClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPI();
        openAPI.setComponents(new Components()
                .addSchemas("myObject", myObject)
                .addSchemas("refObj", property)
        );
        codegen.setOpenAPI(openAPI);
        CodegenModel cm = codegen.fromModel("myObject", myObject);

        assertEquals(cm.getVars().size(), 1);
        CodegenProperty cp = cm.getVars().get(0);
        assertEquals(cp.baseName, "somePropertyWithMinMaxAndPattern");
        assertEquals(cp.nameInPascalCase, "SomePropertyWithMinMaxAndPattern");
        assertEquals(cp.nameInCamelCase, "somePropertyWithMinMaxAndPattern");
        assertEquals(cp.nameInSnakeCase, "SOME_PROPERTY_WITH_MIN_MAX_AND_PATTERN");
        assertEquals(cp.dataType, "String");
        assertEquals(cp.name, "somePropertyWithMinMaxAndPattern");
        assertEquals(cp.baseType, "String");
        Assert.assertFalse(cp.isContainer);
        Assert.assertFalse(cp.isLong);
        Assert.assertFalse(cp.isInteger);
        assertTrue(cp.isString);
        assertEquals(cp.getter, "getSomePropertyWithMinMaxAndPattern");
        assertEquals(cp.minLength, Integer.valueOf(3));
        assertEquals(cp.maxLength, Integer.valueOf(10));
        assertEquals(cp.pattern, "^[A-Z]+$");
    }

    @Test(description = "convert an array schema")
    public void arraySchemaTest() {
        final Schema testSchema = new ObjectSchema()
                .addProperties("pets", new ArraySchema()
                        .items(new Schema<>().$ref("#/components/schemas/Pet")));
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("Pet", new ObjectSchema().addProperties("name", new StringSchema()));
        final DefaultCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("test", testSchema);

        assertEquals(cm.vars.size(), 1);
        CodegenProperty cp1 = cm.vars.get(0);
        assertEquals(cp1.baseName, "pets");
        assertEquals(cp1.dataType, "List<Pet>");
        assertEquals(cp1.name, "pets");
        assertEquals(cp1.baseType, "List");
        assertTrue(cp1.isContainer);
        assertTrue(cp1.isArray);
        Assert.assertFalse(cp1.isMap);
        assertEquals(cp1.getter, "getPets");
        assertEquals(cp1.items.baseType, "Pet");

        assertTrue(cm.imports.contains("List"));
        assertTrue(cm.imports.contains("Pet"));
    }

    @Test(description = "convert an array schema in a RequestBody")
    public void arraySchemaTestInRequestBody() {
        final Schema testSchema = new ArraySchema()
                .items(new Schema<>().$ref("#/components/schemas/Pet"));
        Operation operation = new Operation()
                .requestBody(new RequestBody()
                        .content(new Content().addMediaType("application/json",
                                new MediaType().schema(testSchema))))
                .responses(
                        new ApiResponses().addApiResponse("204", new ApiResponse()
                                .description("Ok response")));
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("Pet", new ObjectSchema().addProperties("name", new StringSchema()));
        final DefaultCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        final CodegenOperation co = codegen.fromOperation("testSchema", "GET", operation, null);

        assertEquals(co.bodyParams.size(), 1);
        CodegenParameter cp1 = co.bodyParams.get(0);
        assertEquals(cp1.baseType, "Pet");
        assertEquals(cp1.dataType, "List<Pet>");
        assertTrue(cp1.isContainer);
        assertTrue(cp1.isArray);
        Assert.assertFalse(cp1.isMap);
        assertEquals(cp1.items.baseType, "Pet");
        assertEquals(cp1.items.complexType, "Pet");
        assertEquals(cp1.items.dataType, "Pet");

        assertEquals(co.responses.size(), 1);

        assertTrue(co.imports.contains("List"));
        assertTrue(co.imports.contains("Pet"));
    }

    @Test(description = "convert an array schema in an ApiResponse")
    public void arraySchemaTestInOperationResponse() {
        final Schema testSchema = new ArraySchema()
                .items(new Schema<>().$ref("#/components/schemas/Pet"));
        Operation operation = new Operation().responses(
                new ApiResponses().addApiResponse("200", new ApiResponse()
                        .description("Ok response")
                        .content(new Content().addMediaType("application/json",
                                new MediaType().schema(testSchema)))));
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("Pet", new ObjectSchema().addProperties("name", new StringSchema()));
        final DefaultCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        final CodegenOperation co = codegen.fromOperation("testSchema", "GET", operation, null);

        assertEquals(co.responses.size(), 1);
        CodegenResponse cr = co.responses.get(0);
        assertEquals(cr.baseType, "Pet");
        assertEquals(cr.dataType, "List<Pet>");
        assertEquals(cr.containerType, "array");

        assertTrue(co.imports.contains("Pet"));
    }

    @Test(description = "convert an array of array schema")
    public void arrayOfArraySchemaTest() {
        final Schema testSchema = new ObjectSchema()
                .addProperties("pets", new ArraySchema()
                        .items(new ArraySchema()
                                .items(new Schema<>().$ref("#/components/schemas/Pet"))));
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("Pet", new ObjectSchema().addProperties("name", new StringSchema()));
        final DefaultCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("test", testSchema);

        assertEquals(cm.vars.size(), 1);
        CodegenProperty cp1 = cm.vars.get(0);
        assertEquals(cp1.baseName, "pets");
        assertEquals(cp1.dataType, "List<List<Pet>>");
        assertEquals(cp1.name, "pets");
        assertEquals(cp1.baseType, "List");
        assertEquals(cp1.getter, "getPets");

        assertTrue(cm.imports.contains("List"));
        assertTrue(cm.imports.contains("Pet"));
    }

    @Test(description = "convert an array of array schema in a RequestBody")
    public void arrayOfArraySchemaTestInRequestBody() {
        final Schema testSchema = new ArraySchema()
                .items(new ArraySchema()
                        .items(new Schema<>().$ref("#/components/schemas/Pet")));
        Operation operation = new Operation()
                .requestBody(new RequestBody()
                        .content(new Content().addMediaType("application/json",
                                new MediaType().schema(testSchema))))
                .responses(
                        new ApiResponses().addApiResponse("204", new ApiResponse()
                                .description("Ok response")));
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("Pet", new ObjectSchema().addProperties("name", new StringSchema()));
        final DefaultCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        final CodegenOperation co = codegen.fromOperation("testSchema", "GET", operation, null);

        assertEquals(co.bodyParams.size(), 1);
        CodegenParameter cp1 = co.bodyParams.get(0);
        assertEquals(cp1.baseType, "List");
        assertEquals(cp1.dataType, "List<List<Pet>>");
        assertTrue(cp1.isContainer);
        assertTrue(cp1.isArray);
        Assert.assertFalse(cp1.isMap);
        assertEquals(cp1.items.baseType, "List");
        assertEquals(cp1.items.complexType, "Pet");
        assertEquals(cp1.items.dataType, "List<Pet>");
        assertEquals(cp1.items.items.baseType, "Pet");
        assertEquals(cp1.items.items.complexType, "Pet");
        assertEquals(cp1.items.items.dataType, "Pet");

        assertEquals(co.responses.size(), 1);

        assertTrue(co.imports.contains("Pet"));
        assertTrue(co.imports.contains("List"));
    }

    @Test(description = "convert an array schema in an ApiResponse")
    public void arrayOfArraySchemaTestInOperationResponse() {
        final Schema testSchema = new ArraySchema()
                .items(new ArraySchema()
                        .items(new Schema<>().$ref("#/components/schemas/Pet")));
        Operation operation = new Operation().responses(
                new ApiResponses().addApiResponse("200", new ApiResponse()
                        .description("Ok response")
                        .content(new Content().addMediaType("application/json",
                                new MediaType().schema(testSchema)))));
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("Pet", new ObjectSchema().addProperties("name", new StringSchema()));
        final DefaultCodegen codegen = new JavaClientCodegen();
        codegen.setOpenAPI(openAPI);
        final CodegenOperation co = codegen.fromOperation("testSchema", "GET", operation, null);

        assertEquals(co.responses.size(), 1);
        CodegenResponse cr = co.responses.get(0);
        assertEquals(cr.baseType, "Pet");
        assertEquals(cr.dataType, "List<List<Pet>>");
        assertEquals(cr.containerType, "array");

        assertTrue(co.imports.contains("Pet"));
    }

    @Test
    public void generateModel() throws Exception {
        String inputSpec = "src/test/resources/3_0/petstore.json";

        final File output = Files.createTempDirectory("test").toFile();
        output.deleteOnExit();

        assertTrue(new File(inputSpec).exists());

        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName("java")
                .setLibrary("jersey2")
                //.addAdditionalProperty("withXml", true)
                .addAdditionalProperty(CodegenConstants.SERIALIZABLE_MODEL, true)
                .setInputSpec(inputSpec)
                .setOutputDir(output.getAbsolutePath());

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        new DefaultGenerator().opts(clientOptInput).generate();

        File orderFile = new File(output, "src/main/java/org/openapitools/client/model/Order.java");
        assertTrue(orderFile.exists());
    }

    @Test
    public void generateEmpty() throws Exception {
        String inputSpec = "src/test/resources/3_0/ping.yaml";

        final File output = Files.createTempDirectory("test").toFile();
        output.deleteOnExit();
        assertTrue(new File(inputSpec).exists());

        JavaClientCodegen config = new org.openapitools.codegen.languages.JavaClientCodegen();
        config.setHideGenerationTimestamp(true);
        config.setOutputDir(output.getAbsolutePath());

        final OpenAPI openAPI = TestUtils.parseFlattenSpec(inputSpec);

        final ClientOptInput opts = new ClientOptInput();
        opts.config(config);
        opts.openAPI(openAPI);
        new DefaultGenerator().opts(opts).generate();

        File orderFile = new File(output, "src/main/java/org/openapitools/client/api/DefaultApi.java");
        assertTrue(orderFile.exists());
    }
}
