/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
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

package org.openapitools.codegen.ktorm;

import static org.openapitools.codegen.TestUtils.createCodegenModelWrapper;

import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.TestUtils;
import org.openapitools.codegen.languages.KtormSchemaCodegen;



import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.parser.util.SchemaTypeUtil;

import java.util.Map;

public class KtormSchemaCodegenTest {

    private CodegenModel getModel(Schema schema, String pkName, Boolean surrogateKey) {
        final KtormSchemaCodegen codegen = new KtormSchemaCodegen();
        OpenAPI openAPI = TestUtils.createOpenAPIWithOneSchema("sample", schema);
        codegen.setAddSurrogateKey(surrogateKey);
        codegen.setPrimaryKeyConvention(pkName);
        codegen.setOpenAPI(openAPI);
        CodegenModel cm = codegen.fromModel("sample", schema);
        codegen.postProcessModels(createCodegenModelWrapper(cm));
        return cm;
    }

    private Map<String, Object> getExtension(CodegenProperty property) {
        return (Map<String, Object>)
            property.vendorExtensions.get(KtormSchemaCodegen.VENDOR_EXTENSION_SCHEMA);
    }

    private Map<String, Object> getColumnDefinition(Map<String, Object> schema) {
        return (Map<String, Object>)
            schema.get("columnDefinition");
    }

    private Map<String, Object> getRelationDefinition(Map<String, Object> schema) {
        return (Map<String, Object>)
            schema.get("relationDefinition");
    }

    private Map<String, Object> getKtormSchema(Schema propertySchema) {
        final Schema schema = new Schema()
            .description("a sample model")
            .addProperties("key", propertySchema)
            .addRequiredItem("key");
        final CodegenModel cm = getModel(schema, "id", false);
        final CodegenProperty prop = cm.vars.get(0);
        return getExtension(prop);
    }

    private String getMatchedColType(Schema propertySchema) {
        Map<String, Object> ktormSchema = getColumnDefinition(getKtormSchema(propertySchema));
        return (String) ktormSchema.get("colType");
    }

    private String getMatchedKotlinType(Schema propertySchema) {
        Map<String, Object> ktormSchema = getColumnDefinition(getKtormSchema(propertySchema));
        return (String) ktormSchema.get("colKotlinType");
    }

    private String getMatchedRelation(Schema propertySchema) {
        Map<String, Object> ktormSchema = getRelationDefinition(getKtormSchema(propertySchema));
        if (ktormSchema == null) return null;
        return (String) ktormSchema.get("fkName");
    }

    @Test
    public void testMatchedColType() {
        assertEquals(getMatchedColType(new StringSchema()), "text");
        assertEquals(getMatchedColType(new StringSchema().type("char")), "text");
        assertEquals(getMatchedColType(new StringSchema().format("char")), "text");
        assertEquals(getMatchedColType(new BooleanSchema()), "boolean");
        assertEquals(getMatchedColType(new IntegerSchema().type(SchemaTypeUtil.BYTE_FORMAT)), "int");
        assertEquals(getMatchedColType(new IntegerSchema().format(SchemaTypeUtil.BYTE_FORMAT)), "int");
        assertEquals(getMatchedColType(new IntegerSchema().type("short")), "int");
        assertEquals(getMatchedColType(new IntegerSchema().format("short")), "int");
        assertEquals(getMatchedColType(new IntegerSchema()), "int");
        assertEquals(getMatchedColType(new IntegerSchema().type("integer")), "int");
        assertEquals(getMatchedColType(new IntegerSchema().format("integer")), "int");
        assertEquals(getMatchedColType(new IntegerSchema().format(SchemaTypeUtil.INTEGER32_FORMAT)), "int");
        assertEquals(getMatchedColType(new IntegerSchema().type("long")), "long");
        assertEquals(getMatchedColType(new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT)), "long");
        assertEquals(getMatchedColType(new ObjectSchema().type(SchemaTypeUtil.FLOAT_FORMAT)), "float");
        assertEquals(getMatchedColType(new NumberSchema().format(SchemaTypeUtil.FLOAT_FORMAT)), "float");
        assertEquals(getMatchedColType(new ObjectSchema().type(SchemaTypeUtil.DOUBLE_FORMAT)), "double");
        assertEquals(getMatchedColType(new NumberSchema().format(SchemaTypeUtil.DOUBLE_FORMAT)), "double");
        assertEquals(getMatchedColType(new ObjectSchema().type(SchemaTypeUtil.FLOAT_FORMAT).format(SchemaTypeUtil.DOUBLE_FORMAT)), "float");
        assertEquals(getMatchedColType(new ObjectSchema().type(SchemaTypeUtil.DOUBLE_FORMAT).format(SchemaTypeUtil.FLOAT_FORMAT)), "double");
        assertEquals(getMatchedColType(new ObjectSchema().type("real")), "double");
        assertEquals(getMatchedColType(new NumberSchema().format("real")), "decimal");
        assertEquals(getMatchedColType(new NumberSchema().type(SchemaTypeUtil.NUMBER_TYPE)), "decimal");
        assertEquals(getMatchedColType(new NumberSchema().type("decimal")), "decimal");
        assertEquals(getMatchedColType(new NumberSchema().type("BigDecimal")), "decimal");
        assertEquals(getMatchedColType(new ByteArraySchema()), "blob");
        assertEquals(getMatchedColType(new ArraySchema().items(new IntegerSchema().type(SchemaTypeUtil.BYTE_FORMAT))), "blob");
        assertEquals(getMatchedColType(new ArraySchema().items(new IntegerSchema().format(SchemaTypeUtil.BYTE_FORMAT))), "blob");
        assertEquals(getMatchedColType(new ArraySchema()), "blob");
        assertEquals(getMatchedColType(new ObjectSchema().type("list")), "blob");
        assertEquals(getMatchedColType(new ObjectSchema().type("set")), "blob");
        assertEquals(getMatchedColType(new ObjectSchema().type("map")), "blob");
        assertEquals(getMatchedColType(new ObjectSchema()), "blob");
        assertEquals(getMatchedColType(new ObjectSchema().type("binary")), "blob");
        assertEquals(getMatchedColType(new ObjectSchema().type("AnyType")), "blob");
        assertEquals(getMatchedColType(new BinarySchema()), "blob");
        assertEquals(getMatchedColType(new FileSchema()), "blob");
        assertEquals(getMatchedColType(new DateSchema()), "date");
        assertEquals(getMatchedColType(new DateTimeSchema()), "datetime");
        assertEquals(getMatchedColType(new UUIDSchema()), "text");
        assertEquals(getMatchedColType(new ObjectSchema().type("UUID")), "text");
        assertEquals(getMatchedColType(new StringSchema().format("URI")), "text");
        assertEquals(getMatchedColType(new ObjectSchema().type("URI")), "text");
        assertEquals(getMatchedColType(new StringSchema().format("password")), "text");
        assertEquals(getMatchedColType(new StringSchema().type("password")), "text");
    }

    @Test
    public void testMatchedColKotlinType() {
        // *1 - format specifiers aren't used
        assertEquals(getMatchedKotlinType(new StringSchema()), "kotlin.String");
        assertEquals(getMatchedKotlinType(new StringSchema().type("char")), "kotlin.String");
        assertEquals(getMatchedKotlinType(new StringSchema().format("char")), "kotlin.String");
        assertEquals(getMatchedKotlinType(new BooleanSchema()), "kotlin.Boolean");
        assertEquals(getMatchedKotlinType(new IntegerSchema().type(SchemaTypeUtil.BYTE_FORMAT)), "kotlin.Byte");
        assertEquals(getMatchedKotlinType(new IntegerSchema().format(SchemaTypeUtil.BYTE_FORMAT)), "kotlin.Int"); //*1
        assertEquals(getMatchedKotlinType(new IntegerSchema().type("short")), "kotlin.Short");
        assertEquals(getMatchedKotlinType(new IntegerSchema().format("short")), "kotlin.Int"); //*1
        assertEquals(getMatchedKotlinType(new IntegerSchema()), "kotlin.Int");
        assertEquals(getMatchedKotlinType(new IntegerSchema().type("integer")), "kotlin.Int");
        assertEquals(getMatchedKotlinType(new IntegerSchema().format("integer")), "kotlin.Int"); //*1
        assertEquals(getMatchedKotlinType(new IntegerSchema().format(SchemaTypeUtil.INTEGER32_FORMAT)), "kotlin.Int");
        assertEquals(getMatchedKotlinType(new IntegerSchema().type("long")), "kotlin.Long");
        assertEquals(getMatchedKotlinType(new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT)), "kotlin.Long");
        assertEquals(getMatchedKotlinType(new ObjectSchema().type(SchemaTypeUtil.FLOAT_FORMAT)), "kotlin.Float");
        assertEquals(getMatchedKotlinType(new NumberSchema().format(SchemaTypeUtil.FLOAT_FORMAT)), "kotlin.Float");
        assertEquals(getMatchedKotlinType(new ObjectSchema().type(SchemaTypeUtil.DOUBLE_FORMAT)), "kotlin.Double");
        assertEquals(getMatchedKotlinType(new NumberSchema().format(SchemaTypeUtil.DOUBLE_FORMAT)), "kotlin.Double");
        assertEquals(getMatchedKotlinType(new ObjectSchema().type(SchemaTypeUtil.FLOAT_FORMAT).format(SchemaTypeUtil.DOUBLE_FORMAT)), "kotlin.Float"); //*1
        assertEquals(getMatchedKotlinType(new ObjectSchema().type(SchemaTypeUtil.DOUBLE_FORMAT).format(SchemaTypeUtil.FLOAT_FORMAT)), "kotlin.Double"); //*1
        assertEquals(getMatchedKotlinType(new ObjectSchema().type("real")), "kotlin.Double");
        assertEquals(getMatchedKotlinType(new NumberSchema().format("real")), "java.math.BigDecimal"); //*1
        assertEquals(getMatchedKotlinType(new NumberSchema().type(SchemaTypeUtil.NUMBER_TYPE)), "java.math.BigDecimal");
        assertEquals(getMatchedKotlinType(new NumberSchema().type("decimal")), "java.math.BigDecimal");
        assertEquals(getMatchedKotlinType(new NumberSchema().type("BigDecimal")), "java.math.BigDecimal");
        assertEquals(getMatchedKotlinType(new ByteArraySchema()), "kotlin.ByteArray");
        assertEquals(getMatchedKotlinType(new ArraySchema().items(new IntegerSchema().type(SchemaTypeUtil.BYTE_FORMAT))), "kotlin.Array<kotlin.Byte>");
        assertEquals(getMatchedKotlinType(new ArraySchema().items(new IntegerSchema().format(SchemaTypeUtil.BYTE_FORMAT))), "kotlin.Array<kotlin.Int>"); //*1
        assertEquals(getMatchedKotlinType(new ArraySchema()), "kotlin.Array<kotlin.String>");
        assertEquals(getMatchedKotlinType(new ObjectSchema().type("list")), "kotlin.collections.List");
        assertEquals(getMatchedKotlinType(new ObjectSchema().type("set")), "kotlin.collections.Set");
        assertEquals(getMatchedKotlinType(new ObjectSchema().type("map")), "kotlin.collections.Map");
        assertEquals(getMatchedKotlinType(new ObjectSchema()), "kotlin.Any");
        assertEquals(getMatchedKotlinType(new ObjectSchema().type("binary")), "kotlin.ByteArray");
        assertEquals(getMatchedKotlinType(new ObjectSchema().type("AnyType")), "kotlin.Any");
        assertEquals(getMatchedKotlinType(new BinarySchema()), "java.io.File"); //looks like a bug
        assertEquals(getMatchedKotlinType(new FileSchema()), "java.io.File");
        assertEquals(getMatchedKotlinType(new DateSchema()), "java.time.LocalDate");
        assertEquals(getMatchedKotlinType(new DateTimeSchema()), "java.time.LocalDateTime");
        assertEquals(getMatchedKotlinType(new UUIDSchema()), "java.util.UUID");
        assertEquals(getMatchedKotlinType(new ObjectSchema().type("UUID")), "java.util.UUID");
        assertEquals(getMatchedKotlinType(new StringSchema().format("URI")), "java.net.URI");
        assertEquals(getMatchedKotlinType(new ObjectSchema().type("URI")), "java.net.URI");
        assertEquals(getMatchedKotlinType(new StringSchema().format("password")), "kotlin.String");
        assertEquals(getMatchedKotlinType(new StringSchema().type("password")), "kotlin.String");
    }

    @Test
    public void testNonMatchedRelation() {
        assertEquals(getMatchedRelation(new StringSchema()), null);
        assertEquals(getMatchedRelation(new StringSchema().type("char")), null);
        assertEquals(getMatchedRelation(new StringSchema().format("char")), null);
        assertEquals(getMatchedRelation(new BooleanSchema()), null);
        assertEquals(getMatchedRelation(new IntegerSchema().type(SchemaTypeUtil.BYTE_FORMAT)), null);
        assertEquals(getMatchedRelation(new IntegerSchema().format(SchemaTypeUtil.BYTE_FORMAT)), null);
        assertEquals(getMatchedRelation(new IntegerSchema().type("short")), null);
        assertEquals(getMatchedRelation(new IntegerSchema().format("short")), null);
        assertEquals(getMatchedRelation(new IntegerSchema()), null);
        assertEquals(getMatchedRelation(new IntegerSchema().type("integer")), null);
        assertEquals(getMatchedRelation(new IntegerSchema().format("integer")), null);
        assertEquals(getMatchedRelation(new IntegerSchema().format(SchemaTypeUtil.INTEGER32_FORMAT)), null);
        assertEquals(getMatchedRelation(new IntegerSchema().type("long")), null);
        assertEquals(getMatchedRelation(new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT)), null);
        assertEquals(getMatchedRelation(new ObjectSchema().type(SchemaTypeUtil.FLOAT_FORMAT)), null);
        assertEquals(getMatchedRelation(new NumberSchema().format(SchemaTypeUtil.FLOAT_FORMAT)), null);
        assertEquals(getMatchedRelation(new ObjectSchema().type(SchemaTypeUtil.DOUBLE_FORMAT)), null);
        assertEquals(getMatchedRelation(new NumberSchema().format(SchemaTypeUtil.DOUBLE_FORMAT)), null);
        assertEquals(getMatchedRelation(new ObjectSchema().type(SchemaTypeUtil.FLOAT_FORMAT).format(SchemaTypeUtil.DOUBLE_FORMAT)), null);
        assertEquals(getMatchedRelation(new ObjectSchema().type(SchemaTypeUtil.DOUBLE_FORMAT).format(SchemaTypeUtil.FLOAT_FORMAT)), null);
        assertEquals(getMatchedRelation(new ObjectSchema().type("real")), null);
        assertEquals(getMatchedRelation(new NumberSchema().format("real")), null);
        assertEquals(getMatchedRelation(new NumberSchema().type(SchemaTypeUtil.NUMBER_TYPE)), null);
        assertEquals(getMatchedRelation(new NumberSchema().type("decimal")), null);
        assertEquals(getMatchedRelation(new NumberSchema().type("BigDecimal")), null);
        assertEquals(getMatchedRelation(new ByteArraySchema()), null);
        assertEquals(getMatchedRelation(new ObjectSchema().type("list")), null);
        assertEquals(getMatchedRelation(new ObjectSchema().type("set")), null);
        assertEquals(getMatchedRelation(new ObjectSchema().type("map")), null);
        assertEquals(getMatchedRelation(new ObjectSchema()), null);
        assertEquals(getMatchedRelation(new ObjectSchema().type("binary")), null);
        assertEquals(getMatchedRelation(new ObjectSchema().type("AnyType")), null);
        assertEquals(getMatchedRelation(new BinarySchema()), null);
        assertEquals(getMatchedRelation(new FileSchema()), null);
        assertEquals(getMatchedRelation(new DateSchema()), null);
        assertEquals(getMatchedRelation(new DateTimeSchema()), null);
        assertEquals(getMatchedRelation(new UUIDSchema()), null);
        assertEquals(getMatchedRelation(new ObjectSchema().type("UUID")), null);
        assertEquals(getMatchedRelation(new StringSchema().format("URI")), null);
        assertEquals(getMatchedRelation(new ObjectSchema().type("URI")), null);
        assertEquals(getMatchedRelation(new StringSchema().format("password")), null);
        assertEquals(getMatchedRelation(new StringSchema().type("password")), null);
    }

    @Test
    public void testMatchedRelation() {
        //foreign keys
        assertEquals(getMatchedRelation(new ObjectSchema().type("Something")), "something");
        assertEquals(getMatchedColType(new ObjectSchema().type("Something")), "long");
        assertEquals(getMatchedRelation(new ObjectSchema().type("UserNamespace.UserClass")), "userNamespaceUserClass");
        assertEquals(getMatchedColType(new ObjectSchema().type("UserNamespace.UserClass")), "long");
        //arrays are special case, we convert them to 1:N relations
        assertEquals(getMatchedRelation(new ArraySchema()), "key");
        assertEquals(getMatchedRelation(new ArraySchema().items(new ObjectSchema().type("Something"))), "something");
        assertEquals(getMatchedRelation(new ArraySchema().items(new ObjectSchema().type("UserNamespace.UserClass"))), "userNamespaceUserClass");
        assertEquals(getMatchedRelation(new ArraySchema().items(new IntegerSchema().type(SchemaTypeUtil.BYTE_FORMAT))), "key");
        assertEquals(getMatchedRelation(new ArraySchema().items(new StringSchema())), "key");
        //blob will be the default type, the template shouldn't include those fields
        assertEquals(getMatchedColType(new ArraySchema()), "blob");
        assertEquals(getMatchedColType(new ArraySchema().items(new ObjectSchema().type("Something"))), "blob");
        assertEquals(getMatchedColType(new ArraySchema().items(new ObjectSchema().type("UserNamespace.UserClass"))), "blob");
        assertEquals(getMatchedColType(new ArraySchema().items(new IntegerSchema().type(SchemaTypeUtil.BYTE_FORMAT))), "blob");
        assertEquals(getMatchedColType(new ArraySchema().items(new StringSchema())), "blob");
    }

    @Test
    public void testDefinePrimaryKey() {
        final Schema schema = new Schema()
            .description("a sample model")
            .addProperties("key" , new IntegerSchema())
            .addRequiredItem("key");
        CodegenModel cm = getModel(schema, "key", false);
        assertEquals(cm.vars.size(), 1);
        CodegenProperty prop = cm.vars.get(0);
        Map<String, Object>  propSchema = getColumnDefinition(getExtension(prop));
        assertEquals(propSchema.get("colPrimaryKey"), true);
        assertEquals(propSchema.get("colType"), "int");
    }

    @Test
    public void testDontAddSurrogateKey() {
        final Schema schema = new Schema()
            .description("a sample model")
            .addProperties("key" , new IntegerSchema())
            .addRequiredItem("key");
        CodegenModel cm = getModel(schema, "id", false);
        assertEquals(cm.vars.size(), 1);
        CodegenProperty prop = cm.vars.get(0);
        Map<String, Object>  propSchema = getColumnDefinition(getExtension(prop));
        assertEquals(propSchema.get("colPrimaryKey"), false);
        assertEquals(propSchema.get("colType"), "int");
    }

    @Test
    public void testAddSurrogateKey() {
        final Schema schema = new Schema()
            .description("a sample model")
            .addProperties("key", new IntegerSchema());
        CodegenModel cm = getModel(schema, "id", true);
        assertEquals(cm.vars.size(), 2);
        CodegenProperty prop = cm.vars.get(0);
        Map<String, Object>  propSchema = getColumnDefinition(getExtension(prop));
        assertEquals(propSchema.get("colNotNull"), true);
        assertEquals(propSchema.get("colPrimaryKey"), true);
        assertEquals(propSchema.get("colName"), "id");
        assertEquals(propSchema.get("colType"), "long"); //by default
        CodegenProperty prop2 = cm.vars.get(1);
        Map<String, Object>  propSchema2 = getColumnDefinition(getExtension(prop2));
        assertEquals(propSchema2.get("colNotNull"), false);
        assertEquals(propSchema2.get("colPrimaryKey"), false);
        assertEquals(propSchema2.get("colName"), "key");
        assertEquals(propSchema2.get("colType"), "int");

    }

}
