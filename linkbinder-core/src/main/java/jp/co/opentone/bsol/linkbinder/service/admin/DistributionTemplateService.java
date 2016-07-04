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
package jp.co.opentone.bsol.linkbinder.service.admin;

import java.util.List;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeader;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderDelete;
import jp.co.opentone.bsol.linkbinder.dto.DistributionInfo;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;

/**
 * このサービスではDistributionテンプレートに関する処理を提供する.
 * @author opentone
 */
public interface DistributionTemplateService extends IService {

    /**
     * メッセージコード：更新成功.
     */
    String MSG_SUCCESS_SAVED = ApplicationMessageCode.DISTRIBUTION_TEMPLATE_SAVED;

    /**
     * メッセージコード：該当データ無し.
     */
    String NO_DATA_FOUND = ApplicationMessageCode.NO_DATA_FOUND;

    /**
     * メッセージコード：レコード無し.
     */
    String MSG_FAIL_FIND = ApplicationMessageCode.MSG_FAIL_FIND;

    /**
     * メッセージコード：更新失敗.
     */
    String MSG_FAIL_UPDATE = ApplicationMessageCode.MSG_FAIL_UPDATE;

    /**
     * メッセージコード：削除失敗.
     */
    String MSG_FAIL_DELETE = ApplicationMessageCode.MSG_FAIL_DELETE;

    /**
     * メッセージコード：宛先テンプレートヘッダー設定なし.
     */
    String MSG_NOT_IN_DIST_TEMPLATE_HEADER = ApplicationMessageCode.MSG_NOT_IN_DIST_TEMPLATE_HEADER;

    /**
     * メッセージコード：活動単位設定なし.
     */
    String MSG_NOT_IN_GROUP = ApplicationMessageCode.MSG_NOT_IN_GROUP;

    /**
     * メッセージコード：ユーザーの設定なし.
     */
    String MSG_NOT_IN_USER = ApplicationMessageCode.MSG_NOT_IN_USER;

    /**
     * メッセージコード：プロジェクトの設定なし.
     */
    String MSG_NOT_IN_PROJECT = ApplicationMessageCode.MSG_NOT_IN_PROJECT;

    /**
     * メッセージコード：対象プロジェクトのコレポングループ設定なし.
     */
    String MSG_NOT_IN_CORRESPON_GROUPS = ApplicationMessageCode.MSG_NOT_IN_CORRESPON_GROUPS;

    /**
     * メッセージコード：重複したグループID.
     */
    String MSG_GROUP_DUPLICATE = ApplicationMessageCode.MSG_GROUP_DUPLICATE;

    /**
     * メッセージコード：重複したユーザーID.
     */
    String MSG_USER_DUPLICATE = ApplicationMessageCode.MSG_USER_DUPLICATE;

    /**
     * メッセージコード：予期不能なエラー.
     */
    String MSG_FAIL_UNKNOWN = ApplicationMessageCode.MSG_FAIL_UNKNOWN;

    /**
     * Distributionテンプレートヘッダーに登録するEMP No.
     * <p>
     * 初期仕様では使用しないため"0000"固定.
     * やがて、ユーザーごとのテンプレートが必要になった場合に使用する.
     * </p>
     */
    String DIST_TEMP_HEADER_EMP_NO = "0000";

    /**
     * 指定された配布先テンプレートIDに該当する配布先テンプレート情報を取得する.
     * @param id 配布先テンプレートID
     * @return 配布先テンプレート情報
     * @throws ServiceAbortException 取得失敗
     */
    DistTemplateHeader find(Long id) throws ServiceAbortException;

    /**
     * 指定されたプロジェクトIDに該当する配布先テンプレートの一覧を取得する.
     * @param projectId プロジェクトID
     * @return projectIdに該当する配布先テンプレートのリスト
     * @throws ServiceAbortException 取得失敗
     */
    List<DistTemplateHeader> findDistTemplateList(String projectId)
        throws ServiceAbortException;

    /**
     * Distributionテンプレート情報を保存する.
     * @param distTemplateHeader 保存情報
     * @param empNo 更新ユーザー従業員番号
     * @return 保存後のDistributionテンプレート情報
     * @throws ServiceAbortException 処理失敗
     */
    DistTemplateHeader save(DistTemplateHeader distTemplateHeader, String empNo)
        throws ServiceAbortException;

    /**
     * 指定された配布先テンプレートを論理削除する.
     * @param distTemplateHeader 配布先テンプレート
     * @param empNo 更新ユーザー従業員番号
     * @throws ServiceAbortException 論理削除失敗
     */
    void delete(DistTemplateHeaderDelete distTemplateHeader, String empNo)
        throws ServiceAbortException;

    /**
     * プロジェクトに設定されている活動単位情報を取得する.
     * @param projectId プロジェクトID.
     * @return DistributionInfo
     * @throws ServiceAbortException 処理エラー.
     */
    DistributionInfo findDistributionInfo(String projectId)
        throws ServiceAbortException;
}
