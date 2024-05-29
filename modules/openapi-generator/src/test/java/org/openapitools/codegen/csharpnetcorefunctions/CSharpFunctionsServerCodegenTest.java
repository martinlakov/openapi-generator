/*
 * Copyright 2020 OpenAPI-Generator Contributors (https://openapi-generator.tech)
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

package org.openapitools.codegen.csharpnetcorefunctions;

import org.openapitools.codegen.languages.CSharpFunctionsServerCodegen;



public class CSharpFunctionsServerCodegenTest {

    @Test
    public void testToEnumVarName() throws Exception {
        final CSharpFunctionsServerCodegen codegen = new CSharpFunctionsServerCodegen();
        codegen.processOpts();

        assertEquals(codegen.toEnumVarName("FooBar", "string"), "FooBarEnum");
        assertEquals(codegen.toEnumVarName("fooBar", "string"), "FooBarEnum");
        assertEquals(codegen.toEnumVarName("foo-bar", "string"), "FooBarEnum");
        assertEquals(codegen.toEnumVarName("foo_bar", "string"), "FooBarEnum");
        assertEquals(codegen.toEnumVarName("foo bar", "string"), "FooBarEnum");

        // The below cases do not work currently, camelize doesn't support uppercase
        // assertEquals(codegen.toEnumVarName("FOO-BAR", "string"), "FooBar");
        // assertEquals(codegen.toEnumVarName("FOO_BAR", "string"), "FooBar");
    }
}
