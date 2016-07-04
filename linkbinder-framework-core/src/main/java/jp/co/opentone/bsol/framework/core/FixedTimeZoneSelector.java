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
package jp.co.opentone.bsol.framework.core;

import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;

/**
 * アプリケーション全体で一つのタイムゾーンを返すクラス.
 * <p>
 * アプリケーション設定情報から<code>date.format.timezone</code>で定義された
 * タイムゾーンを取得する.
 * </p>
 * <p>
 * 設定情報が未設定の場合、<code>Asia/Tokyo</code>がアプリケーションのタイムゾーンとして
 * 利用される.
 * </p>
 * @see SystemConfig
 * @author opentone
 */
public class FixedTimeZoneSelector extends TimeZoneSelector {

    /**
     * タイムゾーン指定を取得するKEY.
     */
    private static final String KEY_TIMEZONE = "date.format.timezone";

    /**
     * このアプリケーションで使用するデフォルトのタイムゾーン.
     */
    private static final TimeZone DEFAULT_TIME_ZONE;
    static {
        String id = SystemConfig.getValue(KEY_TIMEZONE);
        if (StringUtils.isNotEmpty(id)) {
            DEFAULT_TIME_ZONE = TimeZone.getTimeZone(id);
        } else {
            DEFAULT_TIME_ZONE = TimeZone.getTimeZone("Asia/Tokyo");
        }
    }


    /* (非 Javadoc)
     * @see TimeZoneSelector#getTimeZone()
     */
    @Override
    public TimeZone getTimeZone() {
        return DEFAULT_TIME_ZONE;
    }
}
