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

/**
 * {@link DBValue#STRING_NULL}をデータベースのNULLに変換するクラス.
 * <p>
 * iBATISのsqlMapConfig.xmlに次の設定を行うと当クラスは有効に機能する.
 * </p>
 * <pre>
 * &lt;typeHandler
 *     javaType=&quot;java.lang.String&quot;
 *     callback=&quot;jp.co.opentone.bsol.framework.extension.ibatis.StringNullValueHandler&quot; /&gt;
 * </pre>
 * @author opentone
 */
public class StringNullValueHandler extends NullValueHandler {

    @Override
    public Object getValue(ResultGetter getter) throws SQLException {
        return getter.getString();
    }

    @Override
    public boolean isNull(Object parameter) {
        String val = (String) parameter;
        return DBValue.STRING_NULL.equals(val);
    }

    @Override
    public void setNull(ParameterSetter setter, Object parameter) throws SQLException {
        setter.setNull(Types.VARCHAR);
    }

    @Override
    public void setValue(ParameterSetter setter, Object parameter) throws SQLException {
        setter.setString((String) parameter);
    }
}
