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
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;

/**
 * 添付ファイルをダウンロード可能なページ.
 * @author opentone
 */
public interface AttachmentDownloadablePage extends Page {

    /**
     * ダウンロード対象の添付ファイル情報を返す.
     * @return 添付ファイル情報
     */
    AttachmentInfo getDownloadingAttachmentInfo();

    /**
     * View関連処理のためのヘルパーオブジェクトを返す.
     * @return ヘルパーオブジェクト
     */
    ViewHelper getViewHelper();
}
