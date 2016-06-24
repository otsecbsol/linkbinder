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

import java.util.List;

import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.OrderedTableNameMap;

/**
 * Java BeanのリストをDataSetとして保持する.
 * @author opentone
 */
public class BeanDataSet extends AbstractDataSet {

    /**
     * 対象のテーブル.
     */
    private OrderedTableNameMap tables;

    public BeanDataSet(String tableName, List<?> beans, ITableMetaData metaData)
    throws DataSetException {
        tables = createTableNameMap();

        ITable table = new BeanTable(tableName, beans, metaData);
        tables.add(tableName, table);
    }

    public BeanDataSet(String tableName, List<?> beans) throws DataSetException {
        tables = createTableNameMap();

        ITable table = new BeanTable(tableName, beans);
        tables.add(tableName, table);
    }

    public BeanDataSet() throws DataSetException {
        tables = createTableNameMap();
    }

    public void addTable(BeanTable table) throws AmbiguousTableNameException {
        tables.add(table.getTableMetaData().getTableName(), table);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ITableIterator createIterator(boolean reversed) throws DataSetException {
        ITable[] t = (ITable[]) this.tables.orderedValues().toArray(new ITable[0]);
        return new DefaultTableIterator(t, reversed);
    }

    public void setTableMetaData(String tableName, ITableMetaData metaData) {
        BeanTable table = (BeanTable) tables.get(tableName);
        if (table != null) {
            table.setTableMetaData(metaData);
        }
    }

}
