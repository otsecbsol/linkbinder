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
package jp.co.opentone.bsol.framework.test.dataset.bean;

import java.util.Date;

import org.dbunit.dataset.datatype.DataType;

/**
 * クラスからデータベースの型を決定する.
 * @author opentone
 */
public class BeanDataTypeResolver {

    /**
     * clazzに対応するデータベースの型を返す.
     * @param clazz class
     * @return データベースの型
     */
    public DataType relosveDataType(Class<?> clazz) {
        if (String.class == clazz) {
            return DataType.VARCHAR;
        } else if (Integer.class == clazz) {
            return DataType.NUMERIC;
        } else if (Long.class == clazz) {
            return DataType.NUMERIC;
        } else if (Date.class == clazz) {
            return DataType.TIMESTAMP;
        }
        return null;
    }
}
