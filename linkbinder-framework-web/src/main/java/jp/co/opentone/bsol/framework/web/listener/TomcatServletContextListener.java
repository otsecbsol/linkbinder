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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;

import jp.co.opentone.bsol.framework.core.util.LogUtil;

/**
 * Tomcatでアプリケーションを起動する前の設定を行う{@link ServletContextListener}.
 * @author opentone
 */
public class TomcatServletContextListener implements ServletContextListener {

    /** logger. */
    private Logger log = LogUtil.getLogger();

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener
     *      #contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 数値型のフィールドにゼロが自動設定されてしまう挙動をOFFにする
        System.setProperty("org.apache.el.parser.COERCE_TO_ZERO", "false");

        log.info("{} initialized.", getClass().getSimpleName());
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener
     *      #contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 何もしない
    }
}
