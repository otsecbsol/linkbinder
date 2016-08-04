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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import jp.co.opentone.bsol.linkbinder.attachment.SavedAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentExtractedTextEditablePage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.core.validation.constraints.DateString;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.action.AbstractCorresponEditPageAction;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.UploadedFile;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroup;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeader;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateUser;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService;
import jp.co.opentone.bsol.linkbinder.service.admin.DistributionTemplateService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponValidateService;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponPageFormatter;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.action.control.CorresponEditPageElementControl;
import jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentDownloadAction;
import jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentDownloadablePage;
import jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentUploadAction;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponDataSource;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditPageInitializer;
import jp.co.opentone.bsol.linkbinder.view.validator.AttachmentValidator;

/**
 * コレポン文書入力画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CorresponEditPage extends AbstractCorresponPage
        implements AttachmentDownloadablePage, AttachmentExtractedTextEditablePage {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6819171074955985797L;
    /**
     * 表題（新規登録）ラベル.
     */
    public static final String NEW = "文書 新規登録";
    /**
     * 表題（更新）ラベル.
     */
    public static final String UPDATE = "文書 更新";
    /**
     * 宛先編集ダイアログの編集結果を1要素毎に区切る文字.
     */
    public static final String DELIM_ADDRESS_EMP_NO = ",";
    /**
     * 宛先編集ダイアログの各編集結果をユーザー/グループIDに区切るための文字.
     */
    public static final String DELIM_ADDRESS_GROUP_USER = "-";
    /**
     * 1件のコレポン文書に添付可能なファイルの最大数.
     */
    private static final int ATTACHMENTS_SIZE = 5;

    /**
     * 1件のコレポン文書に設定可能なカスタムフィールドの最大数.
     */
    private static final int CUSTOM_FIELDS_SIZE = 10;

    /**
     * JSON形式の宛先をデコードするための型情報.
     */
    private static final Type MAPPING_TYPE;
    static {
        try {
            MAPPING_TYPE = CorresponEditPage.class.getDeclaredField("m").getGenericType();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * JSON形式の宛先をデコードするための型情報を取得するためのダミーフィールド.
     * <p>
     * staticイニシャライザ内で名前を参照されるだけで、処理には一切使用されない.
     * </p>
     */
    @SuppressWarnings("unused")
    private List<AddressCorresponGroup> m;

    /**
     * このページの初期化に関する処理を行うオブジェクト.
     */
    @Resource
    private CorresponEditPageInitializer initializer;

    /**
     * コレポン文書関連画面で利用する共通データ.
     */
    @Resource
    @Transfer
    private CorresponDataSource dataSource;

    /**
     * 添付ファイルの最大数.
     */
    private int maxAttachmentsSize = ATTACHMENTS_SIZE;

    /**
     * 表示タイトル.
     */
    @Transfer
    private String title;
    /**
     * 新規登録処理を表すクエリパラメータの値.
     */
    @Transfer
    private String newEdit;
    /**
     * 初期画面表示判定値.
     */
    @Transfer
    private boolean initialDisplaySuccess = false;
    /**
     * コレポン文書サービス.
     */
    @Resource
    private CorresponService corresponService;
    /**
     * コレポン文書検証サービス.
     */
    @Resource
    private CorresponValidateService corresponValidateService;
    /**
     * 活動単位サービス.
     */
    @Resource
    private CorresponGroupService corresponGroupService;
    /**
     * ユーザーサービス.
     */
    @Resource
    private UserService userService;
    /**
     * Distribution templateサービス.
     */
    @Resource
    private DistributionTemplateService distTemplateService;

    /**
     * 画面コンポーネント表示制御オブジェクト.
     */
    @Transfer
    private CorresponEditPageElementControl elemControl =
            new CorresponEditPageElementControl();
    /**
     * 編集対象のコレポン文書.
     */
    @Transfer
    private Correspon correspon = new Correspon();
    /**
     * コレポン文書の表示内容を整形するオブジェクト.
     */
    private CorresponPageFormatter formatter;
    /**
     * 送信元活動単位の候補リスト.
     * <p>
     * ログインユーザーが所属する活動単位.
     * </p>
     */
    @Transfer
    private List<CorresponGroup> fromGroup;
    /**
     * 表示対象のコレポン文書.
     */
    @Transfer
    private List<CorresponType> corresponType;
    /**
     * 表示対象のカスタムフィールド.
     */
    @Transfer
    private List<CustomField> customField;
    /**
     * 表示対象のカスタム値1.
     */
    @Transfer
    private List<CustomFieldValue> customFieldValue1;
    /**
     * 表示対象のカスタム値2.
     */
    @Transfer
    private List<CustomFieldValue> customFieldValue2;
    /**
     * 表示対象のカスタム値3.
     */
    @Transfer
    private List<CustomFieldValue> customFieldValue3;
    /**
     * 表示対象のカスタム値4.
     */
    @Transfer
    private List<CustomFieldValue> customFieldValue4;
    /**
     * 表示対象のカスタム値5.
     */
    @Transfer
    private List<CustomFieldValue> customFieldValue5;
    /**
     * 表示対象のカスタム値6.
     */
    @Transfer
    private List<CustomFieldValue> customFieldValue6;
    /**
     * 表示対象のカスタム値7.
     */
    @Transfer
    private List<CustomFieldValue> customFieldValue7;
    /**
     * 表示対象のカスタム値8.
     */
    @Transfer
    private List<CustomFieldValue> customFieldValue8;
    /**
     * 表示対象のカスタム値9.
     */
    @Transfer
    private List<CustomFieldValue> customFieldValue9;
    /**
     * 表示対象のカスタム値10.
     */
    @Transfer
    private List<CustomFieldValue> customFieldValue10;
    /**
     * 表示対象の活動単位.
     * <p>
     * プロジェクト内の全ての活動単位.
     * </p>
     */
    @Transfer
    private List<CorresponGroup> corresponGroups;
    /**
     * 表示対象のユーザー.
     * <p>
     * プロジェクト内の全てのユーザー.
     * </p>
     */
    @Transfer
    private List<User> projectUsers;

    /**
     * 表示対象コレポン文書のID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private Long id;
    /**
     * 返信済コレポン文書のID.
     * <p>
     * 返信済文書を引用して返信文書を新規作成する場合、遷移元から値が自動設定される.
     * </p>
     */
    @Transfer
    private Long repliedId;
    /**
     * 表示対象コレポン文書StatusNo.
     * <p>
     * コレポン文書StatusNo.
     * </p>
     */
    @Transfer
    private Integer corresponStatus = CorresponStatus.OPEN.getValue();
    /**
     * 表示対象ワークフローStatusNo.
     * <p>
     * ワークフローStatusNo.
     * </p>
     */
    @Transfer
    private Integer workflowStatus = WorkflowStatus.DRAFT.getValue();
    /**
     * 表示対象from.
     * <p>
     * from.
     * </p>
     */
    @Transfer
    @Required
    private Long from = 1L;

    //CHECKSTYLE:OFF
    /**
     * Toに設定されたかどうかを保持する.
     * バリデーションチェック用.
     */
    @Transfer
    @Required
    private String toAddressValues;

    /**
     * 宛先に設定された活動単位ID.
     */
    @Transfer
    private Long corresponGroupId;
    /**
     * 宛先に設定された従業員番号のCSV形式の文字列.
     */
    @Transfer
    private String addressUserValues;
    //CHECKSTYLE:ON

    /**
     * コレポン文書種別.
     */
    @Transfer
    @Required
    private Long type = 1L;
    /**
     * 表示対象返subject.
     * <p>
     * subject.
     * </p>
     */
    @Transfer
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 300)
    //CHECKSTYLE:ON
    private String subject;
    /**
     * 表示対象body.
     * <p>
     * body.
     * </p>
     */
    @Transfer
    @Required
    private String body;
    /**
     * 表示対象返信要否.
     * <p>
     * 返信要否.
     * </p>
     */
    @Transfer
    private Integer replyRequired = ReplyRequired.NO.getValue();
    /**
     * 表示対象返信期限日.
     * <p>
     * 返信期限日.
     * </p>
     */
    @Transfer
    @DateString
    private String deadlineForReply;

    /**
     * 添付ファイル情報表示エリアの表示状態.
     * <p>
     * 設定された添付ファイル1
     * </p>
     */
    private UploadedFile attachment1;
    /**
     * 添付ファイル情報表示エリアの表示状態.
     * <p>
     * 設定された添付ファイル2
     * </p>
     */
    private UploadedFile attachment2;
    /**
     * 添付ファイル情報表示エリアの表示状態.
     * <p>
     * 設定された添付ファイル3
     * </p>
     */
    private UploadedFile attachment3;
    /**
     * 添付ファイル情報表示エリアの表示状態.
     * <p>
     * 設定された添付ファイル4
     * </p>
     */
    private UploadedFile attachment4;
    /**
     * 添付ファイル情報表示エリアの表示状態.
     * <p>
     * 設定された添付ファイル5
     * </p>
     */
    private UploadedFile attachment5;

    /**
     * 編集中の添付ファイル情報.
     */
    private AttachmentInfo editingAttachment;

    /**
     * ダウンロード対象ファイルのID.
     */
    private Long fileId;
    /**
     * ダウンロード対象ファイルの番号.
     */
    private int fileNo;

    /**
     * 入力値：カスタムフィールド1値.
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String customField1Value;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド1Id
     * </p>
     */
    @Transfer
    private Long customField1Id;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド2値
     * </p>
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String customField2Value;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド2Id
     * </p>
     */
    @Transfer
    private Long customField2Id;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド3値
     * </p>
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String customField3Value;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド3Id
     * </p>
     */
    @Transfer
    private Long customField3Id;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド4値
     * </p>
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String customField4Value;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド4Id
     * </p>
     */
    @Transfer
    private Long customField4Id;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド5値
     * </p>
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String customField5Value;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド5Id
     * </p>
     */
    @Transfer
    private Long customField5Id;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド6値
     * </p>
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String customField6Value;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド6Id
     * </p>
     */
    @Transfer
    private Long customField6Id;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド7値
     * </p>
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String customField7Value;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド7Id
     * </p>
     */
    @Transfer
    private Long customField7Id;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド8値
     * </p>
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String customField8Value;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド8Id
     * </p>
     */
    @Transfer
    private Long customField8Id;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド9値
     * </p>
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String customField9Value;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド9Id
     * </p>
     */
    @Transfer
    private Long customField9Id;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド10値
     * </p>
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String customField10Value;
    /**
     * 表示エリアの表示状態.
     * <p>
     * 設定されたカスタムフィールド10Id
     * </p>
     */
    @Transfer
    private Long customField10Id;

    /**
     * 入力項目：文書状態.
     */
    @Transfer
    private List<SelectItem> selectCorresponStatus;
    /**
     * 入力項目：活動単位.
     */
    @Transfer
    private List<SelectItem> selectFrom;
    /**
     * 選択項目：コレポン文書種別.
     */
    @Transfer
    private List<SelectItem> selectCorresponType;
    /**
     * 入力項目：返信要否.
     */
    @Transfer
    private List<SelectItem> selectReplyRequired;
    /**
     * 表示制御.
     */
    @Transfer
    private String editMode;

    /**
     * 添付ファイル有無(添付１).
     * <p>
     * 複写元から添付ファイル1を引き継ぐ場合はtrue.
     * </p>
     */
    @Transfer
    private boolean attachment1Transfer;
    /**
     * 添付ファイル有無(添付２).
     * <p>
     * 複写元から添付ファイル2を引き継ぐ場合はtrue.
     * </p>
     */
    @Transfer
    private boolean attachment2Transfer;
    /**
     * 添付ファイル有無(添付３).
     * <p>
     * 複写元から添付ファイル3を引き継ぐ場合はtrue.
     * </p>
     */
    @Transfer
    private boolean attachment3Transfer;
    /**
     * 添付ファイル有無(添付４).
     * <p>
     * 複写元から添付ファイル4を引き継ぐ場合はtrue.
     * </p>
     */
    @Transfer
    private boolean attachment4Transfer;
    /**
     * 添付ファイル有無(添付５).
     * <p>
     * 複写元から添付ファイル5を引き継ぐ場合はtrue.
     * </p>
     */
    @Transfer
    private boolean attachment5Transfer;

    /**
     * 削除された添付ファイル.
     * <p>
     * JSPで、各添付ファイル項目での削除有無を判定するために利用する.
     * </p>
     */
    private List<Boolean> attachmentDeletedList = new ArrayList<Boolean>();
    {
        //  添付ファイルの最大数分の要素を初期化
        for (int i = 0; i < ATTACHMENTS_SIZE; i++) {
            attachmentDeletedList.add(Boolean.FALSE);
        }
    }

    /**
     * 登録する添付ファイル情報.
     */
    @Transfer
    private List<AttachmentInfo> attachments;
    /**
     * 登録済から削除となった添付ファイル情報.
     */
    @Transfer
    private List<AttachmentInfo> removedAttachments;

    /**
     * 宛先編集・削除を行う対象(To/Cc).
     */
    private Integer detectedAddressType;

    /**
     * 宛先編集・削除を行う行番号.
     */
    private int detectedAddressIndex;

    /**
     * ダイアログで使用する各JSON形式の情報の読み込み済みかどうか.
     * <p>
     * 読み込み済みの場合はtrue.
     * </p>
     */
    private boolean jsonValuesLoaded;

    /**
     * 宛先(To)DataModel.
     */
    private transient DataModel<?> toDataModel;
    /**
     * 宛先(Cc)DataModel.
     */
    private transient DataModel<?> ccDataModel;

    /**
     * Distribution templateリスト.
     */
    @Transfer
    private List<SelectItem> selectDistributionTemplate;

    /**
     * Distribution template.
     */
    @Transfer
    private Long distributionTemplate;

    /**
     * このプロジェクトのDistribution template一覧.
     */
    @Transfer
    private List<DistTemplateHeader> distributionTemplateList;
    //*****END

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponEditPage() {
    }

    /**
     * @return the selectDistributionTemplate
     */
    public List<SelectItem> getSelectDistributionTemplate() {
        return selectDistributionTemplate;
    }

    /**
     * @param selectDistributionTemplate the selectDistributionTemplate to set
     */
    public void setSelectDistributionTemplate(List<SelectItem> selectDistributionTemplate) {
        this.selectDistributionTemplate = selectDistributionTemplate;
    }

    /**
     * @return the distributionTemplate
     */
    public Long getDistributionTemplate() {
        return distributionTemplate;
    }

    /**
     * @param distributionTemplate the distributionTemplate to set
     */
    public void setDistributionTemplate(Long distributionTemplate) {
        this.distributionTemplate = distributionTemplate;
    }

    /**
     * @return the distributionTemplateList
     */
    public List<DistTemplateHeader> getDistributionTemplateList() {
        return distributionTemplateList;
    }

    /**
     * @param distributionTemplateList the distributionTemplateList to set
     */
    public void setDistributionTemplateList(List<DistTemplateHeader> distributionTemplateList) {
        this.distributionTemplateList = distributionTemplateList;
    }

    @Override
    public String showAttachmentExtractedTextEditDialog(AttachmentInfo attachment) {
        editingAttachment = attachment;
        return null;
    }

    @Override
    public void saveExtractedText() {
        // 何もしない
    }

    @Override
    public void cancelExtractedTextEdit() {
        editingAttachment = emptyAttachmentInfo();
    }

    private AttachmentInfo emptyAttachmentInfo() {
        return new AttachmentInfo() {
            @Override
            public byte[] getContent() throws ServiceAbortException {
                return null;
            }

            @Override
            public Attachment toAttachment() throws ServiceAbortException {
                return null;
            }
        };
    }

    @Override
    public AttachmentInfo getEditingAttachment() {
        return editingAttachment != null
                ? editingAttachment
                : emptyAttachmentInfo();
    }

    /**
     * 入力検証グループ名を返す.
     * @return 入力検証グループ名
     */
    public String getValidationGroups() {
        if (isActionInvoked("form:Next")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * 登録する添付ファイル情報を返す.
     * @return 添付ファイル情報
     */
    public List<AttachmentInfo> getAttachments() {
        if (attachments == null) {
            attachments = new ArrayList<AttachmentInfo>();
        }
        return attachments;
    }

    /**
     * 添付ファイル情報を登録対象に追加する.
     * @param attachmentInfo 登録する添付ファイル情報
     */
    public void addAttachment(AttachmentInfo attachmentInfo) {
        getAttachments().add(attachmentInfo);
    }

    /**
     * 指定位置に登録する添付ファイル情報を追加する.
     * @param index 位置
     * @param attachmentInfo 登録する添付ファイル情報
     */
    public void addAttachmentAt(int index, AttachmentInfo attachmentInfo) {
        getAttachments().add(index, attachmentInfo);
    }

    /**
     * 添付ファイル情報をクリアする.
     */
    public void clearAttachments() {
        getAttachments().clear();
    }

    /**
     * 指定された位置の添付ファイル情報を取得する.
     * @param index 位置
     * @return 添付ファイル情報
     */
    public AttachmentInfo getAttachmentAt(int index) {
        return getAttachments().get(index);
    }

    /**
     * 指定された位置の添付ファイル情報を削除する.
     * @param index 位置
     */
    public void removeAttachmentAt(int index) {
        List<AttachmentInfo> a = getAttachments();
        if (a.size() > index) {
            a.remove(index);
        }

    }
    /**
     * 指定された位置の添付ファイル情報を置き換える.
     * @param index 位置
     * @param attachmentInfo 添付ファイル情報
     */
    public void setAttachmentAt(int index, AttachmentInfo attachmentInfo) {
        List<AttachmentInfo> a = getAttachments();
        if (a.size() > index) {
            a.set(index, attachmentInfo);
        }
    }

    /**
     * 削除される添付ファイル情報を返す.
     * @return 削除される添付ファイル情報
     */
    public List<AttachmentInfo> getRemovedAttachments() {
        if (removedAttachments == null) {
            removedAttachments = new ArrayList<AttachmentInfo>();
        }
        return removedAttachments;
    }

    /**
     * 添付ファイル情報を削除対象に追加する.
     * @param attachmentInfo 添付ファイル情報
     */
    public void addRemovedAttachment(AttachmentInfo attachmentInfo) {
        getRemovedAttachments().add(attachmentInfo);
    }

    /**
     * 現在添付されているファイルの数を返す.
     * @return 添付ファイルの数
     */
    public int getAttachedCount() {
        return getAttachments().size();
    }

    public void setAttachedCount(int attachedCount) {
    }

    /**
     * 添付済みで削除を行ったファイルの数を返す.
     * @return 添付済み削除ファイル数
     */
    public int getAttachedCountByDelete() {
        int rt = 0;
        for (int i = 0; i < attachmentDeletedList.size(); i++) {
            Object o = attachmentDeletedList.get(i);
            if (Boolean.valueOf(o.toString()).booleanValue()) {
                rt++;
            }
        }
        return rt;
    }

    public void setAttachedCountByDelete(int attachedCountByDelete) {
    }

    /**
     * コレポン文書の表示内容を整形するオブジェクトを返す.
     * @return コレポン文書整形オブジェクト
     */
    public CorresponPageFormatter getFormatter() {
        if (formatter == null) {
            formatter = new CorresponPageFormatter(correspon);
        }
        formatter.setCorrespon(correspon);
        return formatter;
    }

    /**
     * 入力されたコレポン文書種別をコレポン文書オブジェクトに設定する.
     * @param c コレポン文書オブジェクト
     */
    private void setCorresponTypeTo(Correspon c) {
        Long projectCorresponTypeId = getType();
        for (CorresponType t : getCorresponType()) {
            if (t.getProjectCorresponTypeId().equals(projectCorresponTypeId)) {
                c.setCorresponType(t);
                break;
            }
        }
    }

    /**
     * 入力された文書状態をコレポン文書オブジェクトに設定する.
     * @param c コレポン文書オブジェクト.
     */
    private void setCorresponStatusTo(Correspon c) {
        if (!CorresponDataSource.VALUE_NOT_SELECTED.equals(getCorresponStatus())) {
            for (CorresponStatus v : CorresponStatus.values()) {
                if (v.getValue().equals(getCorresponStatus())) {
                    c.setCorresponStatus(v);
                    break;
                }
            }
        }
    }

    /**
     * 入力された返信要否・返信期限をコレポン文書オブジェクトに設定する.
     * @param c コレポン文書オブジェクト
     */
    private void setReplyRequiredTo(Correspon c) {
        if (!CorresponDataSource.VALUE_NOT_SELECTED.equals(getReplyRequired())) {
            for (ReplyRequired v : ReplyRequired.values()) {
                if (v.getValue().equals(getReplyRequired())) {
                    c.setReplyRequired(v);
                    break;
                }
            }
        }
        c.setDeadlineForReply(DateUtil.convertStringToDate(getDeadlineForReply()));
    }

    /**
     * 入力された送信元をコレポン文書オブジェクトに設定する.
     * @param c コレポン文書オブジェクト
     */
    private void setFromTo(Correspon c) {
        Long fromId = getFrom();
        for (CorresponGroup g : getCorresponGroups()) {
            if (g.getId().equals(fromId)) {
                c.setFromCorresponGroup(g);
                break;
            }
        }
    }

    /**
     * 入力された添付ファイル情報をコレポン文書オブジェクトに設定する.
     * @param c コレポン文書オブジェクト
     * @throws ServiceAbortException 設定に失敗
     */
    private void setAttachmentTo(Correspon c) throws ServiceAbortException {
        // 登録用添付ファイルの準備
        List<Attachment> list = new ArrayList<Attachment>();
        if (getAttachments() != null) {
            for (AttachmentInfo info : getAttachments()) {
                list.add(info.toAttachment());
            }
        }
        if (getRemovedAttachments() != null) {
            for (AttachmentInfo info : getRemovedAttachments()) {
                list.add(info.toAttachment());
            }
        }
        c.setUpdateAttachments(list);
    }

    /**
     * 入力されたカスタムフィールドをコレポン文書オブジェクトに設定する.
     * @param c コレポン文書オブジェクト
     */
    private void setCustomFieldsTo(Correspon c) {
        c.setCustomField1Value(getCustomField1Value());
        c.setCustomField2Value(getCustomField2Value());
        c.setCustomField3Value(getCustomField3Value());
        c.setCustomField4Value(getCustomField4Value());
        c.setCustomField5Value(getCustomField5Value());
        c.setCustomField6Value(getCustomField6Value());
        c.setCustomField7Value(getCustomField7Value());
        c.setCustomField8Value(getCustomField8Value());
        c.setCustomField9Value(getCustomField9Value());
        c.setCustomField10Value(getCustomField10Value());
    }

    /**
     * 入力情報をコレポン文書オブジェクトにセットする.
     */
    /**
     * @throws ServiceAbortException
     */
    private void setupCorrespon() throws ServiceAbortException {
        Correspon c = this.correspon;

        // Project情報設定
        c.setProjectId(getCurrentProjectId());
        c.setProjectNameE(getCurrentProject().getNameE());
        // コレポン状態設定
        setCorresponStatusTo(c);
        // FROM活動単位設定
        setFromTo(c);
        // コレポン種別設定
        setCorresponTypeTo(c);
        // SUBJECT設定
        c.setSubject(getSubject());
        // BODY設定
        c.setBody(getBody());
        // 返信要否・返信期限設定
        setReplyRequiredTo(c);
        // 添付ファイル
        setAttachmentTo(c);
        // カスタムフィールド設定
        setCustomFieldsTo(c);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.attachment
     *     .AttachmentDownloadablePage#getDownloadingAttachmentInfo()
     */
    public AttachmentInfo getDownloadingAttachmentInfo() {
        AttachmentInfo result = getAttachmentAt(getFileNo() - 1);
        return result;
    }

    /**
     * 返信期限を編集可能な、返信要否の値をJavaScriptの配列形式で返す.
     * @return 返信要否の値
     */
    public String getEditableReplyRequiredValues() {
        return StringUtils.join(CorresponDataSource.EDITABLE_REPLY_REQUIRED_VALUES, ',');
    }

    /**
     * 送信元活動単位を表示するSelectItemを作成する.
     */
    public void createSelectFrom() {
        selectFrom = viewHelper.createSelectItem(fromGroup, "id", "name");
    }

    /**
     * コレポン文書状態を表示するSelectItemを作成する.
     * @param values コレポン文書状態
     */
    public void createSelectCorresponStatus(CorresponStatus[] values) {
        selectCorresponStatus = viewHelper.createSelectItem(values);
    }

    /**
     * コレポン文書種別を表示するSelectItemを作成する.
     */
    public void createSelectCorresponType() {
        selectCorresponType =
            viewHelper.createSelectItem(corresponType, "projectCorresponTypeId", "label");
    }

    /**
     * 返信要否を表示するSelectItemを作成する.
     * @param values 返信要否
     */
    public void createSelectReplyRequired(ReplyRequired[] values) {
        setSelectReplyRequired(viewHelper.createSelectItem(values));
    }

    /**
     * Distribution templateを表示するSelectItemを作成する.
     */
    public void createSelectDistributionTemplate() {
        selectDistributionTemplate =
            viewHelper.createSelectItem(distributionTemplateList, "id", "name");
    }

    /**
     * @return the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param correspon
     *            the correspon to set
     */
    public void setCorrespon(Correspon correspon) {
        this.correspon = correspon;
    }

    /**
     * @return the correspon
     */
    public Correspon getCorrespon() {
        return correspon;
    }

    /**
     * @return the corresponGroup
     */
    public List<CorresponGroup> getFromGroup() {
        return fromGroup;
    }

    /**
     * @param fromGroup
     *            the fromGroup to set
     */
    public void setFromGroup(List<CorresponGroup> fromGroup) {
        this.fromGroup = fromGroup;
    }

    /**
     * @param corresponType
     *            the corresponType to set
     */
    public void setCorresponType(List<CorresponType> corresponType) {
        this.corresponType = corresponType;
    }

    /**
     * @return the corresponType
     */
    public List<CorresponType> getCorresponType() {
        return this.corresponType;
    }

    /**
     * @param customField
     *            the customField to set
     */
    public void setCustomField(List<CustomField> customField) {
        this.customField = customField;
    }

    /**
     * @return the customField
     */
    public List<CustomField> getCustomField() {
        return this.customField;
    }

    /**
     * @param customFieldValue1
     *            the customFieldValue1 to set
     */
    public void setCustomFieldValue1(List<CustomFieldValue> customFieldValue1) {
        this.customFieldValue1 = customFieldValue1;
    }

    /**
     * @return the customFieldValue
     */
    public List<CustomFieldValue> getCustomFieldValue1() {
        return this.customFieldValue1;
    }

    /**
     * @param customFieldValue2
     *            the customFieldValue2 to set
     */
    public void setCustomFieldValue2(List<CustomFieldValue> customFieldValue2) {
        this.customFieldValue2 = customFieldValue2;
    }

    /**
     * @return the customFieldValue2
     */
    public List<CustomFieldValue> getCustomFieldValue2() {
        return this.customFieldValue2;
    }

    /**
     * @param customFieldValue3
     *            the customFieldValue3 to set
     */
    public void setCustomFieldValue3(List<CustomFieldValue> customFieldValue3) {
        this.customFieldValue3 = customFieldValue3;
    }

    /**
     * @return the customFieldValue3
     */
    public List<CustomFieldValue> getCustomFieldValue3() {
        return this.customFieldValue3;
    }

    /**
     * @param customFieldValue4
     *            the customFieldValue4 to set
     */
    public void setCustomFieldValue4(List<CustomFieldValue> customFieldValue4) {
        this.customFieldValue4 = customFieldValue4;
    }

    /**
     * @return the customFieldValue4
     */
    public List<CustomFieldValue> getCustomFieldValue4() {
        return this.customFieldValue4;
    }

    /**
     * @param customFieldValue5
     *            the customFieldValue5 to set
     */
    public void setCustomFieldValue5(List<CustomFieldValue> customFieldValue5) {
        this.customFieldValue5 = customFieldValue5;
    }

    /**
     * @return the customFieldValue5
     */
    public List<CustomFieldValue> getCustomFieldValue5() {
        return this.customFieldValue5;
    }

    /**
     * @param customFieldValue6
     *            the customFieldValue6 to set
     */
    public void setCustomFieldValue6(List<CustomFieldValue> customFieldValue6) {
        this.customFieldValue6 = customFieldValue6;
    }

    /**
     * @return the customFieldValue6
     */
    public List<CustomFieldValue> getCustomFieldValue6() {
        return this.customFieldValue6;
    }

    /**
     * @param customFieldValue7
     *            the customFieldValue7 to set
     */
    public void setCustomFieldValue7(List<CustomFieldValue> customFieldValue7) {
        this.customFieldValue7 = customFieldValue7;
    }

    /**
     * @return the customFieldValue7
     */
    public List<CustomFieldValue> getCustomFieldValue7() {
        return this.customFieldValue7;
    }

    /**
     * @param customFieldValue8
     *            the customFieldValue8 to set
     */
    public void setCustomFieldValue8(List<CustomFieldValue> customFieldValue8) {
        this.customFieldValue8 = customFieldValue8;
    }

    /**
     * @return the customFieldValue8
     */
    public List<CustomFieldValue> getCustomFieldValue8() {
        return this.customFieldValue8;
    }

    /**
     * @param customFieldValue9
     *            the customFieldValue9 to set
     */
    public void setCustomFieldValue9(List<CustomFieldValue> customFieldValue9) {
        this.customFieldValue9 = customFieldValue9;
    }

    /**
     * @return the customFieldValue9
     */
    public List<CustomFieldValue> getCustomFieldValue9() {
        return this.customFieldValue9;
    }

    /**
     * @param customFieldValue10
     *            the customFieldValue10 to set
     */
    public void setCustomFieldValue10(List<CustomFieldValue> customFieldValue10) {
        this.customFieldValue10 = customFieldValue10;
    }

    /**
     * @return the customFieldValue10
     */
    public List<CustomFieldValue> getCustomFieldValue10() {
        return this.customFieldValue10;
    }

    /**
     * @param corresponGroups
     *            the CorresponGroups to set
     */
    public void setCorresponGroups(List<CorresponGroup> corresponGroups) {
        this.corresponGroups = corresponGroups;
    }

    /**
     * @return the corresponGroups
     */
    public List<CorresponGroup> getCorresponGroups() {
        return this.corresponGroups;
    }

    /**
     * @param projectUsers
     *            the projectUsers to set
     */
    public void setProjectUsers(List<User> projectUsers) {
        this.projectUsers = projectUsers;
    }

    /**
     * @return the projectUsers
     */
    public List<User> getProjectUsers() {
        return this.projectUsers;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param corresponStatus
     *            the corresponStatus to set
     */
    public void setCorresponStatus(Integer corresponStatus) {
        this.corresponStatus = corresponStatus;
    }

    /**
     * @return the corresponStatus
     */
    public Integer getCorresponStatus() {
        return corresponStatus;
    }

    /**
     * @param workflowStatus
     *            the workflowStatus to set
     */
    public void setWorkflowStatus(Integer workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    /**
     * @return the workflowStatus
     */
    public Integer getWorkflowStatus() {
        return this.workflowStatus;
    }

    /**
     * @param from
     *            the from to set
     */
    public void setFrom(Long from) {
        this.from = from;
    }

    /**
     * @return the from
     */
    public Long getFrom() {
        return from;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(Long type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public Long getType() {
        return type;
    }

    /**
     * @param subject
     *            the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * @param body
     *            the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return this.body;
    }

    /**
     * @param deadlineForReply
     *            the deadlineForReply to set
     */
    public void setDeadlineForReply(String deadlineForReply) {
        this.deadlineForReply = deadlineForReply;
    }

    /**
     * @return the deadlineForReply
     */
    public String getDeadlineForReply() {
        return this.deadlineForReply;
    }

    /**
     * @param attachment1
     *            the attachment1 to set
     */
    public void setAttachment1(UploadedFile attachment1) {
        this.attachment1 = attachment1;
    }

    /**
     * @return the attachment1
     */
    public UploadedFile getAttachment1() {
        if (attachment1 == null) {
            attachment1 = new UploadedFile();
        }
        return attachment1;
    }

    /**
     * @param attachment2
     *            the attachment2 to set
     */
    public void setAttachment2(UploadedFile attachment2) {
        this.attachment2 = attachment2;
    }

    /**
     * @return the attachment2
     */
    public UploadedFile getAttachment2() {
        if (attachment2 == null) {
            attachment2 = new UploadedFile();
        }
        return attachment2;
    }

    /**
     * @param attachment3
     *            the attachment3 to set
     */
    public void setAttachment3(UploadedFile attachment3) {
        this.attachment3 = attachment3;
    }

    /**
     * @return the attachment3
     */
    public UploadedFile getAttachment3() {
        if (attachment3 == null) {
            attachment3 = new UploadedFile();
        }
        return attachment3;
    }

    /**
     * @param attachment4
     *            the attachment4 to set
     */
    public void setAttachment4(UploadedFile attachment4) {
        this.attachment4 = attachment4;
    }

    /**
     * @return the attachment4
     */
    public UploadedFile getAttachment4() {
        if (attachment4 == null) {
            attachment4 = new UploadedFile();
        }
        return attachment4;
    }

    /**
     * @param attachment5
     *            the attachment5 to set
     */
    public void setAttachment5(UploadedFile attachment5) {
        this.attachment5 = attachment5;
    }

    /**
     * @return the attachment5
     */
    public UploadedFile getAttachment5() {
        if (attachment5 == null) {
            attachment5 = new UploadedFile();
        }
        return attachment5;
    }

    /**
     * @return the fileId
     */
    public Long getFileId() {
        return fileId;
    }

    /**
     * @param fileId
     *            the fileId to set
     */
    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    /**
     * @return the fileNo
     */
    public int getFileNo() {
        return fileNo;
    }

    /**
     * @param fileNo
     *            the fileNo to set
     */
    public void setFileNo(int fileNo) {
        this.fileNo = fileNo;
    }

    /**
     * @return the customField1Value
     */
    public String getCustomField1Value() {
        return customField1Value;
    }

    /**
     * @param customField1Value
     *            the customField1Value to set
     */
    public void setCustomField1Value(String customField1Value) {
        this.customField1Value = customField1Value;
    }

    /**
     * @param customField1Id
     *            the customField1Id to set
     */
    public void setCustomField1Id(Long customField1Id) {
        this.customField1Id = customField1Id;
    }

    /**
     * @return the customField1Id
     */
    public Long getCustomField1Id() {
        return customField1Id;
    }

    /**
     * @param customField2Value
     *            the customField2Value to set
     */
    public void setCustomField2Value(String customField2Value) {
        this.customField2Value = customField2Value;
    }

    /**
     * @return the customField2Value
     */
    public String getCustomField2Value() {
        return customField2Value;
    }

    /**
     * @param customField2Id
     *            the customField2Id to set
     */
    public void setCustomField2Id(Long customField2Id) {
        this.customField2Id = customField2Id;
    }

    /**
     * @return the customField2Id
     */
    public Long getCustomField2Id() {
        return customField2Id;
    }

    /**
     * @param customField3Value
     *            the customField3Value to set
     */
    public void setCustomField3Value(String customField3Value) {
        this.customField3Value = customField3Value;
    }

    /**
     * @return the customField3Value
     */
    public String getCustomField3Value() {
        return customField3Value;
    }

    /**
     * @param customField3Id
     *            the customField3Id to set
     */
    public void setCustomField3Id(Long customField3Id) {
        this.customField3Id = customField3Id;
    }

    /**
     * @return the customField3Id
     */
    public Long getCustomField3Id() {
        return customField3Id;
    }

    /**
     * @param customField4Value
     *            the customField4Value to set
     */
    public void setCustomField4Value(String customField4Value) {
        this.customField4Value = customField4Value;
    }

    /**
     * @return the customField4Value
     */
    public String getCustomField4Value() {
        return customField4Value;
    }

    /**
     * @param customField4Id
     *            the customField4Id to set
     */
    public void setCustomField4Id(Long customField4Id) {
        this.customField4Id = customField4Id;
    }

    /**
     * @return the customField4Id
     */
    public Long getCustomField4Id() {
        return customField4Id;
    }

    /**
     * @param customField5Value
     *            the customField5Value to set
     */
    public void setCustomField5Value(String customField5Value) {
        this.customField5Value = customField5Value;
    }

    /**
     * @return the customField5Value
     */
    public String getCustomField5Value() {
        return customField5Value;
    }

    /**
     * @param customField5Id
     *            the customField5Id to set
     */
    public void setCustomField5Id(Long customField5Id) {
        this.customField5Id = customField5Id;
    }

    /**
     * @return the customField5Id
     */
    public Long getCustomField5Id() {
        return customField5Id;
    }

    /**
     * @param customField6Value
     *            the customField6Value to set
     */
    public void setCustomField6Value(String customField6Value) {
        this.customField6Value = customField6Value;
    }

    /**
     * @return the customField6Value
     */
    public String getCustomField6Value() {
        return customField6Value;
    }

    /**
     * @param customField6Id
     *            the customField6Id to set
     */
    public void setCustomField6Id(Long customField6Id) {
        this.customField6Id = customField6Id;
    }

    /**
     * @return the customField6Id
     */
    public Long getCustomField6Id() {
        return customField6Id;
    }

    /**
     * @param customField7Value
     *            the customField7Value to set
     */
    public void setCustomField7Value(String customField7Value) {
        this.customField7Value = customField7Value;
    }

    /**
     * @return the customField7Value
     */
    public String getCustomField7Value() {
        return customField7Value;
    }

    /**
     * @param customField7Id
     *            the customField7Id to set
     */
    public void setCustomField7Id(Long customField7Id) {
        this.customField7Id = customField7Id;
    }

    /**
     * @return the customField7Id
     */
    public Long getCustomField7Id() {
        return customField7Id;
    }

    /**
     * @param customField8Value
     *            the customField8Value to set
     */
    public void setCustomField8Value(String customField8Value) {
        this.customField8Value = customField8Value;
    }

    /**
     * @return the customField8Value
     */
    public String getCustomField8Value() {
        return customField8Value;
    }

    /**
     * @param customField8Id
     *            the customField8Id to set
     */
    public void setCustomField8Id(Long customField8Id) {
        this.customField8Id = customField8Id;
    }

    /**
     * @return the customField8Id
     */
    public Long getCustomField8Id() {
        return customField8Id;
    }

    /**
     * @param customField9Value
     *            the customField9Value to set
     */
    public void setCustomField9Value(String customField9Value) {
        this.customField9Value = customField9Value;
    }

    /**
     * @return the customField9Value
     */
    public String getCustomField9Value() {
        return customField9Value;
    }

    /**
     * @param customField9Id
     *            the customField9Id to set
     */
    public void setCustomField9Id(Long customField9Id) {
        this.customField9Id = customField9Id;
    }

    /**
     * @return the customField9Id
     */
    public Long getCustomField9Id() {
        return customField9Id;
    }

    /**
     * @param customField10Value
     *            the customField10Value to set
     */
    public void setCustomField10Value(String customField10Value) {
        this.customField10Value = customField10Value;
    }

    /**
     * @return the customField10Value
     */
    public String getCustomField10Value() {
        return customField10Value;
    }

    /**
     * @param customField10Id
     *            the customField10Id to set
     */
    public void setCustomField10Id(Long customField10Id) {
        this.customField10Id = customField10Id;
    }

    /**
     * @return the customField10Id
     */
    public Long getCustomField10Id() {
        return customField10Id;
    }

    /**
     * @param selectCorresponStatus
     *            the selectCorresponStatus to set
     */
    public void setSelectCorresponStatus(List<SelectItem> selectCorresponStatus) {
        this.selectCorresponStatus = selectCorresponStatus;
    }

    /**
     * @return the selectCorresponStatus
     */
    public List<SelectItem> getSelectCorresponStatus() {
        return selectCorresponStatus;
    }

    /**
     * @param selectFrom
     *            the selectFrom to set
     */
    public void setSelectFrom(List<SelectItem> selectFrom) {
        this.selectFrom = selectFrom;
    }

    /**
     * @return the selectFrom
     */
    public List<SelectItem> getSelectFrom() {
        return selectFrom;
    }

    /**
     * @param selectCorresponType
     *            the selectCorresponType to set
     */
    public void setSelectCorresponType(List<SelectItem> selectCorresponType) {
        this.selectCorresponType = selectCorresponType;
    }

    /**
     * @return the selectCorresponType
     */
    public List<SelectItem> getSelectCorresponType() {
        return selectCorresponType;
    }

    /**
     * @return the editMode
     */
    public String getEditMode() {
        return editMode;
    }

    /**
     * @param editMode
     *            the editMode to set
     */
    public void setEditMode(String editMode) {
        this.editMode = editMode;
    }

    /**
     * @return boolean
     */
    public boolean isAttachment1Transfer() {
        return attachment1Transfer;
    }

    /**
     * @param attachment1Transfer
     *            the attachment1Transfer to set
     */
    public void setAttachment1Transfer(boolean attachment1Transfer) {
        this.attachment1Transfer = attachment1Transfer;
    }

    /**
     * @return boolean
     */
    public boolean isAttachment2Transfer() {
        return attachment2Transfer;
    }

    /**
     * @param attachment2Transfer
     *            the attachment2Transfer to set
     */
    public void setAttachment2Transfer(boolean attachment2Transfer) {
        this.attachment2Transfer = attachment2Transfer;
    }

    /**
     * @return boolean
     */
    public boolean isAttachment3Transfer() {
        return attachment3Transfer;
    }

    /**
     * @param attachment3Transfer
     *            the attachment3Transfer to set
     */
    public void setAttachment3Transfer(boolean attachment3Transfer) {
        this.attachment3Transfer = attachment3Transfer;
    }

    /**
     * @return boolean
     */
    public boolean isAttachment4Transfer() {
        return attachment4Transfer;
    }

    /**
     * @param attachment4Transfer
     *            the attachment4Transfer to set
     */
    public void setAttachment4Transfer(boolean attachment4Transfer) {
        this.attachment4Transfer = attachment4Transfer;
    }

    /**
     * @return boolean
     */
    public boolean isAttachment5Transfer() {
        return attachment5Transfer;
    }

    /**
     * @param attachment5Transfer
     *            the attachment5Transfer to set
     */
    public void setAttachment5Transfer(boolean attachment5Transfer) {
        this.attachment5Transfer = attachment5Transfer;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(CorresponDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return the dataSource
     */
    public CorresponDataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param toAddressValues the toAddressValues to set
     */
    public void setToAddressValues(String toAddressValues) {
        this.toAddressValues = toAddressValues;
    }

    /**
     * @return the toAddressValues
     */
    public String getToAddressValues() {
        return toAddressValues;
    }

    /**
     * @param corresponGroupId the corresponGroupId to set
     */
    public void setCorresponGroupId(Long corresponGroupId) {
        this.corresponGroupId = corresponGroupId;
    }

    /**
     * @return the corresponGroupId
     */
    public Long getCorresponGroupId() {
        return corresponGroupId;
    }

    /**
     * @param addressUserValues the addressUserValues to set
     */
    public void setAddressUserValues(String addressUserValues) {
        this.addressUserValues = addressUserValues;
    }

    /**
     * @return the addressUserValues
     */
    public String getAddressUserValues() {
        return addressUserValues;
    }

    /**
     * @return the toDataModel
     */
    public DataModel<?> getToDataModel() {
        if (toDataModel == null) {
            toDataModel = new ListDataModel<AddressCorresponGroup>();
        }

        if (correspon != null) {
            toDataModel.setWrappedData(correspon.getToAddressCorresponGroups());
        }
        return toDataModel;
    }

    /**
     * 現在の宛先(To)DataModelのインデックスカウンタを取得.
     * @return index
     */
    public int getToDataIndex() {
        int index = toDataModel.getRowIndex();
        return index;
    }

    /**
     * 現在の宛先(Cc)DataModelのインデックスカウンタを取得.
     * @return index
     */
    public int getCcDataIndex() {
        int index = ccDataModel.getRowIndex();
        return index;
    }

    public String getToEmpNoGroupId() {
        AddressCorresponGroup acg = (AddressCorresponGroup) toDataModel.getRowData();
        return getEmpNoGroupIdCSV(acg);
    }

    public void setToEmpNoGroupId(String toEmpNo) {
        // 何もしない
    }

    /**
     * @return the ccDataModel
     */
    public DataModel<?> getCcDataModel() {
        if (ccDataModel == null) {
            ccDataModel = new ListDataModel<AddressCorresponGroup>();
        }

        if (correspon != null) {
            ccDataModel.setWrappedData(correspon.getCcAddressCorresponGroups());
        }
        return ccDataModel;
    }

    public String getCcEmpNoGroupId() {
        AddressCorresponGroup acg = (AddressCorresponGroup) ccDataModel.getRowData();
        return getEmpNoGroupIdCSV(acg);
    }

    public void setCcEmpNoGroupId(String ccEmpNo) {
        // 何もしない
    }

    private String getEmpNoGroupIdCSV(AddressCorresponGroup acg) {
        List<AddressUser> users = acg.getUsers();
        if (users == null || users.size() == 0) {
            return null;
        }

        String[] values = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            AddressUser user = users.get(i);
            values[i] = user.getUser().getEmpNo()
                        + DELIM_ADDRESS_GROUP_USER
                        + acg.getCorresponGroup().getId();
        }
        return StringUtils.join(values, DELIM_ADDRESS_EMP_NO);
    }

    /**
     * @param replyRequired the replyRequired to set
     */
    public void setReplyRequired(Integer replyRequired) {
        this.replyRequired = replyRequired;
    }

    /**
     * @return the replyRequired
     */
    public Integer getReplyRequired() {
        return replyRequired;
    }

    /**
     * @param selectReplyRequired the selectReplyRequired to set
     */
    public void setSelectReplyRequired(List<SelectItem> selectReplyRequired) {
        this.selectReplyRequired = selectReplyRequired;
    }

    /**
     * @return the selectReplyRequired
     */
    public List<SelectItem> getSelectReplyRequired() {
        return selectReplyRequired;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(List<AttachmentInfo> attachments) {
        this.attachments = attachments;
    }

    /**
     * @param removedAttachments the removedAttachments to set
     */
    public void setRemovedAttachments(List<AttachmentInfo> removedAttachments) {
        this.removedAttachments = removedAttachments;
    }

    /**
     * @param attachmentDeletedList the attachmentDeletedList to set
     */
    public void setAttachmentDeletedList(List<Boolean> attachmentDeletedList) {
        this.attachmentDeletedList = attachmentDeletedList;
    }

    /**
     * @return the attachmentDeletedList
     */
    public List<Boolean> getAttachmentDeletedList() {
        return attachmentDeletedList;
    }

    /**
     * @param newEdit the newEdit to set
     */
    public void setNewEdit(String newEdit) {
        this.newEdit = newEdit;
    }

    /**
     * @return the newEdit
     */
    public String getNewEdit() {
        return newEdit;
    }

    /**
     * initialDisplaySuccessを設定します.
     * @param initialDisplaySuccess the initialDisplaySuccess to set
     */
    public void setInitialDisplaySuccess(boolean initialDisplaySuccess) {
        this.initialDisplaySuccess = initialDisplaySuccess;
    }

    /**
     * initialDisplaySuccessを取得します.
     * @return the initialDisplaySuccess
     */
    public boolean isInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }

    /**
     * @param elemControl the elemControl to set
     */
    public void setElemControl(CorresponEditPageElementControl elemControl) {
        this.elemControl = elemControl;
    }

    /**
     * @return the elemControl
     */
    public CorresponEditPageElementControl getElemControl() {
        return elemControl;
    }

    /**
     * @return the corresponService
     */
    public CorresponService getCorresponService() {
        return corresponService;
    }

    /**
     * @return the corresponGroupService
     */
    public CorresponGroupService getCorresponGroupService() {
        return corresponGroupService;
    }

    /**
     * @return the userService
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * @param repliedId the repliedId to set
     */
    public void setRepliedId(Long repliedId) {
        this.repliedId = repliedId;
    }

    /**
     * @return the repliedId
     */
    public Long getRepliedId() {
        return repliedId;
    }

    /**
     * @param maxAttachmentsSize the maxAttachmentsSize to set
     */
    public void setMaxAttachmentsSize(int maxAttachmentsSize) {
        this.maxAttachmentsSize = maxAttachmentsSize;
    }

    /**
     * @return the maxAttachmentsSize
     */
    public int getMaxAttachmentsSize() {
        return maxAttachmentsSize;
    }

    /**
     * @param detectedAddressType the detectedAddressType to set
     */
    public void setDetectedAddressType(Integer detectedAddressType) {
        this.detectedAddressType = detectedAddressType;
    }

    /**
     * @return the detectedAddressType
     */
    public Integer getDetectedAddressType() {
        return detectedAddressType;
    }

    /**
     * @param detectedAddressIndex the detectedAddressIndex to set
     */
    public void setDetectedAddressIndex(int detectedAddressIndex) {
        this.detectedAddressIndex = detectedAddressIndex;
    }

    /**
     * @return the detectedAddressIndex
     */
    public int getDetectedAddressIndex() {
        return detectedAddressIndex;
    }

    /**
     * @param jsonValuesLoaded the jsonValuesLoaded to set
     */
    public void setJsonValuesLoaded(boolean jsonValuesLoaded) {
        this.jsonValuesLoaded = jsonValuesLoaded;
    }

    /**
     * @return the jsonValuesLoaded
     */
    public boolean isJsonValuesLoaded() {
        return jsonValuesLoaded;
    }

    /**
     * 添付ファイルを検証する.
     * <p>
     * このメソッドはJSPのValidationフェーズで呼び出される.
     * </p>
     * @param context FacesContext
     * @param component 入力コンポーネント
     * @param value 入力値
     */
    public void validateAttachment(FacesContext context, UIComponent component, Object value) {
        if (!isNextAction()) {
            return;
        }
        if (StringUtils.isNotEmpty((String) value)) {
            AttachmentValidator validator = new AttachmentValidator();
            validator.validate(context, component, value);
        }
    }

    /**
     * 宛先(To)を検証する.
     * <p>
     * このメソッドはJSPのValidationフェーズで呼び出される.
     * </p>
     * @param context FacesContext
     * @param component 入力コンポーネント
     * @param value 入力値
     * @throws Exception 例外
     */
    public void validateToAddress(
            FacesContext context, UIComponent component, Object value) throws Exception {
        // TODO 検証のためExceptionをthrow
        throw new Exception("AddressValidator.validateToAddress");
//        AddressValidator validator = new AddressValidator(MAPPING_TYPE, true);
//        validator.validate(context, component, value);
    }

    /**
     * 宛先(Cc)を検証する.
     * <p>
     * このメソッドはJSPのValidationフェーズで呼び出される.
     * </p>
     * @param context FacesContext
     * @param component 入力コンポーネント
     * @param value 入力値
     * @throws Exception 例外
     */
    public void validateCcAddress(
            FacesContext context, UIComponent component, Object value) throws Exception {
        // TODO 検証のためExceptionをthrow
        throw new Exception("AddressValidator.validateCcAddress");
//        AddressValidator validator = new AddressValidator(MAPPING_TYPE, false);
//        validator.validate(context, component, value);
    }

    /**
     * 指定のタイプの宛先情報を全て削除対象にする.
     * @return null
     */
    public String deleteAll() {
        if (AddressType.TO.getValue().equals(detectedAddressType)) {
            for (AddressCorresponGroup ag : correspon.getToAddressCorresponGroups()) {
                ag.setMode(UpdateMode.DELETE);
            }
            checkToAddress();
        } else if (AddressType.CC.getValue().equals(detectedAddressType)) {
            for (AddressCorresponGroup ag : correspon.getCcAddressCorresponGroups()) {
                ag.setMode(UpdateMode.DELETE);
            }
        } else {
            // 何もせずに終了
        }
        return null;
    }

    public String deleteAttachemect() {
        return null;
    }

    /**
     * 指定のタイプの指定行の宛先情報を削除対象にする.
     * @return null
     */
    public String delete() {
        if (AddressType.TO.getValue().equals(detectedAddressType)) {
            List<AddressCorresponGroup> ags = correspon.getToAddressCorresponGroups();
            if (ags.size() > detectedAddressIndex) {
                ags.get(detectedAddressIndex).setMode(UpdateMode.DELETE);
            }
            checkToAddress();
        } else if (AddressType.CC.getValue().equals(detectedAddressType)) {
            List<AddressCorresponGroup> ags = correspon.getCcAddressCorresponGroups();
            if (ags.size() > detectedAddressIndex) {
                ags.get(detectedAddressIndex).setMode(UpdateMode.DELETE);
            }
        } else {
            // 何もせずに終了
        }
        return null;
    }

    /**
     * 宛先の追加・編集を行う.
     * @return null
     */
    public String edit() {
        // 宛先編集
        handler.handleAction(new EditAddressAction(this));
        checkToAddress();
        return null;
    }

    /**
     * TOの情報がセットされているかどうかをチェックし、セットされていれば値をセットする.
     * <p>
     * TOが１件もセットされていない場合はnullをセットする.
     * セットされた値はTOの入力チェック用のパラメータとして使用する.
     * １件もセットされていなければnullになるため、必須チェックでエラーとなる.
     * </p>
     */
    private void checkToAddress() {
        // Toが設定されていない場合、nullをセットしてエラーが発生するようにする
        toAddressValues = null;

        // バリデーションチェック用のパラメータをセットする
        List<AddressCorresponGroup> acgs = correspon.getToAddressCorresponGroups();
        if (acgs != null) {
            for (AddressCorresponGroup acg : acgs) {
                if (!UpdateMode.DELETE.equals(acg.getMode())) {
                    if (acg.getUsers() == null || acg.getUsers().isEmpty()) {
                        toAddressValues = null;
                        return;
                    }
                    toAddressValues = Integer.toString(acg.getUsers().size());
                }
            }
        }
    }

    /**
     * ダイアログで使用するJSON形式の情報を読み込み.
     * 実際にはフラグをtrueにしておき、非同期で再読み込みをさせる.
     * @return null
     */
    public String loadJsonValues() {
        jsonValuesLoaded = true;
        return null;
    }

    /**
     * 現在のリクエストがnextアクションによって発生した場合はtrue.
     * @return nextアクションの場合true
     */
    public boolean isNextAction() {
        return isActionInvoked("form:Next");
    }

    /**
     * コレポン入力確認画面へ遷移 validateチェック.
     *
     * @return 遷移先画面ID
     */
    public String next() {
        // File一時保存
        if (!handler.handleAction(new AttachmentUploadAction(this))) {
            return null;
        }
        if (!handler.handleAction(new CorresponValueSetupAction(this))) {
            return null;
        }

        // 一時Update後、削除フラグを戻す
        for (int i = 0; i < this.attachmentDeletedList.size(); i++) {
            this.attachmentDeletedList.set(i, Boolean.FALSE);
        }

        // Validateチェック
        if (!handler.handleAction(new NextAction(this))) {
            return null;
        }
        setTransferNext(true);
        transferBackPage();
        return toUrl(String.format("corresponConfirmation?newEdit=%s",
                            newEdit != null ? newEdit : ""));
    }

    public String uploadingException() {
        handler.handleAction(new UploadingExceptionAction(this));
        return null;
    }

    public String importingException() {
        handler.handleAction(new ImportingExceptionAction(this));
        return null;
    }

    /**
     * 指定された添付ファイルをダウンロードする.
     * @return 遷移先. 常にnull
     */
    public String download() {
        if (handler.handleAction(new CorresponValueSetupAction(this))) {
            handler.handleAction(new AttachmentDownloadAction(this));
        }
        return null;
    }

    /**
     * Distribution templateを選択・設定する.
     * @return null
     */
    public String applyDistributionTemplate() {
        handler.handleAction(new ApplyDistributionTemplateAction(this));
        return null;
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. クエリパラメータで指定されたIDに対応する、コレポン文書を表示する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
        checkToAddress();
    }

    /**
     * カスタムフィールドのサイズを取得する.
     * @return カスタムフィールドのサイズ
     */
    public int getCustomFieldSize() {
        return CUSTOM_FIELDS_SIZE;
    }

    /**
     * 添付ファイル欄のサイズを取得する.
     * @return 添付ファイル欄のサイズ
     */
    public int getAttachmentSize() {
        return ATTACHMENTS_SIZE;
    }

    public void preValidate(ComponentSystemEvent e) {
        setJsonValuesLoaded(false);
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -9114746457687637142L;
        /** アクション発生元ページ. */
        private CorresponEditPage page;
        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CorresponEditPage page) {
            super(page);
            this.page = page;
        }
        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            String projectId = page.getCurrentProjectId();
            if (StringUtils.isEmpty(projectId)) {
                throw new ServiceAbortException(
                    "Project ID is not specified.", MessageCode.E_INVALID_PARAMETER);
            }
            page.initializer.initialize(page);
            page.elemControl.setUp(page);

            page.setInitialDisplaySuccess(true);
        }
    }

    /**
     * 宛先情報の追加・編集を行う.
     * @author opentone
     */
    static class EditAddressAction extends AbstractCorresponEditPageAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 7396367429509429866L;
        /** 入力画面. */
        private CorresponEditPage page;

        public EditAddressAction(CorresponEditPage page) {
            super(page);
            this.page = page;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            if (AddressType.TO.getValue().equals(page.detectedAddressType)) {
                editAddress(page.correspon.getToAddressCorresponGroups(), true);
            } else if (AddressType.CC.getValue().equals(page.detectedAddressType)) {
                editAddress(page.correspon.getCcAddressCorresponGroups(), false);
            } else {
                // 何もせずに終了
            }
        }

        private boolean isGroupSelected(Long groupId) {
            return groupId > 0L;
        }

        private AddressUser createAddressUser(String empNo, boolean to)
                throws ServiceAbortException {
            AddressUser au = new AddressUser();
            au.setAddressUserType(to ? AddressUserType.ATTENTION : AddressUserType.NORMAL_USER);
            au.setUser(page.getUserService().findByEmpNo(empNo));

            return au;
        }

        private List<AddressUser> createInputAddressUsers(boolean to) throws ServiceAbortException {
            List<AddressUser> result = new ArrayList<AddressUser>();
            if (StringUtils.isNotEmpty(page.getAddressUserValues())) {
                for (String val : page.getAddressUserValues().split(DELIM_ADDRESS_EMP_NO)) {
                    String empNo = val.substring(0, val.indexOf(DELIM_ADDRESS_GROUP_USER));
                    AddressUser au = createAddressUser(empNo, to);
                    if (findAddressUser(result, au) == null) {
                        result.add(au);
                    }
                }
            }
            return result;
        }

        private void editAddressUser(AddressCorresponGroup ag, boolean to)
                throws ServiceAbortException {
            List<AddressUser> input = createInputAddressUsers(to);
            ag.setUsers(new ArrayList<AddressUser>());
            for (AddressUser au : input) {
                if (!ag.containsUser(au.getUser().getEmpNo())) {
                    ag.addUser(au);
                }
            }
        }

        private List<AddressCorresponGroup> createInputAddressCorresponGroupByEachUser(boolean to)
                throws ServiceAbortException {
            // 順序を保持するためLinkedHashMapのインスタンスを利用します
            Map<Long, AddressCorresponGroup> groups =
                new LinkedHashMap<Long, AddressCorresponGroup>();
            if (StringUtils.isNotEmpty(page.getAddressUserValues())) {
                for (String val : page.getAddressUserValues().split(DELIM_ADDRESS_EMP_NO)) {
                    String[] empNoAndGroupId = val.split(DELIM_ADDRESS_GROUP_USER);
                    String empNo = empNoAndGroupId[0];
                    if (StringUtils.isEmpty(empNoAndGroupId[1])) {
                        continue;
                    }

                    Long groupId = Long.valueOf(empNoAndGroupId[1]);
                    if (groupId > 0L) {
                        AddressCorresponGroup ag = groups.get(groupId);
                        if (ag == null) {
                            ag = createAddressCorresponGroup(groupId, to);
                            groups.put(groupId, ag);
                        }
                        if (!ag.containsUser(empNo)) {
                            ag.addUser(createAddressUser(empNo, to));
                        }
                    }
                }
            }
            return new ArrayList<AddressCorresponGroup>(groups.values());
        }

        private List<AddressCorresponGroup> createInputAddressCorresponGroup(boolean to)
                throws ServiceAbortException {
            List<AddressCorresponGroup> result = new ArrayList<AddressCorresponGroup>();
            Long groupId = page.getCorresponGroupId();
            if (isGroupSelected(groupId)) {
                AddressCorresponGroup ag = createAddressCorresponGroup(groupId, to);
                result.add(ag);
                ag.setUsers(createInputAddressUsers(to));
            } else {
                result.addAll(createInputAddressCorresponGroupByEachUser(to));
            }

            return result;
        }

        private AddressCorresponGroup findAddressCorresponGroup(
                List<AddressCorresponGroup> current, AddressCorresponGroup condition) {
            final Long id = condition.getCorresponGroup().getId();
            return (AddressCorresponGroup) CollectionUtils.find(current, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    AddressCorresponGroup g = (AddressCorresponGroup) object;
                    return g.getCorresponGroup() != null
                        && g.getCorresponGroup().getId().equals(id);
                }
            });
        }

        private AddressUser findAddressUser(List<AddressUser> current, AddressUser condition) {
            final String empNo = condition.getUser().getEmpNo();
            return (AddressUser) CollectionUtils.find(current, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    User u = ((AddressUser) object).getUser();
                    return u != null && u.getEmpNo().equals(empNo);
                }
            });
        }

        private List<AddressUser> mergeAddressUser(
                List<AddressUser> current, List<AddressUser> input) {
            List<AddressUser> result = new ArrayList<AddressUser>(current);
            for (AddressUser inputUser : input) {
                if (findAddressUser(current, inputUser) == null) {
                    result.add(inputUser);
                }
            }
            return result;
        }

        private void addAddressCorresponGroups(List<AddressCorresponGroup> current, boolean to)
                throws ServiceAbortException {
            List<AddressCorresponGroup> masterList = page.correspon.getAddressCorresponGroups();
            if (masterList == null) {
                masterList = new ArrayList<AddressCorresponGroup>();
                page.correspon.setAddressCorresponGroups(masterList);
            }

            List<AddressCorresponGroup> input = createInputAddressCorresponGroup(to);
            for (final AddressCorresponGroup inputGroup : input) {
                AddressCorresponGroup ag = findAddressCorresponGroup(current, inputGroup);
                // 宛先にGroupが存在しなければ新たに追加
                // 存在していれば既存の宛先にユーザーを追加
                if (ag == null) {
                    masterList.add(inputGroup);
                } else {
                    ag.setUsers(mergeAddressUser(ag.getUsers(), inputGroup.getUsers()));
                    // AddressCorresponGroup新規作成後、そこにユーザーを追加する際に
                    // 新規Corresponの場合はModeがUpdateにならないようにしなくてはならない。
                    // そのため、ここで新規Corresponかどうかを判定する。
                    if (!page.correspon.isNew()) {
                        // 更新Corresponは、AddressCorresponGroupが新規のものを除外して
                        // ModeをUpdateにする。
                        if (!ag.isNew()) {
                            ag.setMode(UpdateMode.UPDATE);
                        }
                    }
                }
            }
        }

        /**
         * 指定行の宛先の編集を行う.
         * 行が指定されていない場合は追加となる.
         * 同じグループが宛先に追加済みの場合は、まとめて表示する.
         *
         * @param groups 宛先-活動単位リスト(TOのみ、CCのみのリストが渡される)
         * @param to 対象がTOの場合はtrue
         */
        private void editAddress(List<AddressCorresponGroup> groups, boolean to)
                throws ServiceAbortException {
            if (page.detectedAddressIndex != -1) {
                // 行が特定されている場合は編集を行う
                editAddressCorresponGroup(groups, to);
            } else {
                // 行が特定されていない場合は追加として扱う
                addAddressCorresponGroups(groups, to);
            }
        }

        /**
         * @param groups
         * @param to
         * @throws ServiceAbortException
         */
        private void editAddressCorresponGroup(
                List<AddressCorresponGroup> groups, boolean to)
                throws ServiceAbortException {
            if (groups.size() > page.detectedAddressIndex) {
                AddressCorresponGroup group = groups.get(page.detectedAddressIndex);
                group.setCorresponGroup(page.corresponGroupService.find(page.corresponGroupId));

                // 新規に追加されたものは更新にはしない
                if (!group.getMode().equals(UpdateMode.NEW)) {
                    group.setMode(UpdateMode.UPDATE);
                }
                editAddressUser(group, to);
            }
        }
    }

    /**
     * 入力値をコレポン文書オブジェクトに設定する.
     * @author opentone
     */
    static class CorresponValueSetupAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 9006595350487746107L;
        /** 入力画面. */
        private CorresponEditPage page;

        public CorresponValueSetupAction(CorresponEditPage page) {
            super(page);
            this.page = page;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.setupCorrespon();
        }
    }

    /**
     * 入力を検証する.
     *
     * @author opentone
     */
    static class NextAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 4250549676989431952L;
        /** 入力画面. */
        private CorresponEditPage page;

        /**
         * ページを指定してインスタンスを生成する.
         * @param page このアクションを起動したページ
         */
        public NextAction(CorresponEditPage page) {
            super(page);
            this.page = page;
        }
        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // 宛先設定状況を更新
            page.checkToAddress();
            page.corresponValidateService.validateToAddress(page.toAddressValues);

            page.corresponValidateService.validate(page.correspon);
            page.setNextPageMessage(ApplicationMessageCode.CONTENT_WILL_BE_SAVED);
        }
    }


    /**
     * Distribution templateを設定する.
     * @author opentone
     */
    static class ApplyDistributionTemplateAction
        extends AbstractCorresponEditPageAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -7470301706231198875L;
        /** 入力画面. */
        private CorresponEditPage page;

        public ApplyDistributionTemplateAction(CorresponEditPage page) {
            super(page);
            this.page = page;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // 現在のすべての活動単位をDelete扱いとする.
            deleteAddress(page.correspon.getToAddressCorresponGroups());
            deleteAddress(page.correspon.getCcAddressCorresponGroups());
            // Distributionの内容を選択する.
            Long headerId = page.getDistributionTemplate();
            DistTemplateHeader header = page.distTemplateService.find(headerId);
            setAddressCorresponGroup(header);
        }

        private void setAddressCorresponGroup(DistTemplateHeader header)
            throws ServiceAbortException {
            if (null != header.getToDistTemplateGroups()) {
                int minUserSize = Integer.MAX_VALUE;
                for (DistTemplateGroup group : header.getToDistTemplateGroups()) {
                    AddressCorresponGroup g
                        = createAddressCorresponGroup(group.getGroupId(), true);
                    setUser(g, group, true);
                    addAddressCorresponGroup(g);
                    minUserSize = Math.min(minUserSize, g.getUsers().size());
                }
                String value = (0 < minUserSize) ? String.format("%d", minUserSize) : null;
                page.setToAddressValues(value);
            }
            if (null != header.getCcDistTemplateGroups()) {
                for (DistTemplateGroup group : header.getCcDistTemplateGroups()) {
                    AddressCorresponGroup g
                        = createAddressCorresponGroup(group.getGroupId(), false);
                    setUser(g, group, false);
                    addAddressCorresponGroup(g);
                }
            }
        }

        private void setUser(AddressCorresponGroup addressGroup,
                               DistTemplateGroup distGroup,
                               boolean to) throws ServiceAbortException {
            if (null != distGroup.getUsers()) {
                List<AddressUser> userList
                    = new ArrayList<AddressUser>(distGroup.getUsers().size());
                for (DistTemplateUser templateUser : distGroup.getUsers()) {
                    AddressUser user = new AddressUser();
                    user.setAddressUserType(
                        to ? AddressUserType.ATTENTION : AddressUserType.NORMAL_USER);
                    user.setUser(page.getUserService().findByEmpNo(templateUser.getEmpNo()));
                    userList.add(user);
                }
                addressGroup.setUsers(userList);
            }
        }

        private void addAddressCorresponGroup(AddressCorresponGroup group) {
            List<AddressCorresponGroup> acgs = page.correspon.getAddressCorresponGroups();
            if (acgs == null) {
                acgs = new ArrayList<AddressCorresponGroup>();
                page.correspon.setAddressCorresponGroups(acgs);
            }
            acgs.add(group);
        }
        /**
         * 全てのグループをDELETEモードとする.
         * @param groups 画面上の活動単位リスト.
         */
        private void deleteAddress(List<AddressCorresponGroup> groups) {
            if (null != groups) {
                for (AddressCorresponGroup group : groups) {
                    group.setMode(UpdateMode.DELETE);
                }
            }
        }
    }

    /**
     * ファイルアップロードに失敗した際、意図的にエラーは発生させる.
     * @author opentone
     *
     * <p>
     * $Date: 2013-09-17 13:29:06 +0900 (火, 17  9 2013) $
     * $Rev: 5481 $
     * $Author: aoyagi $
     */
    static class UploadingExceptionAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -5299883299634618948L;

        /**
         * ページを指定してインスタンスを生成する.
         * @param page このアクションを起動したページ
         */
        public UploadingExceptionAction(CorresponEditPage page) {
            super(page);
        }
        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            throw new ServiceAbortException(ApplicationMessageCode.ERROR_UPLOADING_FILE);
        }
    }


    /**
     * ファイルアップロードに失敗した際、意図的にエラーは発生させる.
     * @author opentone
     */
    static class ImportingExceptionAction extends AbstractAction {

        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 6865407295689889398L;

        /**
         * ページを指定してインスタンスを生成する.
         * @param page このアクションを起動したページ
         */
        public ImportingExceptionAction(CorresponEditPage page) {
            super(page);
        }
        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            throw new ServiceAbortException(ApplicationMessageCode.ERROR_UPLOADING_IMPORT_FILE);
        }
    }
}
