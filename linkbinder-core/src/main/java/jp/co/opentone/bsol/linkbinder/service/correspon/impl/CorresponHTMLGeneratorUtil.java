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
package jp.co.opentone.bsol.linkbinder.service.correspon.impl;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.core.util.HTMLConvertUtil;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponPageFormatter;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponResponseHistoryModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author opentone
 */
public class CorresponHTMLGeneratorUtil extends HTMLConvertUtil {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -3022236451273258335L;

    /**
     * TRタグ制御Classの区切り文字.
     */
    public static final String DELIM_CLASS = ",";

    /**
     * TRタグ制御Class：無効.
     */
    public static final String CANCELED = "canceled";

    /**
     * TRタグ制御Class：強調表示.
     */
    public static final String HIGHLIGHT = "highlight";

    /**
     * TRタグ制御Class：返信期限切れ.
     */
    public static final String DEADLINE = "deadline";

    /**
     * TRタグ制御Class：奇数行.
     */
    public static final String ODD = "odd";

    /**
     * TRタグ制御Class：偶数行.
     */
    public static final String EVEN = "even";

    /**
     * サーバーURL.
     */
    private String baseURL;
    /**
     * アプリケーションコンテキストURL.
     */
    private String contextURL;
    /**
     * アイコン名.
     */
    private String iconName;
    /**
     * サーバーベースパス.
     */
    private String basePath;
    /**
     * スタイルシート名.
     */
    private String stylesheetName;
    /**
     * コレポン文書整形オブジェクト.
     */
    private CorresponPageFormatter formatter;
    /**
     * 応答履歴.
     */
    private List<CorresponResponseHistoryModel> corresponResponseHistoryModel;

    /**
     * Person in Charge利用可否.
     */
    private boolean usePersonInCharge;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponHTMLGeneratorUtil() {
        super();
    }

    /**
     * 印刷に使用するスタイルシートの内容を文字列で返す.
     * @return スタイルシートの内容
     * @throws IOException スタイルシートの読込に失敗
     */
    public String getStylesheetContent() throws IOException {
        URL css = new URL(getBaseURL() + getStylesheetName());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = null;
        try {
            in = css.openStream();
            int i;
            //CHECKSTYLE:OFF
            byte[] buf = new byte[4096];
            //CHECKSTYLE:ON
            while ((i = in.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, i);
            }
            return out.toString(SystemConfig.getValue(Constants.KEY_HTML_ENCODING));
        } finally {
            in.close();
        }
    }

    /**
     * baseURLを取得します.
     * @return the baseURL
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * baseURLを設定します.
     * @param baseURL the baseURL to set
     */
    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * iconNameを取得します.
     * @return the iconName
     */
    public String getIconName() {
        return iconName;
    }

    /**
     * iconNameを設定します.
     * @param iconName the iconName to set
     */
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    /**
     * iconNameを取得します.
     * @return iconName
     */
    public String getIconPathName() {
        return getBaseURL() + iconName;
    }

    /**
     * コレポン文書番号を整形して返す.
     * @param correspon コレポン文書
     * @return 整形後のコレポン文書番号
     */
    public String formatCorresponNo(Correspon correspon) {
        if (formatter == null) {
            formatter = new CorresponPageFormatter(correspon);
        }
        formatter.setCorrespon(correspon);

        return formatter.getCorresponNo();

    }

    /**
     * @param basePath the basePath to set
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /**
     * @return the basePath
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * @param stylesheetName the stylesheetName to set
     */
    public void setStylesheetName(String stylesheetName) {
        this.stylesheetName = stylesheetName;
    }

    /**
     * @return the stylesheetName
     */
    public String getStylesheetName() {
        return stylesheetName;
    }

    /**
     * @param contextURL the contextURL to set
     */
    public void setContextURL(String contextURL) {
        this.contextURL = contextURL;
    }

    /**
     * @return the contextURL
     */
    public String getContextURL() {
        return contextURL;
    }

    /**
     * @param corresponResponseHistoryModel the corresponResponseHistoryModel to set
     */
    public void setCorresponResponseHistoryModel(
        List<CorresponResponseHistoryModel> corresponResponseHistoryModel) {
        this.corresponResponseHistoryModel = corresponResponseHistoryModel;
    }

    /**
     * @return the corresponResponseHistoryModel
     */
    public List<CorresponResponseHistoryModel> getCorresponResponseHistoryModel() {
        return corresponResponseHistoryModel;
    }

    /**
     * @param usePersonInCharge the usePersonInCharge to set
     */
    public void setUsePersonInCharge(boolean usePersonInCharge) {
        this.usePersonInCharge = usePersonInCharge;
    }

    /**
     * @return the usePersonInCharge
     */
    public boolean isUsePersonInCharge() {
        return usePersonInCharge;
    }

    /**
     * TRタグ制御Classを取得する.
     * @param oddRow Listの行数（0～）
     * @param history コレポン文書履歴
     * @param id 強調表示するコレポン文書ID
     * @return 応答履歴のTRタグ制御Class
     */
    public String getClassId(boolean oddRow, CorresponResponseHistory history, Long id) {
        if (history.getCorrespon().getId().equals(id)) {
            return HIGHLIGHT;
        } else if (CorresponStatus.CANCELED.equals(history.getCorrespon().getCorresponStatus())) {
            return CANCELED;
        } else if (DateUtil.isExpire(history.getCorrespon().getDeadlineForReply())
                && CorresponStatus.OPEN.equals(history.getCorrespon().getCorresponStatus())) {
            return DEADLINE;
        } else if (oddRow) {
            return ODD;
        } else {
            return EVEN;
        }
    }
}
