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
package jp.co.opentone.bsol.linkbinder.event;

import java.util.Date;

/**
 * このアプリケーションで発生したイベント.
 * @author opentone
 */
public interface DomainEvent {

    /**
     * イベント名を返す.
     * @return イベント名
     */
    String getEventName();

    /**
     * このイベントの発生日時を返す.
     * @return 発生日時
     */
    Date getOccuredAt();
}
