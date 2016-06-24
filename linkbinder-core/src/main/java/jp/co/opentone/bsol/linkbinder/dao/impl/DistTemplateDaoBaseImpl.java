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

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;

import jp.co.opentone.bsol.framework.core.dao.Dao;
import jp.co.opentone.bsol.framework.core.dao.DataSourceSelector;

/**
 * DistributionテンプレートDaoの基底クラス.
 *
 * @author opentone
 *
 */
public abstract class DistTemplateDaoBaseImpl
    extends SqlMapClientDaoSupport implements Dao, Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7505405648895089860L;

    /**
     * Logger.
     */
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 実行環境により利用するDataSourceを切り換えるためのオブジェクト.
     */
    @Resource
    private DataSourceSelector selector;

    /**
     * 初期化.
     */
    @PostConstruct
    public void initialize() {
        initDataSource();
    }

    private void initDataSource() {
        selectDataSource(selector);
    }

    /**
     * 実行環境毎の適切なデータソースを選択し このオブジェクトに保持します.
     * @param dataSourceSelector
     *            データソース切り替えオブジェクト
     */
    public void selectDataSource(DataSourceSelector dataSourceSelector) {
        super.setDataSource(dataSourceSelector.select());
    }

    /**
     * SqlMapClientを設定する. 親クラスであるSqlMapClientDaoSupportはSqlMapClientが必須なので
     * このメソッド内で親クラスのプロパティを設定します.
     * @param sqlMapClient
     *            SqlMapClient
     */
    @Resource
    public final void setMySqlMapClient(SqlMapClient sqlMapClient) {
        super.setSqlMapClient(sqlMapClient);
    }
}
