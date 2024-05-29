package org.openapitools.codegen;




public class SpecValidationExceptionTest {

    @Test
    public void shouldGetDefaultMessage() {
        SpecValidationException specValidationException = new SpecValidationException();

        String expectedResult = new StringBuffer("null | Error count: 0, Warning count: 0")
                .append(System.lineSeparator()).append("Errors: ")
                .append(System.lineSeparator()).toString();

        assertEquals(specValidationException.getMessage(), expectedResult);
    }
}