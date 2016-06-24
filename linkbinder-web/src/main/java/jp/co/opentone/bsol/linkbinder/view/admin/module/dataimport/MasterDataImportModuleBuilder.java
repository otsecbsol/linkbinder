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
package jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportType;

/**
 * 取込モジュールビルダ.
 * @author opentone
 */
@Component
public class MasterDataImportModuleBuilder implements Serializable, ApplicationContextAware {

    /** Spring ApplicationContext. */
    private ApplicationContext context;
    /** このアプリケーションに定義されたすべての取込モジュール. */
    @SuppressWarnings("rawtypes")
    private Map<String, MasterDataImportModule> modules;

    /**
     * 初期化処理.
     */
    @PostConstruct
    public void initialize() {
        modules = context.getBeansOfType(MasterDataImportModule.class);
    }

    /**
     * 指定された取込データを処理できるモジュールを生成して返す.
     * @param importType 取込データ種別
     * @return モジュール
     */
    public MasterDataImportModule<?> build(MasterDataImportType importType) {
        return detect(importType)
                .orElseThrow(() -> new ApplicationFatalRuntimeException("moduleが見つかりません"));
    }

    /**
     * 取込データ種別を処理できるモジュールを探す.
     * @param type 取込データ種別
     * @return モジュール
     */
    @SuppressWarnings("rawtypes")
    protected Optional<MasterDataImportModule> detect(MasterDataImportType type) {
        return modules.values().stream()
            .filter(m -> m.accept(type))
            .findFirst();
    }

    /**
     * Spring ApplicationContextを設定する.
     */
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }
}
