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
package jp.co.opentone.bsol.linkbinder.view.correspon.attachment;

import jp.co.opentone.bsol.framework.web.view.Page;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;

/**
 * 添付ファイルから抽出したテキストを編集可能なページ
 * @author opentone
 */
public interface AttachmentExtractedTextEditablePage extends Page {

    /**
     * 編集ダイアログを表示する.
     * @param attachment 編集対象の添付ファイル
     * @return 遷移先
     */
    String showAttachmentExtractedTextEditDialog(AttachmentInfo attachment);

    /**
     * 編集内容を保存する.
     */
    void saveExtractedText();

    /**
     * 編集を取り消す.
     */
    void cancelExtractedTextEdit();

    /**
     * 編集中の添付ファイル情報を返す.
     * @return 添付ファイル情報
     */
    AttachmentInfo getEditingAttachment();

    /**
     * 検証グループ名を返す.
     * @return 検証グループ名
     */
    String getValidationGroups();
}
