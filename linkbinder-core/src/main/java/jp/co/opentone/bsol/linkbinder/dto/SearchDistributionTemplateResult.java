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

import java.util.List;

/**
 * Distributionテンプレート検索結果Dto.
 *
 * @author opentone
 *
 */
public class SearchDistributionTemplateResult extends AbstractDto {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -1814028543420048894L;

    /**
     * 検索条件.
     */
    private SearchDistributionTemplate condition;

    /**
     * 検索条件に該当する全件数.
     */
    private int itemTotalSize;

    /**
     * 表示対象のページ番号.
     */
    private int pageNum;

    /**
     * 表示対象のページに含まれる件数.
     */
    private int itemPageSize;

    /**
     * 検索結果リスト.
     */
    private List<DistTemplateHeader> list;

    /**
     * 検索条件を取得します.
     * @return 検索条件.
     */
    public SearchDistributionTemplate getCondition() {
        return condition;
    }

    /**
     * 検索条件を設定します.
     * @param condition 検索条件.
     */
    public void setCondition(SearchDistributionTemplate condition) {
        this.condition = condition;
        //TODO
        setPageNum(condition.getPageNo());
    }

    /**
     * 検索条件に該当する全件数を取得します.
     * @return 検索条件に該当する全件数.
     */
    public int getItemTotalSize() {
        return itemTotalSize;
    }

    /**
     * 検索条件に該当する全件数を設定します.
     * @param itemTotalSize 検索条件に該当する全件数.
     */
    public void setItemTotalSize(int itemTotalSize) {
        this.itemTotalSize = itemTotalSize;
    }

    /**
     * 表示対象のページ番号を取得します.
     * @return 表示対象のページ番号.
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * 表示対象のページ番号を設定します.
     * @param pageNum 表示対象のページ番号.
     */
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * 表示対象のページに含まれる件数を取得します.
     * @return 表示対象のページに含まれる件数.
     */
    public int getItemPageSize() {
        return itemPageSize;
    }

    /**
     * 表示対象のページに含まれる件数を設定します.
     * @param itemPageSize 表示対象のページに含まれる件数.
     */
    public void setItemPageSize(int itemPageSize) {
        this.itemPageSize = itemPageSize;
    }

    /**
     * 検索結果リストを取得します.
     * @return 検索結果リスト.
     */
    public List<DistTemplateHeader> getList() {
        return list;
    }

    /**
     * 検索結果リストを設定します.
     * @param list 検索結果リスト.
     */
    public void setList(List<DistTemplateHeader> list) {
        this.list = list;
        if (null != list) {
            setItemPageSize(list.size());
        }
    }
    /**
     * 検索結果リストの件数を取得します.
     * @return 件数.
     */
    public int getListSize() {
        return (null == this.list) ? 0 : (this.list.size());
    }
}
