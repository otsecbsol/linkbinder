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

/**
 * テーブル [v_correspon] の情報に、一覧表示用の情報を追加したDto.
 *
 * @author opentone
 *
 */
public class CorresponIndex extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -819314917010171032L;

    /**
     * コレポン文書.
     */
    private Correspon correspon;

    /**
     * チェックボックス.
     */
    private boolean checked;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponIndex() {
    }

    /**
     * コレポン文書を設定する.
     * @param correspon コレポン文書
     */
    public void setCorrespon(Correspon correspon) {
        this.correspon = correspon;
    }

    /**
     * コレポン文書を取得する.
     * @return コレポン文書
     */
    public Correspon getCorrespon() {
        return correspon;
    }

    /**
     * チェックボックスの値を設定する.
     * @param checked チェックボックスの値
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * チェックボックスの値を取得する.
     * @return チェックボックスの値
     */
    public boolean isChecked() {
        return checked;
    }
}
