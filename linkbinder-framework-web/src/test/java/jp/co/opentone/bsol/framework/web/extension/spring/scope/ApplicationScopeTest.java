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
package jp.co.opentone.bsol.framework.web.extension.spring.scope;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;


/**
 * ApplicationScopeのテストケース.
 * <p>
 * @author opentone
 */
public class ApplicationScopeTest {

    private ApplicationScope scope;
    private Map<String, Object> map;

    @Before
    public void setUp() {
        map = new HashMap<String, Object>();
        scope = new ApplicationScope() {
            @Override
            Map<String, Object> getMap() {
                return map;
            }
        };
    }

    @After
    public void teardown() {
        map.clear();
    }

    @Test
    public void testGet() {
        String name = "foo";
        final Set<Object> ret = new HashSet<Object>();
        ObjectFactory<?> f = new ObjectFactory<Object>() {
            public Object getObject() throws BeansException {
                if (ret.isEmpty()) {
                    return null;
                }
                Iterator<Object> it = ret.iterator();
                return it.next();
            }
        };

        //  ApplicationScopeに無くObjectFactoryで新しいBeanが生成されると
        //  ApplicationScopeに追加される
        ret.add(this);
        Object result = scope.get(name, f);
        assertEquals(this, result);

        //  2度目はMapから同じインスタンスが返される
        ret.clear();
        result = scope.get(name, f);
        assertEquals(this, result);

        //  別名だと違うインスタンス
        result = scope.get("bar", f);
        assertFalse(this == result);

        //  削除の戻り値は同じインスタンス
        result = scope.remove(name);
        assertEquals(this, result);
    }
}
