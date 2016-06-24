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
package jp.co.opentone.bsol.framework.test;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author opentone
 */
public class ExtendedOracle10DataTypeFactory extends Oracle10DataTypeFactory {
    /**
     * Logger for this class.
     */
    private static final Logger log =
            LoggerFactory.getLogger(ExtendedOracle10DataTypeFactory.class);

    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        return super.createDataType(sqlType, sqlTypeName);
    }
}
