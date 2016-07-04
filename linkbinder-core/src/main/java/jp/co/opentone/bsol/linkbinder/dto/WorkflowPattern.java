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
package jp.co.opentone.bsol.linkbinder.dto;

import jp.co.opentone.bsol.framework.core.dao.Entity;


/**
 * 承認フローパターン.
 *
 * @author opentone
 *
 */
public class WorkflowPattern extends AbstractDto implements Entity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7643015093757886270L;

    /**
     * ID.
     */
    private Long id;
    /**
     * 承認フローコード.
     */
    private String workflowCd;
    /**
     * 名前.
     */
    private String name;
    /**
     * 説明.
     */
    private String description;

    /**
     * 空のインスタンスを生成する.
     */
    public WorkflowPattern() {
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param workflowCd the workflowCd to set
     */
    public void setWorkflowCd(String workflowCd) {
        this.workflowCd = workflowCd;
    }

    /**
     * @return the workflowCd
     */
    public String getWorkflowCd() {
        return workflowCd;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
