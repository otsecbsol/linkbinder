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
package jp.co.opentone.bsol.linkbinder.dto;

import java.util.List;


/**
 * データベース編集モード.
 *
 * @author opentone
 *
 */
public enum UpdateMode {

    /** 新規登録. */
    NEW,
    /** 更新. */
    UPDATE,
    /** 削除(論理削除). */
    DELETE,
    /** 編集なし. */
    NONE;

    /**
     * holdersの編集モードを一律modeに設定する.
     * @param holders データベース編集モードの設定対象
     * @param mode データベース編集モード
     */
    public static void setUpdateMode(List<? extends ModeHolder> holders, UpdateMode mode) {
        if (holders != null) {
            for (ModeHolder h : holders) {
                h.setMode(mode);
            }
        }
    }

    /**
     * データベース編集モードを持つオブジェクトを表すインターフェイス.
     * @author opentone
     */
    static interface ModeHolder {
        /**
         * 編集モードを設定する.
         * @param mode 編集モード
         */
        void setMode(UpdateMode mode);
        /**
         * 編集モードを返す.
         * @return 編集モード
         */
        UpdateMode getMode();
    }
}
