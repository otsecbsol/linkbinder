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
package jp.co.opentone.bsol.framework.web.extension.jsf.convert;

import java.util.TimeZone;

import jp.co.opentone.bsol.framework.core.TimeZoneSelector;

/**
 * {@link javax.faces.convert.DateTimeConverter}に、デフォルトタイムゾーンを
 * 設定できるようにした拡張クラス.
 * @author opentone
 */
public class DateTimeConverter extends javax.faces.convert.DateTimeConverter {

    /**
     * アプリケーションが利用するタイムゾーンを提供するオブジェクト.
     */
    private TimeZoneSelector selector = TimeZoneSelector.getSelector();

    /**
     * このオブジェクトが使用するタイムゾーン.
     */
    private TimeZone timeZone;

    public DateTimeConverter() {
        super.setTimeZone(getDefaultTimeZone());
    }

    /* (非 Javadoc)
     * @see javax.faces.convert.DateTimeConverter#getTimeZone()
     */
    @Override
    public TimeZone getTimeZone() {
        return timeZone != null ? timeZone : getDefaultTimeZone();
    }

    /* (非 Javadoc)
     * @see javax.faces.convert.DateTimeConverter#setTimeZone(java.util.TimeZone)
     */
    @Override
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        super.setTimeZone(timeZone);
    }

    private TimeZone getDefaultTimeZone() {
        return selector.getTimeZone();
    }
}
