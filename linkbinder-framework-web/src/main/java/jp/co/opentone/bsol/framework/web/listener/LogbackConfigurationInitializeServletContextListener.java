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
package jp.co.opentone.bsol.framework.web.listener;

import java.nio.file.Paths;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import ch.qos.logback.core.joran.spi.JoranException;
import jp.co.opentone.bsol.framework.core.log.LogbackConfigurationLoader;
import jp.co.opentone.bsol.framework.core.util.LogUtil;

/**
 * アプリケーション起動時にログ出力設定を初期化する {@link ServletContextListener}.
 * @author opentone
 */
public class LogbackConfigurationInitializeServletContextListener implements ServletContextListener {

    /** logger. */
    private static Logger LOG = LogUtil.getLogger();

    /* (非 Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // なにもしない
    }

    /* (非 Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        String dir = getConfigPath();
        LOG.debug("リソースパス：{}", dir);

        if (StringUtils.isEmpty(dir)) {
            LOG.info("リソースパスが定義されていないためデフォルトのログ出力設定を使用します");
            return;
        }

        try {
            new LogbackConfigurationLoader().load(
                    Paths.get(dir, "logback.xml").toFile(),
                    Paths.get(dir, "logback.groovy").toFile());
        } catch (JoranException e) {
            LOG.warn("ログ出力設定に失敗しました。デフォルトのログ出力設定を使用します");
        }
    }

    /**
     * ログ出力設定ファイル格納ディレクトリを返す.
     * @return ディレクトリ. 指定されていない場合はnull
     */
    private String getConfigPath() {
        return System.getProperty(LogbackConfigurationLoader.OPT_LOGBACK_DIR);
    }
}
