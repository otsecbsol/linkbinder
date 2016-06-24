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


/**
 * テーブル [v_correspon] のユーザー情報でのサマリ情報を表すDto.
 *
 * @author opentone
 *
 */
public class CorresponUserSummary extends AbstractDto {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 5037694927374376660L;

    /**
     * サマリ情報：Draft状態の数.
     */
    private Long draftCount;

    /**
     * サマリ情報:Request for Check, Request for Approve, Under Consideration状態の数.
     */
    private Long underReviewCount;

    /**
     * サマリ情報：Checkerに指定されていて、Request for Check状態の数.
     */
    private Long requestForCheckCount;

    /**
     * サマリ情報：Approverに指定されていて、Request for Approve状態の数.
     */
    private Long requestForApproveCount;

    /**
     * サマリ情報：Denied状態の作成した文書の数.
     */
    private Long deniedCount;

    /**
     * サマリ情報：Issued状態で、Openの数.
     */
    private Long openCount;

    /**
     * サマリ情報：Issued状態で、Closedの数.
     */
    private Long closedCount;

    /**
     * サマリ情報：Issued状態で、Canceledの数.
     */
    private Long canceledCount;

    /**
     * サマリ情報：Action（返信要）文書で、Attentionに指定されている数.
     */
    private Long attentionCount;

    /**
     * サマリ情報：Action（返信要）文書で、Person in Chargeに指定されている数.
     */
    private Long personInChargeCount;

    /**
     * サマリ情報：Ccに指定されている数.
     */
    private Long ccCount;

    /**
     * サマリ情報：ログインユーザー宛文書の未読数.
     */
    private Long newCount;

    /**
     * サマリ情報：ログインユーザー宛文書の既読数.
     */
    private Long readCount;

   /**
     * 空のインスタンスを生成する.
     */
    public CorresponUserSummary() {
    }

    /**
     * draftCountを取得します.
     * @return the draftCount
     */
    public Long getDraftCount() {
        return draftCount;
    }

    /**
     * draftCountを設定します.
     * @param draftCount the draftCount to set
     */
    public void setDraftCount(Long draftCount) {
        this.draftCount = draftCount;
    }

    /**
     * underReviewCountを取得します.
     * @return the undeReviewCount
     */
    public Long getUnderReviewCount() {
        return underReviewCount;
    }

    /**
     * underReviewCountを設定します.
     * @param undeReviewCount the undeReviewCount to set
     */
    public void setUnderReviewCount(Long undeReviewCount) {
        this.underReviewCount = undeReviewCount;
    }

    /**
     * requestForCheckCountを取得します.
     * @return the requestForCheckCount
     */
    public Long getRequestForCheckCount() {
        return requestForCheckCount;
    }

    /**
     * requestForCheckCountを設定します.
     * @param requestForCheckCount the requestForCheckCount to set
     */
    public void setRequestForCheckCount(Long requestForCheckCount) {
        this.requestForCheckCount = requestForCheckCount;
    }

    /**
     * requestForApproveCountを取得します.
     * @return the requestForApproveCount
     */
    public Long getRequestForApproveCount() {
        return requestForApproveCount;
    }

    /**
     * requestForApproveCountを設定します.
     * @param requestForApproveCount the requestForApproveCount to set
     */
    public void setRequestForApproveCount(Long requestForApproveCount) {
        this.requestForApproveCount = requestForApproveCount;
    }

    /**
     * deinedCountを取得します.
     * @return the deinedCount
     */
    public Long getDeniedCount() {
        return deniedCount;
    }

    /**
     * deniedCountを設定します.
     * @param deniedCount the deniedCount to set
     */
    public void setDeniedCount(Long deniedCount) {
        this.deniedCount = deniedCount;
    }

    /**
     * openCountを取得します.
     * @return the openCount
     */
    public Long getOpenCount() {
        return openCount;
    }

    /**
     * openCountを設定します.
     * @param openCount the openCount to set
     */
    public void setOpenCount(Long openCount) {
        this.openCount = openCount;
    }

    /**
     * closedCountを取得します.
     * @return the closedCount
     */
    public Long getClosedCount() {
        return closedCount;
    }

    /**
     * closedCountを設定します.
     * @param closedCount the closedCount to set
     */
    public void setClosedCount(Long closedCount) {
        this.closedCount = closedCount;
    }

    /**
     * canceledCountを取得します.
     * @return the canceledCount
     */
    public Long getCanceledCount() {
        return canceledCount;
    }

    /**
     * canceledCountを設定します.
     * @param canceledCount the canceledCount to set
     */
    public void setCanceledCount(Long canceledCount) {
        this.canceledCount = canceledCount;
    }

    /**
     * attentionCountを取得します.
     * @return the attentionCount
     */
    public Long getAttentionCount() {
        return attentionCount;
    }

    /**
     * attentionCountを設定します.
     * @param attentionCount the attentionCount to set
     */
    public void setAttentionCount(Long attentionCount) {
        this.attentionCount = attentionCount;
    }

    /**
     * personInChargeCountを取得します.
     * @return the personInChargeCount
     */
    public Long getPersonInChargeCount() {
        return personInChargeCount;
    }

    /**
     * personInChargeCountを設定します.
     * @param personInChargeCount the personInChargeCount to set
     */
    public void setPersonInChargeCount(Long personInChargeCount) {
        this.personInChargeCount = personInChargeCount;
    }

    /**
     * ccCountを取得します.
     * @return the ccCount
     */
    public Long getCcCount() {
        return ccCount;
    }

    /**
     * ccCountを設定します.
     * @param ccCount the ccCount to set
     */
    public void setCcCount(Long ccCount) {
        this.ccCount = ccCount;
    }

    /**
     * newCountを取得します.
     * @return the newCount
     */
    public Long getNewCount() {
        return newCount;
    }

    /**
     * newCountを設定します.
     * @param newCount the newCount to set
     */
    public void setNewCount(Long newCount) {
        this.newCount = newCount;
    }

    /**
     * readCountを取得します.
     * @return the readCount
     */
    public Long getReadCount() {
        return readCount;
    }

    /**
     * readCountを設定します.
     * @param readCount the readCount to set
     */
    public void setReadCount(Long readCount) {
        this.readCount = readCount;
    }
}
