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
package jp.co.opentone.bsol.linkbinder.action;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;

/**
 * @author opentone
 */
public abstract class AbstractCorresponEditPageAction extends AbstractAction {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -5274374981053247890L;

    /**
     * CorresponEditPageオブジェクト.
     */
    private CorresponEditPage page;

    /**
     * ページを指定してインスタンス化する.
     * @param page CorresponEditPageオブジェクト
     */
    public AbstractCorresponEditPageAction(CorresponEditPage page) {
        super(page);
        this.page = page;
    }

    /**
     * 宛先-活動単位のDTOを作成する.
     * @param corresponGroupId 活動単位ID
     * @param to 対象がTOの場合はtrue
     * @return 宛先-活動単位
     * @exception ServiceAbortException 処理エラー.
     */
    protected AddressCorresponGroup
        createAddressCorresponGroup(Long corresponGroupId, boolean to)
            throws ServiceAbortException {
        AddressCorresponGroup group = new AddressCorresponGroup();
        group.setCorresponId(page.getId());
        group.setCorresponGroup(page.getCorresponGroupService().find(corresponGroupId));
        group.setAddressType(to ? AddressType.TO : AddressType.CC);
        group.setMode(UpdateMode.NEW);
        return group;
    }
}

