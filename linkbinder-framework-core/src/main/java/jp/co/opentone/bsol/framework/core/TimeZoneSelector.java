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
package jp.co.opentone.bsol.framework.core;

import java.util.TimeZone;

/**
 * アプリケーションが利用する@{link {@link TimeZone}を提供する.
 * @author opentone
 */
public abstract class TimeZoneSelector {

    /**
     * {@link TimeZone}を提供するインスタンスを返す.
     * @return {@link TimeZoneSelector}のインスタンス.
     */
    public static TimeZoneSelector getSelector() {
        //FIXME プロジェクト毎、ユーザー毎など
        //      実行コンテキストによってタイムゾーンを変更する場合は
        //      対応する新しいTimeZoneSelectorの実装クラスを作成し、
        //      設定ファイル等でselectorインスタンスを切り替えられるようにする
        return new FixedTimeZoneSelector();
    }

    /**
     * アプリケーションが利用する@{link {@link TimeZone}インスタンスを返す.
     * @return アプリケーションが利用するタイムゾーン
     */
    public abstract TimeZone getTimeZone();
}
