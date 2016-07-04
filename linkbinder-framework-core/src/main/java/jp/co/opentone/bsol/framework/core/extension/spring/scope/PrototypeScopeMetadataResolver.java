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
package jp.co.opentone.bsol.framework.core.extension.spring.scope;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;

/**
 * component-scanで登録されるbeanのscopeをprototype固定に設定する.
 * @author opentone
 */
public class PrototypeScopeMetadataResolver implements ScopeMetadataResolver {

    /*
     * (非 Javadoc)
     * @seeorg.springframework.context.annotation.ScopeMetadataResolver#
     * resolveScopeMetadata
     * (org.springframework.beans.factory.config.BeanDefinition)
     */
    @Override
    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        ScopeMetadata metadata = new ScopeMetadata();
        metadata.setScopeName(BeanDefinition.SCOPE_PROTOTYPE);

        return metadata;
    }
}
