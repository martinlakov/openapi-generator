package org.openapitools.codegen.xojo.client;

import org.openapitools.codegen.*;
import org.openapitools.codegen.languages.XojoClientCodegen;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class XojoClientCodegenTest {

    XojoClientCodegen codegen = new XojoClientCodegen();

    @Test
    public void testToEnumVarNameCapitalizedReservedWord() throws Exception {
        assertEquals(codegen.toEnumVarName("AS", null), "Escapedas");
    }

    @Test
    public void testToEnumVarNameReservedWord() throws Exception {
        assertEquals(codegen.toEnumVarName("Public", null), "Escapedpublic");
    }

    @Test
    public void testToEnumVarNameShouldNotBreakNonReservedWord() throws Exception {
        assertEquals(codegen.toEnumVarName("Error", null), "Error");
    }

    @Test
    public void testToEnumVarNameShouldNotBreakCorrectName() throws Exception {
        assertEquals(codegen.toEnumVarName("EntryName", null), "EntryName");
    }

    @Test
    public void testToEnumVarNameSingleWordAllCaps() throws Exception {
        assertEquals(codegen.toEnumVarName("VALUE", null), "Value");
    }

    @Test
    public void testToEnumVarNameSingleWordLowercase() throws Exception {
        assertEquals(codegen.toEnumVarName("value", null), "Value");
    }

    @Test
    public void testToEnumVarNameCapitalsWithUnderscore() throws Exception {
        assertEquals(codegen.toEnumVarName("ENTRY_NAME", null), "EntryName");
    }

    @Test
    public void testToEnumVarNameCapitalsWithDash() throws Exception {
        assertEquals(codegen.toEnumVarName("ENTRY-NAME", null), "EntryName");
    }

    @Test
    public void testToEnumVarNameCapitalsWithSpace() throws Exception {
        assertEquals(codegen.toEnumVarName("ENTRY NAME", null), "EntryName");
    }

    @Test
    public void testToEnumVarNameLowercaseWithUnderscore() throws Exception {
        assertEquals(codegen.toEnumVarName("entry_name", null), "EntryName");
    }

    @Test
    public void testToEnumVarNameStartingWithNumber() throws Exception {
        assertEquals(codegen.toEnumVarName("123EntryName", null), "Escaped123EntryName");
        assertEquals(codegen.toEnumVarName("123Entry_name", null), "Escaped123EntryName");
        assertEquals(codegen.toEnumVarName("123EntryName123", null), "Escaped123EntryName123");
    }

    @Test
    public void testToEnumVarNameSpecialCharacters() throws Exception {
        assertEquals(codegen.toEnumVarName("1:1", null), "Escaped1Colon1");
        assertEquals(codegen.toEnumVarName("1:One", null), "Escaped1ColonOne");
        assertEquals(codegen.toEnumVarName("Apple&Pie", null), "AppleAmpersandPie");
        assertEquals(codegen.toEnumVarName("$", null), "Dollar");
        assertEquals(codegen.toEnumVarName("+1", null), "EscapedPlus1");
        assertEquals(codegen.toEnumVarName(">=", null), "GreaterThanOrEqualTo");
    }

    @Test
    @DisplayName("returns Data when response format is binary")
    public void binaryDataTest() {
        final OpenAPI openAPI = TestUtils.parseFlattenSpec("src/test/resources/2_0/binaryDataTest.json");
        final DefaultCodegen codegen = new XojoClientCodegen();
        codegen.setOpenAPI(openAPI);
        final String path = "/tests/binaryResponse";
        final Operation p = openAPI.getPaths().get(path).getPost();
        final CodegenOperation op = codegen.fromOperation(path, "post", p, null);

        assertEquals(op.returnType, "FolderItem");
        assertEquals(op.bodyParam.dataType, "FolderItem");
        assertTrue(op.bodyParam.isBinary);
        assertTrue(op.responses.get(0).isBinary);
    }

    @Test
    @DisplayName("returns Date when response format is date per default")
    public void dateDefaultTest() {
        final OpenAPI openAPI = TestUtils.parseFlattenSpec("src/test/resources/2_0/datePropertyTest.json");
        final DefaultCodegen codegen = new XojoClientCodegen();
        codegen.setOpenAPI(openAPI);
        final String path = "/tests/dateResponse";
        final Operation p = openAPI.getPaths().get(path).getPost();
        final CodegenOperation op = codegen.fromOperation(path, "post", p, null);

        assertEquals(op.returnType, "Date");
        assertEquals(op.bodyParam.dataType, "Date");
    }

    @Test
    @DisplayName("type from languageSpecificPrimitives should not be prefixed")
    public void prefixExceptionTest() {
        final DefaultCodegen codegen = new XojoClientCodegen();
        codegen.setModelNamePrefix("API");

        final String result = codegen.toModelName("Currency");
        assertEquals(result, "Currency");
    }

    @Test
    @DisplayName("type from languageSpecificPrimitives should not be suffixed")
    public void suffixExceptionTest() {
        final DefaultCodegen codegen = new XojoClientCodegen();
        codegen.setModelNameSuffix("API");

        final String result = codegen.toModelName("Currency");
        assertEquals(result, "Currency");
    }

    @Test
    @DisplayName("Other types should be prefixed")
    public void prefixTest() {
        final DefaultCodegen codegen = new XojoClientCodegen();
        codegen.setModelNamePrefix("API");

        final String result = codegen.toModelName("MyType");
        assertEquals(result, "APIMyType");
    }

    @Test
    @DisplayName("Other types should be suffixed")
    public void suffixTest() {
        final DefaultCodegen codegen = new XojoClientCodegen();
        codegen.setModelNameSuffix("API");

        final String result = codegen.toModelName("MyType");
        assertEquals(result, "MyTypeAPI");
    }
}
