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
package jp.co.opentone.bsol.framework.core.config;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;

/**
 * アプリケーションで利用する定義情報を初期化するクラス.
 * <p>
 * アプリケーションの初期化時に、利用する定義情報を初期化するため、 Springのbean定義ファイルに次の設定を行う.
 * </p>
 * <pre>
 * &lt;bean id=&quot;systemConfigSource&quot; class=&quot;...&quot;&gt;
 *   ... (アプリケーション固有定義情報の設定)
 * &amp;lt/bean&gt;
 * &lt;bean id=&quot;systemConfigInitializer&quot;
 *   class=&quot;jp.co.opentone.bsol.framework.config.SystemConfigInitializer&quot;
 *   init-method=&quot;initialize&quot; /&gt;
 * </pre>
 * @author opentone
 */
public class SystemConfigInitializer {

    /**
     * 定義情報.
     */
    @Resource(name = "systemConfigSource")
    private MessageSource messageSource;

    /**
     * アプリケーションで利用する定義情報を初期化する.
     */
    public void initialize() {
        SystemConfig.setConfig(messageSource);
    }

}
