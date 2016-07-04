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
package jp.co.opentone.bsol.linkbinder.dao.mock;

import java.util.ArrayList;
import java.util.List;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.SiteDao;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchSiteCondition;

/**
 *
 * @author opentone
 *
 */
public class SiteDaoMock extends AbstractDao<Site> implements SiteDao {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -4997253313308856984L;

    public SiteDaoMock() {
        super("mock");
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.SiteDao#find(jp.co.opentone.bsol.linkbinder.dto.SearchSiteCondition
     * )
     */
    public List<Site> find(SearchSiteCondition condition) {
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");
        List<Site> sites = new ArrayList<Site>();

        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setDeleteNo(0L);
        site.setVersionNo(1L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);

        sites.add(site);

        site = new Site();
        site.setId(2L);
        site.setSiteCd("SJK");
        site.setName("Shinjuku");
        site.setDeleteNo(0L);
        site.setVersionNo(1L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);

        sites.add(site);

        site = new Site();
        site.setId(3L);
        site.setSiteCd("JOG");
        site.setName("Jogi");
        site.setDeleteNo(0L);
        site.setVersionNo(1L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);

        sites.add(site);

        return sites;
    }

    @Override
    public Long create(Site entity) throws KeyDuplicateException {
        return null;
    }

    @Override
    public Integer delete(Site entity) throws KeyDuplicateException, StaleRecordException {
        return null;
    }

    /*
     * (.framework.core.dao * @see
     * GenericDao#findById(java.lang.Long)
     */
    @Override
    public Site findById(Long id) throws RecordNotFoundException {
        return null;
    }

    /*
     * .framework.core.daooc)
     * @see
     * GenericDao#findByIdForUpdate(java.lang.Long)
     */
    @Override
    public Site findByIdForUpdate(Long id) throws RecordNotFoundException {
        return null;
    }

    /*
     * .framework.core.daoJavadoc)
     * @see
     * jp.co..framework.core.daocore.dao.GenericDao#update(jp.co.opentone.bsol.framework
     * .core.dao.Entity)
     */
    @Override
    public Integer update(Site entity) throws KeyDuplicateException, StaleRecordException {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.SiteDao#count(jp.co.opentone.bsol.linkbinder.dto.SearchSiteCondition
     * )
     */
    public int count(SearchSiteCondition condition) {
        return 2;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.SiteDao#countCheck(jp.co.opentone.bsol.linkbinder.dto.condition
     * .SearchSiteCondition)
     */
    public int countCheck(SearchSiteCondition condition) {
        return 1;
    }

}
