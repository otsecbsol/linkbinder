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
package jp.co.opentone.bsol.framework.web.view.flash;

import java.io.Serializable;
import java.util.Map;

import javax.faces.context.FacesContext;

/**
 * リクエスト間でデータを保持するコンテナを操作するクラス. Ruby on Railsのflashオブジェクトに似た動きをする.
 * @author opentone
 */
public class Flash implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 2792141684879166210L;

    /**
     * Flash情報をセッションに格納するためのキー.
     */
    public static final String KEY_FLASH = "KEY_" + Flash.class.getName();

    /**
     * 値を格納する.
     * @param key
     *            キー
     * @param value
     *            値
     */
    public void setValue(String key, Object value) {
        getContainer().setValue(key, value);
    }

    /**
     * 値を取得する.
     * @param <T>
     *            取得する値の型
     * @param key
     *            キー
     * @return 値. 値が無い場合はnull
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        return (T) getContainer().getValue(key);
    }

    /**
     * 指定した値を次のリクエストに持ち越す.
     * @param key
     *            キー
     */
    public void transfer(String key) {
        getContainer().transfer(key);
    }

    /**
     * 現在保持している全ての値を次のリクエストに持ち越す.
     */
    public void transferAll() {
        getContainer().transferAll();
    }

    /**
     * 前のリクエストの値をクリアする.
     */
    public void refresh() {
        getContainer().refresh();
    }

    @SuppressWarnings("unchecked")
    private FlashContainer getContainer() {
        Map<String, Object> session =
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        FlashContainer container;
        synchronized (this) {
            container = (FlashContainer) session.get(KEY_FLASH);
            if (container == null) {
                container = new FlashContainer();
                session.put(KEY_FLASH, container);
            }
        }
        return container;
    }
}
