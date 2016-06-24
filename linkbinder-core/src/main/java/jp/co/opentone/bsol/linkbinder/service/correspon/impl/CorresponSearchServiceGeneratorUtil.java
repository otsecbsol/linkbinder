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
package jp.co.opentone.bsol.linkbinder.service.correspon.impl;

import java.util.Date;

import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponIndexHeader;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;

/**
 * HTML形式への変換用ユーティリティクラス.
 * @author opentone
 */
public class CorresponSearchServiceGeneratorUtil extends CorresponHTMLGeneratorUtil {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 5493124701735261961L;

    /**
     * 既読・未読ステータスが未読の場合のStyle.
     */
    private static final String UNREAD_STYLE = "<span class=\"unread\">%s</span>";

    /**
     * 表示するカラム情報.
     */
    private CorresponIndexHeader header;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponSearchServiceGeneratorUtil() {
    }

    /**
     * 表示するカラム情報をセットして生成する.
     * @param header ヘッダ情報
     */
    public CorresponSearchServiceGeneratorUtil(CorresponIndexHeader header) {
        this.setHeader(header);
    }

    /**
     * headerを取得します.
     * @return the header
     */
    public CorresponIndexHeader getHeader() {
        return header;
    }

    /**
     * headerを設定します.
     * @param header the header to set
     */
    public void setHeader(CorresponIndexHeader header) {
        this.header = header;
    }

    /**
     * カラムを表示するかどうか判定する.
     * @param name カラム名
     * @return カラム表示
     */
    public boolean isView(String name) {
        if (header == null) {
            return true;
        }

        return header.createListOnlyTrueProp().contains(name);
    }

    /**
     * 文書状態が無効かどうか判定する.
     * @param corresponStatus 文書状態
     * @return true:無効
     */
    public boolean isCanceled(CorresponStatus corresponStatus) {
        return CorresponStatus.CANCELED.equals(corresponStatus);
    }

    /**
     * 返信期限を過ぎているかどうか判定する.
     * @param deadline 返信期限
     * @return true:返信期限を過ぎている
     */
    public boolean isDeadline(Date deadline) {
        return DateUtil.isExpire(deadline);
    }

    /**
     * 既読・未読ステータスが未読の場合、スタイルを追加する.
     * @param value output用文字列
     * @param status 既読・未読ステータス
     * @return output用文字列
     */
    public String getUnreadStyle(String value, ReadStatus status) {
        if (ReadStatus.NEW.equals(status)) {
            return String.format(UNREAD_STYLE, value);
        }
        return value;
    }

    /**
     * To(Group)を表示形式に変換して返す.
     * @param c コレポン文書
     * @return To(Group)の表示形式
     */
    public Object getToGroup(Correspon c) {
        return encode(c.getToGroupName());
    }
}
