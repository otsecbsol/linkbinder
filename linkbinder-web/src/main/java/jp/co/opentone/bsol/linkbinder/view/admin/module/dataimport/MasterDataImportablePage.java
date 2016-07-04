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
package jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport;

import java.util.List;

import jp.co.opentone.bsol.framework.web.view.Page;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportProcessType;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportType;

/**
 * マスタデータ取込可能なページ.
 * @author opentone
 */
public interface MasterDataImportablePage extends Page {

    /**
     * 取込ファイル情報を返す.
     * @return 取込ファイル
     */
    AttachmentInfo getImportFileInfo();
    /**
     * 取込ファイル情報を設定する.
     * @param importFileInfo 取込ファイル情報
     */
    void setImportFileInfo(AttachmentInfo importFileInfo);

    /**
     * 登録データ種別を返す.
     * @return 登録データ種別
     */
    MasterDataImportType getImportType();
    /**
     * 登録処理種別を設定する.
     * @return 登録処理種別
     */
    MasterDataImportProcessType getImportProcessType();

    /**
     * 取込処理が正常終了した場合のコールバック.
     */
    void importSucceeded();

    /**
     * 取込前の変換処理に失敗した場合のコールバック.
     * @param message メッセージリスト
     */
    void importParseFailed(List<String> message);
}
