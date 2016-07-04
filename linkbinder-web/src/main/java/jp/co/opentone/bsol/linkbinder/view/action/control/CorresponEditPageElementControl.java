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
package jp.co.opentone.bsol.linkbinder.view.action.control;

import java.io.Serializable;

import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;

/**
 * コレポン文書入力画面に表示する項目の状態を制御する.
 * @author opentone
 */
public class CorresponEditPageElementControl implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8445666855118069062L;

    /**
     * コレポン文書編集画面.
     */
    private CorresponEditPage page;
    /**
     * コレポン文書.
     */
    private Correspon correspon;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponEditPageElementControl() {
    }

    /**
     * このオブジェクトの処理に必要なオブジェクトを設定する.
     * @param editPage コレポン文書編集画面
     */
    public void setUp(CorresponEditPage editPage) {
        this.page = editPage;
        this.correspon = this.page.getCorrespon();
    }

    /**
     * 返信文書の場合はtrueを返す.
     * @return 返信文書の場合はtrue
     */
    public boolean isReplyCorrespon() {
        return correspon != null && correspon.getParentCorresponId() != null;
    }

    /**
     * 宛先(To)を追加可能な場合はtrueを返す.
     * @return 追加可能な場合はtrue
     */
    public boolean isToAddressAddable() {
        //  依頼文書の場合は新しい宛先(To)を追加することができる
        return true;
    }

    /**
     * 宛先(To)を全て削除することが可能な場合はtrueを返す.
     * @return 全て削除が可能な場合はtrue
     */
    public boolean isToAddressAllDeletable() {
        return true;
    }

    /**
     * 宛先(Cc)を追加可能な場合はtrueを返す.
     * @return 追加可能な場合はtrue
     */
    public boolean isCcAddressAddable() {
        //  宛先(Cc)は適切な権限さえあればいつでも追加できる
        return true;
    }

    /**
     * 宛先(Cc)を全て削除することが可能な場合はtrueを返す.
     * @return 全て削除が可能な場合はtrue
     */
    public boolean isCcAddressAllDeletable() {
        return true;
    }

    /**
     * 宛先を追加・削除可能な場合はtrueを返す.
     * @return 編集可能な場合はtrue
     */
    public boolean isToAddressEditable() {
        return true;
    }

    /**
     * 送信元活動単位が編集可能な場合はtrueを返す.
     * @return 編集可能な場合はtrue.
     */
    public boolean isFromEditable() {
        return true;
    }

    /**
     * コレポン文書状態が編集可能な場合はtrueを返す.
     * @return 編集可能な場合はtrue
     */
    public boolean isCorresponStatusEditable() {
        return true;
    }

    /**
     * コレポン文書種別が編集可能な場合はtrueを返す.
     * @return 編集可能な場合はtrue
     */
    public boolean isCorresponTypeEditable() {
        if (correspon != null) {
            switch (correspon.getWorkflowStatus()) {
            case REQUEST_FOR_CHECK :
            case REQUEST_FOR_APPROVAL :
            case UNDER_CONSIDERATION :
                return false;
            default:
                return true;
            }
        }
        return false;
    }
}
