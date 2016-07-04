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


import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.linkbinder.Constants;

/**
 * 応答履歴の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class CorresponResponseHistory extends AbstractDto {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6556026604316516287L;

    /**
     * 件名のインデント.
     */
    private static final String INDENT = "&nbsp;&nbsp;&nbsp;&nbsp;";

    /**
     * 件名が制限文字数を超えた場合に、後に付ける三点リーダ.
     */
    private static final String SUBJECT_SUFFIX = "...";

    /**
     * コレポン文書.
     * <p>
     * </p>
     */
    private Correspon correspon;

    /**
     * 返信文書の階層(0始まり).
     * <p>
     * </p>
     */
    private Long level;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponResponseHistory() {
    }

    /**
     * @param correspon
     *            the correspon to set
     */
    public void setCorrespon(Correspon correspon) {
        this.correspon = correspon;
    }

    /**
     * @return the correspon
     */
    public Correspon getCorrespon() {
        return correspon;
    }

    /**
     * @param level
     *            the level to set
     */
    public void setLevel(Long level) {
        this.level = level;
    }

    /**
     * @return the level
     */
    public Long getLevel() {
        return level;
    }

    /**
     * @return コレポン文書に設定されている宛先(To)グループ数.
     */
    public int getToCount() {
        return correspon.getAddressCorresponGroups().size();
    }

    /**
     * @return インデント付きの件名.
     */
    public String getIndent() {
        StringBuilder sb = new StringBuilder(64);
        for (long l = 0; l < level; l++) {
            sb.append(INDENT);
        }
        return sb.toString();
    }

    /**
     * コレポン文書の件名を制限バイト数で切った状態で返す.
     * 文字列長がバイト数制限を超える場合「...」を付与する
     * @return バイト数制限で切った件名.
     */
    public String getSubject() {
        int max = Integer.parseInt(SystemConfig.getValue(Constants.HISTORY_SUBJECT_MAXLENGTH));
        StringBuilder result = new StringBuilder();
        String subject = correspon.getSubject();
        for (int i = 0, length = 0; i < subject.length(); i++) {
            // ASCII範囲内の文字を1バイトとみなす
            length += (subject.charAt(i) <= 0x7f) ? 1 : 2;
            if (length > max) {
                result.append(SUBJECT_SUFFIX);
                break;
            }
            result.append(subject.charAt(i));
        }
        return result.toString();
    }
}
