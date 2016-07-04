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
package jp.co.opentone.bsol.linkbinder.view.correspon;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.framework.web.view.flash.Flash;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * CorresponPageの親クラス.
 * @author opentone
 */
public abstract class AbstractCorresponPage extends AbstractPage {
    /**
     * SerialVersionID.
     */
    private static final long serialVersionUID = -379403705879141137L;

    /**
     * 戻り先ページをFlashに保持する時のキー名.
     */
    private static final String KEY_BACK_PAGE = "backPage";

    /**
     * コレポンID一覧をFlashに保持する時のキー名.
     */
    private static final String KEY_CORRESPON_IDS = "corresponIds";

    /**
     * コレポンID一覧内の表示している位置をFlashに保持する時のキー名.
     */
    private static final String KEY_DISPLAY_INDEX = "displayIndex";

    /**
     * 戻り先ページ名.
     */
    private String backPage;

    /**
     * 前後移動用コレポンIDリスト.
     */
    @Transfer
    private List<Long> corresponIds;

    /**
     * 前後移動用コレポンIDリスト上の表示している位置.
     */
    @Transfer
    private int displayIndex;

    /**
     * 空のインスタンスを生成する.
     */
    public AbstractCorresponPage() {
    }

    /**
     * 戻り先ページ名をセットする.
     * @param backPage 戻り先ページ名
     */
    public void setBackPage(String backPage) {
        this.backPage = backPage;
    }

    /**
     * 戻り先ページ名を返す.
     * <p>
     * デフォルトの戻り先を使用する場合<code>null</code>が返される.
     * </p>
     * @return 戻り先ページ名
     */
    public String getBackPage() {
        return backPage;
    }

    /**
     * 前後移動用コレポンIDリストをセットする.
     * @param corresponIds コレポンIDリスト
     */
    public void setCorresponIds(List<Long> corresponIds) {
        this.corresponIds = corresponIds;
    }

    /**
     * 前後移動用コレポンIDリストを返す.
     * @return コレポンIDリスト
     */
    public List<Long> getCorresponIds() {
        return corresponIds;
    }

    /**
     * 表示しているコレポン文書の位置をセットする.
     * @param displayIndex コレポン文書表示位置
     */
    public void setDisplayIndex(int displayIndex) {
        this.displayIndex = displayIndex;
    }

    /**
     * 表示しているコレポン文書の位置を返す.
     * @return displayIndex コレポン文書表示位置
     */
    public int getDisplayIndex() {
        return displayIndex;
    }

    /**
     * 一覧の総件数.
     * @return 総件数
     */
    public int getTotalCount() {
        if (getCorresponIds() != null) {
            return getCorresponIds().size();
        }
        return 0;
    }

    /**
     * Transferされたデータを受け取る.
     */
    @PostConstruct
    public void receiveTransferValue() {
        Flash flash = new Flash();
        this.backPage = flash.getValue(KEY_BACK_PAGE);
        this.corresponIds = flash.getValue(KEY_CORRESPON_IDS);

        Integer temp = flash.getValue(KEY_DISPLAY_INDEX);
        if (temp != null) {
            this.displayIndex = temp.intValue();
        }
    }

    /**
     * ページ間で戻り先ページの値を持ち歩く.
     */
    public void transferBackPage() {
        if (StringUtils.isEmpty(this.backPage)) {
            return;
        }

        Flash flash = new Flash();
        flash.setValue(KEY_BACK_PAGE, this.backPage);
    }

    /**
     * ページ間で表示情報の値を持ち歩く.
     */
    public void transferCorresponDisplayInfo() {
        if (this.corresponIds == null) {
            return;
        }

        Flash flash = new Flash();
        flash.setValue(KEY_CORRESPON_IDS, this.corresponIds);
        flash.setValue(KEY_DISPLAY_INDEX, Integer.valueOf(this.displayIndex));
    }
}
