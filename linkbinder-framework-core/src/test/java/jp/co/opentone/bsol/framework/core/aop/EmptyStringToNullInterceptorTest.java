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
package jp.co.opentone.bsol.framework.core.aop;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.test.mock.view.MockPage;


/**
 * {@link EmptyStringToNullInterceptor}のテストケース.
 * @author opentone
 */
@ContextConfiguration(locations={"classpath:emptyStringToNullInterceptorTestContext.xml"})
public class EmptyStringToNullInterceptorTest extends AbstractTestCase {


    /**
     * プレゼンテーション層コンポーネントのMock.
     */
    @Resource
    private MockPage page;

    private Long id = 10L;
    private String name = "test";

    @Before
    public void setUp() {
        page.setId(id);
        page.setName(name);
    }

    /**
     * pageオブジェクトの、String型のsetterに空文字を渡すとnullに変換されていることを確認する.
     * @throws Exception
     */
    @Test
    public void testSetter() throws Exception {
        assertEquals(id, page.getId());
        assertEquals(name, page.getName());

        page.setName(null);
        assertEquals(null, page.getName());

        page.setName("");
        assertEquals(null, page.getName());

        System.out.println(page.getClass().getName());
    }
}
