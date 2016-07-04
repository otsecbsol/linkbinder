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


/**
 * テーブル [v_project] の情報に、メニュー表示用の情報を追加したDto.
 *
 * @author opentone
 *
 */
public class ProjectSummary extends AbstractDto {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -4824035461650598994L;

    /**
     * プロジェクト.
     */
    private Project project;

    /**
     * サマリ情報：Attentionに指定されている数.
     */
    private Long attentionCount;

    /**
     * サマリ情報：Person in Chargeに指定されている数.
     */
    private Long personInChargeCount;

    /**
     * サマリ情報：Ccに指定されている数.
     */
    private Long ccCount;

    /**
     * サマリ情報：Person in Chargeの利用可否.
     */
    private boolean usePersonInCharge;

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectSummary() {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getAttentionCount() {
        return attentionCount;
    }

    public void setAttentionCount(Long attentionCount) {
        this.attentionCount = attentionCount;
    }

    public Long getPersonInChargeCount() {
        return personInChargeCount;
    }

    public void setPersonInChargeCount(Long personInChargeCount) {
        this.personInChargeCount = personInChargeCount;
    }

    public Long getCcCount() {
        return ccCount;
    }

    public void setCcCount(Long ccCount) {
        this.ccCount = ccCount;
    }

    public void setUsePersonInCharge(boolean usePersonInCharge) {
        this.usePersonInCharge = usePersonInCharge;
    }

    public boolean isUsePersonInCharge() {
        return usePersonInCharge;
    }
}
