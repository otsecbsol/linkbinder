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

package jp.co.opentone.bsol.linkbinder.message;

import jp.co.opentone.bsol.framework.core.message.MessageCode;

/**
 * 当アプリケーションのメッセージ定数.
 * @author opentone
 */
public class ApplicationMessageCode extends MessageCode {

    /**
     * 空のインスタンスを生成する.
     * <p>
     * 外部からのインスタンス化はできない.
     * </p>
     */
    private ApplicationMessageCode() {
    }


    /**
     * ID: I001.
     */
    public static final String SAVE_SUCCESSFUL = "I001";

    /**
     * ID: I002.
     */
    public static final String CONTENT_WILL_BE_SAVED = "I002";

    /**
     * ID: I003.
     */
    public static final String CORRESPON_SAVED = "I003";

    /**
     * ID: I004.
     */
    public static final String COMPANY_SAVED = "I004";

    /**
     * ID: I005.
     */
    public static final String DISCIPLINE_SAVED = "I005";

    /**
     * ID: I006.
     */
    public static final String SITE_SAVED = "I006";

    /**
     * ID: I007.
     */
    public static final String CORRESPON_TYPE_SAVED = "I007";

    /**
     * ID: I008.
     */
    public static final String CUSTOM_FIELD_SAVED = "I008";

    /**
     * ID: I009.
     */
    public static final String COMPANY_ASSIGNED = "I009";

    /**
     * ID: I010.
     */
    public static final String DISCIPLINE_ADDED = "I010";

    /**
     * ID: I011.
     */
    public static final String CORRESPON_TYPE_ASSIGNED = "I011";

    /**
     * ID: I012.
     */
    public static final String CUSTOM_FIELD_ASSIGNED = "I012";

    /**
     * ID: I013.
     */
    public static final String CORRESPON_CHECKED = "I013";

    /**
     * ID: I014.
     */
    public static final String CORRESPON_APPROVED = "I014";

    /**
     * ID: I015.
     */
    public static final String CORRESPON_DENIED = "I015";

    /**
     * ID: I016.
     */
    public static final String COMPANY_DELETED = "I016";

    /**
     * ID: I017.
     */
    public static final String DISCIPLINE_DELETED = "I017";

    /**
     * ID: I018.
     */
    public static final String SITE_DELETED = "I018";

    /**
     * ID: I019.
     */
    public static final String CORRESPON_GROUP_DELETED = "I019";

    /**
     * ID: I020.
     */
    public static final String CORRESPON_TYPE_DELETED = "I020";

    /**
     * ID: I021.
     */
    public static final String CUSTOM_FIELD_DELETED = "I021";

    /**
     * ID: I022.
     */
    public static final String CORRESPON_DELETED = "I022";

    /**
     * ID: I023.
     */
    public static final String CORRESPONS_DELETED = "I023";

    /**
     * ID: I024.
     */
    public static final String CORRESPON_REQUESTED = "I024";

    /**
     * ID: I025.
     */
    public static final String CORRESPON_ISSUED = "I025";

    /**
     * ID: I026.
     */
    public static final String WORKFLOW_TEMPLATE_DELETED = "I026";

    /**
     * ID: I027.
     */
    public static final String PROJECT_CUSTOM_SETTING_SAVED = "I027";

    /**
     * ID: I028.
     */
    public static final String FAVORITE_FILTER_SAVED = "I028";

    /**
     * ID: I029.
     */
    public static final String FAVORITE_FILTER_DELETED = "I029";

    /**
     * ID: I030.
     */
    public static final String FINISHED = "I030";

    /**
     * ID: I101：Distributionテンプレート更新成功.
     */
    public static final String DISTRIBUTION_TEMPLATE_SAVED = "I101";

    /**
     * ID: I101：Distributionテンプレート削除成功.
     */
    public static final String DISTRIBUTION_TEMPLATE_DELETED = "I102";

    /**
     * ID: I103：ファイルインポート処理成功.
     */
    public static final String IMPORT_FILE_READ = "I103";

    /**
     * ID: I103：ファイルインポート処理成功(ファイルサイズ超).
     */
    public static final String IMPORT_FILE_READ_TOO_MANY_FILE = "I104";

    /**
     * ID: IJG001.
     */
    public static final String CONFIRM_AND_START_APPROVAL_PROCESS = "IJG001";

    /**
     * ID: IJG002.
     */
    public static final String CONFIRM_AND_START_TO_UPDATE_IF_NECESSARY = "IJG002";

    /**
     * ID: E001.
     */
    public static final String NO_DATA_FOUND = "E001";

    /**
     * ID: E002.
     */
    public static final String CANNOT_PERFORM_BECAUSE_PROJECT_DIFF = "E002";

    /**
     * ID: E003.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED = "E003";

    /**
     * ID: E004.
     */
    public static final String ERROR_UPLOADING_FILE = "E004";

    /**
     * ID: E005.
     */
    public static final String NO_PAGE_FOUND = "E005";

    /**
     * ID: E006.
     */
    public static final String INVALID_ORIGINAL_CORRESPON = "E006";

    /**
     * ID: E007.
     */
    public static final String CANNOT_PERFORM_BECAUSE_ORIGINAL_CORRESPON_NOT_EXIST = "E007";

    /**
     * ID: E008.
     */
    public static final String CANNOT_PERFORM_BECAUSE_GROUP_NOT_EXIST = "E008";

    /**
     * ID: E009.
     */
    public static final String INVALID_GROUP = "E009";

    /**
     * ID: E010.
     */
    public static final String CANNOT_PERFORM_BECAUSE_USER_NOT_EXIST = "E010";

    /**
     * ID: E011.
     */
    public static final String INVALID_USER = "E011";

    /**
     * ID: E012.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_NOT_EXIST = "E012";

    /**
     * ID: E013.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_NOT_EXIST = "E013";

    /**
     * ID: E014.
     */
    public static final String INVALID_CORRESPON_TYPE = "E014";

    /**
     * ID: E015.
     */
    public static final String FILE_ALREADY_ATTACHED = "E015";

    /**
     * ID: E016.
     */
    public static final String CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT = "E016";

    /**
     * ID: E017.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID = "E017";

    /**
     * ID: E018.
     */
    public static final String CANNOT_PERFORM_BECAUSE_WORKFLOW_SEQUENCE_INVALID = "E018";

    /**
     * ID: E019.
     */
    public static final String CANNOT_PERFORM_BECAUSE_WORKFLOW_PATTERN_INVALID = "E019";

    /**
     * ID: E020.
     */
    public static final String EXCEED_MAXIMUM_NUMBER_OF_CHECKERS = "E020";

    /**
     * ID: E021.
     */
    public static final String EXCEED_MAXIMUM_NUMBER_OF_APPROVERS = "E021";

    /**
     * ID: E022.
     */
    public static final String NO_APPROVER_SPECIFIED = "E022";

    /**
     * ID: E023.
     */
    public static final String CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID = "E023";

    /**
     * ID: E024.
     */
    public static final String CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER = "E024";

    /**
     * ID: E025.
     */
    public static final String CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER = "E025";

    /**
     * ID: E026.
     */
    public static final String CANNOT_PERFORM_BECAUSE_ORIGINAL_WORKFLOW_STATUS_INVALID = "E026";

    /**
     * ID: E027.
     */
    public static final String DUPLICATED_CHECKER = "E027";

    /**
     * ID: E028.
     */
    public static final String CANNOT_PERFORM_BECAUSE_DISCIPLINE_ALREADY_RELATED_WITH_SITE = "E028";

    /**
     * ID: E029.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_ALREADY_RELATED_WITH_CORRESPON = "E029";

    /**
     * ID: E030.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_GROUP_ALREADY_RELATED_WITH_USER = "E030";

    /**
     * ID: E031.
     */
    public static final String CANNOT_PERFORM_BECAUSE_SITE_ALREADY_RELATED_WITH_DISCIPLINE = "E031";

    /**
     * ID: E032.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_ALREADY_ASSIGNED_TO_PROJECT = "E032";

    /**
     * ID: E033.
     */
    public static final String ERROR_SAVING_FILE = "E033";

    /**
     * ID: E034.
     */
    public static final String ERROR_DELETING_FILE = "E034";

    /**
     * ID: E035.
     */
    public static final String ERROR_GETTING_FILE = "E035";

    /**
     * ID: E036.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_NOT_SELECTED = "E036";

    /**
     * ID: E037.
     */
    public static final String RETURNED_MORE_THAN_RECORDS = "E037";

    /**
     * ID: E038.
     */
    public static final String DUPLICATED_CHECKER_APPROVER = "E038";

    /**
     * ID: E039.
     */
    public static final String PASSWORD_EXPIRED = "E039";

    /**
     * ID: E040.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_GROUP_NOT_RELATED_WITH_USER = "E040";

    /**
     * ID: E041.
     */
    public static final String CANNOT_PERFORM_BECAUSE_ROLE_INSUFFICIENT = "E041";

    /**
     * ID: E042.
     */
    public static final String CANNOT_PERFORM_BECAUSE_PROJECT_NOT_RELATED_WITH_USER = "E042";

    /**
     * ID: E043.
     */
    public static final String LOGIN_FAILED = "E043";

    /**
     * ID: E044.
     */
    public static final String CANNOT_PERFORM_BECAUSE_SITE_ALREADY_UPDATED = "E044";

    /**
     * ID: E045.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_GROUP_ALREADY_UPDATED = "E045";

    /**
     * ID: E046.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_CODE_ALREADY_EXISTS = "E046";

    /**
     * ID: E047.
     */
    public static final String CANNOT_PERFORM_BECAUSE_ISSUE_PURPOSE_ALREDAY_EXISTS = "E047";

    /**
     * ID: E048.
     */
    public static final String CANNOT_PERFORM_BECAUSE_COMPANY_CODE_ALREADY_EXISTS = "E048";

    /**
     * ID: E049.
     */
    public static final String CANNOT_PERFORM_BECAUSE_DISCIPLINE_CODE_ALREADY_EXISTS = "E049";

    /**
     * ID: E050.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_LABEL_ALREADY_EXISTS = "E050";

    /**
     * ID: E051.
     */
    public static final String CANNOT_PERFORM_BECAUSE_SITE_CODE_ALREADY_EXISTS = "E051";

    /**
     * ID: E052.
     */
    public static final String CANNOT_PERFORM_BECAUSE_SITE_DISCIPLINE_ALREADY_EXISTS = "E052";

    /**
     * ID: E053.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_ALREADY_ASSIGNED_TO_CORRESPON = "E053";

    /**
     * ID: E054.
     */
    public static final String INVALID_CUSTOM_FIELD = "E054";

    /**
     * ID: E055.
     */
    public static final String CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_RELATED_WITH_USER = "E055";

    /**
     * ID: E056.
     */
    public static final String CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_ASSIGNED_TO_PROJECT = "E056";

    /**
     * ID: E057.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_ALREADY_ASSIGNED_TO_PROJECT = "E057";

    /**
     * ID: E058.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_GROUP_ALREADY_ASSIGNED_TO_CORRESPON = "E058";

    /**
     * ID: E059.
     */
    public static final String CANNOT_PERFORM_BECAUSE_USER_SETTINGS_ALREADY_UPDATED = "E059";

    /**
     * ID: E060.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_ALREADY_UPDATED = "E060";

    /**
     * ID: E061.
     */
    public static final String CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_UPDATED = "E061";

    /**
     * ID: E062.
     */
    public static final String CANNOT_PERFORM_BECAUSE_DISCIPLINE_ALREADY_UPDATED = "E062";

    /**
     * ID: E063.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_ALREADY_UPDATED = "E063";

    /**
     * ID: E064.
     */
    public static final String ERROR_OCCURRED = "E064";

    /**
     * ID: E065.
     */
    public static final String CANNOT_PERFORM_BECAUSE_ORIGINAL_CORRESPON_FOR_REPLY_NOT_EXIST = "E065";

    /**
     * ID: E066.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORREPON_ALREADY_ISSUED = "E066";

    /**
     * ID: E067.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORREPON_CANCELED = "E067";

    /**
     * ID: E068.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORREPON_NOT_CANCELED = "E068";

    /**
     * ID: E069.
     */
    public static final String CANNOT_PERFORM_BECAUSE_INVALID_PERSON_IN_CHARGE = "E069";

    /**
     * ID: E070.
     */
    public static final String CANNOT_PERFORM_BECAUSE_PERSON_IN_CHARGE_NOT_EXIST = "E070";

    /**
     * ID: E071.
     */
    public static final String CANNOT_PERFORM_BECAUSE_PERSON_IN_CHARGE_ALREDY_CREATED_REPLY_CORRESPON = "E071";

    /**
     * ID: E072.
     */
    public static final String CANNOT_PERFORM_BECAUSE_TEMPLATE_NOT_EXIST = "E072";

    /**
     * ID: E073.
     */
    public static final String CANNOT_PERFORM_BECAUSE_TEMPLATE_NAME_ALREADY_EXISTS = "E073";

    /**
     * ID: E074.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORRESPON_NOT_OPENED = "E074";

    /**
     * ID: E075.
     */
    public static final String CANNOT_PERFORM_BECAUSE_ATTENTION_NOT_EXIST = "E075";

    /**
     * ID: E076.
     */
    public static final String INVALID_ATTENTION = "E076";

    /**
     * ID: E077.
     */
    public static final String CANNOT_PERFORM_BECAUSE_CORREPON_NOT_ISSUED_AND_CANCELED = "E077";

    /**
     * ID: E078.
     */
    public static final String CANNOT_PERFORM_BECAUSE_ORIGINAL_CORRESPON_FOR_REVISION_NOT_EXIST = "E078";

    /**
     * ID: E079.
     */
    public static final String CANNOT_PERFORM_BECAUSE_INDEX_BEING_UPDATED = "E079";

    /**
     * ID: E080.
     */
    public static final String DUPLICATED_CUSTOM_FIELD_VALUES = "E080";

    /**
     * ID: E081.
     */
    public static final String DUPLICATED_TO_GROUP = "E081";

    /**
     * ID: E082.
     */
    public static final String DUPLICATED_CC_GROUP = "E082";

    /**
     * ID: E083.
     */
    public static final String CANNOT_PERFORM_BECAUSE_INVISIBLE_CORRESPON = "E083";

    /**
     * ID: E084.
     */
    public static final String CANNOT_PERFORM_BECAUSE_USER_ALREADY_DELETED = "E084";

    /**
     * ID: E085.
     */
    public static final String CANNOT_PERFORM_BECAUSE_NOT_ALLOW_CHANGE_CHECKER = "E085";

    /**
     * ID: E086.
     */
    public static final String SESSION_HAS_TIMED_OUT = "E086";

    /**
     * ID: E087.
     */
    public static final String CANNOT_PERFORM_BECAUSE_PROJECT_CUSTOM_SETTING_NOT_EXIST = "E087";

    /**
     * ID: E088.
     */
    public static final String CANNOT_PERFORM_BECAUSE_FAVORITE_FILTER_ALREADY_UPDATED = "E088";

    /**
     * ID: E089.
     */
    public static final String MALFORMED_EMAIL_ADDRESS = "E089";

    /**
     * ID:E090.
     */
    public static final String ERROR_SAVING_EMAIL_ADDRESS = "E090";

    /**
     * ID:E092.
     */
    public static final String ERROR_GETTING_EMAIL_ADDRESS = "E092";

    /**
     * Distibution Template処理メッセージコード：レコード無し.
     */
    public static final String MSG_FAIL_FIND = "E101";

    /**
     * Distibution Template処理メッセージコード：更新失敗.
     */
    public static final String MSG_FAIL_UPDATE = "E102";

    /**
     * Distibution Template処理メッセージコード：削除失敗.
     */
    public static final String MSG_FAIL_DELETE = "E103";

    /**
     * Distibution Template処理メッセージコード：宛先テンプレートヘッダー設定なし.
     */
    public static final String MSG_NOT_IN_DIST_TEMPLATE_HEADER = "E104";

    /**
     * Distibution Template処理メッセージコード：活動単位設定なし.
     */
    public static final String MSG_NOT_IN_GROUP = "E105";

    /**
     * Distibution Template処理メッセージコード：ユーザーの設定なし.
     */
    public static final String MSG_NOT_IN_USER = "E106";

    /**
     * Distibution Template処理メッセージコード：プロジェクトの設定なし.
     */
    public static final String MSG_NOT_IN_PROJECT = "E107";

    /**
     * Distibution Template処理メッセージコード：対象プロジェクトのコレポングループ設定なし.
     */
    public static final String MSG_NOT_IN_CORRESPON_GROUPS = "E108";

    /**
     * Distibution Template処理メッセージコード：重複したグループID.
     */
    public static final String MSG_GROUP_DUPLICATE = "E109";

    /**
     * Distibution Template処理メッセージコード：重複したユーザーID.
     */
    public static final String MSG_USER_DUPLICATE = "E110";

    /**
     * Distibution Template処理メッセージコード：グループ選択無し.
     */
    public static final String MSG_GROUP_NOT_SPECIFIED = "E111";


    /**
     * Distibution Template処理メッセージコード：予期不能なエラー.
     */
    public static final String MSG_FAIL_UNKNOWN = "E120";

    /**
     * ID: インポートファイルのアップロードエラー.
     */
    public static final String ERROR_UPLOADING_IMPORT_FILE = "E121";

    /**
     * ID: インポートファイルのフォーマットエラー.
     */
    public static final String ERROR_PARSING_IMPORT_FILE = "E122";

    /**
     * ID: インポートファイルの指定無し.
     */
    public static final String ERROR_IMPORT_FILE_NONE = "E123";

    /**
     * ID: プロジェクトが存在しない.
     */
    public static final String ERROR_PROJECT_NOT_FOUND = "E124";

    /**
     * ID: プロジェクト削除に失敗.
     */
    public static final String ERROR_PROJECT_FAILED_TO_DELETE = "E125";

    /**
     * ID: プロジェクト重複.
     */
    public static final String ERROR_PROJECT_DUPLICATED = "E126";
    /**
     * ID: プロジェクト更新に失敗.
     */
    public static final String ERROR_PROJECT_FAILED_TO_UPDATE = "E127";
    /**
     * ID: ユーザーが存在しない.
     */
    public static final String ERROR_USER_NOT_FOUND = "E128";

    /**
     * ID: ユーザー削除に失敗.
     */
    public static final String ERROR_USER_FAILED_TO_DELETE = "E129";

    /**
     * ID: ユーザー重複.
     */
    public static final String ERROR_USER_DUPLICATED = "E130";

    /**
     * ID: ユーザー更新に失敗.
     */
    public static final String ERROR_USER_FAILED_TO_UPDATE = "E131";

    /**
     * ID: ユーザーの取得に失敗.
     */
    public static final String ERROR_GET_USER = "E132";
    /**
     * ID: ユーザーの登録に失敗.
     */
    public static final String ERROR_INSERT_USER = "E133";
    /**
     * ID: ユーザー情報の更新に失敗.
     */
    public static final String ERROR_UPDATE_USER = "E134";
    /**
     * ID: ユーザー情報の削除に失敗.
     */
    public static final String ERROR_DELETE_USER = "E135";
    /**
     * ID: ユーザーが存在しない.
     */
    public static final String ERROR_USER_NOT_EXIST = "E136";
    /**
     * ID: パスワードの暗号化に失敗.
     */
    public static final String ERROR_HASH_PASSWORD = "E137";
    /**
     * ID: プロジェクトの取得に失敗.
     */
    public static final String ERROR_GET_PROJECT = "E138";
    /**
     * ID: プロジェクトの登録に失敗.
     */
    public static final String ERROR_INSERT_PROJECT = "E139";
    /**
     * ID: プロジェクト情報の更新に失敗.
     */
    public static final String ERROR_UPDATE_PROJECT = "E140";
    /**
     * ID: プロジェクト情報の削除に失敗.
     */
    public static final String ERROR_DELETE_PROJECT = "E141";
    /**
     * ID: プロジェクトが存在しない.
     */
    public static final String ERROR_PROJECT_NOT_EXIST = "E142";
    /**
    * ID: プロジェクトIDが重複している.
    */
   public static final String ERROR_PROJECT_DUPLICATE = "E143";
   /**
    * ID: プロジェクトIDが重複している.
    */
   public static final String CANNOT_PERFORM_BECAUSE_CUSTOM_PROJECT_RELATED_WITH_DOCUMENT = "E144";
   /**
    * ID: 入力パスワード不一致.
    */
   public static final String PASSWORD_UNMATCH = "E145";
   /**
    * ID: メールアドレス重複.
    */
   public static final String MAILADDRESS_DUPLICATED = "E146";
   /**
    * ID: メールアドレス重複(一括更新時).
    */
   public static final String MAILADDRESS_DUPLICATED_BULK_UPDATE = "E147";
    /**
     * ID: 列数不正.
     */
    public static final String ERROR_INVALID_COLUMN_COUNT = "E148";


    /**
     * ID: EJG006.
     */
    public static final String CANNOT_START_BECAUSE_NO_AUTHORITY_TO_USE = "EJG006";

    /**
     * ID: EJG009.
     */
    public static final String CANNOT_START_BECAUSE_OF_ANY_REASON = "EJG009";

}
