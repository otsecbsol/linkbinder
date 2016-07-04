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
package jp.co.opentone.bsol.linkbinder;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.User;
import net.arnx.jsonic.JSON;

/**
 * @author opentone
 */
public class JSONICTest extends AbstractTestCase {

    @Test
    public void testDtoToJSON1() {

        User u;
        List<User> users = new ArrayList<User>();
        Map<String, User> userMap = new HashMap<String, User>();

        u = new User();
        u.setEmpNo("ZZA01");
        u.setNameE("Taro Yamada");

        users.add(u);
        userMap.put(u.getEmpNo(), u);

        u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Jiro Yamada");

        users.add(u);
        userMap.put(u.getEmpNo(), u);

        System.out.println(JSON.encode(users, true));
        System.out.println(JSON.encode(userMap, true));
    }

    @Test
    public void testDtoToJSON2() {

        CorresponGroup g;
        List<CorresponGroup> groups = new ArrayList<CorresponGroup>();
        Map<Long, CorresponGroup> groupMap = new HashMap<Long, CorresponGroup>();

        g = new CorresponGroup();
        g.setId(1L);
        g.setName("YOC:IT");

        groups.add(g);
        groupMap.put(g.getId(), g);

        g = new CorresponGroup();
        g.setId(2L);
        g.setName("YOC:PIPING");

        groups.add(g);
        groupMap.put(g.getId(), g);

        System.out.println(JSON.encode(groups, true));
        System.out.println(JSON.encode(groupMap));
    }

    @Test
    public void testDtoToJSON3() {

        CorresponGroup g;
        List<CorresponGroup> groups = new ArrayList<CorresponGroup>();
        Map<Long, CorresponGroup> groupMap = new HashMap<Long, CorresponGroup>();

        g = new CorresponGroup();
        g.setId(1L);
        g.setName("YOC:IT\"");

        groups.add(g);
        groupMap.put(g.getId(), g);

        g = new CorresponGroup();
        g.setId(2L);
        g.setName("YOC:PIPING");

        groups.add(g);
        groupMap.put(g.getId(), g);

        System.out.println(JSON.encode(groups, true));
        System.out.println(JSON.encode(groupMap));
    }

    @Test
    public void testJSON1() {

        CorresponGroup g;
        List<CorresponGroup> groups = new ArrayList<CorresponGroup>();
        Map<Long, CorresponGroup> groupMap = new HashMap<Long, CorresponGroup>();

        g = new CorresponGroup();
        g.setId(1L);
        g.setName("YOC:IT\"");

        groups.add(g);
        groupMap.put(g.getId(), g);

        g = new CorresponGroup();
        g.setId(2L);
        g.setName("YOC:PIPING");

        groups.add(g);
        groupMap.put(g.getId(), g);

        JSON json = new JSON() {
//            /* (non-Javadoc)
//             * @see net.arnx.jsonic.JSON#preformat(net.arnx.jsonic.JSON.Context, java.lang.Object)
//             */
//            @Override
//            protected Object preformat(Context context, Object value) throws Exception {
//                if (value instanceof String) {
//                    System.out.println(value);
//                    String str = (String) value;
//                    if (str.indexOf('\\') >= 0) {
//                        return str.replaceAll("\\", "\\\\");
//                    }
//                    return value;
////                    return ((String) value).replaceAll("\\", "\\\\");
//                }
//                return super.preformat(context, value);
//            }
        };

        System.out.println(json.format(groupMap).replaceAll("\\\\", "\\\\\\\\"));
    }

    @Test
    public void testJSONToObject() {
        String json = "[{\"1\":[\"ZZA01\",\"ZZA02\"]},{\"2\":[\"ZZA03\",\"ZZA04\"]}]";
        List<Map<Long, List<String>>> list =
            (List<Map<Long, List<String>>>) JSON.decode(json, List.class);

        assertEquals(2, list.size());
        for (Map<Long, List<String>> entry : list) {
            System.out.println(entry);
        }
    }

    public List<CorresponGroupUserMapping> m;
    @Test
    public void testJSONToObject2() throws Exception {
        String json =
            "[{corresponGroupId:\"1\",users:[{empNo:\"ZZA01\"},{empNo:\"ZZA02\"}]}," +
            "{corresponGroupId:\"2\",users:[{empNo:\"ZZA03\"},{empNo:\"ZZA04\"}]}]";

        List<CorresponGroupUserMapping> mappings =
            (List<CorresponGroupUserMapping>)
                    JSON.decode(json,
                                getClass().getField("m").getGenericType());


        assertEquals(2, mappings.size());
        for (CorresponGroupUserMapping m : mappings) {
            System.out.println(m);
        }
    }

    @Test
    public void testMappings() throws Exception {
        List<AddressCorresponGroup> list = new ArrayList<AddressCorresponGroup>();
        List<AddressUser> aul = new ArrayList<AddressUser>();
        AddressCorresponGroup ag;
        CorresponGroup g;
        AddressUser au;
        User u;

        ag = new AddressCorresponGroup();
        g = new CorresponGroup();
        g.setId(1L);
        ag.setCorresponGroup(g);

        aul = new ArrayList<AddressUser>();
        au = new AddressUser();
        u = new User();
        u.setEmpNo("ZZA01");
        au.setUser(u);
        aul.add(au);
//
        ag.setUsers(aul);
        list.add(ag);

        System.out.println(JSON.encode(list));
    }
}
