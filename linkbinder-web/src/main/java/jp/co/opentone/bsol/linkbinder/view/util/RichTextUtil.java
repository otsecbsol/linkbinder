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
package jp.co.opentone.bsol.linkbinder.view.util;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.linkbinder.Constants;
/**
 * リッチテキスト情報のユーティリティクラス.
 * @author opentone
 */
public class RichTextUtil {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7439484833629218578L;

    /**
     * 最大バイト数.
     */
    private int maxByte;

    /**
     * 改行コード \n.
     */
    private static final char LF  = '\n';
    /**
     * 改行コード \r.
     */
    private static final char CR  = '\r';

    /**
     * 入力 '>' .
     */
    private static final String LT = "&lt;";
    /**
     * 入力 '<' .
     */
    private static final String GT = "&gt;";

    /**
     * 入力 ' '.
     */
    private static final String NBSP = "&nbsp;";

    /**
     * 自動改行タグ.
     */
    private static final String WBR = "<wbr/>";

    /**
     * 入力 '&'（指定文字チェック用）.
     */
    private static final char AND = '&';

    /**
     * 入力 ';'（指定文字チェック用）.
     */
    private static final char SEMICOLON = ';';

    /**
     * 右タグ '>'.
     */
    private static final char R_PAREN = '>';

    /**
     * 左タグ '<'.
     */
    private static final char L_PAREN = '<';

    /**
     * 実態参照の最大値.
     */
    private static final int MAX_LENGTH_ENTITY_REFERENCE = 8;

    /**
     * 文字数のカウント.
     */
    private int fontCount;

    /**
     * タグの条件フラグ.
     */
    private boolean tagFlag;

    /**
     * 指定文字処理有無フラグ.
     */
    private boolean specificFlag;

    /**
     * 文字情報一時保存用.
     */
    private StringBuilder keepData;

    /**
     * HTMLタグを処理中の場合はtrue.
     */
    private boolean inTag;
    /**
     * コンストラクタ.
     */
    public RichTextUtil() {
    }

    /**
     * 指定の情報に設定されたバイト数間隔でWBRタグを挿入する.
     *
     * @param value 変換前情報
     * @return 変換後情報
     */
    public String createRichText(String value) {
        initialize();
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isEmpty(value)) {
            return sb.toString();
        }
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);

            if (ch == L_PAREN) {
                inTag = true;
            }

            if (ch == LF || ch == CR) {
                sb.append(ch);
            } else if (ch == R_PAREN || ch == L_PAREN) {
                sb.append(createByTagChar(ch));
            } else {
                if (ch == AND) {
                    specificFlag = true;
                }
                if (specificFlag) {
                    sb.append(createByAndChar(value.charAt(i)));
                } else {
                    sb.append(createByNormalChar(ch));
                }
            }

            if (ch == R_PAREN) {
                inTag = false;
            }
        }
        return sb.toString();
    }

    /**
     * 値の初期化.
     */
    private void initialize() {
        String properData = SystemConfig.getValue(Constants.KEY_RICH_TEXT_MAX);
        if (StringUtils.isNotEmpty(properData)) {
            maxByte = Integer.parseInt(properData);
        } else {
            maxByte = 0;
        }
        fontCount = 0;
        tagFlag = false;
        keepData = new StringBuilder();
        specificFlag = false;
        inTag = false;
    }

    /**
     * タグ文字に対しての処理.
     * @param ch 文字情報
     * @return 変換文字列情報
     */
    private String createByTagChar(char ch) {
        StringBuilder value = new StringBuilder();
        if (specificFlag && keepData.length() > 0) {
            appendKeepData(value);
        }
        tagFlag = (ch == R_PAREN);
        fontCount = 0;

        return value.append(ch).toString();
    }

    /**
     * &文字に対しての処理.
     *
     * @param ch 文字情報
     * @return 変換文字列情報
     */
    private String createByAndChar(char ch) {
        StringBuilder value = new StringBuilder();
        // &の後に&が入力されている -> 実態参照ではない
        if (ch == AND && keepData.length() > 0) {
            appendKeepData(value);
        }

        keepData.append(ch);
        if (StringUtils.equals(keepData.toString(), LT)
                || StringUtils.equals(keepData.toString(), GT)
                || StringUtils.equals(keepData.toString(), NBSP)) {
            // 1文字として計算する
            fontCount++;
            appendWBRTag(value);
            value.append(keepData);
            keepData = new StringBuilder();
            specificFlag = false;
        } else if (keepData.length() > MAX_LENGTH_ENTITY_REFERENCE) {
            appendKeepData(value);
            specificFlag = false;
        } else if (ch == SEMICOLON) {
            // 2バイト文字として計算する
            fontCount += 2;
            value.append(keepData);
            keepData = new StringBuilder();
            specificFlag = false;
        }
        return value.toString();
    }

    /**
     * 通常文字に対しての処理.
     *
     * @param ch 文字情報
     * @return 変換文字列情報
     */
    private String createByNormalChar(char ch) {
        StringBuilder value = new StringBuilder();
        if (tagFlag) {
            fontCount += String.valueOf(ch).getBytes().length;
        }
        appendWBRTag(value);

        return value.append(ch).toString();
    }

    /**
     * 保存データを通常文字列として処理する.
     * @param value 変換文字列情報
     */
    private void appendKeepData(StringBuilder value) {
        for (int y = 0; y < keepData.length(); y++) {
            char keepCh = keepData.charAt(y);
            fontCount += String.valueOf(keepCh).getBytes().length;
            appendWBRTag(value);
            value.append(keepCh);
        }
        keepData = new StringBuilder();
    }

    /**
     * 文字バイト数を判定し、自動改行タグを挿入する.
     * @param value 変換文字列情報
     */
    private void appendWBRTag(StringBuilder value) {
        if (fontCount > maxByte && !inTag) {
            value.append(WBR);
            fontCount = 1;
        }
    }
}
