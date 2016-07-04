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
 * テーブル [v_correspon_group] の情報に、一覧表示用の情報を追加したDto.
 *
 * @author opentone
 *
 */
public class SearchCorresponGroupResult extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 7007732574843812644L;

    /**
     * このクラスのオブジェクトの文字列表現から除外するフィールド名.
     */
    private static final Set<String> TO_STRING_IGNORE_FIELDS;
    static {
        Set<String> fields = new HashSet<String>();
        fields.add("corresponGroupList");

        TO_STRING_IGNORE_FIELDS = Collections.unmodifiableSet(fields);
    }

    /**
     * コレポン文書種別リスト.
     */
    private List<CorresponGroup> corresponGroupList;

    /**
     * 表示件数.
     */
    private int count;

    /**
     * 空のインスタンスを生成する.
     */
    public SearchCorresponGroupResult() {
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
     * 活動単位リストをセットする.
     * @param corresponGroupList
     *            活動単位リスト
     */
    public void setCorresponGroupList(List<CorresponGroup> corresponGroupList) {
        this.corresponGroupList = corresponGroupList;
    }

    /**
     * 活動単位リストを取得する.
     * @return 活動単位リスト
     */
    public List<CorresponGroup> getCorresponGroupList() {
        return corresponGroupList;
    }

    /**
     * 表示件数をセットする.
     * @param count
     *            表示件数
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * 表示件数を取得する.
     * @return 表示件数
     */
    public int getCount() {
        return count;
    }

}
