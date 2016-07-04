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
package jp.co.opentone.bsol.linkbinder.dao;

import javax.annotation.Resource;
import javax.sql.DataSource;

import jp.co.opentone.bsol.framework.core.dao.DataSourceSelector;

/**
 * Unit Test用DataSourceSelector.
 * <p>
 * applicationContext.xmlに、id="testDataSource" で定義されたDataSourceを返す.
 * </p>
 * @author opentone
 */
public class MockDataSourceSelector implements DataSourceSelector {

    @Resource(name = "testDataSource")
    private DataSource dataSource;
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4115287909571781709L;

    public DataSource select() {
        return dataSource;
    }
}
