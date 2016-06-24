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
 * このサービスではコレポン文書の検証に関する処理を提供する.
 * @author opentone
 */
public interface CorresponValidateService extends IService {
    /**
     * 指定されたコレポン文書を検証する.
     * @param correspon コレポン文書
     * @return true = 検証OK
     * @throws ServiceAbortException コレポン文書の検証に失敗
     */
    boolean validate(Correspon correspon) throws ServiceAbortException;

    /**
     * 宛先(To)の存在チェックを行う.
     * @param toAddressValues 宛先設定状況
     * @return true = 検証成功-宛先あり
     * @throws ServiceAbortException 検証失敗
     */
    boolean validateToAddress(String toAddressValues) throws ServiceAbortException;
}
