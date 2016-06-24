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
package jp.co.opentone.bsol.framework.core.log;

import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Marker;

import com.ibatis.common.jdbc.logging.PreparedStatementLogProxy;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * iBATISが出力するPreparedStatementのパラメータログのうち、
 * 指定されたキーワードが含まれるSQLのパラメータログの出力を抑制するフィルタ.
 * <p>
 * {@code logback.xml}に次のように定義すると有効になる.
 * </p>
 * <pre>
 * &lt;turboFilter class="jp.co.opentone.bsol.framework.log.PreparedStatementParameterFilter"&gt;
 *   &lt;Keywords&gt;keyword1,keyword2&lt;/Keywords&gt;
 * &lt;/turboFilter&gt;
 * </pre>
 * @see PreparedStatementLogProxy
 * @see TurboFilter
 * @author opentone
 */
public class PreparedStatementParameterFilter extends TurboFilter {

    /**
     * PreparedStatementオブジェクトのログを出力するLogger名.
     * <p>
     * この名前を持つLoggerが当クラスの処理対象となる.
     * </p>
     */
    public static final String LOGGER_NAME = PreparedStatement.class.getName();

    /**
     * フィルターが有効に働いた時に出力されるメッセージ.
     */
    public static final String MESSAGE_FILTERED =
                            String.format("{pstm-%%s} Filtered by %s",
                                          PreparedStatementParameterFilter.class.getSimpleName());
    /**
     * iBATISのSQL文実行ログに一致する正規表現.
     */
    public static final Pattern PATTERN_EXECUTE =
                            Pattern.compile("^\\{pstm-(\\d+)\\}\\s+Executing Statement.*");
    /**
     * iBATISのSQLパラメータログに一致する正規表現.
     * <p>
     * この正規表現に該当するログの出力を抑制する.
     * </p>
     */
    public static final Pattern PATTERN_PARAMETERS =
                            Pattern.compile("^\\{pstm-(\\d+)\\}\\s+Parameters.*");
    /**
     * SQL文実行ログとSQLパラメータログを関連付けるための情報.
     */
    private ThreadLocal<LoggingInfo> logging = new ThreadLocal<LoggingInfo>();

    /**
     * 出力対象外とするキーワード.
     * <p>
     * {@code logback.xml}にカンマ区切りで定義.<br/>
     * このキーワードがSQLに含まれていたら
     * SQLのパラメータ値を出力しない.
     * </p>
     */
    private String keywords;
    /**
     * 出力対象外キーワード格納コンテナ.
     */
    private Set<String> keywordSet;

    /* (non-Javadoc)
     * @see ch.qos.logback.classic.turbo.TurboFilter#decide(
     *             org.slf4j.Marker, ch.qos.logback.classic.Logger,
     *             ch.qos.logback.classic.Level,
     *             java.lang.String,
     *             java.lang.Object[],
     *             java.lang.Throwable)
     */
    @Override
    public FilterReply decide(Marker marker,
                                Logger logger,
                                Level level,
                                String format,
                                Object[] params, Throwable t) {
        if (!isStarted()
            || format == null
            || !isPreparedStatementLogger(logger)) {
            return FilterReply.NEUTRAL;
        }

        FilterReply result = FilterReply.NEUTRAL;
        if (!isLogging() && containsAnyKeyword(format)) {
            Matcher m = PATTERN_EXECUTE.matcher(format);
            if (m.matches()) {
                LoggingInfo info = new LoggingInfo(m.group(1), true);
                setLoggingInfo(info);
            }
        } else if (isLogging()) {
            Matcher m = PATTERN_PARAMETERS.matcher(format);
            if (m.matches()) {
                String id = m.group(1);
                LoggingInfo current = getLoggingInfo();
                if (id.equals(current.id)) {
                    //  フィルタリング対象のSQL id と一致した場合に限り
                    //  パラメータログ出力を抑制する
                    logger.info(marker, String.format(MESSAGE_FILTERED, id));
                    result = FilterReply.DENY;
                }
            }
            setLoggingInfo(null);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see ch.qos.logback.classic.turbo.TurboFilter#start()
     */
    @Override
    public void start() {
        if (StringUtils.isNotEmpty(keywords)) {
            keywordSet = new HashSet<String>();
            for (String s : keywords.split(",")) {
                keywordSet.add(s.trim());
            }
            super.start();
        }
    }

    private boolean containsAnyKeyword(String message) {
        for (String k : keywordSet) {
            if (message.indexOf(k) != -1) {
                return true;
            }
        }
        return false;
    }

    private boolean isPreparedStatementLogger(Logger logger) {
        return logger.getName().equals(LOGGER_NAME);
    }

    private boolean isLogging() {
        LoggingInfo info = logging.get();
        return info != null && info.logging;
    }

    private LoggingInfo getLoggingInfo() {
        return logging.get();
    }

    private void setLoggingInfo(LoggingInfo info) {
        logging.set(info);
    }


    /**
     * @param keywords the keywords to set
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * @return the keywords
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * フィルタリング対象のログ情報.
     * @author opentone
     */
    static class LoggingInfo {
        /** 現在対象のログを出力中の場合はtrue. */
        private boolean logging;
        /** SQL id. */
        private String id;

        private LoggingInfo(String id, boolean logging) {
            this.id = id;
            this.logging = logging;
        }
    }
}
