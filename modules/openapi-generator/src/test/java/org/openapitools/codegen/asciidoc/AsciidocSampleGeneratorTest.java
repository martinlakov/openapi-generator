package org.openapitools.codegen.asciidoc;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;

import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.languages.AsciidocDocumentationCodegen;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** several asciidoc content checks with sample openapi v3. */
public class AsciidocSampleGeneratorTest {

    static public String markupContent = null;
    static public String markupFileName = null;

    static File specDir = new File("src/test/resources/3_0/asciidoc/specs/");
    static File snippetDir = new File("src/test/resources/3_0/asciidoc/generated-snippets/");

    @BeforeAll
    static public void beforeClassGenerateTestMarkup() throws Exception {

        File outputTempDirectory = Files.createTempDirectory("test-asciidoc-sample-generator.").toFile();

        assertTrue(specDir.exists(), "test cancel, not specDir found to use." + specDir.getPath());
        assertTrue(snippetDir.exists(), "test cancel, not snippedDir found to use." + snippetDir.getPath());

        final CodegenConfigurator configurator = new CodegenConfigurator().setGeneratorName("asciidoc")
                .setInputSpec("src/test/resources/3_0/asciidoc/api-docs.json")
                .setOutputDir(outputTempDirectory.getAbsolutePath())
                .addAdditionalProperty(AsciidocDocumentationCodegen.SPEC_DIR, specDir.toString())
                .addAdditionalProperty(AsciidocDocumentationCodegen.SNIPPET_DIR, snippetDir.toString());

        DefaultGenerator generator = new DefaultGenerator();
        List<File> files = generator.opts(configurator.toClientOptInput()).generate();

        for (File file : files) {
            if (file.getName().equals("index.adoc")) {
                markupFileName = file.getAbsoluteFile().toString();
                markupContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            }
        }
    }

    @AfterAll
    static public void afterClassCleanUpTestMarkup() throws Exception {
        if (markupFileName != null) {
            Files.deleteIfExists(Paths.get(markupFileName));
        }
    }

    @Test
    public void testMarkupExistence() {
        assertNotNull(markupContent, "asciidoc content index.adoc not created.");
    }

    /**
     * ensure api-docs.json includes sample and spec files directory as attributes.
     */
    @Test
    public void testSampleAsciidocMarkupGenerationFromJsonWithAttributes() {
        assertTrue(markupContent.contains(":specDir: " + specDir.toString()),
                "expected :specDir: in: " + markupContent.substring(0, 350));
        assertTrue(markupContent.contains(":snippetDir: " + snippetDir.toString()),
                "expected :snippetDir: in: " + markupContent.substring(0, 350));
    }

    /**
     * ensure api-docs.json includes sample and spec files into markup.
     */
    @Test
    public void testSampleAsciidocMarkupGenerationFromJsonWithIncludes() {

        // include correct markup from separate directories, relative links
        assertTrue(markupContent.contains("include::{specDir}rest/project/GET/spec.adoc["),
                "expected project spec.adoc to be included in " + markupFileName);

        assertTrue(markupContent.contains("include::{specDir}rest/project/GET/implementation.adoc["),
                "expected project implementation.adoc to be included in " + markupFileName);

        assertTrue(markupContent.contains("include::{snippetDir}rest/project/GET/http-request.adoc["),
                "expected project http-request.adoc to be included in " + markupFileName);

        assertTrue(markupContent.contains("include::{snippetDir}rest/project/GET/http-response.adoc["),
                "expected project http-response.adoc to be included in " + markupFileName);

        assertTrue(markupContent.contains("link:rest/project/GET/GET.json["),
                "expected link: not found in file: " + markupFileName);
    }

    /**
     * markup doc header content.
     */
    @Test
    public void testSampleAsciidocMarkupGenerationFromJsonWithContent() {
        assertTrue(markupContent.contains("= time@work rest api"),
                "missing main header for api spec from json: " + markupContent.substring(0, 100));

    }

    /**
     * fix: parameter name unchanged.
     */
    @Test
    public void testSampleAsciidocMarkupGenerationParameterNameUnchanged() {
        assertTrue(markupContent.contains("from-iso-date-string"),
                "keep parameter name from-iso-date-string unchanged.");
    }

    /**
     * added apikey info in access section.
     */
    @Test
    public void testSampleAsciidocMarkupGenerationAccessApiKey() {
        assertTrue(markupContent.contains("*APIKey*"),
                "access section mit apikey expected.");
        assertFalse(markupContent.contains("*OAuth*"),
                "access section no oauth expected.");
        assertFalse(markupContent.contains("*HTTP Basic*"),
                "access section no http basic expected.");
    }

    /**
     * no form params in this sample spec.
     */
    @Test
    public void testSampleAsciidocMarkupGenerationWithoutFormParameter() {
        assertFalse(markupContent.contains("= Form Parameter"),
                "no form parameters in this openapi spec expected.");
    }

}
