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

/**
 * JSON変換用の活動単位Dto.
 *
 * @author opentone
 *
 */
public class CorresponGroupJSON extends AbstractDto {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6036179106600911229L;

    /**
     * Id.
     * <p>
     * [v_correspon_group.id]
     * </p>
     */
    private Long id;

    /**
     * Name.
     * <p>
     * [v_correspon_group.name]
     * </p>
     */
    private String name;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponGroupJSON() {
    }

    /**
     * 活動単位からJSON変換用の活動単位を作成する.
     * @param group 活動単位
     * @return JSON変換用の活動単位
     */
    public static CorresponGroupJSON newInstance(CorresponGroup group) {
        CorresponGroupJSON json = new CorresponGroupJSON();
        if (group != null) {
            json.setId(group.getId());
            json.setName(group.getName());
        }
        return json;
    }

    /**
     * id の値を返す.
     * <p>
     * [v_correspon_group.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [v_correspon_group.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * name の値を返す.
     * <p>
     * [v_correspon_group.name]
     * </p>
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * name の値を設定する.
     * <p>
     * [v_correspon_group.name]
     * </p>
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
    }
}
