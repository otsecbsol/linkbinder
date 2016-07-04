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
package jp.co.opentone.bsol.linkbinder.dao;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.Dao;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;

/**
 * SpringのContextからDaoを見つけるクラス.
 *
 * @author opentone
 *
 */
public class DaoFinder implements ApplicationContextAware {
    /**
     * Daoの実装クラスとMockクラスを切り替えるためのキー情報.
     */
    public static final String KEY_DAO_USE_MOCK = "dao.use.mock";

    /** logger. */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Spring frameworkのApplicationContext.
     * <p>
     * {@link AbstractService#getDao(Class)}で、Springの管理下にある Daoオブジェクトを取得するために利用
     * </p>
     */
    private ApplicationContext applicationContext;

    /**
     * 指定されたDaoインターフェイスの実装クラスのオブジェクトを返す.
     * @param <T>
     *            対象のDaoインターフェイス
     * @param daoClass
     *            対象のDaoインターフェイス
     * @return 実装クラスのオブジェクト
     */
    @SuppressWarnings("unchecked")
    public <T extends Dao> T getDao(Class<?> daoClass) {
        ArgumentValidator.validateNotNull(daoClass);

        String daoName = getDaoName(daoClass);
        if (log.isDebugEnabled()) {
            log.debug("use Dao = {}", daoName);
        }
        return (T) applicationContext.getBean(daoName);
    }

    private String getDaoName(Class<?> daoClass) {
        String daoName = daoClass.getSimpleName();
        String beanName =
            daoName.substring(0, 1).toLowerCase(Locale.getDefault()) + daoName.substring(1);

        // 実装クラスとMockクラスの切り替え
        if (isUseMock(daoName)) {
            return String.format("%sMock", beanName);
        } else {
            return String.format("%sImpl", beanName);
        }
    }

    private boolean isUseMock(String daoName) {
        String useMock = SystemConfig.getValue(KEY_DAO_USE_MOCK);
        if (log.isDebugEnabled()) {
            log.debug("{} = {}", KEY_DAO_USE_MOCK, useMock);
        }
        if (StringUtils.isNotEmpty(useMock) && Boolean.parseBoolean(useMock)) {
            String useMockDao = SystemConfig.getValue(KEY_DAO_USE_MOCK + '.' + daoName);
            if (log.isDebugEnabled()) {
                log.debug("{}.{} = {}", new Object[]{KEY_DAO_USE_MOCK, daoName, useMockDao});
            }
            return (StringUtils.isNotEmpty(useMockDao) && Boolean.parseBoolean(useMockDao));
        }
        return false;
    }

    public void setApplicationContext(ApplicationContext applicationcontext) throws BeansException {
        this.applicationContext = applicationcontext;
    }

}
