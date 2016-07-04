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
package jp.co.opentone.bsol.linkbinder.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;

/**
 * 全文検索アプリケーションで扱う文書クラス.
 * <p>
 * 画面上で使用する属性の取得メソッドを用意している。<br />
 * また、出力形式などの変換も行う。
 * </p>
 *
 * @author opentone
 *
 */
public class FullTextSearchCorresponsResult implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -1501288908991451340L;

    /**
     * 文書の要約.
     */
    private List<FullTextSearchSummaryData> summaryList;

    private Long id;
    private String title;
    private String mdate;
    private String workflowStatus;
    private String attachmentId;

    /**
     * コレポン文書IDを取得する.
     *
     * @return  ID
     */
    public Long getId() {
        return id;
    }

    /**
     * ハイライト変換したタイトルを取得する.
     *
     * @return  タイトル
     */
    public String getTitle() {
        return title;
    }

    /**
     * yyyy年MM月dd日 HH時mm分ss秒 形式に変換した更新日付を取得する.
     *
     * @return  更新日付
     */
    public String getMdate() {
        return mdate;
//        /** 設定されたフォーマット定義 */
//        SimpleDateFormat dateFormatter =
//            new SimpleDateFormat(SystemConfig.getValue(Constants.DATE_FORMATTER));
//        return DateUtil.convertFromAthotherFormat(title, dateFormatter);
    }

    /**
     * 承認状態を取得する.
     *
     * @return  承認状態
     */
    public String getWorkflowStatus() {
        return workflowStatus;
    }

    /**
     * 文書の要約を取得する.
     *
     * @return 要約
     */
    public List<FullTextSearchSummaryData> getSummaryList() {
        return summaryList;
    }

    /**
     * 文書の要約を設定する.
     *
     * @param summaryList 文書の要約リスト
     */
    public void setSummaryList(List<FullTextSearchSummaryData> summaryList) {
        this.summaryList = summaryList;
    }

    /**
     * 添付ファイルIDを取得する.
     *
     * @return  attachmentId
     */
    public String getAttachmentId() {
        return attachmentId;
    }

    /**
     * 要約の表示/非表示フラグを取得する.
     * <p>
     * 承認状態が発行であるコレポン文書のみの要約を表示する
     * </p>
     *
     * @return the summaryViewflag
     */
    public boolean isSummaryViewFlag() {
        return (StringUtils.equals(WorkflowStatus.ISSUED.getValue().toString(),
                                   getWorkflowStatus()));
    }

    /**
     * @param long1
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param mdate
     */
    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    /**
     * @param workflowStatus
     */
    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    /**
     * @param attachmentId
     */
    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }
}
