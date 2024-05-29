package org.openapitools.codegen.nim;

import org.openapitools.codegen.*;
import org.openapitools.codegen.languages.NimClientCodegen;



public class NimClientCodegenTest {

  @Test
  public void testInitialConfigValues() throws Exception {
    final NimClientCodegen codegen = new NimClientCodegen();
    codegen.processOpts();

    assertEquals(codegen.additionalProperties().get(CodegenConstants.HIDE_GENERATION_TIMESTAMP), Boolean.TRUE);
    assertEquals(codegen.isHideGenerationTimestamp(), true);
  }

  @Test
  public void testSettersForConfigValues() throws Exception {
    final NimClientCodegen codegen = new NimClientCodegen();
    codegen.setHideGenerationTimestamp(false);
    codegen.processOpts();

    assertEquals(codegen.additionalProperties().get(CodegenConstants.HIDE_GENERATION_TIMESTAMP), Boolean.FALSE);
    assertEquals(codegen.isHideGenerationTimestamp(), false);
  }

  @Test
  public void testAdditionalPropertiesPutForConfigValues() throws Exception {
    final NimClientCodegen codegen = new NimClientCodegen();
    codegen.additionalProperties().put(CodegenConstants.HIDE_GENERATION_TIMESTAMP, false);
    codegen.processOpts();

    assertEquals(codegen.additionalProperties().get(CodegenConstants.HIDE_GENERATION_TIMESTAMP), Boolean.FALSE);
    assertEquals(codegen.isHideGenerationTimestamp(), false);
  }
}
