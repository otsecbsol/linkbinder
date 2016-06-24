/*
 * Copyright 2016 OPEN TONE Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.opentone.bsol.linkbinder.view.common.module.redirect;


import static org.junit.Assert.*;

import org.junit.Test;

public class RedirectScreenIdTest {

    @Test
    public void testGetPairIdWith(){
        assertEquals(RedirectScreenId.PROJECT_HOME,
            RedirectScreenId.getPairedIdOf("/projectHome.jsf"));
        assertEquals(RedirectScreenId.CORRESPON,
            RedirectScreenId.getPairedIdOf("/correspon/correspon.jsf"));
        assertNull(
            RedirectScreenId.getPairedIdOf("/correspon/no_match.jsf"));
    }
}
