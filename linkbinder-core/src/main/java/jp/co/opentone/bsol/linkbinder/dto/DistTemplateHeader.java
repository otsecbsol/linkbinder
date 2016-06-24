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

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.dao.VersioningEntity;
import jp.co.opentone.bsol.linkbinder.dto.code.DistributionType;

/**
 * DistributionテンプレートヘッダーDto.
 *
 * @author opentone
 *
 */
@Component
public class DistTemplateHeader extends DistTemplateBase
    implements DistTemplateHeaderCreate,
               DistTemplateHeaderUpdate,
               DistTemplateHeaderDelete,
               VersioningEntity {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 4098351541485200677L;

    /**
     * ID.
     */
    private Long id;

    /**
     * プロジェクトID.
     */
    private String projectId;

    /**
     * 従業員番号.
     */
    private String empNo;

    /**
     * テンプレートコード.
     */
    private String templateCd;

    /**
     * 名称.
     */
    /**
     * テンプレート名.
     */
    private String name;

    /**
     * オプション情報１.
     */
    private String option1;

    /**
     * オプション情報2.
     */
    private String option2;

    /**
     * オプション情報3.
     */
    private String option3;

    /**
     * グループ.
     */
    private List<DistTemplateGroup> distTemplateGroups = new ArrayList<DistTemplateGroup>();

    /**
     * Distribution情報.
     */
    private DistributionInfo distributionInfo;

    /**
     * idの値を格納する.
     *
     * @param id
     *            ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * idの値を取得する.
     *
     * @return id
     *            ID
     */
    public Long getId() {
        return id;
    }

    /**
     * projectIdの値を格納する.
     *
     * @param projectId
     *            プロジェクトID
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * projectIdの値を取得する.
     *
     * @return projectId
     *            プロジェクトID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * empNoの値を格納する.
     *
     * @param empNo
     *            従業員番号
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * empNoの値を取得する.
     *
     * @return empNo
     *            従業員番号
     */
    public String getEmpNo() {
        return empNo;
    }

    /**
     * templateCdの値を格納する.
     *
     * @param templateCd
     *            テンプレートコード
     */
    public void setTemplateCd(String templateCd) {
        this.templateCd = templateCd;
    }

    /**
     * templateCdの値を取得する.
     *
     * @return templateCd
     *            テンプレートコード
     */
    public String getTemplateCd() {
        return templateCd;
    }

    /**
     * 名称を格納する.
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 名称を取得する.
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * option1を格納する.
     * @param option1 オプション情報１(FWBS No.)
     */
    public void setOption1(String option1) {
        this.option1 = option1;
    }

    /**
     * option1を取得する.
     * @return オプション情報１(FWBS No.)
     */
    public String getOption1() {
        return option1;
    }

    /**
     * option2を格納する.
     * @param option2 オプション情報２
     */
    public void setOption2(String option2) {
        this.option2 = option2;
    }

    /**
     * option2を取得する.
     * @return オプション情報２
     */
    public String getOption2() {
        return option2;
    }

    /**
     * option3を格納する.
     * @param option3 オプション情報３
     */
    public void setOption3(String option3) {
        this.option3 = option3;
    }

    /**
     * option3を取得する.
     * @return オプション情報３
     */
    public String getOption3() {
        return option3;
    }

    /**
     * Distributionテンプレート活動単位一覧を格納する.
     * @param distrTemplateGroups Distributionテンプレート活動単位一覧
     */
    public void setDistTemplateGroups(List<DistTemplateGroup> distrTemplateGroups) {
        this.distTemplateGroups = distrTemplateGroups;
    }

    /**
     * Distributionテンプレート活動単位一覧を取得する.
     * @return distTemplateGroups Distributionテンプレート活動単位一覧
     */
    public List<DistTemplateGroup> getDistTemplateGroups() {
        return distTemplateGroups;
    }

    /**
     * Distribution情報の値を格納する.
     * @param distributionInfo Distribution情報
     */
    public void setDistributionInfo(DistributionInfo distributionInfo) {
        this.distributionInfo = distributionInfo;
    }

    /**
     * Distribution情報の値を取得する.
     * @return distributionInfo Distribution情報
     */
    public DistributionInfo getDistributionInfo() {
        return distributionInfo;
    }

    /**
     * このオブジェクトが保持する宛先のうち、Toだけを抽出して返す.
     * 編集モードが削除(DELETE)であるものは除外する.
     * @see UpdateMode
     * @return 宛先(To)
     */
    public List<DistTemplateGroup> getToDistTemplateGroups() {
        List<DistTemplateGroup> result = new ArrayList<DistTemplateGroup>();
        List<DistTemplateGroup> addresses = getDistTemplateGroups();
        if (addresses == null) {
            return result;
        }

        for (DistTemplateGroup dt : addresses) {
            if (dt.getDistributionType() == DistributionType.TO
                && dt.getMode() != UpdateMode.DELETE) {
                result.add(dt);
            }
        }
        return result;
    }

    /**
     * 指定したグループIDがリストの何番目に設定されているかを取得する.
     * @param groupId グループID.(DB上のレコードID)
     * @return 0から始まるインデックス. 該当無い場合は-1を返す.
     */
    public int getDistTemplateGroupIndex(long groupId) {
        int resultIndex = -1;
        List<DistTemplateGroup> groupList = getDistTemplateGroups();
        if (null != groupList) {
            for (int i = 0; i < groupList.size(); i++) {
                DistTemplateGroup group = groupList.get(i);
                if (null != group.getId() && groupId == group.getId().longValue()) {
                    resultIndex = i;
                    break;
                }
            }
        }
        return resultIndex;
    }

    /**
     * このオブジェクトが保持する宛先のうち、Ccだけを抽出して返す.
     * 編集モードが削除(DELETE)であるものは除外する.
     * @see UpdateMode
     * @return 宛先(Cc)
     */
    public List<DistTemplateGroup> getCcDistTemplateGroups() {
        List<DistTemplateGroup> result = new ArrayList<DistTemplateGroup>();
        List<DistTemplateGroup> addresses = getDistTemplateGroups();
        if (addresses == null) {
            return result;
        }

        for (DistTemplateGroup dt : addresses) {
            if (dt.getDistributionType() == DistributionType.CC
                && dt.getMode() != UpdateMode.DELETE) {
                result.add(dt);
            }
        }
        return result;
    }

    /* (非 Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public DistTemplateHeader clone() {
        DistTemplateHeader clone = (DistTemplateHeader) super.clone();
        clone.setId(cloneField(id));
        clone.setProjectId(projectId);
        clone.setEmpNo(empNo);
        clone.setTemplateCd(templateCd);
        clone.setName(name);
        clone.setOption1(option1);
        clone.setOption2(option2);
        clone.setOption3(option3);
        clone.setDistTemplateGroups(cloneList(distTemplateGroups));
        if (distributionInfo != null) {
            clone.setDistributionInfo(distributionInfo.clone());
        }
        return clone;
    }

    /**
     * DistTemplateHeaderCreateインスタンスを作成します.
     * @return DistTemplateHeaderCreateインスタンス
     */
    public static DistTemplateHeaderCreate newDistTemplateHeaderCreate() {
        return new DistTemplateHeader();
    }

    /**
     * DistTemplateHeaderUpdateインスタンスを作成します.
     * @return DistTemplateHeaderUpdateインスタンス
     */
    public static DistTemplateHeaderUpdate newDistTemplateHeaderUpdate() {
        return new DistTemplateHeader();
    }

    /**
     * DistTemplateHeaderDeleteインスタンスを作成します.
     * @return DistTemplateHeaderDeleteインスタンス
     */
    public static DistTemplateHeaderDelete newDistTemplateHeaderDelete() {
        return new DistTemplateHeader();
    }
}
