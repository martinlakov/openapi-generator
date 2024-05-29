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

package org.openapitools.codegen.bash;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.DefaultCodegen;
import org.openapitools.codegen.TestUtils;
import org.openapitools.codegen.languages.BashClientCodegen;



@SuppressWarnings("static-method")
public class BashTest {

    @Test(description = "test basic petstore operation with Bash extensions")
    public void petstoreOperationTest() {

        final OpenAPI openAPI
            = TestUtils.parseFlattenSpec("src/test/resources/2_0/petstore-bash.json");
        final DefaultCodegen codegen = new BashClientCodegen();
        codegen.setOpenAPI(openAPI);
        final Operation findPetsByStatusOperation
            = openAPI.getPaths().get("/pet/findByStatus").getGet();

        final CodegenOperation op
            = codegen.fromOperation(
                "/pet/findByStatus",
                "GET",
                findPetsByStatusOperation,
                null);

        assertTrue(
            op.vendorExtensions.containsKey("x-code-samples"));

        assertEquals(
            op.vendorExtensions.get("x-bash-codegen-description"),
            "Multiple status 'values' can be provided with comma separated strings");

        assertEquals(op.allParams.size(), 1);
        CodegenParameter p = op.allParams.get(0);
        assertEquals(p.description, "Status values that need to be considered for filter");
    }

    @Test(description = "test basic petstore operation with example body")
    public void petstoreParameterExampleTest() {

        final OpenAPI openAPI
            = TestUtils.parseFlattenSpec("src/test/resources/2_0/petstore-bash.json");
        final DefaultCodegen codegen = new BashClientCodegen();
        codegen.setOpenAPI(openAPI);
        final Operation addPetOperation
            = openAPI.getPaths().get("/pet").getPost();

        final CodegenOperation op
            = codegen.fromOperation(
                "/pet",
                "POST",
                addPetOperation,
                null);

        assertEquals(op.bodyParams.size(), 1);

        CodegenParameter p = op.bodyParams.get(0);

        assertTrue(p.vendorExtensions
                            .containsKey("x-codegen-body-example"));
        assertEquals(p.description, "Pet object that needs to be added to the store");

    }


    @Test(description = "test Bash client codegen escapeText method")
    public void escapeTextTest() {
        final DefaultCodegen codegen = new BashClientCodegen();


        assertEquals(codegen.escapeText("\\/"), "/");

        assertEquals(codegen.escapeText("\\"), "\\\\");


        ((BashClientCodegen)codegen).setProcessMarkdown(false);

        assertEquals(codegen.escapeText("__Bold text__"),
                            "__Bold text__");

        assertEquals(codegen.escapeText("**Bold text**"),
                            "**Bold text**");

        assertEquals(codegen.escapeText("*Italic text*"),
                            "*Italic text*");

        assertEquals(codegen.escapeText("_Italic text_"),
                            "_Italic text_");


        ((BashClientCodegen)codegen).setProcessMarkdown(true);

        assertEquals(codegen.escapeText("__Bold text__"),
                            "$(tput bold) Bold text $(tput sgr0)");

        assertEquals(codegen.escapeText("**Bold text**"),
                            "$(tput bold) Bold text $(tput sgr0)");

        assertEquals(codegen.escapeText("*Italic text*"),
                            "$(tput dim) Italic text $(tput sgr0)");

        assertEquals(codegen.escapeText("_Italic text_"),
                            "$(tput dim) Italic text $(tput sgr0)");

        assertEquals(codegen.escapeText("# SECTION NAME"),
            "\n$(tput bold)$(tput setaf 7)SECTION NAME$(tput sgr0)");

        assertEquals(codegen.escapeText("## SECTION NAME"),
            "\n$(tput bold)$(tput setaf 7)SECTION NAME$(tput sgr0)");

        assertEquals(codegen.escapeText("### SECTION NAME"),
            "\n$(tput bold)$(tput setaf 7)SECTION NAME$(tput sgr0)");

        assertEquals(codegen.escapeText(
                                "```\nnice -n 100 mvn test\n```"),
                                "\n---\nnice -n 100 mvn test\n---");
    }

    @Test(description = "test Bash client codegen escapeUnsafeCharacters method")
    public void escapeUnsafeCharactersTest() {
        final DefaultCodegen codegen = new BashClientCodegen();

        assertEquals(codegen.escapeUnsafeCharacters("`no backticks`"),
                                                          "'no backticks'");


    }

    @Test(description = "test Bash client codegen escapeReservedWord method")
    public void escapeReservedWordTest() {
        final DefaultCodegen codegen = new BashClientCodegen();

        assertEquals(codegen.escapeReservedWord("case"), "_case");
    }


}
