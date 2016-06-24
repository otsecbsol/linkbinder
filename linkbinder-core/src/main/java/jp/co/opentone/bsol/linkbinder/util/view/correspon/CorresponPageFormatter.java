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
package jp.co.opentone.bsol.linkbinder.util.view.correspon;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;

/**
 * コレポン文書を表示する時の共通的な整形処理.
 * @author opentone
 */
public class CorresponPageFormatter implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8128757423005500116L;

    /** logger. */
    private static final Logger log = LoggerFactory.getLogger(CorresponPageFormatter.class);

    /**
     * 返信文書のSubjectの形式.
     */
    public static final String FORMAT_REPLY_SUBJECT =
            SystemConfig.getValue(Constants.KEY_REPLY_SUBJECT_FORMAT);
    /**
     * 返信文書のBodyの形式.
     */
    public static final String FORMAT_REPLY_BODY =
            SystemConfig.getValue(Constants.KEY_REPLY_BODY_FORMAT);

    /**
     * 返信文書のBodyヘッダーの形式.
     */
    public static final String FORMAT_REPLY_BODY_HEADER =
            SystemConfig.getValue(Constants.KEY_REPLY_BODY_HEADER_FORMAT);

    /**
     * 転送文書のSubjectの形式.
     */
    public static final String FORMAT_FORWARD_SUBJECT =
            SystemConfig.getValue(Constants.KEY_FORWARD_SUBJECT_FORMAT);
    /**
     * 転送文書のBodyの形式.
     */
    public static final String FORMAT_FORWARD_BODY =
            SystemConfig.getValue(Constants.KEY_FORWARD_BODY_FORMAT);

    /**
     * 転送文書のBodyヘッダーの形式.
     */
    public static final String FORMAT_FORWARD_BODY_HEADER =
            SystemConfig.getValue(Constants.KEY_FORWARD_BODY_HEADER_FORMAT);

    /**
     * コレポン文書Noが採番されていないことを表すラベル.
     */
    public static final String DEFAULT_CORRESPON_NO = "(発行後に採番されます)";

    /**
     * コレポン文書.
     */
    private Correspon correspon;

    /**
     * 整形対象のコレポン文書を指定してインスタンス化する.
     * @param correspon 整形対象のコレポン文書
     */
    public CorresponPageFormatter(Correspon correspon) {
        this.correspon = correspon;
    }

    /**
     * 空のインスタンスを生成.
     */
    public CorresponPageFormatter() {
    }

    /**
     * 整形対象のコレポン文書を設定する.
     * @param correspon 整形対象のコレポン文書
     */
    public void setCorrespon(Correspon correspon) {
        this.correspon = correspon;
    }

    /**
     * 表示用のコレポン文書番号を返す.
     *
     * @return コレポン文書番号
     */
    public String getCorresponNo() {
        if (correspon != null && StringUtils.isNotEmpty(correspon.getCorresponNo())) {
            return correspon.getCorresponNo();
        }
        return DEFAULT_CORRESPON_NO;
    }

    /**
     * 返信・転送文書の判別.
     * @return 件名
     */
    private String getFormatedSubject(String format) {
        return String.format(format, correspon.getSubject());
    }

    /**
     * 返信文書となるコレポン文書の件名を返す.
     * @return 件名
     */
    public String getReplySubject() {
        return getFormatedSubject(FORMAT_REPLY_SUBJECT);
    }

    /**
     * 返信文書となるコレポン文書の本文を返す.
     * @return 本文
     */
    public String getReplyBody() {
        return getFormatedBody(FORMAT_REPLY_BODY, FORMAT_REPLY_BODY_HEADER);
    }

    /**
     * 転送文書となるコレポン文書の件名を返す.
     * @return 件名
     */
    public String getForwardSubject() {
        return getFormatedSubject(FORMAT_FORWARD_SUBJECT);
    }

    /**
     * 転送文書となるコレポン文書の本文を返す.
     * @return 本文
     */
    public String getForwardBody() {
        return getFormatedBody(FORMAT_FORWARD_BODY, FORMAT_FORWARD_BODY_HEADER);
    }

    private String createFormatedHeader(Correspon request, String format) {
        User sender = getSender(request);

        String issuedAt = "";
        if (request.getIssuedAt() != null) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            issuedAt = f.format(request.getIssuedAt());
        }

        return String.format(format,
                                issuedAt,
                                sender.getNameE(),
                                sender.getEmpNo());
    }

    private String getFormatedBody(String format, String header) {
        if (log.isDebugEnabled()) {
            log.debug(correspon.getBody());
        }
        String result =
            String.format(format, createFormatedHeader(correspon, header), correspon.getBody());

        if (log.isDebugEnabled()) {
            log.debug(result);
        }
        return result;
    }

    private User getSender(Correspon request) {
        List<Workflow> workflows = request.getWorkflows();
        if (workflows == null || workflows.isEmpty()) {
            return request.getCreatedBy();
        }
        User approver = null;
        for (Workflow w : workflows) {
            if (w.getWorkflowType() == WorkflowType.APPROVER) {
                approver = w.getUser();
            }
        }
        return approver;
    }

    /**
     * 絶対パスで指定したプロジェクトロゴのURLを取得する.
     * @param projectId プロジェクトID
     * @return プロジェクトロゴのURL
     */
    public String getProjectLogoUrl(String projectId) {
        return SystemConfig.getValue(Constants.KEY_PROJECT_LOGO_URL) + "?projectId=" + projectId;
//        return getBaseURL()
//            + SystemConfig.getValue(Constants.KEY_PROJECT_LOGO_URL) + "?projectId=" + projectId;
    }

//    /**
//     * サーバーのURLを取得する.
//     * @return サーバーのURL
//     */
//    private String getBaseURL() {
//        ProcessContext container = ProcessContext.getCurrentContext();
//
//        Map<String, Object> values = container.getValue(SystemConfig.KEY_ACTION_VALUES);
//        return (String) values.get(Constants.KEY_BASE_URL);
//    }
}
