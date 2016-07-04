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
package jp.co.opentone.bsol.linkbinder.service.rss;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;

/**
 * RSSに関する処理を提供する.
 * @author opentone
 */
public interface RSSService extends IService {
    /**
     * 対象ユーザ用のRSSを取得する.
     *
     * @param userId ユーザID
     * @param baseURL BaseURL(最後が'/'であること。例: http://localhost:8080/)
     * @throws ServiceAbortException configの値が不正
     * @return 対象ユーザ用のRSS
     */
    String getRSS(String userId, String baseURL)
        throws ServiceAbortException;
}
