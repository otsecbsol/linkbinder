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

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.dao.VersioningEntity;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.framework.core.util.JSONUtil;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import net.arnx.jsonic.JSONException;

/**
 * テーブル [favorite_filter] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class FavoriteFilter extends AbstractDto implements VersioningEntity {

    /**
     * SerialVerseionUID.
     */
    private static final long serialVersionUID = 7144104644395112311L;
    /**
     * id.
     * <p>
     * [favorite_filter.id]
     * </p>
     */
    private Long id;
    /**
     * project id.
     * <p>
     * [favorite_filter.project_id]
     * </p>
     */
    private String projectId;
    /**
     * User.
     * <p>
     * </p>
     */
    private User user;
    /**
     * favorite name.
     * <p>
     * [favorite_filter.favorite_name]
     * </p>
     */
    private String favoriteName;
//    /**
//     * Search conditions.
//     * <p>
//     * </p>
//     */
//    private Map<String, String> searchConditions;
//
//    /**
//     * Search conditions as String case for database value.
//     * <p>
//     * [favorite_filter.search_conditions]
//     * </p>
//     */
//    private String searchConditionsAsString;
      /**
      * Search conditions as String case for database value.
      * <p>
      * [favorite_filter.search_conditions]
      * </p>
      */
     private String searchConditions;

    /**
     * Created by.
     * <p>
     * [favorite_filter.created_by]
     * </p>
     */
    private User createdBy;
    /**
     * Created at.
     * <p>
     * [favorite_filter.created_at]
     * </p>
     */
    private Date createdAt;
    /**
     * Updated by.
     * <p>
     * [favorite_filter.updated_by]
     * </p>
     */
    private User updatedBy;
    /**
     * Updated at.
     * <p>
     * [favorite_filter.updated_at]
     * </p>
     */
    private Date updatedAt;
    /**
     * Version no.
     * <p>
     * [favorite_filter.version_no]
     * </p>
     */
    private Long versionNo;
    /**
     * Delete no.
     * <p>
     * [favorite_filter.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * 空のインスタンス生成.
     */
    public FavoriteFilter() {
    }

    /**
     * @return the projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the favoriteName
     */
    public String getFavoriteName() {
        return favoriteName;
    }

    /**
     * @param favoriteName the favoriteName to set
     */
    public void setFavoriteName(String favoriteName) {
        this.favoriteName = favoriteName;
    }

     /**
      * @return the searchConditions
      */
     public String getSearchConditions() {
         return searchConditions;
     }
     /**
      * @param conditions the searchConditions to set
      */
     public void setSearchConditions(String conditions) {
         this.searchConditions = conditions;
     }

//    /**
//     * @return the searchConditions
//     */
//    public Map<String, String> getSearchConditions() {
//        return searchConditions;
//    }
//    /**
//     * @param conditions the searchConditions to set
//     */
//    public void setSearchConditions(Map<String, String> conditions) {
//        this.searchConditions = conditions;
//        // searchConditionsAsString と同期をとる
//        this.searchConditionsAsString = convertToString(conditions);
//    }
//
//    /**
//     * @return the searchConditions
//     */
//    public String getSearchConditionsAsString() {
//        return searchConditionsAsString;
//    }
//
//    /**
//     * @param conditions the searchConditions to set
//     */
//    public void setSearchConditionsAsString(String conditions) {
//        this.searchConditionsAsString = conditions;
//        // searchConditions と同期をとる
//        this.searchConditions = convertToMap(conditions);
//    }
//    Map<String, String> convertToMap(String str) {
//        if (StringUtils.isEmpty(str)) {
//            return null;
//        }
//        // searchConditionsAsString をMap変換する。
//        // " key1:value1, key2:value2, ・・・ " 形式を想定。想定外の場合、NULLを返却.
//        // key がNULLの場合は想定しない。不整合なデータが生成されるkamo。
//        Map<String, String> result = new HashMap<String, String>();
//        String[] split1 = StringUtils.split(str, ",");
//        for (String element : split1) {
//            String[] split2 = StringUtils.split(element, ":");
//            switch (split2.length) {
//            case 1:
//                result.put(split2[0], null);
//                break;
//            case 2:
//                result.put(split2[0], split2[1]);
//                break;
//            default:
//            }
//        }
//        return result;
//    }
//    String convertToString(Map<String, String> map) {
//        if (map == null || map.isEmpty()) {
//            return null;
//        }
//        StringBuffer result = new StringBuffer();
//        // searchConditions をString変換する。
//        // " key1:value1, key2:value2, ・・・ " 形式を想定。想定外の場合、NULLを返却.
//        // key がNULLの場合は想定しない。不整合なデータが生成されるkamo。
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            if (!StringUtils.isEmpty(result.toString())) {
//                result.append(", ");
//            }
//            result.append(entry.getKey()).append(":").append(entry.getValue());
//        }
//        return result.toString();
//    }

    /**
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = CloneUtil.cloneDate(createdAt);
    }

    /**
     * @return the updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @return the updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * @param updatedAt the updatedAt to set
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = CloneUtil.cloneDate(updatedAt);
    }

    /**
     * @return the deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * @param deleteNo the deleteNo to set
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param versionNo the versionNo to set
     */
    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

    /* (non-Javadoc)
     * @see Entity#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see VersioningEntity#getVersionNo()
     */
    @Override
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * 検索条件をJSON形式で取得する.
     * @return JSON形式の検索条件（SearchConitions）。検索条件が空の場合、NULLを返却する。
     */
    public SearchCorresponCondition getSearchConditionsAsObject() {
        if (StringUtils.isEmpty(this.searchConditions)) {
            return null;
        }
        try {
            return JSONUtil.decode(this.searchConditions, SearchCorresponCondition.class);
        } catch (JSONException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }
    /**
     * JSON形式の条件をセットする.
     * @param corresponCondition JSON形式の文字列
     */
    public void setSearchConditionsToJson(SearchCorresponCondition corresponCondition) {
        try {
            this.searchConditions = JSONUtil.encode(corresponCondition);
        } catch (JSONException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    /**
     * 新規登録・更新の判定.
     * @return true ： 新規登録<br>false ： 更新
     */
    public boolean isNew() {
        return (getId() == null || getId() == 0);
    }
}
