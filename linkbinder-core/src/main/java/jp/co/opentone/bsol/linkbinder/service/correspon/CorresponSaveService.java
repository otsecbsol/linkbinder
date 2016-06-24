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
import jp.co.opentone.bsol.linkbinder.dto.Correspon;

/**
 * このサービスではコレポン文書に関する処理を提供する.
 * @author opentone
 */
public interface CorresponSaveService extends IService {
    /**
     * 指定されたコレポン文書を保存する.
     * @param correspon コレポン文書
     * @return 保存されたコレポン文書のID
     * @throws ServiceAbortException コレポン文書の保存に失敗
     */
    Long save(Correspon correspon) throws ServiceAbortException;
}
