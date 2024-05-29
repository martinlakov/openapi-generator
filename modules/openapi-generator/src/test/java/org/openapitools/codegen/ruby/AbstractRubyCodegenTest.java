package org.openapitools.codegen.ruby;

import org.openapitools.codegen.languages.AbstractRubyCodegen;

import org.testng.annotations.BeforeMethod;


/**
 * Tests for AbstractRubyCodegen
 */
public class AbstractRubyCodegenTest {
    private AbstractRubyCodegen codegen;

    @BeforeMethod
    public void setup() {
        codegen = new AbstractRubyCodegen() {
        };
    }

    @Test
    public void testEscapeUnsafeCharacters() {
        assertEquals(codegen.escapeUnsafeCharacters("=begin"), "=_begin");
        assertEquals(codegen.escapeUnsafeCharacters("=end"), "=_end");
        assertEquals(codegen.escapeUnsafeCharacters("#{x}"), "\\#{x}");
    }
}
