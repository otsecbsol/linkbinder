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
package jp.co.opentone.bsol.framework.core.log;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * ログ出力のMarkerを定義するクラス.
 * @author opentone
 */
public class Markers {

    /**
     * 空のインスタンスを生成する. 外部からのインスタンス化はできない.
     */
    private Markers() {
    }

    /**
     * {@link Marker}名のプレフィックス.
     */
    public static final String PREFIX = "jp.co.opentone.bsol.framework.core";

    /**
     * 最上位のMarker.
     */
    public static final Marker ROOT = MarkerFactory.getMarker(PREFIX);
    /**
     * プレゼンテーション層に関連するMarker.
     */
    public static final Marker PRESENTATION = MarkerFactory.getMarker(PREFIX + ".presentation");
    /**
     * サービス層に関連するMarker.
     */
    public static final Marker SERVICE = MarkerFactory.getMarker(PREFIX + ".service");
    /**
     * データアクセス層に関連するMarker.
     */
    public static final Marker DATA_ACCESS = MarkerFactory.getMarker(PREFIX + ".dataAccess");

    /**
     * オブジェクトの生成に関連するMarker.
     */
    public static final Marker LIFECYCLE = MarkerFactory.getMarker(PREFIX + ".lifecycle");

    // このクラスで定義したMarkerを登録する
    static {
        Marker[] markers = {ROOT, PRESENTATION, SERVICE, DATA_ACCESS, LIFECYCLE};
        for (Marker m : markers) {
            ROOT.add(m);
        }
    }
}
