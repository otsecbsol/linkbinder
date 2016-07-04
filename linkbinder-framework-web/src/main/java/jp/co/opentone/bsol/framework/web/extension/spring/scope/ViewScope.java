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
package jp.co.opentone.bsol.framework.web.extension.spring.scope;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

/**
 * JSF2.0の@ViewScopedと同様のスコープを実現する.
 * <p>
 * @author opentone
 */
public class ViewScope implements Scope {

    /** logger. */
    private static final Logger log = LoggerFactory.getLogger(ViewScope.class);

    Map<String, Object> getMap() {
        FacesContext c = FacesContext.getCurrentInstance();
        if (c == null) {
            return null;
        }
        if (c.getViewRoot() == null) {
            return null;
        }
        return c.getViewRoot().getViewMap();
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config
     *  .Scope#get(java.lang.String, org.springframework.beans.factory.ObjectFactory)
     */
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Object result = null;
        Map<String, Object> map = getMap();
        if (map != null) {
            if (map.containsKey(name)) {
                result = map.get(name);
                log.trace("Instance of bean '{}' exists in view scope.", name);
            } else {
                Object bean = objectFactory.getObject();
                log.trace("created instance on bean '{}' in view scope.", name);
                map.put(name, bean);
                result = bean;
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.Scope#getConversationId()
     */
    @Override
    public String getConversationId() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config
     *  .Scope#registerDestructionCallback(java.lang.String, java.lang.Runnable)
     */
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.Scope#remove(java.lang.String)
     */
    @Override
    public Object remove(String name) {
        Object result = null;
        Map<String, Object> map = getMap();
        if (map != null) {
            result = map.remove(name);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.Scope#resolveContextualObject(java.lang.String)
     */
    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }
}
