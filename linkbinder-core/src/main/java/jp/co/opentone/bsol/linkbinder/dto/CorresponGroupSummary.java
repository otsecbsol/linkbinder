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
 * テーブル [v_correspon] のコレポン文書種別と活動単位でのサマリ情報を表すDto.
 *
 * @author opentone
 *
 */
public class CorresponGroupSummary extends AbstractDto {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -2701514187857462809L;

    /**
     * コレポン種別.
     */
    private CorresponType corresponType;

    /**
     * サマリ情報：Toに指定されている数.
     */
    private Long toCount;

    /**
     * サマリ情報：Ccに指定されている数.
     */
    private Long ccCount;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponGroupSummary() {
    }

    /**
     * corresponTypeを取得します.
     * @return the corresponType
     */
    public CorresponType getCorresponType() {
        return corresponType;
    }

    /**
     * corresponTypeを設定します.
     * @param corresponType the corresponType to set
     */
    public void setCorresponType(CorresponType corresponType) {
        this.corresponType = corresponType;
    }

    /**
     * toCountを取得します.
     * @return the toCount
     */
    public Long getToCount() {
        return toCount;
    }

    /**
     * toCountを設定します.
     * @param toCount the toCount to set
     */
    public void setToCount(Long toCount) {
        this.toCount = toCount;
    }

    /**
     * ccCountを取得します.
     * @return the ccCount
     */
    public Long getCcCount() {
        return ccCount;
    }

    /**
     * ccCountを設定します.
     * @param ccCount the ccCount to set
     */
    public void setCcCount(Long ccCount) {
        this.ccCount = ccCount;
    }
}
