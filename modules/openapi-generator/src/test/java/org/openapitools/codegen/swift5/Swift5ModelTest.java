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

package org.openapitools.codegen.swift5;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.parser.util.SchemaTypeUtil;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.DefaultCodegen;
import org.openapitools.codegen.TestUtils;
import org.openapitools.codegen.languages.Swift5ClientCodegen;



@SuppressWarnings("static-method")
public class Swift5ModelTest {

    @Test(description = "convert a simple java model", enabled = true)
    public void simpleModelTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperty("id", new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT))
                .addProperty("name", new StringSchema())
                .addProperty("createdAt", new DateTimeSchema())
                .addProperty("binary", new BinarySchema())
                .addProperty("byte", new ByteArraySchema())
                .addProperty("uuid", new UUIDSchema())
                .addProperty("dateOfBirth", new DateSchema())
                .addRequiredItem("id")
                .addRequiredItem("name")
                .discriminator(new Discriminator().propertyName("test"));
        final DefaultCodegen codegen = new Swift5ClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        final CodegenModel cm = codegen.fromModel("sample", schema);

        assertEquals(cm.name, "sample");
        assertEquals(cm.classname, "Sample");
        assertEquals(cm.description, "a sample model");
        assertEquals(cm.vars.size(), 7);
        assertEquals(cm.getDiscriminatorName(),"test");

        final CodegenProperty property1 = cm.vars.get(0);
        assertEquals(property1.baseName, "id");
        assertEquals(property1.dataType, "Int64");
        assertEquals(property1.name, "id");
        Assert.assertNull(property1.defaultValue);
        assertEquals(property1.baseType, "Int64");
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
        assertEquals(property3.dataType, "Date");
        assertEquals(property3.name, "createdAt");
        Assert.assertNull(property3.defaultValue);
        assertEquals(property3.baseType, "Date");
        Assert.assertFalse(property3.required);
        Assert.assertFalse(property3.isContainer);

        final CodegenProperty property4 = cm.vars.get(3);
        assertEquals(property4.baseName, "binary");
        assertEquals(property4.dataType, "URL");
        assertEquals(property4.name, "binary");
        Assert.assertNull(property4.defaultValue);
        assertEquals(property4.baseType, "URL");
        Assert.assertFalse(property4.required);
        Assert.assertFalse(property4.isContainer);

        final CodegenProperty property5 = cm.vars.get(4);
        assertEquals(property5.baseName, "byte");
        assertEquals(property5.dataType, "Data");
        assertEquals(property5.name, "byte");
        Assert.assertNull(property5.defaultValue);
        assertEquals(property5.baseType, "Data");
        Assert.assertFalse(property5.required);
        Assert.assertFalse(property5.isContainer);

        final CodegenProperty property6 = cm.vars.get(5);
        assertEquals(property6.baseName, "uuid");
        assertEquals(property6.dataType, "UUID");
        assertEquals(property6.name, "uuid");
        Assert.assertNull(property6.defaultValue);
        assertEquals(property6.baseType, "UUID");
        Assert.assertFalse(property6.required);
        Assert.assertFalse(property6.isContainer);

        final CodegenProperty property7 = cm.vars.get(6);
        assertEquals(property7.baseName, "dateOfBirth");
        assertEquals(property7.dataType, "Date");
        assertEquals(property7.name, "dateOfBirth");
        Assert.assertNull(property7.defaultValue);
        assertEquals(property7.baseType, "Date");
        Assert.assertFalse(property7.required);
        Assert.assertFalse(property7.isContainer);
    }

    @Test(description = "convert a simple java model", enabled = true)
    public void useCustomDateTimeTest() {
        final Schema schema = new Schema()
                .description("a sample model")
                .addProperty("id", new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT))
                .addProperty("name", new StringSchema())
                .addProperty("createdAt", new DateTimeSchema())
                .addProperty("binary", new BinarySchema())
                .addProperty("byte", new ByteArraySchema())
                .addProperty("uuid", new UUIDSchema())
                .addProperty("dateOfBirth", new DateSchema())
                .addRequiredItem("id")
                .addRequiredItem("name")
                .discriminator(new Discriminator().propertyName("test"));
        final DefaultCodegen codegen = new Swift5ClientCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setOpenAPI(openAPI);
        codegen.additionalProperties().put(Swift5ClientCodegen.USE_CUSTOM_DATE_WITHOUT_TIME, true);
        codegen.processOpts();

        final CodegenModel cm = codegen.fromModel("sample", schema);
        final CodegenProperty property7 = cm.vars.get(6);

        final CodegenProperty property3 = cm.vars.get(2);
        assertEquals(property3.baseName, "createdAt");
        assertEquals(property3.dataType, "Date");
        assertEquals(property3.name, "createdAt");
        Assert.assertNull(property3.defaultValue);
        assertEquals(property3.baseType, "Date");
        Assert.assertFalse(property3.required);
        Assert.assertFalse(property3.isContainer);

        assertEquals(property7.baseName, "dateOfBirth");
        assertEquals(property7.dataType, "OpenAPIDateWithoutTime");
        assertEquals(property7.name, "dateOfBirth");
        Assert.assertNull(property7.defaultValue);
        assertEquals(property7.baseType, "OpenAPIDateWithoutTime");
        Assert.assertFalse(property7.required);
        Assert.assertFalse(property7.isContainer);
    }

}
