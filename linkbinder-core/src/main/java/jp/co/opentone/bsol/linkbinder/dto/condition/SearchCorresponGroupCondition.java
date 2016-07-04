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
package jp.co.opentone.bsol.linkbinder.dto.condition;

import java.util.Date;

import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.framework.core.util.SQLConvertUtil;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * 活動単位の検索条件を保持する.
 *
 * @author opentone
 *
 */
public class SearchCorresponGroupCondition extends AbstractCondition {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 3495834974465067255L;

    /**
     * デフォルトソート列.
     */
    private static final String DEFAULT_SORT_COLUMN = "name";

    /**
     * 活動単位ID.
     */
    private Long id;

    /**
     * プロジェクトID.
     */
    private String projectId;

    /**
     * プロジェクト名(英語).
     */
    private String projectNameE;

    /**
     * 拠点ID.
     */
    private Long siteId;

    /**
     * 拠点コード.
     */
    private Long siteCd;

    /**
     * 拠点名.
     */
    private String siteName;

    /**
     * 部門ID.
     */
    private Long disciplineId;

    /**
     * 部門コード.
     */
    private Long disciplineCd;

    /**
     * 部門名.
     */
    private Long disciplineName;

    /**
     * 活動単位名.
     */
    private String name;

    /**
     * 作成者従業員番号.
     */
    private User createdBy;

    /**
     * 作成日時.
     */
    private Date createdAt;

    /**
     * 更新者従業員番号.
     */
    private User updatedBy;

    /**
     * 更新日時.
     */
    private Date updatedAt;

    /**
     * バージョンNo.
     */
    private Long versionNo;

    /**
     * 削除No.
     */
    private Long deleteNo;

    /**
     * SystemAdminフラグ.
     */
    private boolean systemAdmin;

    /**
     * ProjectAdminフラグ.
     */
    private boolean projectAdmin;

    /**
     * GroupAdminフラグ.
     */
    private boolean groupAdmin;

    /**
     * 検索するユーザー.
     */
    private User searchUser;

    /**
     * ソート列.
     */
    private String sortColumn = DEFAULT_SORT_COLUMN;

    /**
     * 空のインスタンスを生成する.
     */
    public SearchCorresponGroupCondition() {
    }

    /**
     * 活動単位IDを取得する.
     * @return 活動単位ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 活動単位IDをセットする.
     * @param id 活動単位ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * プロジェクトIDを取得する.
     * @return プロジェクトID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * プロジェクトIDをセットする.
     * @param projectId プロジェクトID
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * プロジェクト名（英語）を取得する.
     * @return プロジェクト名(英語)
     */
    public String getProjectNameE() {
        return projectNameE;
    }

    /**
     * プロジェクト名(英語)をセットする.
     * @param projectNameE プロジェクト名(英語)
     */
    public void setProjectNameE(String projectNameE) {
        this.projectNameE = projectNameE;
    }

    /**
     * 拠点IDを取得する.
     * @return 拠点ID
     */
    public Long getSiteId() {
        return siteId;
    }

    /**
     * 拠点IDをセットする.
     * @param siteId 拠点ID
     */
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    /**
     * 拠点コードを取得する.
     * @return 拠点コード
     */
    public Long getSiteCd() {
        return siteCd;
    }

    /**
     * 拠点コードをセットする.
     * @param siteCd 拠点コード
     */
    public void setSiteCd(Long siteCd) {
        this.siteCd = siteCd;
    }

    /**
     * 拠点名を取得する.
     * @return 拠点名
     */
    public String getSiteName() {
        return siteName;
    }

    /**
     * 拠点名をセットする.
     * @param siteName 拠点名
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    /**
     * 部門IDを取得する.
     * @return 部門ID
     */
    public Long getDisciplineId() {
        return disciplineId;
    }

    /**
     * 部門IDをセットする.
     * @param disciplineId 部門ID
     */
    public void setDisciplineId(Long disciplineId) {
        this.disciplineId = disciplineId;
    }

    /**
     * 部門コードを取得する.
     * @return 部門コード
     */
    public Long getDisciplineCd() {
        return disciplineCd;
    }

    /**
     * 部門コードをセットする.
     * @param disciplineCd 部門コード
     */
    public void setDisciplineCd(Long disciplineCd) {
        this.disciplineCd = disciplineCd;
    }

    /**
     * 部門名を取得する.
     * @return 部門名
     */
    public Long getDisciplineName() {
        return disciplineName;
    }

    /**
     * 部門名をセットする.
     * @param disciplineName 部門名
     */
    public void setDisciplineName(Long disciplineName) {
        this.disciplineName = disciplineName;
    }

    /**
     * 活動単位名を取得する.
     * @return 活動単位名
     */
    public String getName() {
        return name;
    }

    /**
     * 活動単位名をセットする.
     * @param name 活動単位名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 活動単位作成者を取得する.
     * @return 活動単位作成者
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * 活動単位作成者をセットする.
     * @param createdBy 活動単位作成者
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 活動単位作成日を取得する.
     * @return 活動単位作成日
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * 活動単位作成日をセットする.
     * @param createdAt 活動単位作成日
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = CloneUtil.cloneDate(createdAt);
    }

    /**
     * 活動単位更新者を取得する.
     * @return 活動単位更新者
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 活動単位更新者をセットする.
     * @param updatedBy 活動単位更新者
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * 活動単位更新日を取得する.
     * @return 活動単位更新日
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * 活動単位更新日をセットする.
     * @param updatedAt 活動単位更新日
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = CloneUtil.cloneDate(updatedAt);
    }

    /**
     * バージョンナンバーを取得する.
     * @return バージョンナンバー
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * バージョンナンバーをセットする.
     * @param versionNo バージョンナンバー
     */
    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

    /**
     * 削除ナンバーを取得する.
     * @return 削除ナンバー
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * 削除ナンバーをセットする.
     * @param deleteNo 削除ナンバー
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * SystemAdminフラグを返す.
     * @return SystemAdminならtrue / 以外はfalse
     */
    public boolean isSystemAdmin() {
        return systemAdmin;
    }

    /**
     * SystemAdminフラグを設定する.
     * @param systemAdmin SystemAdminフラグ
     */
    public void setSystemAdmin(boolean systemAdmin) {
        this.systemAdmin = systemAdmin;
    }

    /**
     * ProjectAdminフラグを返す.
     * @return ProjectAdminならtrue / ProjectAdminではないfalse
     */
    public boolean isProjectAdmin() {
        return projectAdmin;
    }

    /**
     * ProjectAdminフラグを設定する.
     * @param projectAdmin ProjectAdminフラグ
     */
    public void setProjectAdmin(boolean projectAdmin) {
        this.projectAdmin = projectAdmin;
    }

    /**
     * GroupAdminフラグを返す.
     * @return GroupAdminならtrue / GroupAdminではないfalse
     */
    public boolean isGroupAdmin() {
        return groupAdmin;
    }

    /**
     * GroupAdminフラグを設定する.
     * @param groupAdmin GroupAdminフラグ
     */
    public void setGroupAdmin(boolean groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    /**
     * 検索するユーザーを返す.
     * @return 検索を行うユーザー
     */
    public User getSearchUser() {
        return searchUser;
    }

    /**
     * 検索するユーザーを設定する.
     * @param searchUser 検索を行うユーザー
     */
    public void setSearchUser(User searchUser) {
        this.searchUser = searchUser;
    }

    /**
     * ソート列を設定する.
     * @param sortColumn ソート列
     */
    public void setSortColumn(String sortColumn) {
        this.sortColumn = SQLConvertUtil.encode(sortColumn);
    }

    /**
     * ソート列を返す.
     * @return ソート列
     */
    public String getSortColumn() {
        return sortColumn;
    }

}
