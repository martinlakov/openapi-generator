package org.openapitools.codegen.typescript.fetch;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.*;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.Generator;
import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.TestUtils;
import org.openapitools.codegen.languages.AbstractTypeScriptClientCodegen;
import org.openapitools.codegen.languages.TypeScriptFetchClientCodegen;
import org.openapitools.codegen.typescript.TypeScriptGroups;
import org.openapitools.codegen.utils.ModelUtils;



import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = {TypeScriptGroups.TYPESCRIPT, TypeScriptGroups.TYPESCRIPT_FETCH})
public class TypeScriptFetchClientCodegenTest {
    @Test
    public void testSnapshotVersion() {
        OpenAPI api = TestUtils.createOpenAPI();
        TypeScriptFetchClientCodegen codegen = new TypeScriptFetchClientCodegen();

        codegen.additionalProperties().put("npmName", "@openapi/typescript-fetch-petstore");
        codegen.additionalProperties().put("snapshot", true);
        codegen.additionalProperties().put("npmVersion", "1.0.0-SNAPSHOT");
        codegen.processOpts();
        codegen.preprocessOpenAPI(api);

        assertTrue(codegen.getNpmVersion().matches("^1.0.0-SNAPSHOT.[0-9]{12}$"));

        codegen = new TypeScriptFetchClientCodegen();
        codegen.additionalProperties().put("npmName", "@openapi/typescript-fetch-petstore");
        codegen.additionalProperties().put("snapshot", true);
        codegen.additionalProperties().put("npmVersion", "3.0.0-M1");
        codegen.processOpts();
        codegen.preprocessOpenAPI(api);

        assertTrue(codegen.getNpmVersion().matches("^3.0.0-M1-SNAPSHOT.[0-9]{12}$"));

    }

    @Test
    public void testOptionalResponseImports() {
        TypeScriptFetchClientCodegen codegen = new TypeScriptFetchClientCodegen();
        final OpenAPI openApi = TestUtils.parseFlattenSpec("src/test/resources/3_0/optionalResponse.yaml");
        codegen.setOpenAPI(openApi);
        PathItem path = openApi.getPaths().get("/api/Users/{userId}");
        CodegenOperation operation = codegen.fromOperation("/api/Users/{userId}", "get", path.getGet(), path.getServers());
        assertEquals(operation.isResponseOptional, true);
    }

    @Test
    public void testModelsWithoutPaths() throws IOException {
        final String specPath = "src/test/resources/3_1/reusable-components-without-paths.yaml";

        Map<String, Object> properties = new HashMap<>();
        properties.put("supportsES6", true);

        File output = Files.createTempDirectory("test").toFile();
        output.deleteOnExit();

        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName("typescript-fetch")
                .setInputSpec(specPath)
                .setAdditionalProperties(properties)
                .setOutputDir(output.getAbsolutePath().replace("\\", "/"));

        Generator generator = new DefaultGenerator();
        List<File> files = generator.opts(configurator.toClientOptInput()).generate();
        files.forEach(File::deleteOnExit);

        TestUtils.assertFileExists(Paths.get(output + "/index.ts"));
        TestUtils.assertFileExists(Paths.get(output + "/runtime.ts"));
        TestUtils.assertFileExists(Paths.get(output + "/models/Pet.ts"));
        TestUtils.assertFileExists(Paths.get(output + "/models/index.ts"));
    }

    @Test
    public void testWithoutSnapshotVersion() {
        OpenAPI api = TestUtils.createOpenAPI();
        TypeScriptFetchClientCodegen codegen = new TypeScriptFetchClientCodegen();

        codegen.additionalProperties().put("npmName", "@openapi/typescript-fetch-petstore");
        codegen.additionalProperties().put("snapshot", false);
        codegen.additionalProperties().put("npmVersion", "1.0.0-SNAPSHOT");
        codegen.processOpts();
        codegen.preprocessOpenAPI(api);

        assertTrue(codegen.getNpmVersion().matches("^1.0.0-SNAPSHOT$"));

        codegen = new TypeScriptFetchClientCodegen();
        codegen.additionalProperties().put("npmName", "@openapi/typescript-fetch-petstore");
        codegen.additionalProperties().put("snapshot", false);
        codegen.additionalProperties().put("npmVersion", "3.0.0-M1");
        codegen.processOpts();
        codegen.preprocessOpenAPI(api);

        assertTrue(codegen.getNpmVersion().matches("^3.0.0-M1$"));

    }

    @Test
    public void toVarName() {
        TypeScriptFetchClientCodegen codegen = new TypeScriptFetchClientCodegen();
        codegen.processOpts();
        assertEquals(codegen.toVarName("valid_var"), "validVar");

        codegen = new TypeScriptFetchClientCodegen();
        codegen.additionalProperties().put(CodegenConstants.MODEL_PROPERTY_NAMING, "original");
        codegen.processOpts();
        assertEquals(codegen.toVarName("valid_var"), "valid_var");
    }

    @Test
    public void toEnumVarName() {
        TypeScriptFetchClientCodegen codegen = new TypeScriptFetchClientCodegen();
        codegen.processOpts();
        assertEquals(codegen.toEnumVarName("", "string"), "Empty");
        assertEquals(codegen.toEnumVarName("$", "string"), "Dollar");
        assertEquals(codegen.toEnumVarName("valid_var", "string"), "ValidVar");
        assertEquals(codegen.toEnumVarName("-valid_var+", "string"), "ValidVar");
        assertEquals(codegen.toEnumVarName("30valid_+var", "string"), "_30validVar");

        codegen = new TypeScriptFetchClientCodegen();
        codegen.additionalProperties().put(CodegenConstants.ENUM_PROPERTY_NAMING, "original");
        codegen.processOpts();
        assertEquals(codegen.toEnumVarName("", "string"), "empty");
        assertEquals(codegen.toEnumVarName("$", "string"), "Dollar");
        assertEquals(codegen.toEnumVarName("valid_var", "string"), "valid_var");
        assertEquals(codegen.toEnumVarName("-valid_var+", "string"), "valid_var");
        assertEquals(codegen.toEnumVarName("30valid_+var", "string"), "_30valid_var");

        codegen = new TypeScriptFetchClientCodegen();
        codegen.additionalProperties().put(CodegenConstants.ENUM_PROPERTY_NAMING, "UPPERCASE");
        codegen.additionalProperties().put(AbstractTypeScriptClientCodegen.ENUM_PROPERTY_NAMING_REPLACE_SPECIAL_CHAR, "true");
        codegen.processOpts();
        assertEquals(codegen.toEnumVarName("", "string"), "EMPTY");
        assertEquals(codegen.toEnumVarName("$", "string"), "DOLLAR");
        assertEquals(codegen.toEnumVarName("valid_var", "string"), "VALID_VAR");
        assertEquals(codegen.toEnumVarName("-valid_+var", "string"), "MINUS_VALID_PLUS_VAR");
        assertEquals(codegen.toEnumVarName("-valid_var+", "string"), "MINUS_VALID_VAR_PLUS");
        assertEquals(codegen.toEnumVarName("30valid_+var", "string"), "_30VALID_PLUS_VAR");

    }

    @Test
    public void getTypeDeclarationTest() {
        Schema<?> childSchema = new ArraySchema().items(new StringSchema());

        OpenAPI api = TestUtils.createOpenAPI();
        api.getComponents().addSchemas("Child", childSchema);

        TypeScriptFetchClientCodegen codegen = new TypeScriptFetchClientCodegen();
        codegen.setOpenAPI(api);

        // Cf. issue #4968: Array of Alias of Array
        Schema<?> parentSchema = new ArraySchema().items(
            new Schema().$ref("#/components/schemas/Child")
        );

        ModelUtils.setGenerateAliasAsModel(false);
        assertEquals(codegen.getTypeDeclaration(parentSchema), "Array<Array<string>>");

        ModelUtils.setGenerateAliasAsModel(true);
        assertEquals(codegen.getTypeDeclaration(parentSchema), "Array<Child>");

        // Same for Map
        parentSchema = new MapSchema().additionalProperties(new Schema().$ref("#/components/schemas/Child"));

        ModelUtils.setGenerateAliasAsModel(false);
        assertEquals(codegen.getTypeDeclaration(parentSchema), "{ [key: string]: Array<string>; }");

        ModelUtils.setGenerateAliasAsModel(true);
        assertEquals(codegen.getTypeDeclaration(parentSchema), "{ [key: string]: Child; }");
    }

    @Test
    public void containsESMTSConfigFileInCaseOfES6AndNPM() {
        TypeScriptFetchClientCodegen codegen = new TypeScriptFetchClientCodegen();

        codegen.additionalProperties().put("npmName", "@openapi/typescript-fetch-petstore");
        codegen.additionalProperties().put("snapshot", false);
        codegen.additionalProperties().put("npmVersion", "1.0.0-SNAPSHOT");
        codegen.additionalProperties().put("supportsES6", true);

        codegen.processOpts();

        assertThat(codegen.supportingFiles()).contains(new SupportingFile("tsconfig.mustache", "", "tsconfig.json"));
        assertThat(codegen.supportingFiles()).contains(new SupportingFile("tsconfig.esm.mustache", "", "tsconfig.esm.json"));
    }

    @Test
    public void doesNotContainESMTSConfigFileInCaseOfES5AndNPM() {
        TypeScriptFetchClientCodegen codegen = new TypeScriptFetchClientCodegen();

        codegen.additionalProperties().put("npmName", "@openapi/typescript-fetch-petstore");
        codegen.additionalProperties().put("snapshot", false);
        codegen.additionalProperties().put("npmVersion", "1.0.0-SNAPSHOT");
        codegen.additionalProperties().put("supportsES6", false);

        codegen.processOpts();

        assertThat(codegen.supportingFiles()).contains(new SupportingFile("tsconfig.mustache", "", "tsconfig.json"));
        assertThat(codegen.supportingFiles()).doesNotContain(new SupportingFile("tsconfig.esm.mustache", "", "tsconfig.esm.json"));
    }

    @Test(description = "Verify file name formatting from model name in PascalCase")
    public void testModelFileNameInPascalCase() {
        final TypeScriptFetchClientCodegen codegen = new TypeScriptFetchClientCodegen();
        codegen.setFileNaming(TypeScriptFetchClientCodegen.PASCAL_CASE);
        assertEquals("FirstSimpleModel", codegen.toModelFilename("FirstSimpleModel"));
        codegen.setModelNameSuffix("suffix");
        assertEquals("FirstSimpleModelSuffix", codegen.toModelFilename("FirstSimpleModel"));
        codegen.setModelNamePrefix("prefix");
        assertEquals("PrefixFirstSimpleModelSuffix", codegen.toModelFilename("FirstSimpleModel"));
    }

    @Test(description = "Verify file name formatting from model name in camelCase")
    public void testModelFileNameInCamelCase() {
        final TypeScriptFetchClientCodegen codegen = new TypeScriptFetchClientCodegen();
        codegen.setFileNaming(TypeScriptFetchClientCodegen.CAMEL_CASE);
        assertEquals("firstSimpleModel", codegen.toModelFilename("FirstSimpleModel"));
        codegen.setModelNameSuffix("suffix");
        assertEquals("firstSimpleModelSuffix", codegen.toModelFilename("FirstSimpleModel"));
        codegen.setModelNamePrefix("prefix");
        assertEquals("prefixFirstSimpleModelSuffix", codegen.toModelFilename("FirstSimpleModel"));
    }

    @Test(description = "Verify file name formatting from model name in kebab-case")
    public void testModelFileNameInKebabCase() {
        final TypeScriptFetchClientCodegen codegen = new TypeScriptFetchClientCodegen();
        codegen.setFileNaming("kebab-case");
        assertEquals("first-simple-model", codegen.toModelFilename("FirstSimpleModel"));
        codegen.setModelNameSuffix("suffix");
        assertEquals("first-simple-model-suffix", codegen.toModelFilename("FirstSimpleModel"));
        codegen.setModelNamePrefix("prefix");
        assertEquals("prefix-first-simple-model-suffix", codegen.toModelFilename("FirstSimpleModel"));
    }

    @Test(description = "Verify file name formatting from api name in PascalCase, camelCase and kebab-case")
    public void testApiFileNameInVariousFormat() {
        final TypeScriptFetchClientCodegen codegen = new TypeScriptFetchClientCodegen();
        codegen.setFileNaming(TypeScriptFetchClientCodegen.PASCAL_CASE);
        String prefix = codegen.getApiNamePrefix() != null ? codegen.getApiNamePrefix() : "";
        String suffix = codegen.getApiNameSuffix() != null ? codegen.getApiNameSuffix() : "";
        assertEquals(StringUtils.capitalize(prefix + "FirstSimpleController") + StringUtils.capitalize(suffix),
                codegen.toApiFilename("FirstSimpleController"));
        codegen.setFileNaming(TypeScriptFetchClientCodegen.CAMEL_CASE);
        assertEquals(StringUtils.uncapitalize(prefix + "FirstSimpleController") + StringUtils.capitalize(suffix),
                codegen.toApiFilename("FirstSimpleController"));
        codegen.setFileNaming(TypeScriptFetchClientCodegen.KEBAB_CASE);
        assertEquals((prefix.isBlank() ? "" : (StringUtils.lowerCase(suffix) + "-")) + "first-simple-controller" + (suffix.isBlank() ? "" : ("-" + StringUtils.lowerCase(suffix))),
                codegen.toApiFilename("FirstSimpleController"));
    }

    @Test(description = "Verify names of files generated in kebab-case and imports")
    public void testGeneratedFilenamesInKebabCase() throws IOException {

        Map<String, Object> properties = new HashMap<>();
        properties.put("fileNaming", TypeScriptFetchClientCodegen.KEBAB_CASE);

        File output = generate(properties);

        Path pet = Paths.get(output + "/models/pet.ts");
        TestUtils.assertFileExists(pet);
        TestUtils.assertFileContains(pet, "} from './pet-category';");
        TestUtils.assertFileExists(Paths.get(output + "/models/pet-category.ts"));
        TestUtils.assertFileExists(Paths.get(output + "/apis/pet-controller-api.ts"));
    }

    @Test(description = "Verify names of files generated in camelCase and imports")
    public void testGeneratedFilenamesInCamelCase() throws IOException {

        Map<String, Object> properties = new HashMap<>();
        properties.put("fileNaming", TypeScriptFetchClientCodegen.CAMEL_CASE);

        File output = generate(properties);

        Path pet = Paths.get(output + "/models/pet.ts");
        TestUtils.assertFileExists(pet);
        TestUtils.assertFileContains(pet, "} from './petCategory';");
        TestUtils.assertFileExists(Paths.get(output + "/models/petCategory.ts"));
        TestUtils.assertFileExists(Paths.get(output + "/apis/petControllerApi.ts"));
    }

    private static File generate(Map<String, Object> properties) throws IOException {
        File output = Files.createTempDirectory("test").toFile();
        output.deleteOnExit();

        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName("typescript-fetch")
                .setInputSpec("src/test/resources/3_0/typescript-fetch/example-for-file-naming-option.yaml")
                .setAdditionalProperties(properties)
                .setOutputDir(output.getAbsolutePath().replace("\\", "/"));

        Generator generator = new DefaultGenerator();
        List<File> files = generator.opts(configurator.toClientOptInput()).generate();
        files.forEach(File::deleteOnExit);
        return output;
    }
}
