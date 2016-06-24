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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;

/**
 * {@link DBValue#INTEGER_NULL}をデータベースのNULLに変換するクラス.
 * <p>
 * iBATISのsqlMapConfig.xmlに次の設定を行うと当クラスは有効に機能する.
 * </p>
 * <pre>
 * &lt;typeHandler
 *     javaType=&quot;java.lang.Integer&quot;
 *     callback=&quot;jp.co.opentone.bsol.framework.extension.ibatis.IntegerNullValueHandler&quot; /&gt;
 * </pre>
 * @author opentone
 */
public class IntegerNullValueHandler extends NullValueHandler {

    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(IntegerNullValueHandler.class);

    @Override
    public Object getValue(ResultGetter getter) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("#getValue() called");
        }
        return getter.getInt();
    }

    @Override
    public boolean isNull(Object parameter) {
        if (log.isDebugEnabled()) {
            log.debug("#isNull() called");
        }
        Integer val = (Integer) parameter;
        return DBValue.INTEGER_NULL.intValue() == val.intValue();
    }

    @Override
    public void setNull(ParameterSetter setter, Object parameter) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("#setNull() called");
        }
        setter.setNull(Types.INTEGER);
    }

    @Override
    public void setValue(ParameterSetter setter, Object parameter) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("#setValue() called");
        }
        setter.setInt((Integer) parameter);
    }
}
