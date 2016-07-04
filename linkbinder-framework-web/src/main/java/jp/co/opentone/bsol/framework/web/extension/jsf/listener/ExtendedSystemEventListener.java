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
package jp.co.opentone.bsol.framework.web.extension.jsf.listener;

import javax.faces.component.UIViewRoot;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.web.view.listener.ExtendedActionListener;
import jp.co.opentone.bsol.framework.web.view.util.PropertyUtil;

/**
 * MERシステムのイベントリスナ基底クラスです.
 * @author opentone
 */
public abstract class ExtendedSystemEventListener implements SystemEventListener {

    /* (非 Javadoc)
     * @see javax.faces.event.SystemEventListener#isListenerForSource(java.lang.Object)
     */
    @Override
    public boolean isListenerForSource(Object app) {
        boolean result = (app instanceof UIViewRoot);
        return result;
    }

    /**
     * viewIdに対応付けられているActionListener実装クラスインスタンスを生成する.
     * <p>
     * 設定ファイル(system-event-listener.properties)で、イベント名とviewId(画面ID)
     * を組み合わせた内容が設定キーです。
     * <p>
     * 例）prerender.admin.adminHome=jp.co.opentone.bsol.mer.view.admin.AdminHomePrerenderListener
     * <p>
     * 引数prefixには上例の"prerender"に該当する文字列を指定します.
     * @param event {@link SystemEvent}
     * @param prefix 設定prefix
     * @return 生成したActionListenerインスタンス. 設定無しの場合はnull
     * @throws AbortProcessingException {@link AbortProcessingException}
     */
    protected ExtendedActionListener createActionEventListener(SystemEvent event, String prefix)
        throws AbortProcessingException {
        ExtendedActionListener listener = null;

        Object source = event.getSource();
        if (source instanceof UIViewRoot) {
            UIViewRoot viewRoot = (UIViewRoot) source;
            String viewId = viewRoot.getViewId();
            if (StringUtils.isNotEmpty(viewId)) {
                // viewIdをプロパティキーに変換
                String propKey = PropertyUtil.createPropertyKeyForViewId(prefix, viewId);
                listener = createActionEventListener(propKey);
            }
        }
        return listener;
    }

    /**
     * 設定プロパティキーに対応付けられているActionListener実装クラスインスタンスを生成する.
     * @param propKey プロパティキー
     * @return 生成したActionListenerインスタンス. 設定無しの場合はnull
     */
    protected ExtendedActionListener createActionEventListener(String propKey) {
        ExtendedActionListener listener = null;
        String className = SystemConfig.getValue(propKey);
        if (StringUtils.isNotEmpty(className)) {
            try {
                Class<?> obj = Class.forName(className);
                listener = (ExtendedActionListener) obj.newInstance();
            } catch (Exception e) {
                throw new AbortProcessingException(e);
            }
        }
        return listener;
    }

    /**
     * SystemEventのソースがUIViewRootの場合はUIViewRootを返します.
     * @param event {@link SystemEvent}
     * @return ソースがUIViewRootの場合はUIViewRoot. そうでない場合はnull.
     */
    protected UIViewRoot getUIViewRoot(SystemEvent event) {
        UIViewRoot viewRoot = null;
        Object source = event.getSource();
        if (source instanceof UIViewRoot) {
            viewRoot = (UIViewRoot) source;
        }
        return viewRoot;
    }

    /**
     * SystemEventのソースがUIViewRootの場合はviewIdを返します.
     * @param event {@link SystemEvent}
     * @return ソースがUIViewRootの場合はviewId. そうでない場合はnull.
     */
    protected String getViewId(SystemEvent event) {
        String viewId = null;
        UIViewRoot viewRoot = getUIViewRoot(event);
        if (null != viewRoot) {
            viewId = viewRoot.getViewId();
        }
        return viewId;
    }
}
