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
package jp.co.opentone.bsol.framework.core.dao;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.Environment;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
/**
 * ログインユーザー名の規約によりデータソースを切り替えるクラス.
 * <p>
 * SpringのBean定義ファイルから、次の名前で定義されたデータソースを取得する.
 * <ul>
 * <li>"ZZA"で始まるユーザーIDの場合、testDataSource</li>
 * <li>"ZZB"で始まるユーザーIDの場合、demoDataSource</li>
 * <li>上記以外の場合、productionDataSource</li>
 * </ul>
 * </p>
 * @author opentone
 */
public class UserIdConventionDataSourceSelector implements DataSourceSelector {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1189504938195184341L;

    /**
     * logger.
     */
    private static final Logger log =
        LoggerFactory.getLogger(UserIdConventionDataSourceSelector.class);

    /**
     * 本番環境用データソース.
     */
    @Resource(name = "productionDataSource")
    private transient DataSource productionDataSource;
    /**
     * テスト環境用データソース.
     */
    @Resource(name = "testDataSource")
    private transient DataSource testDataSource;
    /**
     * デモ環境用データソース.
     */
    @Resource(name = "demoDataSource")
    private transient DataSource demoDataSource;

    /*
     * (non-Javadoc)
     * @see DataSourceSelector#select()
     */
    @Override
    public DataSource select() {
        switch (Environment.getEnvironment()) {
        case PRODUCTION :
            log.info("[Production] DataSource selected.");
            return productionDataSource;
        case DEMO :
            log.info("[Demo] DataSource selected.");
            return demoDataSource;
        case TEST :
            log.info("[Test] DataSource selected.");
            return testDataSource;
        default :
            throw new ApplicationFatalRuntimeException("DataSource was not selected.");
        }
    }
}
