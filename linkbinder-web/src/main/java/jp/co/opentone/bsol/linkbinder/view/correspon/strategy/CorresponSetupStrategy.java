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

import java.io.Serializable;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.attachment.CopiedAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;

/**
 * コレポン文書入力画面に初期表示するコレポン文書オブジェクトを生成する.
 * @author opentone
 */
public abstract class CorresponSetupStrategy implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 3510527652458447078L;

    /**
     * コレポン文書入力画面.
     */
    //CHECKSTYLE:OFF
    protected CorresponEditPage page;
    //CHECKSTYLE:ON

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponSetupStrategy() {
    }

    /**
     * コレポン文書入力画面に対応する、このクラスのインスタンスを返す.
     * @param page コレポン文書入力画面
     * @param editMode 編集モード
     * @return 編集モードに対応した処理を行うオブジェクト
     */
    public static CorresponSetupStrategy getCorresponSetupStrategy(
            CorresponEditPage page, CorresponEditMode editMode) {
        CorresponSetupStrategy strategy;
        switch (editMode) {
        case REPLY :
            strategy = new ReplyCorresponSetupStrategy();
            break;
        case REPLY_WITH_PREVIOUS_CORRESPON :
            strategy = new ReplyWithPreviousCorresponSetupStrategy();
            break;
        case UPDATE :
            strategy = new UpdateCorresponSetupStrategy();
            break;
        case COPY :
            strategy = new CopyCorresponSetupStrategy();
            break;
        case REVISE :
            strategy = new ReviseCorresponSetupStrategy();
            break;
        case BACK :
            strategy = new BackCorresponSetupStrategy();
            break;
        case FORWARD :
            strategy = new ForwardCorresponSetupStrategy();
            break;
        case NEW:
        default :
            strategy = new NewCorresponSetupStrategy();
            break;
        }
        strategy.page = page;
        strategy.setupTitle();

        return strategy;
    }

    public void setupTitle() {
        page.setTitle(getPageTitle());
    }

    /**
     * 指定された添付ファイル情報を、複写されたファイル情報としてページに設定する.
     * @param c 複写元コレポン文書
     * @param id 追加するファイルID
     * @param fileName 追加するファイル名
     */
    protected void addAttachmentToPage(Correspon c, Long id, String fileName) {
        Attachment a = new Attachment();
        a.setId(id);
        a.setCorresponId(c.getId());
        a.setFileName(fileName);

        CopiedAttachmentInfo info = new CopiedAttachmentInfo(a, page.getCorresponService());
        page.addAttachment(info);
    }

    /**
     * コレポン文書入力画面に初期表示するコレポン文書オブジェクトを準備し、pageに格納する.
     * @throws ServiceAbortException コレポン文書の準備に失敗
     */
    public abstract void setup() throws ServiceAbortException;

    /**
     * コレポン文書入力画面のタイトルを返す.
     * @return 画面タイトル
     */
    protected abstract String getPageTitle();


    /**
     * コレポン文書オブジェクトに設定された宛先から、返信に関する情報をクリアする.
     * <p>
     * @param correspon コレポン文書
     */
    public static void clearRepliedInformationFromAddresses(Correspon correspon) {
        if (correspon.getAddressCorresponGroups() == null) {
            return;
        }

        for (AddressCorresponGroup ag : correspon.getAddressCorresponGroups()) {
            if (ag.getUsers() != null) {
                for (AddressUser au : ag.getUsers()) {
                    if (au.getPersonInCharges() != null) {
                        au.getPersonInCharges().clear();
                    }
                }
            }
            ag.setReplyCount(0L);
        }
    }
}
