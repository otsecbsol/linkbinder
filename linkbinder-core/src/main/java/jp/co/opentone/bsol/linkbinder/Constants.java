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
package jp.co.opentone.bsol.linkbinder;

/**
 * 当システムで横断的に利用する定数.
 * @author opentone
 */
public class Constants {

    /**
     * 定数プレフィックス.
     */
    private static final String PREFIX = Constants.class.getName() + "_";

    /**
     * sessionに格納されたユーザー情報.
     * <p>
     * Springが管理するbean名なので定数プレフィックスは付けない.
     * </p>
     */
    public static final String KEY_CURRENT_USER = "currentUser";

    /**
     * 新しいsessionを開始することを通知するための値.
     */
    public static final String KEY_START_NEW_SESSION = PREFIX + "START_NEW_SESSION";

    /**
     * 実行コンテキストのプロジェクト.
     */
    public static final String KEY_PROJECT = PREFIX + "CURRENT_PROJECT";

    /**
     * 実行コンテキストのプロジェクト-ユーザー.
     */
    public static final String KEY_PROJECT_USER = PREFIX + "CURRENT_PROJECT_USER";

    /**
     * コレポン文書の検索条件.
     */
    public static final String KEY_SEARCH_CORRESPON_CONDITION = PREFIX + "SEARCH_CORRESPON_CONDITION";

    /**
     * 全文検索用検索条件.
     */
    public static final String KEY_SEARCH_FULL_TEXT_SEARCH_CORRESPON_CONDTION =
                PREFIX + "SEARCH_FULL_TEXT_SEARCH＿CORRESPON_CONDITION";

    /**
     * サーバーのベースURL.
     */
    public static final String KEY_BASE_URL = PREFIX + "KEY_BASE_URL";
    /**
     * アプリケーションコンテキストURL.
     */
    public static final String KEY_CONTEXT_URL = PREFIX + "KEY_CONTEXT_URL";

    /**
     * 他システム利用可能状態格納マップ.
     */
    public static final String KEY_SYSTEM_AVAILABLE_STATE_MAP = PREFIX + "KEY_SYSTEM_AVAILABLE_STATE_MAP";

    /**
     * クライアントに出力するCSVデータの文字エンコーディング.
     */
    public static final String KEY_CSV_ENCODING = "csv.encoding";
    /**
     * クライアントに出力するHTMLデータの文字エンコーディング.
     */
    public static final String KEY_HTML_ENCODING = "html.encoding";

    /**
     * 返信文書件名の形式を取得するキー.
     */
    public static final String KEY_REPLY_SUBJECT_FORMAT = "reply.subject.format";
    /**
     * 返信文書本文の形式を取得するキー.
     */
    public static final String KEY_REPLY_BODY_FORMAT = "reply.body.format";
    /**
     * 返信文書本文ヘッダの形式を取得するキー.
     */
    public static final String KEY_REPLY_BODY_HEADER_FORMAT = "reply.body.header.format";

    /**
     * 転送文書件名の形式を取得するキー.
     */
    public static final String KEY_FORWARD_SUBJECT_FORMAT = "forward.subject.format";
    /**
     * 転送文書本文の形式を取得するキー.
     */
    public static final String KEY_FORWARD_BODY_FORMAT = "forward.body.format";
    /**
     * 転送文書本文ヘッダの形式を取得するキー.
     */
    public static final String KEY_FORWARD_BODY_HEADER_FORMAT = "forward.body.header.format";

    /**
     * セキュリティレベル設定値取得キー: Group Admin.
     */
    public static final String KEY_SECURITY_LEVEL_GROUP_ADMIN =
            "securityLevel.groupAdmin";
    /**
     * セキュリティレベル設定値取得キー: Normal user.
     */
    public static final String KEY_SECURITY_LEVEL_NORMAL_USER = "securityLevel.normalUser";

    /**
     * セキュリティフラグ取得キー：System Admin.
     */
    public static final String KEY_SECURITY_FLG_SYSTEM_ADMIN  = "securityflg.systemadmin";

    /**
     * セキュリティフラグ取得キー：Project Admin.
     */
    public static final String KEY_SECURITY_FLG_PROJECT_ADMIN = "securityflg.projectadmin";

    /**
     * パスワード変更アプリケーションURL.
     */
    public static final String KEY_PASSWORD_MANAGEMENT_URL = "password.management.url";

    /**
     * ファイル一時保存パスキー情報.
     */
    public static final String KEY_FILE_DIR_PATH = "dir.upload.temp";

    /**
     * リッチテキスト条件MAX文字数キー情報.
     */
    public static final String KEY_RICH_TEXT_MAX = "rich.text.max.byte";

    /**
     * プロジェクトロゴURL.
     */
    public static final String KEY_PROJECT_LOGO_URL = "project.logo.url";
    /**
     * プロジェクトロゴ格納ディレクトリ.
     */
    public static final String KEY_PROJECT_LOGO_DIR = "project.logo.dir";

    /**
     * デフォルトプロジェクトロゴファイル.
     */
    public static final String KEY_PROJECT_LOGO_DEFAULT = "project.logo.default";

    /**
     * プロジェクトロゴファイル拡張子.
     */
    public static final String KEY_PROJECT_LOGO_EXTENSION = "project.logo.extension";

    /**
     * カスタムフィールド最大件数キー.
     */
    public static final String KEY_CUSTOM_FIELD_MAX_COUNT = "custom.field.max.count";

    /**
     * 既読・未読登録モード.
     */
    public static final String READ_STATUS_MODE = "UPDATE_READ_STATUS";

    /**
     * 検索結果件数の上限値(corresponSearch.xhtml).
     */
    public static final String SEARCH_RESULT_MAX = "corresponsearch.maxresultnumber";

    /**
     * 検索結果件数の上限値(correspnIndex.xhtml).
     */
    public static final String ADVANCED_SEARCH_RESULT_MAX = "corresponIndex.maxresultnumber";

    /**
     * 設定ファイルから更新日付の表示フォーマットを取得するためのキーの定義.
     */
    public static final String DATE_FORMATTER = "date.format";

    /**
     * RSSにて取得するコレポンの期間(単位: 日).
     * 値が20かつ2/20の場合、2/1～2/20のコレポンが対象となる
     */
    public static final String RSS_DAY_PERIOD = "rss.dayperiod";

    /**
     * 応答履歴表示に表示する件名の最大文字数.
     */
    public static final String HISTORY_SUBJECT_MAXLENGTH = "history.subject.maxlength";

    /**
     * requestスコープにrssUserIdを設定する際に使うキー(template.jspの変数と対応).
     */
    public static final String REQUEST_SCOPE_KEY_RSSUSERID  = "rssUserId";

    /**
     * requestスコープにrssFeedKeyを設定する際に使うキー(template.jspの変数と対応).
     */
    public static final String REQUEST_SCOPE_KEY_RSSFEEDKEY = "rssFeedKey";

    /**
     * リダイレクト先画面IDをセッションに設定する際に使うキー.
     */
    public static final String KEY_REDIRECT_SCREEN_ID = "redirectScreenId";

    /** File Upload ファイル最大数. */
    public static final String KEY_FILE_MAX_SIZE = "file.max.size";

    /** File Upload ファイル名最大長. */
    public static final String KEY_FILENAME_MAX_LENGTH = "filename.max.length";

    /** ViewExpiredException発生時の詳細情報をログ出力するか判定するためのファイル名. */
    public static final String KEY_LOG_SWITCH_VIEWEXPIREDEXCEPTION = "log.switch.viewExpiredException";

    /** Elasticsearchホスト名取得キー. */
    public static final String KEY_ELASTICSEARCH_HOST = "elasticsearch.host";

    /** Elasticsearchポート番号取得キー. */
    public static final String KEY_ELASTICSEARCH_PORT = "elasticsearch.port";

    /** Elasticsearch インデックス設定情報ファイル取得キー. */
    public static final String KEY_ELASTICSEARCH_INDEX_SETTING = "elasticsearch.index.setting.file";

    /** Elasticsearch タイプマッピングファイル取得キー. */
    public static final String KEY_ELASTICSEARCH_TYPE_MAPPING = "elasticsearch.type.mapping.file";

    /** Elasticsearch 全文検索対象タイプ名. */
    public static final String KEY_ELASTICSEARCH_TYPE_NAME = "elasticsearch.type.name";

    /** Redisホスト名取得キー. */
    public static final String KEY_REDIS_HOST = "redis.host";

    /** Google Cloud Vision API 利用可否取得キー. */
    public static final String KEY_GOOGLE_VISION_USE = "googlevision.use";
    /** Google Cloud Vision API 認証情報ファイル取得キー. */
    public static final String KEY_GOOGLE_VISION_SERVICE_ACCOUNT_FILE = "googlevision.service.account.file";
    /** Google Cloud Vision API アプリケーション名取得キー. */
    public static final String KEY_GOOGLE_VISION_APPLICATION_NAME = "googlevision.application.name";
    /** Google Cloud Vision API 結果最大件数取得キー. */
    public static final String KEY_GOOGLE_VISION_API_RESULT_COUNT = "googlevision.api.result.count";

    /**
     * sessionに格納されたコレポン文書IDリストのKEY.
     * @author opentone
     */
    public static final String KEY_CORRESPON_IDS = PREFIX + "corresponIds";

    /**
     * sessionに格納されたコレポン文書検索結果数のKEY.
     * @author opentone
     */
    public static final String KEY_CORRESPON_DATA_COUNT = PREFIX + "corresponDataCount";

    /**
     * 学習用コンテンツ項目名取得キー.
     */
    public static final String KEY_LEARNING_TITLE = "learning.title";

    /**
     * 学習用コンテンツ用PJID取得キー.
     */
    public static final String KEY_LEARNING_PJ = "learning.pj";

    /**
     * 空のインスタンスを生成する.
     * 外部からのインスタンス化禁止.
     */
    private Constants() {
    }
}
