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

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.DistTemplateUserDao;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateUserCreate;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateUserDelete;

/**
 * Distributionテンプレートに設定されるユーザーデータを操作するDaoクラスの実装.
 *
 * @author opentone
 *
 */
@Repository
public class DistTemplateUserDaoImpl extends DistTemplateDaoBaseImpl
    implements DistTemplateUserDao {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 7601286564659500915L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateUserDao#create(jp.co.opentone.bsol.mer.dto.DistTemplateUser)
     */
    @Override
    public Long create(DistTemplateUserCreate distTemplateUser) {
        ArgumentValidator.validateNotNull(distTemplateUser, "distTemplateUser");
        ArgumentValidator.validateNotNull(distTemplateUser.getDistTemplateGroupId(),
            "distTemplateGroupId");
        ArgumentValidator.validateNotNull(distTemplateUser.getOrderNo(), "orderNo");
        ArgumentValidator.validateNotNull(distTemplateUser.getEmpNo(), "empNo");
        ArgumentValidator.validateNotEmpty(distTemplateUser.getCreatedBy(), "createdBy");
        ArgumentValidator.validateNotNull(distTemplateUser.getCreatedAt(), "createdAt");
        ArgumentValidator.validateNotEmpty(distTemplateUser.getUpdatedBy(), "updatedBy");
        ArgumentValidator.validateNotNull(distTemplateUser.getUpdatedAt(), "updatedAt");

        Long id = (Long) getSqlMapClientTemplate().insert(
                "distTemplateUser.create", distTemplateUser);
        return id;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateUserDao
     *      #deleteByDistTemplateGroupId(jp.co.opentone.bsol.mer.dto.DistTemplateUser)
     */
    @Override
    public Integer deleteByDistTemplateGroupId(DistTemplateUserDelete distTemplateUser) {
        ArgumentValidator.validateNotNull(distTemplateUser, "distTemplateUser");
        ArgumentValidator.validateGreaterThan(distTemplateUser.getDistTemplateGroupId(),
                                                                0L, "distTemplateGroupId");
        ArgumentValidator.validateNotEmpty(distTemplateUser.getUpdatedBy(), "updatedBy");
        ArgumentValidator.validateNotNull(distTemplateUser.getUpdatedAt(), "updatedAt");

        int count = getSqlMapClientTemplate().update(
                "distTemplateUser.deleteByDistTemplateGroupId", distTemplateUser);
        return Integer.valueOf(count);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DistTemplateUserDao#userCount(java.lang.Long)
     */
    @Override
    public Integer userCount(Long distTempGroupId) {
        ArgumentValidator.validateNotNull(distTempGroupId, "distTempGroupId");
        Integer count =
            (Integer) getSqlMapClientTemplate().
                queryForObject("distTemplateUser.userCount", distTempGroupId);
        return count;
    }
}
