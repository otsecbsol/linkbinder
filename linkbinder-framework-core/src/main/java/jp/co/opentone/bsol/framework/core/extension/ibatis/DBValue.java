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
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * データベースの値を表す.
 * @author opentone
 */
public class DBValue {

    /**
     * 空のインスタンスを生成する.
     * 外部からのインスタンス化禁止.
     */
    private DBValue() {
    }

    /**
     * Java String -> NULL.
     */
    public static final String STRING_NULL = "";
    /**
     * Java Integer -> NULL.
     */
    public static final Integer INTEGER_NULL = Integer.MIN_VALUE;
    /**
     * Java Integer -> NULL.
     */
    public static final BigDecimal DECIMAL_NULL = new BigDecimal(Integer.MIN_VALUE);
    /**
     * Java java.util.Date -> NULL.
     */
    public static final Date DATE_NULL = new GregorianCalendar(0, 0, 0, 0, 0, 0).getTime();
}
