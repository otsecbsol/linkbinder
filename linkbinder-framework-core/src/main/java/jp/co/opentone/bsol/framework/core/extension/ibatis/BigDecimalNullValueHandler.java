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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;

/**
 * {@link DBValue#DECIMAL_NULL}をデータベースのNULLに変換するクラス.
 * <p>
 * iBATISのsqlMapConfig.xmlに次の設定を行うと当クラスは有効に機能する.
 * </p>
 * <pre>
 * &lt;typeHandler
 *     javaType=&quot;java.math.BigDecimal&quot;
 *     callback=
 *     &quot;BigDecimalNullValueHandler&quot; /&gt;
 * </pre>
 * @author opentone
 */
public class BigDecimalNullValueHandler extends NullValueHandler {

    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(BigDecimalNullValueHandler.class);

    @Override
    public Object getValue(ResultGetter getter) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("#getValue() called");
        }
        return getter.getBigDecimal();
    }

    @Override
    public boolean isNull(Object parameter) {
        if (log.isDebugEnabled()) {
            log.debug("#isNull() called");
        }
        BigDecimal val = (BigDecimal) parameter;
        return val != null && DBValue.DECIMAL_NULL.compareTo(val) == 0;
    }

    @Override
    public void setNull(ParameterSetter setter, Object parameter) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("#setNull() called");
        }
        setter.setNull(Types.DECIMAL);
    }

    @Override
    public void setValue(ParameterSetter setter, Object parameter) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("#setValue() called");
        }
        setter.setBigDecimal((BigDecimal) parameter);
    }
}
