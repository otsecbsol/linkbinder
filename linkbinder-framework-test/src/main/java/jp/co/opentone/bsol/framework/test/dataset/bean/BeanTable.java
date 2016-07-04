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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;

/**
 * Java BeanのリストをDBUnitのITable形式に変換して保持するクラス.
 * @author opentone
 */
public class BeanTable extends AbstractTable {

    /**
     * テーブル名.
     */
    private String tableName;
    /**
     * テーブルのメタ情報.
     */
    private ITableMetaData tableMetaData;
    /**
     * 対象となるJava Beanのリスト.
     */
    private List<?> beans;

    public BeanTable(String tableName, List<?> beans, ITableMetaData metaData) {
        this.tableName = tableName;
        this.beans = beans;
        this.tableMetaData = metaData;
    }

    public BeanTable(String tableName, List<?> beans) throws DataSetException {
        this.tableName = tableName;
        this.beans = beans;
        this.tableMetaData = createTableMetaData();
    }

    private ITableMetaData createTableMetaData() throws DataSetException {
        if (beans.isEmpty()) {
            return new DefaultTableMetaData(tableName, new Column[0]);
        } else {
            try {
                List<Column> columns = new ArrayList<Column>();
                BeanInfo info = Introspector.getBeanInfo(beans.get(0).getClass());
                PropertyDescriptor[] descs = info.getPropertyDescriptors();
                for (PropertyDescriptor desc : descs) {
                    if (!isIgnoreProperty(desc)) {
                        System.out.println(desc.getName());
                        columns.add(new Column(desc.getName(), DataType.UNKNOWN));
                    }
                }

                Column[] c = (Column[]) columns.toArray(new Column[columns.size()]);
                return new DefaultTableMetaData(tableName, c);
            } catch (IntrospectionException e) {
                throw new DataSetException(e);
            }
        }
    }

    private boolean isIgnoreProperty(PropertyDescriptor desc) {
        final Set<String> ignoreNames = new HashSet<String>();
        ignoreNames.add("class");

        return desc.getReadMethod() == null || ignoreNames.contains(desc.getName());
    }

    public int getRowCount() {
        return beans.size();
    }

    public ITableMetaData getTableMetaData() {
        return tableMetaData;
    }

    public Object getValue(int row, String column) throws DataSetException {
        assertValidRowIndex(row);
        Object bean = beans.get(row);

        try {
            Object value = PropertyUtils.getProperty(bean, column);

            // XlsTableとの比較のための対応
            // 他形式のTableとの比較は考慮していません
            if (value instanceof Long) {
                BigDecimal ret = new BigDecimal((Long) value);
//                BigDecimal ret = new BigDecimal((Long) value).setScale(1);
                return ret;
            } else if (value instanceof Integer) {
                BigDecimal ret = new BigDecimal((Integer) value);
//                BigDecimal ret = new BigDecimal((Integer) value).setScale(1);
                return ret;
            } else if (value instanceof Date) {
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return f.format((Date) value);
            } else {
                return value;
            }
        } catch (NestedNullException e) {
            // ネストしたプロパティがnullの場合に発生する例外なので
            // ここではnullを返して正常終了する
            return null;
        } catch (IllegalAccessException e) {
            throw new DataSetException(e);
        } catch (InvocationTargetException e) {
            throw new DataSetException(e);
        } catch (NoSuchMethodException e) {
            throw new DataSetException(e);
        }
    }

    public void setTableMetaData(ITableMetaData metaData) {
        this.tableMetaData = metaData;
    }
}
