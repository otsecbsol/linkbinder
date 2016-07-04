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
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;

/**
 * このサービスでは既読・未読に関する処理を提供する.
 * @author opentone
 */
public interface CorresponReadStatusService extends IService {
    /**
     * コレポン文書をコレポンIDで既読・未読状態に更新する.
     * @param id
     *            コレポン文書ID
     * @param readStatus 読み込み状態
     * @return 更新件数
     * @throws ServiceAbortException 保存に失敗したときエラー
     */
    Integer updateReadStatusByCorresponId(Long id , ReadStatus readStatus)
                                                       throws ServiceAbortException;
    /**
     * コレポン文書を既読・未読状態に更新する.
     * 既読・未読IDで更新します.
     * @param id
     *            コレポン文書ID
     * @param readStatus 読み込み状態
     * @return 登録・更新対象既読・未読ID
     * @throws ServiceAbortException 保存に失敗したときエラー
     */
    Long updateReadStatusById(Long id , ReadStatus readStatus)
                                                     throws ServiceAbortException;
}
