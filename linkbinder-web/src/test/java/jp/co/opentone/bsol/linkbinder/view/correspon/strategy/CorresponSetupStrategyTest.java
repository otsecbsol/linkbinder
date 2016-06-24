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
package jp.co.opentone.bsol.linkbinder.view.correspon.strategy;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;


/**
 * {@link CorresponSetupStrategy}のテストケース.
 * @author opentone
 */
public class CorresponSetupStrategyTest extends AbstractTestCase {

    private CorresponEditPage page;

    @Before
    public void setUp() {
        page = new CorresponEditPage();
        page.setTitle("文書");
        page.setCorrespon(createCorrespon(1L, "AA"));
    }

    private Correspon createCorrespon(Long id, String corresponNo) {
        Correspon correspon = new Correspon();
        correspon.setId(id);
        correspon.setCorresponNo(corresponNo);

        correspon.setAddressCorresponGroups(createAddressCorresponGroups());
        return correspon;
    }

    private List<AddressCorresponGroup> createAddressCorresponGroups() {
        List<AddressCorresponGroup> addressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        addressCorresponGroups.add(createAddressCorresponGroup(1L));
        addressCorresponGroups.add(createAddressCorresponGroup(2L));
        addressCorresponGroups.add(createAddressCorresponGroup(3L));
        addressCorresponGroups.add(createAddressCorresponGroup(4L));
        return addressCorresponGroups;
    }

    private AddressCorresponGroup createAddressCorresponGroup(Long id) {
        AddressCorresponGroup addressCorresponGroup = new AddressCorresponGroup();
        addressCorresponGroup.setId(id);
        addressCorresponGroup.setReplyCount(1L);
        return addressCorresponGroup;
    }

    /**
     * コレポン文書入力画面編集モードとstrategyのマッピング定義を返す.
     * @return
     */
    private static Map<CorresponEditMode, Class<? extends CorresponSetupStrategy>> createStrategyMap() {

        Map<CorresponEditMode, Class<? extends CorresponSetupStrategy>> strategies =
                new HashMap<CorresponEditMode, Class<? extends CorresponSetupStrategy>>();

        strategies.put(null, NewCorresponSetupStrategy.class);
        strategies.put(CorresponEditMode.NEW, NewCorresponSetupStrategy.class);
        strategies.put(CorresponEditMode.BACK, BackCorresponSetupStrategy.class);
        strategies.put(CorresponEditMode.COPY, CopyCorresponSetupStrategy.class);
        strategies.put(CorresponEditMode.FORWARD, ForwardCorresponSetupStrategy.class);
        strategies.put(CorresponEditMode.REPLY, ReplyCorresponSetupStrategy.class);
        strategies.put(CorresponEditMode.REPLY_WITH_PREVIOUS_CORRESPON, ReplyWithPreviousCorresponSetupStrategy.class);
        strategies.put(CorresponEditMode.REVISE, ReviseCorresponSetupStrategy.class);
        strategies.put(CorresponEditMode.UPDATE, UpdateCorresponSetupStrategy.class);

        return strategies;
    }

    /**
     * {@link CorresponSetupStrategy#getCorresponSetupStrategy(CorresponEditPage, CorresponEditMode)}
     * の検証.
     */
    @Test
    public void testGetCorresponSetupStrategy() {

        Map<CorresponEditMode, Class<? extends CorresponSetupStrategy>> strategies =
            createStrategyMap();

        //  編集モードに応じたオブジェクトが返されるか検証する
        for (CorresponEditMode m : CorresponEditMode.values()) {
            CorresponSetupStrategy s =
                    CorresponSetupStrategy.getCorresponSetupStrategy(page, m);

            assertNotNull(s);
            assertEquals(strategies.get(m), s.getClass());
            assertEquals(page, s.page);
            assertEquals(page.getTitle(), s.getPageTitle());
        }
    }

    /**
     * {@link CorresponSetupStrategy#clearRepliedInformationFromAddresses()}
     * の検証.
     */
    @Test
    public void testClearRepliedInformationFromAddresses() {
        Correspon correspon = page.getCorrespon();
        List<AddressCorresponGroup> addressCorresponGroups = correspon.getAddressCorresponGroups();

        // クリア前は返信数がセットされた状態
        for (AddressCorresponGroup acg : addressCorresponGroups) {
            assertEquals(1L, acg.getReplyCount().longValue());
        }

        CorresponSetupStrategy.clearRepliedInformationFromAddresses(correspon);

        // クリア後は返信数が0になる
        for (AddressCorresponGroup acg : addressCorresponGroups) {
            assertEquals(0L, acg.getReplyCount().longValue());
        }
    }
}
