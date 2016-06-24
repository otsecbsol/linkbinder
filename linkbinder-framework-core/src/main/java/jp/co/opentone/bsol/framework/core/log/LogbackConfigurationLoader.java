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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import jp.co.opentone.bsol.framework.core.util.LogUtil;

/**
 * logbackの定義ファイルを読み込むクラス.
 * @author opentone
 */
public class LogbackConfigurationLoader {
    /** logback.xmlの配置場所を表す設定キー. */
    public static final String OPT_LOGBACK_DIR = "OPT_LOGBACK_DIR";

    /** logger. */
    private static Logger LOG = LogUtil.getLogger();

    public void load(File... configList) throws JoranException {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (File config : configList) {
            if (config.exists()) {
                if (config.isFile()) {
                    if (config.canRead()) {
                        JoranConfigurator configurator = new JoranConfigurator();
                        configurator.setContext(context);

                        // 設定をクリアして再読み込み
                        context.reset();
                        configurator.doConfigure(config);
                        LOG.warn("logback設定ファイル再設定が完了しました。{}", config.getAbsolutePath());
                        break;
                    } else {
                        LOG.warn("logback設定ファイルが読み込めません。{}", config.getAbsolutePath());
                    }
                } else {
                    LOG.warn("logback設定ファイルがディレクトリです。{}", config.getAbsolutePath());
                }
            } else {
                LOG.info("logback設定ファイルが見つかりません。{}", config.getAbsolutePath());
            }
        }
    }
}
