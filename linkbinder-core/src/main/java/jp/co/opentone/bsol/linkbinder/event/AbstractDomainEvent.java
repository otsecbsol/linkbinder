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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jp.co.opentone.bsol.framework.core.util.DateUtil;

/**
 * ドメインイベントの抽象クラス.
 * @author opentone
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS)
public abstract class AbstractDomainEvent implements DomainEvent {

    private Date occuredAt;

    public AbstractDomainEvent() {
        this.occuredAt = DateUtil.getNow();
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.event.DomainEvent#getEventName()
     */
    @Override
    @JsonIgnore
    public String getEventName() {
        return getClass().getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getOccuredAt() {
        return this.occuredAt;
    }
}
