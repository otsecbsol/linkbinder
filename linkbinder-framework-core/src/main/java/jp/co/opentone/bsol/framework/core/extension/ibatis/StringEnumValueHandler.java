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
package jp.co.opentone.bsol.framework.core.extension.ibatis;

import java.sql.SQLException;
import java.sql.Types;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * Enum型(コード値:文字列)とテーブルの列(VARCHAR)を変換する.
 * @author opentone
 */
public abstract class StringEnumValueHandler implements TypeHandlerCallback {

    /**
     * このオブジェクトがサポートする値を返す.
     * @return 値の配列
     */
    public abstract StringEnumValue[] getValues();

    /*
     * (non-Javadoc)
     * @see
     * com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#getResult(com
     * .ibatis.sqlmap.client.extensions.ResultGetter)
     */
    @Override
    public Object getResult(ResultGetter getter) throws SQLException {
        String value = getter.getString();
        if (getter.wasNull()) {
            return null;
        }

        for (StringEnumValue val : getValues()) {
            if (val.getValue().equals(value)) {
                return val;
            }
        }
        throw new UnsupportedOperationException("no such value " + value);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#setParameter(
     * com.ibatis.sqlmap.client.extensions.ParameterSetter, java.lang.Object)
     */
    @Override
    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
        if (parameter == null) {
            setter.setNull(Types.VARCHAR);
        } else {
            StringEnumValue val = (StringEnumValue) parameter;
            setter.setString(val.getValue());
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#valueOf(java.
     * lang.String)
     */
    @Override
    public Object valueOf(String s) {
        return s;
    }
}
