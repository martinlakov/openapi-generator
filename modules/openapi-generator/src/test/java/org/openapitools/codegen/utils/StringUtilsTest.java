package org.openapitools.codegen.utils;




import static org.openapitools.codegen.utils.CamelizeOption.LOWERCASE_FIRST_CHAR;
import static org.openapitools.codegen.utils.CamelizeOption.LOWERCASE_FIRST_LETTER;
import static org.openapitools.codegen.utils.StringUtils.*;

public class StringUtilsTest {
    // we'll assume that <i>underscore</i> (Twitter elephant bird) works fine
    @Test
    public void testUnderscore() {
        assertEquals(underscore("abcd"), "abcd");
        assertEquals(underscore("abCd"), "ab_cd");
        assertEquals(underscore("ListABCs"), "list_abcs");
    }

    @Test
    public void testCamelize() throws Exception {
        assertEquals(camelize("abcd"), "Abcd");
        assertEquals(camelize("some-value"), "SomeValue");
        assertEquals(camelize("some_value"), "SomeValue");
        assertEquals(camelize("$type"), "$Type");

        assertEquals(camelize("abcd", LOWERCASE_FIRST_LETTER), "abcd");
        assertEquals(camelize("some-value", LOWERCASE_FIRST_LETTER), "someValue");
        assertEquals(camelize("some_value", LOWERCASE_FIRST_LETTER), "someValue");
        assertEquals(camelize("Abcd", LOWERCASE_FIRST_LETTER), "abcd");
        assertEquals(camelize("$type", LOWERCASE_FIRST_LETTER), "$type");

        assertEquals(camelize("123", LOWERCASE_FIRST_LETTER), "123");
        assertEquals(camelize("$123", LOWERCASE_FIRST_LETTER), "$123");


        assertEquals(camelize("some-value", LOWERCASE_FIRST_CHAR), "someValue");
        assertEquals(camelize("$type", LOWERCASE_FIRST_CHAR), "$Type");
    }

    @Test
    public void testDashize() {
        assertEquals(dashize("abcd"), "abcd");
        assertEquals(dashize("some-value"), "some-value");
        assertEquals(dashize("some_value"), "some-value");
        assertEquals(dashize("Foo_Response__links"), "foo-response-links");
        assertEquals(dashize("Foo Response _links"), "foo-response-links");
    }
}
