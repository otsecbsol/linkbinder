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
package jp.co.opentone.bsol.linkbinder.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponDao;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupSummary;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.CorresponUserSummary;
import jp.co.opentone.bsol.linkbinder.dto.RSSCorrespon;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponUserSummaryCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchRSSCorresponCondition;

/**
 * コレポン文書を操作するDao.
 * @author opentone
 *
 */
@Repository
public class CorresponDaoImpl extends AbstractDao<Correspon> implements CorresponDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "correspon";

    /**
     * SQLID: リスト取得.
     */
    private static final String SQL_FIND = "find";

    /**
     * SQLID: 件数取得.
     */
    private static final String SQL_COUNT = "count";

    /**
     * SQLID: 最上位の親コレポン文書取得.
     */
    private static final String SQL_FIND_TOP_PARENT = "findTopParent";

    /**
     * SQLID: コレポン文書種別と活動単位でのサマリ情報取得.
     */
    private static final String SQL_FIND_CORRESPON_GROUP_SUMMARY = "findCorresponGroupSummary";

    /**
     * SQLID: ユーザー情報でのサマリ情報取得.
     */
    private static final String SQL_FIND_CORRESPON_USER_SUMMARY = "findCorresponUserSummary";

    /**
     * SQLID: 指定したカスタムフィールドが関連付いているコレポン文書の件数を取得.
     */
    private static final String SQL_COUNT_CORRESPON_BY_CUSTOM_FIELD = "countCorresponByCustomField";
    /**
     * SQLID: 宛先-ユーザーが返信したコレポン文書を取得.
     */
    private static final String SQL_FIND_REPLY_CORRESPON_BY_ADDRESS_USER_ID =
            "findReplyCorresponByAddressUserId";
    /**
     * SQLID: 宛先-グループが返信したコレポン文書を取得.
     */
    private static final String SQL_FIND_REPLY_CORRESPON_BY_GROUP_ID =
            "findReplyCorresponByGroupId";

    /**
     * SQLID: 指定したコレポン文書種別が紐付いているコレポン文書件数を検索する.
     */
    private static final String SQL_COUNT_CORRESPON_BY_CORRESPON_TYPE =
            "countCorresponByCorresponType";

    /**
     * SQLID: RSS配信対象コレポン文書を検索する.
     */
    private static final String SQL_FIND_RSS_CORRESPON =
            "findRSSCorrespon";

    /**
     * SQLID: 大本のコレポン文書IDを検索する.
     */
    private static final String SQL_FIND_ROOT_CORRESPON_ID =
            "findRootCorresponId";

    /**
     * SQLID: 応答履歴を検索する.
     */
    private static final String SQL_FIND_CORRESPON_RESPONSE_HISTORY =
            "findCorresponResponseHistory";

    /**
     * SQLID: コレポン文書IDを検索する.
     */
    private static final String SQL_FIND_ID =
            "findId";

    /**
     * 学習用プロジェクトへ学習用文書を登録する.
     */
    private static final String SQL_INSERT_LEARNING =
            "copyToLearningProject";

    /**
     * 前方一致検索を行うフィールド名.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("corresponNo");
        FIELDS.add("customFieldValue");
    }

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.AbstractDao#find(jp.co.opentone.bsol.linkbinder.dto.
     * SearchCorresponCondition)
     */
    @SuppressWarnings("unchecked")
    public List<Correspon> find(SearchCorresponCondition condition) {
        // 前方一致検索を行う
        SearchCorresponCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        int skipResults = condition.getSkipNum();
        int maxResults = condition.getPageRowNum();
        return (List<Correspon>) getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND),
            likeCondition,
            skipResults,
            maxResults);
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.AbstractDao#count(jp.co.opentone.bsol.linkbinder.dto.
     * SearchCorresponCondition)
     */
    public int count(SearchCorresponCondition condition) {
        // 前方一致検索を行う
        SearchCorresponCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject(getSqlId(SQL_COUNT),
            likeCondition).toString());
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponDao#findTopParent(java.lang.Long)
     */
    public Correspon findTopParent(Long id) throws RecordNotFoundException {
        Correspon record =
                (Correspon) getSqlMapClientTemplate().queryForObject(getSqlId(SQL_FIND_TOP_PARENT),
                    id);

        if (record == null) {
            throw new RecordNotFoundException(id.toString());
        }
        return record;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponDao
     * #findCorresponGroupSummary(java.lang.String,
     * jp.co.opentone.bsol.linkbinder.dto.CorresponGroup[])
     */
    @SuppressWarnings("unchecked")
    public List<CorresponGroupSummary> findCorresponGroupSummary(String projectId,
        CorresponGroup[] userGroups) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("projectId", projectId);
        map.put("userGroups", userGroups);
        map.put("to", AddressType.TO);
        map.put("cc", AddressType.CC);
        map.put("issued", WorkflowStatus.ISSUED);
        return (List<CorresponGroupSummary>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_CORRESPON_GROUP_SUMMARY), map);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponDao#findCorresponUserSummary(
     * jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponUserSummaryCondition)
     */
    @SuppressWarnings("unchecked")
    public CorresponUserSummary findCorresponUserSummary(
        SearchCorresponUserSummaryCondition condition) {
        List<CorresponUserSummary> result =
            (List<CorresponUserSummary>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_CORRESPON_USER_SUMMARY), condition);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return new CorresponUserSummary();
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CorresponDao#countCorresponByCustomField(java.lang
     * .Long)
     */
    public int countCorresponByCustomField(Long projectCustomFieldId) {
        return Integer.parseInt(getSqlMapClientTemplate()
            .queryForObject(getSqlId(SQL_COUNT_CORRESPON_BY_CUSTOM_FIELD), projectCustomFieldId)
            .toString());
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CorresponDao#findReplyCorresponByAddressUserId(java
     * .lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<Correspon> findReplyCorresponByAddressUserId(Long addressUserId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("addressUserId", addressUserId);
        param.put("workflowStatus", WorkflowStatus.ISSUED);

        return (List<Correspon>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_REPLY_CORRESPON_BY_ADDRESS_USER_ID), param);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CorresponDao#findReplyCorresponByGroupId(java
     * .lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<Correspon> findReplyCorresponByGroupId(Long corresponId, Long groupId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("groupId", groupId);
        param.put("workflowStatus", WorkflowStatus.ISSUED);
        param.put("corresponId", corresponId);

        return (List<Correspon>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_REPLY_CORRESPON_BY_GROUP_ID), param);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CorresponDao#countCorresponByCorresponType(java.
     * lang.Long)
     */
    public int countCorresponByCorresponType(Long projectCorresponTypeId) {
        return Integer
            .parseInt(getSqlMapClientTemplate()
                .queryForObject(getSqlId(SQL_COUNT_CORRESPON_BY_CORRESPON_TYPE),
                    projectCorresponTypeId).toString());
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponDao#findRSSCorrespon(java.lang.String, java.util.Date)
     */
    @SuppressWarnings("unchecked")
    public List<RSSCorrespon> findRSSCorrespon(SearchRSSCorresponCondition condition) {
        return (List<RSSCorrespon>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_RSS_CORRESPON), condition);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponDao#findRootCorresponId(corresponId)
     */
    public Long findRootCorresponId(Long corresponId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("corresponId", corresponId);
        return Long.parseLong(getSqlMapClientTemplate()
            .queryForObject(getSqlId(SQL_FIND_ROOT_CORRESPON_ID), param).toString());
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponDao#findCorresponResponseHistory(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<CorresponResponseHistory> findCorresponResponseHistory(
        Long corresponId, Long currentCorresponId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("corresponId", corresponId);
        param.put("currentCorresponId", currentCorresponId);
        param.put("workflowStatus", WorkflowStatus.ISSUED);
        param.put("addressType", AddressType.TO);
        return (List<CorresponResponseHistory>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_CORRESPON_RESPONSE_HISTORY), param);
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.AbstractDao#findId(jp.co.opentone.bsol.linkbinder.dto.
     * SearchCorresponCondition)
     */
    @SuppressWarnings("unchecked")
    public List<Long> findId(SearchCorresponCondition condition) {
        // 前方一致検索を行う
        SearchCorresponCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        return (List<Long>) getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND_ID),
            likeCondition);
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponDao#findIdInPage(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Long> findIdInPage(SearchCorresponCondition condition) {
        // 前方一致検索を行う
        SearchCorresponCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        int skipResults = condition.getSkipNum();
        int maxResults = condition.getPageRowNum();
        return (List<Long>)getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND_ID),
            likeCondition,
            skipResults,
            maxResults);
    }

    @Override
    public Long insertLearningCorrespon(Correspon correspon) {
        return (Long) getSqlMapClientTemplate().insert(getSqlId(SQL_INSERT_LEARNING), correspon);
    }

    /**
     * 親コレポン文書に紐付く子文書を更新するためのパラメータークラス.
     * @author opentone
     */
    public static class UpdateChildrenCorresponStatusParameter {
        /** 親コレポン文書. */
        //CHECKSTYLE:OFF
        final Correspon correspon;
        //CHECKSTYLE:ON
        /**
         * 親コレポン文書を指定してインスタンス化する.
         * @param correspon 親コレポン文書
         */
        public UpdateChildrenCorresponStatusParameter(Correspon correspon) {
            this.correspon = correspon;
        }

        /**
         * 親コレポン文書のIDを返す.
         * @return ID
         */
        public Long getId() {
            return correspon.getId();
        }

        /**
         * 更新者を返す.
         * @return 更新者
         */
        public User getUpdatedBy() {
            return correspon.getUpdatedBy();
        }

        /**
         * コレポン文書状態を返す.
         * @return コレポン文書状態
         */
        public CorresponStatus getCorresponStatus() {
            return correspon.getCorresponStatus();
        }

        /**
         * 承認状態[Issued]の値を返す.
         * @return 承認状態
         */
        public WorkflowStatus getIssued() {
            return WorkflowStatus.ISSUED;
        }
    }
}
