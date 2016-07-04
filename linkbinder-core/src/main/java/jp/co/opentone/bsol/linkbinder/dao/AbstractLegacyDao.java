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

import jp.co.opentone.bsol.framework.core.dao.LegacyEntity;
import jp.co.opentone.bsol.framework.core.dao.LegacyGenericDao;

/**
 * 旧システムのテーブルを操作するDaoの親クラス.
 *
 * @author opentone
 *
 */
public abstract class AbstractLegacyDao<T extends LegacyEntity> extends BaseDao implements
        LegacyGenericDao<T> {

    /**
     * SerialVersionID.
     */
    private static final long serialVersionUID = 7464251403885883960L;
    /**
     * SQLID: 1件取得.
     */
    public static final String SQL_ID_FIND_BY_ID = "findById";

    /**
     * SQL定義ファイルの名前空間を指定してインスタンス化する.
     * @param namespace
     *            SQL定義ファイルのnamespace
     */
    public AbstractLegacyDao(String namespace) {
        super(namespace);
    }
}
