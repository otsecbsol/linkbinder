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
package jp.co.opentone.bsol.linkbinder.view.auth;

import org.springframework.context.ApplicationContext;

import jp.co.opentone.bsol.framework.core.auth.FeedAuthenticator;
import jp.co.opentone.bsol.framework.core.auth.FeedAuthenticatorFactory;

/**
 * RSSフィード認証オブジェクトを生成するファクトリクラス.
 *
 * @author opentone
 */
public class DefaultFeedAuthenticatorFactory implements FeedAuthenticatorFactory {

    /**
     *
     */
    private static final String BEAN_NAME = "feedAuthenticator";
    /**
     *
     */
    private ApplicationContext context;
    /**
     * 空のインスタンスを生成する.
     */
    public DefaultFeedAuthenticatorFactory() {
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.auth.FeedAuthenticatorFactory#getAuthenticator()
     */
    public FeedAuthenticator getAuthenticator() {
        return (FeedAuthenticator) context.getBean(BEAN_NAME);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.auth.FeedAuthenticatorFactory
     * #setContext(org.springframework.context.ApplicationContext)
     */
    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}
