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

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * Javaコード中で、データベースに設定するNULLを指定した際の変換を行うクラス.
 * @see DBValue
 * @author opentone
 */
public abstract class NullValueHandler implements TypeHandlerCallback {

    /*
     * (非 Javadoc)
     * @see
     * com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#getResult(com
     * .ibatis.sqlmap.client.extensions.ResultGetter)
     */
    @Override
    public Object getResult(ResultGetter getter) throws SQLException {
        Object value = getValue(getter);
        if (getter.wasNull()) {
            return null;
        }

        return value;
    }

    /*
     * (非 Javadoc)
     * @see
     * com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#setParameter(
     * com.ibatis.sqlmap.client.extensions.ParameterSetter, java.lang.Object)
     */
    @Override
    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
        if (isNull(parameter)) {
            setNull(setter, parameter);
        } else {
            setValue(setter, parameter);
        }
    }

    /*
     * (非 Javadoc)
     * @see
     * com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#valueOf(java.
     * lang.String)
     */
    @Override
    public Object valueOf(String s) {
        return s;
    }

    /**
     * 値がNULLの場合trueを返す.
     * @param parameter
     *            値
     * @return NULLの場合true
     */
    public abstract boolean isNull(Object parameter);

    /**
     * NULLを設定する.
     * @param setter
     *            setter
     * @param parameter
     *            値
     * @throws SQLException
     *             設定に失敗
     */
    public abstract void setNull(ParameterSetter setter, Object parameter) throws SQLException;

    /**
     * 値を設定する.
     * @param setter
     *            setter
     * @param parameter
     *            値
     * @throws SQLException
     *             設定に失敗
     */
    public abstract void setValue(ParameterSetter setter, Object parameter) throws SQLException;

    /**
     * 値を取得する.
     * @param getter
     *            getter
     * @return 値
     * @throws SQLException
     *             取得に失敗
     */
    public abstract Object getValue(ResultGetter getter) throws SQLException;
}
