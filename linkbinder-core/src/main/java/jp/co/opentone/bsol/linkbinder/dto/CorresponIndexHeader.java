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

import java.util.ArrayList;
import java.util.List;

/**
 * テーブル [v_correspon] 一覧表示のヘッダの情報を持つDto.
 *
 * @author opentone
 *
 */
public class CorresponIndexHeader extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 6922668721710983512L;

    /**
     * Noの表示順.
     */
    public static final String NUM_NO = "1";

    /**
     * Correspon Noの表示順.
     */
    public static final String NUM_CORRESPON_NO = "2";

    /**
     * Previous Revisionの表示順.
     */
    public static final String NUM_PREVIOUS_REVISION = "3";

    /**
     * Fromの表示順.
     */
    public static final String NUM_FROM = "4";

    /**
     * Toの表示順.
     */
    public static final String NUM_TO = "5";

    /**
     * Typeの表示順.
     */
    public static final String NUM_TYPE = "6";

    /**
     * Subjectの表示順.
     */
    public static final String NUM_SUBJECT = "7";

    /**
     * Workflowの表示順.
     */
    public static final String NUM_WORKFLOW = "8";

    /**
     * Created Onの表示順.
     */
    public static final String NUM_CREATED_ON = "9";

    /**
     * Issued Onの表示順.
     */
    public static final String NUM_ISSUED_ON = "10";

    /**
     * Deadlineの表示順.
     */
    public static final String NUM_DEADLINE = "11";

    /**
     * Updated Onの表示順.
     */
    public static final String NUM_UPDATED_ON = "12";

    /**
     * Created Byの表示順.
     */
    public static final String NUM_CREATED_BY = "13";

    /**
     * Issued Byの表示順.
     */
    public static final String NUM_ISSUED_BY = "14";

    /**
     * Updated Byの表示順.
     */
    public static final String NUM_UPDATED_BY = "15";

    /**
     * Reply Requiredの表示順.
     */
    public static final String NUM_REPLY_REQUIRED = "16";

    /**
     * Statusの表示順.
     */
    public static final String NUM_STATUS = "17";

    /**
     * Noの表示／非表示.
     */
    private boolean no;

    /**
     * Correspondence No.の表示／非表示.
     */
    private boolean corresponNo;

    /**
     * Fromの表示／非表示.
     */
    private boolean from;

    /**
     * Subjectの表示／非表示.
     */
    private boolean subject;

    /**
     * Typeの表示／非表示.
     */
    private boolean type;

    /**
     * Workflow Statusの表示／非表示.
     */
    private boolean workflow;

    /**
     * Created onの表示／非表示.
     */
    private boolean createdOn;

    /**
     * Updated onの表示／非表示.
     */
    private boolean updatedOn;

    /**
     * Created byの表示／非表示.
     */
    private boolean createdBy;

    /**
     * Updated byの表示／非表示.
     */
    private boolean updatedBy;

    /**
     * Deadline for Replyの表示／非表示.
     */
    private boolean deadline;

    /**
     * Statusの表示／非表示.
     */
    private boolean status;

    /**
     * Previous Revisionの表示／非表示.
     */
    private boolean previousRevision;

    /**
     * Toの表示／非表示.
     */
    private boolean to;

    /**
     * Issued Onの表示／非表示.
     */
    private boolean issuedOn;

    /**
     * Issued Byの表示／非表示.
     */
    private boolean issuedBy;

    /**
     * Reply Requiredの表示／非表示.
     */
    private boolean replyRequired;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponIndexHeader() {
    }


    /**
     * noを取得します.
     * @return the no
     */
    public boolean isNo() {
        return no;
    }


    /**
     * noを設定します.
     * @param no the no to set
     */
    public void setNo(boolean no) {
        this.no = no;
    }


    /**
     * corresponNoを取得します.
     * @return the corresponNo
     */
    public boolean isCorresponNo() {
        return corresponNo;
    }


    /**
     * corresponNoを設定します.
     * @param corresponNo the corresponNo to set
     */
    public void setCorresponNo(boolean corresponNo) {
        this.corresponNo = corresponNo;
    }


    /**
     * fromを取得します.
     * @return the from
     */
    public boolean isFrom() {
        return from;
    }


    /**
     * fromを設定します.
     * @param from the from to set
     */
    public void setFrom(boolean from) {
        this.from = from;
    }


    /**
     * subjectを取得します.
     * @return the subject
     */
    public boolean isSubject() {
        return subject;
    }


    /**
     * subjectを設定します.
     * @param subject the subject to set
     */
    public void setSubject(boolean subject) {
        this.subject = subject;
    }


    /**
     * typeを取得します.
     * @return the type
     */
    public boolean isType() {
        return type;
    }


    /**
     * typeを設定します.
     * @param type the type to set
     */
    public void setType(boolean type) {
        this.type = type;
    }


    /**
     * workflowを取得します.
     * @return the workflow
     */
    public boolean isWorkflow() {
        return workflow;
    }


    /**
     * workflowを設定します.
     * @param workflow the workflow to set
     */
    public void setWorkflow(boolean workflow) {
        this.workflow = workflow;
    }


    /**
     * createdOnを取得します.
     * @return the createdOn
     */
    public boolean isCreatedOn() {
        return createdOn;
    }


    /**
     * createdOnを設定します.
     * @param createdOn the createdOn to set
     */
    public void setCreatedOn(boolean createdOn) {
        this.createdOn = createdOn;
    }


    /**
     * updatedOnを取得します.
     * @return the updatedOn
     */
    public boolean isUpdatedOn() {
        return updatedOn;
    }


    /**
     * updatedOnを設定します.
     * @param updatedOn the updatedOn to set
     */
    public void setUpdatedOn(boolean updatedOn) {
        this.updatedOn = updatedOn;
    }


    /**
     * createdByを取得します.
     * @return the createdBy
     */
    public boolean isCreatedBy() {
        return createdBy;
    }


    /**
     * createdByを設定します.
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(boolean createdBy) {
        this.createdBy = createdBy;
    }


    /**
     * updatedByを取得します.
     * @return the updatedBy
     */
    public boolean isUpdatedBy() {
        return updatedBy;
    }


    /**
     * updatedByを設定します.
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(boolean updatedBy) {
        this.updatedBy = updatedBy;
    }


    /**
     * deadlineを取得します.
     * @return the deadline
     */
    public boolean isDeadline() {
        return deadline;
    }


    /**
     * deadlineを設定します.
     * @param deadline the deadline to set
     */
    public void setDeadline(boolean deadline) {
        this.deadline = deadline;
    }


    /**
     * statusを取得します.
     * @return the status
     */
    public boolean isStatus() {
        return status;
    }


    /**
     * statusを設定します.
     * @param status the status to set
     */
    public void setStatus(boolean status) {
        this.status = status;
    }


    /**
     * previousRevisionを取得します.
     * @return the previousRevision
     */
    public boolean isPreviousRevision() {
        return previousRevision;
    }


    /**
     * previousRevisionを設定します.
     * @param previousRevision the previousRevision to set
     */
    public void setPreviousRevision(boolean previousRevision) {
        this.previousRevision = previousRevision;
    }


    /**
     * toを取得します.
     * @return the to
     */
    public boolean isTo() {
        return to;
    }


    /**
     * toを設定します.
     * @param to the to to set
     */
    public void setTo(boolean to) {
        this.to = to;
    }


    /**
     * issuedOnを取得します.
     * @return the issuedOn
     */
    public boolean isIssuedOn() {
        return issuedOn;
    }


    /**
     * issuedOnを設定します.
     * @param issuedOn the issuedOn to set
     */
    public void setIssuedOn(boolean issuedOn) {
        this.issuedOn = issuedOn;
    }


    /**
     * issuedByを取得します.
     * @return the issuedBy
     */
    public boolean isIssuedBy() {
        return issuedBy;
    }


    /**
     * issuedByを設定します.
     * @param issuedBy the issuedBy to set
     */
    public void setIssuedBy(boolean issuedBy) {
        this.issuedBy = issuedBy;
    }


    /**
     * replyRequiredを取得します.
     * @return the replyRequired
     */
    public boolean isReplyRequired() {
        return replyRequired;
    }


    /**
     * replyRequiredを設定します.
     * @param replyRequired the replyRequired to set
     */
    public void setReplyRequired(boolean replyRequired) {
        this.replyRequired = replyRequired;
    }


    /**
     * JSF用：NUM_NOを取得します.
     * @return the NUM_NO
     */
    public String getNumNo() {
        return NUM_NO;
    }


    /**
     * JSF用：NUM_CORRESPON_NOを取得します.
     * @return the NUM_CORRESPON_NO
     */
    public String getNumCorresponNo() {
        return NUM_CORRESPON_NO;
    }


    /**
     * JSF用：NUM_PREVIOUS_REVISIONを取得します.
     * @return the NUM_PREVIOUS_REVISION
     */
    public String getNumPreviousRevision() {
        return NUM_PREVIOUS_REVISION;
    }


    /**
     * JSF用：NUM_FROMを取得します.
     * @return the NUM_FROM
     */
    public String getNumFrom() {
        return NUM_FROM;
    }


    /**
     * JSF用：NUM_TOを取得します.
     * @return the NUM_TO
     */
    public String getNumTo() {
        return NUM_TO;
    }


    /**
     * JSF用：NUM_TYPEを取得します.
     * @return the NUM_TYPE
     */
    public String getNumType() {
        return NUM_TYPE;
    }


    /**
     * JSF用：NUM_SUBJECTを取得します.
     * @return the NUM_SUBJECT
     */
    public String getNumSubject() {
        return NUM_SUBJECT;
    }


    /**
     * JSF用：NUM_WORKFLOWを取得します.
     * @return the NUM_WORKFLOW
     */
    public String getNumWorkflow() {
        return NUM_WORKFLOW;
    }


    /**
     * JSF用：NUM_CREATED_ONを取得します.
     * @return the NUM_CREATED_ON
     */
    public String getNumCreatedOn() {
        return NUM_CREATED_ON;
    }


    /**
     * JSF用：NUM_ISSUED_ONを取得します.
     * @return the NUM_ISSUED_ON
     */
    public String getNumIssuedOn() {
        return NUM_ISSUED_ON;
    }


    /**
     * JSF用：NUM_DEADLINEを取得します.
     * @return the NUM_DEADLINE
     */
    public String getNumDeadline() {
        return NUM_DEADLINE;
    }


    /**
     * JSF用：NUM_UPDATED_ONを取得します.
     * @return the NUM_UPDATED_ON
     */
    public String getNumUpdatedOn() {
        return NUM_UPDATED_ON;
    }


    /**
     * JSF用：NUM_CREATED_BYを取得します.
     * @return the NUM_CREATED_BY
     */
    public String getNumCreatedBy() {
        return NUM_CREATED_BY;
    }


    /**
     * JSF用：NUM_ISSUED_BYを取得します.
     * @return the NUM_ISSUED_BY
     */
    public String getNumIssuedBy() {
        return NUM_ISSUED_BY;
    }


    /**
     * JSF用：NUM_UPDATED_BYを取得します.
     * @return the NUM_UPDATED_BY
     */
    public String getNumUpdatedBy() {
        return NUM_UPDATED_BY;
    }


    /**
     * JSF用：NUM_REPLY_REQUIREDを取得します.
     * @return the NUM_REPLY_REQUIRED
     */
    public String getNumReplyRequired() {
        return NUM_REPLY_REQUIRED;
    }


    /**
     * JSF用：NUM_STATUSを取得します.
     * @return the NUM_STATUS
     */
    public String getNumStatus() {
        return NUM_STATUS;
    }


    /**
     * 値が「true」のプロパティ名をListにして返却します.
     * @return 値が「true」のプロパティ名
     */
    public List<String> createListOnlyTrueProp() {
        List<String> list = new ArrayList<String>();

        addList(list, no, "no");
        addList(list, corresponNo, "corresponNo");
        addList(list, from, "from");
        addList(list, subject, "subject");
        addList(list, type, "type");
        addList(list, workflow, "workflow");
        addList(list, createdOn, "createdOn");
        addList(list, updatedOn, "updatedOn");
        addList(list, createdBy, "createdBy");
        addList(list, updatedBy, "updatedBy");
        addList(list, deadline, "deadline");
        addList(list, status, "status");
        addList(list, previousRevision, "previousRevCorresponNo");
        addList(list, to, "to");
        addList(list, issuedBy, "issuedBy");
        addList(list, issuedOn, "issuedOn");
        addList(list, replyRequired, "replyRequired");

        return list;
    }

    /**
     * プロパティ名をListに格納します.
     * @param list リスト
     * @param check 格納判定
     * @param key プロパティ名
     */
    private void addList(List<String> list, boolean check, String key) {
        if (check) {
            list.add(key);
        }
    }
}
