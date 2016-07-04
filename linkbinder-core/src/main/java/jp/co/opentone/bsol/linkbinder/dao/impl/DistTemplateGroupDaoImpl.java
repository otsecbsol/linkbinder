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
import jp.co.opentone.bsol.linkbinder.dao.DistTemplateGroupDao;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroup;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroupCreate;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroupDelete;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroupUpdate;

/**
 * {@link DistTemplateGroupDao}.
 *
 * @author opentone
 *
 */
@Repository
public class DistTemplateGroupDaoImpl
    extends DistTemplateDaoBaseImpl implements DistTemplateGroupDao {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 7299544266648261660L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateGroupDao#findByDistTemplateHeaderId(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DistTemplateGroup> findByDistTemplateHeaderId(Long id) {
        ArgumentValidator.validateNotNull(id);
        ArgumentValidator.validateGreaterThan(id, 0L);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("distTemplateHeaderId", id);

        List<DistTemplateGroup> list =
            (List<DistTemplateGroup>) getSqlMapClientTemplate().queryForList(
                "distTemplateGroup.findByDistTemplateHeaderId", paramMap);
        return list;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateGroupDao#create(jp.co.opentone.bsol.mer.dto.DistTemplateGroup)
     */
    @Override
    public Long create(DistTemplateGroupCreate distTemplateGroup) {
        ArgumentValidator.validateNotNull(distTemplateGroup);
        ArgumentValidator.validateGreaterThan(distTemplateGroup.getDistTemplateHeaderId(), 0L);
        ArgumentValidator.validateNotNull(distTemplateGroup.getDistributionType(),
                "distributionType");
        ArgumentValidator.validateGreaterThan(distTemplateGroup.getOrderNo(), 0L);
        ArgumentValidator.validateGreaterThan(distTemplateGroup.getGroupId(), 0L);
        ArgumentValidator.validateNotEmpty(distTemplateGroup.getCreatedBy(), "createdBy");
        ArgumentValidator.validateNotNull(distTemplateGroup.getCreatedAt(), "createdAt");
        ArgumentValidator.validateNotEmpty(distTemplateGroup.getUpdatedBy(), "updatedBy");
        ArgumentValidator.validateNotNull(distTemplateGroup.getUpdatedAt(), "updatedAt");

        Long id = (Long) getSqlMapClientTemplate().insert(
                "distTemplateGroup.create", distTemplateGroup);
        return id;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateGroupDao#delete(jp.co.opentone.bsol.mer.dto.DistTemplateGroup)
     */
    @Override
    public Integer delete(DistTemplateGroupDelete distTemplateGroup) {
        ArgumentValidator.validateNotNull(distTemplateGroup);
        ArgumentValidator.validateGreaterThan(distTemplateGroup.getId(), 0L);
        ArgumentValidator.validateNotEmpty(distTemplateGroup.getUpdatedBy(), "updatedBy");
        ArgumentValidator.validateNotNull(distTemplateGroup.getUpdatedAt(), "updatedAt");

        int count = getSqlMapClientTemplate().update(
                "distTemplateGroup.delete", distTemplateGroup);
        return Integer.valueOf(count);
    }


    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateGroupDao#update(jp.co.opentone.bsol.mer.dto.DistTemplateGroup)
     */
    @Override
    public boolean update(DistTemplateGroupUpdate distTemplateGroup) {
        ArgumentValidator.validateNotNull(distTemplateGroup);
        ArgumentValidator.validateGreaterThan(distTemplateGroup.getId(), 0L);
        ArgumentValidator.validateGreaterThan(distTemplateGroup.getDistTemplateHeaderId(), 0L);
        ArgumentValidator.validateNotNull(distTemplateGroup.getDistributionType(),
                "distributionType");
        ArgumentValidator.validateGreaterThan(distTemplateGroup.getOrderNo(), 0L);
        ArgumentValidator.validateGreaterThan(distTemplateGroup.getGroupId(), 0L);
        ArgumentValidator.validateNotEmpty(distTemplateGroup.getUpdatedBy(), "updatedBy");
        ArgumentValidator.validateNotNull(distTemplateGroup.getUpdatedAt(), "updatedAt");

        boolean result = false;
        if (1 == getSqlMapClientTemplate().update("distTemplateGroup.update", distTemplateGroup)) {
            result = true;
        }
        return result;
    }
}
