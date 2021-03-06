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
package jp.co.opentone.bsol.linkbinder.dao.handler;


import jp.co.opentone.bsol.framework.core.extension.ibatis.EnumValue;
import jp.co.opentone.bsol.framework.core.extension.ibatis.EnumValueHandler;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;

/**
 * {@link ReadStatus}とテーブルの列の値をマッピングするクラス.
 * @author opentone
 *
 */
public class ReadStatusValueHandler extends EnumValueHandler {

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.framework.extension.ibatis.EnumValueHandler#getValues()
     */
    @Override
    public EnumValue[] getValues() {
        return ReadStatus.values();
    }
}
