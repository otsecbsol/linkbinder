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
 * JSON変換用の活動単位ごとのユーザーDto.
 *
 * @author opentone
 *
 */
public class CorresponGroupUserDistributionJSON extends AbstractDto {
    /**
     *
     */
    private static final long serialVersionUID = -4572771574611696695L;

    /**
     * Emp no.
     */
    private String empNo;

    /**
     * Label.
     */
    private String label;

    /**
     * Label with role.
     */
    private String labelWithRole;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponGroupUserDistributionJSON() {
    }

    /**
     * ユーザーからJSON変換用のユーザーを作成する.
     * @param groupUser グループユーザー
     * @return JSON変換用のユーザー
     */
    public static CorresponGroupUserDistributionJSON newInstance(
            CorresponGroupUser groupUser) {
        CorresponGroupUserDistributionJSON jsonUser = new CorresponGroupUserDistributionJSON();
        Long id = groupUser.getCorresponGroup().getId();
        String empNo = groupUser.getUser().getEmpNo();

        jsonUser.setLabel(groupUser.getLabelGroupUser(id));
        jsonUser.setLabelWithRole(groupUser.getLabelWithRole(id));
        jsonUser.setEmpNo(empNo);
        return jsonUser;
    }

    /**
     * empNo の値を返す.
     * @return empNo
     */
    public String getEmpNo() {
        return empNo;
    }

    /**
     * empNo の値を設定する.
     * @param empNo
     *            empNo
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * label の値を返す.
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * label の値を設定する.
     * @param label label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * labelWithRole の値を返す.
     * @return labelWithRole
     */
    public String getLabelWithRole() {
        return labelWithRole;
    }

    /**
     * labelWithRole の値を設定する.
     * @param labelWithRole labelWithRole
     */
    public void setLabelWithRole(String labelWithRole) {
        this.labelWithRole = labelWithRole;
    }

    /**
     * このオブエジェクトが保持する内容を全てクリアする.
     */
    public void clearProperties() {
        empNo = null;
        labelWithRole = null;
    }
}