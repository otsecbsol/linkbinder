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


import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.util.RichTextUtil;


/**
 * HTML形式への変換用ユーティリティクラス.
 * @author opentone
 */
public class CorresponServiceGeneratorUtil extends CorresponHTMLGeneratorUtil {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 5493124701735261961L;

    /**
     * コレポン文書一覧からの呼び出しフラグ（ZIPダウンロード）.
     */
    private boolean zip;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponServiceGeneratorUtil() {
    }

    /**
     * コレポン文書一覧からの呼び出しフラグをセットして生成する.
     * @param zip コレポン文書一覧からの呼び出しフラグ
     */
    public CorresponServiceGeneratorUtil(boolean zip) {
        this.zip = zip;
    }

    /**
     * コレポン文書一覧からの呼び出しフラグを取得する.
     * @return コレポン文書一覧からの呼び出しフラグ
     */
    public boolean isZip() {
        return zip;
    }

    /**
     * コレポン文書一覧からの呼び出しフラグを設定する.
     * @param zip コレポン文書一覧からの呼び出しフラグ
     */
    public void setZip(boolean zip) {
        this.zip = zip;
    }

    /**
     * Deadline For Replyを表示するかどうか判定します.
     * @param replyRequired 返信要否
     * @return true：表示する
     */
    public boolean isViewDeadline(ReplyRequired replyRequired) {
        return ReplyRequired.YES.equals(replyRequired);
    }

    /**
     * コレポン文書の本文整形して返す.
     * @param correspon コレポン文書
     * @return 整形後の本文
     */
    public String formatBody(Correspon correspon) {
        RichTextUtil u = new RichTextUtil();
        return u.createRichText(correspon.getBody());
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponHTMLGeneratorUtil#getIconPathName()
     */
    @Override
    public String getIconPathName() {
        String path;
        if (isZip()) {
            path = super.getIconPathName();
            path = path.replaceFirst("https", "http");
        } else {
            path = getContextURL() + getIconName();
        }
        return path;
    }
}
