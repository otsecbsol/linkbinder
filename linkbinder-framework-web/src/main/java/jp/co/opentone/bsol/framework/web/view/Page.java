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

import java.io.Serializable;

/**
 * 画面に関する処理を行う.
 * <p>
 * @author opentone
 */
//CHECKSTYLE:OFF
public interface Page extends Serializable {
//CHECKSTYLE:ON

    /**
     * 次ページへ引き継ぐ値をsessionに格納するキー.
     */
    String KEY_TRANSFER = "KEY_TRANSFER_" + Page.class.getName();

    /**
     * 次ページへ引き継ぐメッセージをFlashに格納するキー.
     */
    String KEY_FLASH_MASSAGE = "KEY_FLASH_MASSAGE_" + Page.class.getName();

    /**
     * 次ページへ引き継ぐメッセージをFlashに格納するキー.
     */
    String KEY_SEARCH_CONDITION = "KEY_SEARCH_CONDITION_" + Page.class.getName();

    /**
     * このオブジェクトのbean名を返す.
     * @return bean名
     */
    String getBeanName();

    /**
     * {@link Transfer} が定義されたフィールドの値を次のページに引き継ぐ場合はtrueを返す.
     * @return 次のページに引き継ぐ場合はtrue
     */
    boolean isTransferNext();

    /**
     * {@link Transfer} が定義されたフィールドの値を次のページに引き継ぐか否かを設定する.
     * @param transferNext
     *            次のページに引き継ぐ場合はtrue
     */
    void setTransferNext(boolean transferNext);

    /**
     * リクエスト処理前の初期化処理を行う.
     */
    void setUp();

    /**
     * 現在実行中アクションの名前を返す.
     * @return 実行中アクション名
     */
    String getActionName();
}
