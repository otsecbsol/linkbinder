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
package jp.co.opentone.bsol.linkbinder.scrapbook;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;

public class CopyPropertiesTest {

    @Test
    public void testPropertyUtilsCopyProperties() throws Exception {

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("id", Long.valueOf(10));
        values.put("name", "test");
        List<CorresponGroup> groups = new ArrayList<CorresponGroup>();
        CorresponGroup g = new CorresponGroup();
        g.setId(1L);
        g.setName("g1");
        groups.add(g);

        g = new CorresponGroup();
        g.setId(2L);
        g.setName("g2");
        groups.add(g);

        values.put("groups", groups);

        Dto dto = new Dto();

        PropertyUtils.copyProperties(dto, values);

        assertEquals(Long.valueOf(10L), dto.getId());
        assertEquals("test", dto.getName());

        List<CorresponGroup> actual = dto.getGroups();
        assertNotNull(actual);
        assertEquals(groups.size(), actual.size());
        assertEquals(groups.get(0).getId(), actual.get(0).getId());
        assertEquals(groups.get(0).getName(), actual.get(0).getName());

    }

    public static class Dto {

        private Long id;
        private String name;
        private List<CorresponGroup> groups;

        /**
         * @param id
         *            the id to set
         */
        public void setId(Long id) {
            this.id = id;
        }

        /**
         * @return the id
         */
        public Long getId() {
            return id;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param groups
         *            the groups to set
         */
        public void setGroups(List<CorresponGroup> groups) {
            this.groups = groups;
        }

        /**
         * @return the groups
         */
        public List<CorresponGroup> getGroups() {
            return groups;
        }
    }
}
