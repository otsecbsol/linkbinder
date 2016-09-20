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
package jp.co.opentone.bsol.linkbinder.service.correspon;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;

import java.util.List;

/**
 * 画像ファイルからのテキスト抽出を行うサービス.
 * @author opentone
 */
public interface ImageTextDetectionService extends IService {
    /**
     * 指定されたファイルからテキストを抽出する.
     * 抽出結果は引数のAttachmentオブジェクトのフィールドに格納される.
     * @param attachments 対象ファイル
     * @throws ServiceAbortException 抽出に失敗した場合
     */
    void detectTextAndFill(List<Attachment> attachments) throws ServiceAbortException;

    /**
     * テキスト抽出を利用できる場合はtrueを返す.
     * @return 利用可能な場合はtrue
     */
    boolean canUse();
}
