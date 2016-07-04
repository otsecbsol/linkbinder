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
/**
 * 固定のデータソースを返すクラス.
 * @author opentone
 */
public class FixedDataSourceSelector implements DataSourceSelector {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1189504938195184341L;

    /**
     * 本番環境用データソース.
     */
    @Resource(name = "productionDataSource")
    private transient DataSource productionDataSource;

    /*
     * (non-Javadoc)
     * @see DataSourceSelector#select()
     */
    @Override
    public DataSource select() {
        return productionDataSource;
    }
}
