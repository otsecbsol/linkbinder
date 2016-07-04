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
package jp.co.opentone.bsol.linkbinder.view.correspon.util;

/**
 * コレポン文書関連画面での編集モード.
 * @author opentone
 */
public enum CorresponEditMode {

    /**
     * 新規.
     */
    NEW,
    /**
     * 返信.
     */
    REPLY,
    /**
     * 返信済文書を引用して返信.
     */
    REPLY_WITH_PREVIOUS_CORRESPON,
    /**
     * 更新.
     */
    UPDATE,
    /**
     * 戻る.
     */
    BACK,
    /**
     * 改訂.
     */
    REVISE,
    /**
     * 複写.
     */
    COPY,
    /**
     * 転送.
     */
    FORWARD;

    /**
     * クエリパラメーターとして使用する時のパラメーター名.
     */
    public static final String PARAM_NAME = "editMode";

    /**
     * このオブジェクトをクエリパラメーター文字列に変換する.
     * @return クエリパラメータ文字列
     */
    public String toQueryString() {
        return String.format("%s=%s", PARAM_NAME, this.name());
    }
}
