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
package jp.co.opentone.bsol.framework.web.extension.jsf;

/**
 * このフレームワーク共通の定義情報.
 * @author opentone
 */
public class Config {

    /**
     * 空のインスタンスを生成する.
     * 外部からのインスタンス化禁止.
     */
    private Config() {
    }

    /**
     * ページの拡張子.
     */
    public static final String PAGE_SUFFIX = ".jsf";
    /**
     * ページクラス名の後置詞.
     */
    public static final String PAGE_CLASS_SUFFIX = "Page";
}
