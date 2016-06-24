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
package jp.co.opentone.bsol.framework.core.message;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;

/**
 * アプリケーションで利用するメッセージを初期化するクラス.
 * <p>
 * アプリケーションの初期化時に利用するメッセージを初期化するため、 Springのbean定義ファイルに次の設定を行う.
 * </p>
 * <pre>
 * &lt;bean id=&quot;messageSource&quot; class=&quot;...&quot;&gt;
 *   ... (アプリケーション固有メッセージの設定)
 * &amp;lt/bean&gt;
 * &lt;bean id=&quot;messageInitializer&quot;
 *   class=&quot;jp.co.opentone.bsol.framework.message.MessageInitializer&quot;
 *   init-method=&quot;initialize&quot; /&gt;
 * </pre>
 * @author opentone
 */
public class MessageInitializer {

    /**
     * メッセージ定義オブジェクト.
     */
    @Resource(name = "messageSource")
    private MessageSource messageSource;

    /**
     * アプリケーションで利用するメッセージを初期化する.
     */
    public void initialize() {
        Messages.setMessageSource(messageSource);
    }
}
