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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * テーブル [v_project_user] の情報に、一覧表示用の情報を追加したDto.
 *
 * @author opentone
 *
 */
public class SearchUserResult extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -3794722628568549740L;

    /**
     * このクラスのオブジェクトの文字列表現から除外するフィールド名.
     */
    private static final Set<String> TO_STRING_IGNORE_FIELDS;
    static {
        Set<String> fields = new HashSet<String>();
        fields.add("userIndexes");

        TO_STRING_IGNORE_FIELDS = Collections.unmodifiableSet(fields);
    }

    /**
     * ユーザー一覧情報リスト.
     */
    private List<UserIndex> userIndexes;

    /**
     * 表示件数.
     */
    private int count;

    /**
     * 空のインスタンスを生成する.
     */
    public SearchUserResult() {
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dto.AbstractDto#isToStringIgnoreField(java.lang.String)
     */
    @Override
    public boolean isToStringIgnoreField(String fieldName) {
        return super.isToStringIgnoreField(fieldName)
                || TO_STRING_IGNORE_FIELDS.contains(fieldName);
    }

    /**
     * userIndexesを取得します.
     * @return the userIndexes
     */
    public List<UserIndex> getUserIndexes() {
        return userIndexes;
    }

    /**
     * userIndexesを設定します.
     * @param userIndexes the userIndexes to set
     */
    public void setUserIndexes(List<UserIndex> userIndexes) {
        this.userIndexes = userIndexes;
    }

    /**
     * countを取得します.
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * countを設定します.
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }
}
