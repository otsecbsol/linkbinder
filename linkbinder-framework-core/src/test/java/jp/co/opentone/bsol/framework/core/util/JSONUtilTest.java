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
package jp.co.opentone.bsol.framework.core.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;


/**
 * {@link JSONUtil}のテストケース.
 * [jp.co.opentone.bsol.linkbinder.view.util] から移管。
 * 移管に伴って、CorresponGroup クラスを InnerClass に置換。
 * @author opentone
 */
public class JSONUtilTest {

    /**
     * {@link JSONUtil#encode(Object)}のテスト.
     */
    @Test
    public void testEncode() {

        DummyCorresponGroup g;
        List<DummyCorresponGroup> groups = new ArrayList<DummyCorresponGroup>();
        Map<Long, DummyCorresponGroup> groupMap = new HashMap<Long, DummyCorresponGroup>();

        g = new DummyCorresponGroup();
        g.setId(1L);
        g.setName("YOC:IT\"");

        groups.add(g);
        groupMap.put(g.getId(), g);

        g = new DummyCorresponGroup();
        g.setId(2L);
        g.setName("YOC:PIPING");
        g.setName("YOC:PIP'ING");

        groups.add(g);
        groupMap.put(g.getId(), g);

        String actual = JSONUtil.encode(groupMap);
        System.out.println(actual);
        assertNotNull(actual);
    }

    /**
     * {@link JSONUtil#replace(String)}のテスト.
     */
    @Test
    public void testReplace() {
        String text;
        // シンプル
        text = "{\"1\":{\"test\"}}";
        assertEquals(text, JSONUtil.replace(text));

        // \"含む
        text = "{\"1\":{\"te\\st\\\"\"}}";
        assertEquals("{\"1\":{\"te\\st\\\"\"}}", JSONUtil.replace(text));

    }

    private class DummyCorresponGroup {

        private long id;
        @SuppressWarnings("unused")
        private String name;

        /**
         * @param l
         */
        public void setId(long l) {
            id = l;
        }

        /**
         * @return
         */
        public Long getId() {
            return id;
        }

        /**
         * @param string
         */
        public void setName(String name) {
            this.name = name;
        }

    }
}
