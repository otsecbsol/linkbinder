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
package jp.co.opentone.bsol.linkbinder.validation.groups;

import java.util.ArrayList;
import java.util.List;

import javax.validation.groups.Default;

import org.apache.commons.lang.StringUtils;

/**
 * JSR-303のValidationGroupを表す文字列を生成する.
 * @author opentone
 */
public class ValidationGroupBuilder {

    /** Validation Groupのインターフェイスを格納するリスト. */
    private List<Class<?>> groups = new ArrayList<Class<?>>();

    /**
     * 空のインスタンスを生成する.
     */
    public ValidationGroupBuilder() {
    }

    /**
     * {@link Default}を格納したこのクラスのインスタンスを返す.
     * @return このクラスのインスタンス
     */
    public ValidationGroupBuilder defaultGroup() {
        return add(Default.class);
    }

    /**
     * {@link SkipValidationGroup}を格納したこのクラスのインスタンスを返す.
     * @return このクラスのインスタンス
     */
    public ValidationGroupBuilder skipValidationGroup() {
        return add(SkipValidationGroup.class);
    }

    /**
     * {@link CreateGroup}を格納したこのクラスのインスタンスを返す.
     * @return このクラスのインスタンス
     */
    public ValidationGroupBuilder createGroup() {
        return add(CreateGroup.class);
    }

    /**
     * {@link UpdateGroup}を格納したこのクラスのインスタンスを返す.
     * @return このクラスのインスタンス
     */
    public ValidationGroupBuilder updateGroup() {
        return add(UpdateGroup.class);
    }

    /**
     * Validation Groupを追加する.
     * @param groupClass Validation Groupを表すクラス
     * @return このクラスのインスタンス
     */
    public ValidationGroupBuilder add(Class<?> groupClass) {
        groups.add(groupClass);
        return this;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        List<String> classNames = new ArrayList<String>();
        for (Class<?> clazz : groups) {
            classNames.add(clazz.getName());
        }

        return StringUtils.join(classNames, ',');
    }
}
