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

package org.openapitools.codegen.android;

import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.languages.AndroidClientCodegen;



public class AndroidClientCodegenTest {

    @Test
    public void testInitialConfigValues() throws Exception {
        final AndroidClientCodegen codegen = new AndroidClientCodegen();
        codegen.processOpts();

        assertEquals(codegen.additionalProperties().get(CodegenConstants.HIDE_GENERATION_TIMESTAMP), Boolean.TRUE);
        assertTrue(codegen.isHideGenerationTimestamp());
        assertEquals(codegen.modelPackage(), "org.openapitools.client.model");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.MODEL_PACKAGE), "org.openapitools.client.model");
        assertEquals(codegen.apiPackage(), "org.openapitools.client.api");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.API_PACKAGE), "org.openapitools.client.api");
        assertEquals(codegen.getInvokerPackage(), "org.openapitools.client");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.INVOKER_PACKAGE), "org.openapitools.client");
    }

    @Test
    public void testSettersForConfigValues() throws Exception {
        final AndroidClientCodegen codegen = new AndroidClientCodegen();
        codegen.setModelPackage("xx.yyyyyyyy.model");
        codegen.setApiPackage("xx.yyyyyyyy.api");
        codegen.setInvokerPackage("xx.yyyyyyyy.invoker");
        codegen.processOpts();

        assertEquals(codegen.modelPackage(), "xx.yyyyyyyy.model");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.MODEL_PACKAGE), "xx.yyyyyyyy.model");
        assertEquals(codegen.apiPackage(), "xx.yyyyyyyy.api");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.API_PACKAGE), "xx.yyyyyyyy.api");
        assertEquals(codegen.getInvokerPackage(), "xx.yyyyyyyy.invoker");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.INVOKER_PACKAGE), "xx.yyyyyyyy.invoker");
    }

    @Test
    public void testAdditionalPropertiesPutForConfigValues() throws Exception {
        final AndroidClientCodegen codegen = new AndroidClientCodegen();
        codegen.additionalProperties().put(CodegenConstants.MODEL_PACKAGE, "xyz.yyyyy.mmmmm.model");
        codegen.additionalProperties().put(CodegenConstants.API_PACKAGE, "xyz.yyyyy.aaaaa.api");
        codegen.additionalProperties().put(CodegenConstants.INVOKER_PACKAGE,"xyz.yyyyy.iiii.invoker");
        codegen.processOpts();

        assertEquals(codegen.modelPackage(), "xyz.yyyyy.mmmmm.model");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.MODEL_PACKAGE), "xyz.yyyyy.mmmmm.model");
        assertEquals(codegen.apiPackage(), "xyz.yyyyy.aaaaa.api");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.API_PACKAGE), "xyz.yyyyy.aaaaa.api");
        assertEquals(codegen.getInvokerPackage(), "xyz.yyyyy.iiii.invoker");
        assertEquals(codegen.additionalProperties().get(CodegenConstants.INVOKER_PACKAGE), "xyz.yyyyy.iiii.invoker");
    }
    @Test
    public void testHideGenerationTimestampDisabled() throws Exception {
        final AndroidClientCodegen codegen = new AndroidClientCodegen();
        codegen.additionalProperties().put("hideGenerationTimestamp", false);
        codegen.processOpts();

        assertEquals(codegen.additionalProperties().get("hideGenerationTimestamp"), Boolean.FALSE);
        Assert.assertFalse(codegen.isHideGenerationTimestamp());
    }

    @Test
    public void testHideGenerationTimestampEnabled() throws Exception {
        final AndroidClientCodegen codegen = new AndroidClientCodegen();
        codegen.additionalProperties().put("hideGenerationTimestamp", true);
        codegen.processOpts();

        assertEquals(codegen.additionalProperties().get("hideGenerationTimestamp"), Boolean.TRUE);
        assertTrue(codegen.isHideGenerationTimestamp());
    }
}
