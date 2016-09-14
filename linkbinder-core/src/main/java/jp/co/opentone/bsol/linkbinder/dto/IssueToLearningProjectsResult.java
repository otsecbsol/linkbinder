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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 学習用プロジェクトへの公開結果.
 *
 * @author opentone
 *
 */
public class IssueToLearningProjectsResult extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 7007732574843812644L;

    /**
     * このクラスのオブジェクトの文字列表現から除外するフィールド名.
     */
    private static final Set<String> TO_STRING_IGNORE_FIELDS;
    static {
        Set<String> fields = new HashSet<>();
        TO_STRING_IGNORE_FIELDS = Collections.unmodifiableSet(fields);
    }

    public static final IssueToLearningProjectsResult EMPTY = new IssueToLearningProjectsResult();

    private List<Correspon> deletedCorresponList = new ArrayList<>();
    private List<Correspon> issuedCorresponList = new ArrayList<>();

    public List<Correspon> getDeletedCorresponList() {
        return deletedCorresponList;
    }

    public void setDeletedCorresponList(List<Correspon> deletedCorresponList) {
        this.deletedCorresponList = deletedCorresponList;
    }

    public List<Correspon> getIssuedCorresponList() {
        return issuedCorresponList;
    }

    public void setIssuedCorresponList(List<Correspon> issuedCorresponList) {
        this.issuedCorresponList = issuedCorresponList;
    }

    public void addDeletedCorresponList(List<Correspon> correspons) {
        deletedCorresponList.addAll(correspons);
    }

    public void addIssuedCorrespon(Correspon correspon) {
        issuedCorresponList.add(correspon);
    }
}
