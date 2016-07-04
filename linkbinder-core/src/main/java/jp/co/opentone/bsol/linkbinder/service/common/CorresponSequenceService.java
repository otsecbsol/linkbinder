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
package jp.co.opentone.bsol.linkbinder.service.common;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;

/**
 * このサービスではコレポン文書の発番に関する処理を提供する.
 * @author opentone
 */
public interface CorresponSequenceService extends IService {

    /**
     * コレポン文書番号を発番する.
     * @param correspon
     *            コレポン文書
     * @return コレポン文書番号
     * @throws ServiceAbortException 発番に失敗したときエラー
     */
    String getCorresponNo(Correspon correspon) throws ServiceAbortException;
}
