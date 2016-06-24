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
package jp.co.opentone.bsol.linkbinder.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.DistTemplateHeaderDao;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeader;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderCreate;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderDelete;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderUpdate;

/**
 *  Distributionテンプレートデータを操作するDaoクラスの実装.
 *
 * @author opentone
 *
 */
@Repository
public class DistTemplateHeaderDaoImpl
    extends DistTemplateDaoBaseImpl implements DistTemplateHeaderDao {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 3339304776624105424L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateHeaderDao#findByProjectId(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DistTemplateHeader> findByProjectId(String id) {
        ArgumentValidator.validateNotNull(id);
        ArgumentValidator.validateNotEmpty(id);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("projectId", id);
        return (List<DistTemplateHeader>) getSqlMapClientTemplate().queryForList(
                    "distTemplateHeader.findByProjectId", paramMap);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateHeaderDao#findById(java.lang.Long)
     */
    @Override
    public DistTemplateHeader findById(Long id) {
        ArgumentValidator.validateNotNull(id);
        ArgumentValidator.validateGreaterThan(id, 0L);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("id", id);
        return (DistTemplateHeader) getSqlMapClientTemplate().queryForObject(
                    "distTemplateHeader.findById", paramMap);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateHeaderDao#create(jp.co.opentone.bsol.mer.dto.DistTemplateHeader)
     */
    @Override
    public Long create(DistTemplateHeaderCreate distTemplateHeader) {
        ArgumentValidator.validateNotNull(distTemplateHeader);
        ArgumentValidator.validateNotEmpty(distTemplateHeader.getProjectId(), "projectId");
        ArgumentValidator.validateNotEmpty(distTemplateHeader.getEmpNo(), "empNo");
        ArgumentValidator.validateNotEmpty(distTemplateHeader.getTemplateCd(), "templateCd");
        ArgumentValidator.validateNotEmpty(distTemplateHeader.getName(), "name");
        ArgumentValidator.validateNotEmpty(distTemplateHeader.getCreatedBy(), "createdBy");
        ArgumentValidator.validateNotNull(distTemplateHeader.getCreatedAt(), "createdAt");
        ArgumentValidator.validateNotEmpty(distTemplateHeader.getUpdatedBy(), "updatedBy");
        ArgumentValidator.validateNotNull(distTemplateHeader.getUpdatedAt(), "updatedAt");
        return (Long) getSqlMapClientTemplate()
                .insert("distTemplateHeader.create", distTemplateHeader);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateHeaderDao#delete(jp.co.opentone.bsol.mer.dto.DistTemplateHeader)
     */
    @Override
    public Integer delete(DistTemplateHeaderDelete distTemplateHeader) {
        ArgumentValidator.validateNotNull(distTemplateHeader);
        ArgumentValidator.validateGreaterThan(distTemplateHeader.getId(), 0L);
        ArgumentValidator.validateNotEmpty(distTemplateHeader.getProjectId(), "projectId");
        ArgumentValidator.validateNotEmpty(distTemplateHeader.getUpdatedBy(), "updatedBy");
        ArgumentValidator.validateNotNull(distTemplateHeader.getUpdatedAt(), "updatedAt");
        ArgumentValidator.validateNotNull(distTemplateHeader.getVersionNo(), "versionNo");

        int count = getSqlMapClientTemplate().update(
                "distTemplateHeader.deleteById", distTemplateHeader);
        return Integer.valueOf(count);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateHeaderDao#update(jp.co.opentone.bsol.mer.dto.DistTemplateHeader)
     */
    @Override
    public Integer update(DistTemplateHeaderUpdate distTemplateHeader) {
        ArgumentValidator.validateNotNull(distTemplateHeader);
        ArgumentValidator.validateNotNull(distTemplateHeader.getId());
        ArgumentValidator.validateGreaterThan(distTemplateHeader.getId(), 0L);
        ArgumentValidator.validateNotEmpty(distTemplateHeader.getProjectId(), "projectId");
        ArgumentValidator.validateNotEmpty(distTemplateHeader.getName(), "name");
        ArgumentValidator.validateNotEmpty(distTemplateHeader.getUpdatedBy(), "updatedBy");
        ArgumentValidator.validateNotNull(distTemplateHeader.getUpdatedAt(), "updatedAt");
        ArgumentValidator.validateNotNull(distTemplateHeader.getVersionNo(), "versionNo");

        int count = getSqlMapClientTemplate().update(
                "distTemplateHeader.update", distTemplateHeader);
        return Integer.valueOf(count);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateHeaderDao#findByProjectIdForUpdate(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public String getTemplateCodeByProject(String projectId) {
        ArgumentValidator.validateNotNull(projectId);

        HashMap<Object, Object> param = new HashMap<Object, Object>();
        param.put("projectId", projectId);

        String maxTemplateCd = null;
        List<String> templateCds = getSqlMapClientTemplate().queryForList(
                "distTemplateHeader.getTemplateCodeByProject", param);
        if (null != templateCds) {
            if (0 < templateCds.size()) {
                maxTemplateCd = templateCds.get(templateCds.size() - 1);
            } else if (0 == templateCds.size()) {
                maxTemplateCd = "0";
            }
        }
        // 使用出来る通番を採番
        int templateCd = Integer.valueOf(maxTemplateCd) + 1;
        return Integer.toString(templateCd);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateHeaderDao#findTemplateList(
     * java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DistTemplateHeader> findTemplateList(String projectId, String option1) {
        ArgumentValidator.validateNotNull(projectId);
        ArgumentValidator.validateNotNull(option1);

        HashMap<Object, Object> param = new HashMap<Object, Object>();
        param.put("projectId", projectId);
        param.put("option1", option1);

        return (List<DistTemplateHeader>) getSqlMapClientTemplate().queryForList(
                    "distTemplateHeader.findTemplateList", param);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateHeaderDao#(findTemplateSelect
     * java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DistTemplateHeader> findTemplateSelect(String projectId, String option1) {
        ArgumentValidator.validateNotNull(projectId);
        ArgumentValidator.validateNotNull(option1);

        HashMap<Object, Object> param = new HashMap<Object, Object>();
        param.put("projectId", projectId);
        param.put("option1", option1);

        return (List<DistTemplateHeader>) getSqlMapClientTemplate().queryForList(
                    "distTemplateHeader.findTemplateSelect", param);
    }
}
