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
package jp.co.opentone.bsol.framework.web.view;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.core.util.LogUtil;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

/**
 * {@link JSON}を拡張したクラス.
 * @author opentone
 */
public class ExtendedJSON extends JSON {

    /** logger. */
    private static Logger log = LogUtil.getLogger();
    /** JSONExceptionのメッセージに含まれる、parseエラーが発生したプロパティ名を特定するための正規表現. */
    private static final Pattern PROP_REGEX = Pattern.compile(".*\\$\\[.+\\]\\.(.+)\\s*$");

    /**
     * マッピングするBeanのプロパティがString型の場合に、空文字をnullに変換するフラグ.
     * <p>
     * デフォルトはfalse.
     * </p>
     */
    private boolean convertBlankToNull;

    /** 発生した例外. */
    private JSONException occured;

    /* (non-Javadoc)
     * @see net.arnx.jsonic.JSON#parse(java.lang.CharSequence, java.lang.reflect.Type)
     */
    @Override
    public Object parse(CharSequence s, Type type) throws JSONException {
        this.occured = null;
        try {
            return super.parse(s, type);
        } catch (JSONException e) {
            log.warn("parse error", e);
            this.occured = e;
            throw e;
        }
    }

    public int getErrorCode() {
        if (occured == null) {
            return 0;
        }
        return occured.getErrorCode();
    }

    public String getErrorPropertyName() {
        if (occured == null) {
            return null;
        }
        String msg = occured.getMessage();
        Matcher m = PROP_REGEX.matcher(msg);

        String result = null;
        if (m.matches() && m.groupCount() > 0) {
            result = m.group(1);
        }
        log.debug("errorPropertyName={}", result);
        return result;
    }

    /*
     * (non-Javadoc)
     * @see net.arnx.jsonic.JSON#postparse(net.arnx.jsonic.JSON.Context,
     * java.lang.Object, java.lang.Class, java.lang.reflect.Type)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected <T> T postparse(Context context, Object value, Class<? extends T> c, Type type)
            throws Exception {
        if (c == Date.class && value != null && !(value instanceof BigDecimal)) {
            return (T) DateUtil.convertStringToDate(String.valueOf(value));
        } else {
            T result = super.postparse(context, value, c, type);
            if (isConvertBlankToNull()
                    && result instanceof String
                    && StringUtils.isEmpty((String) result)) {
                return null;
            }
            return result;
        }
    }

    /**
     * @param convertBlankToNull the convertBlankToNull to set
     */
    public void setConvertBlankToNull(boolean convertBlankToNull) {
        this.convertBlankToNull = convertBlankToNull;
    }

    /**
     * @return the convertBlankToNull
     */
    public boolean isConvertBlankToNull() {
        return convertBlankToNull;
    }
}
