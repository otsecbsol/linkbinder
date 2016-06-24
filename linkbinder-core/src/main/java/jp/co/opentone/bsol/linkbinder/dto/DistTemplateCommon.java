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

import java.util.Date;

/**
 * Distribution Templateの共通要素用IF.
 *
 * @author opentone
 *
 */
public interface DistTemplateCommon {
    /**
     * 作成者従業員番号を取得します.
     * @return 作成者従業員番号
     */
    String getCreatedBy();

    /**
     * 作成者従業員番号を設定します.
     * @param createdBy 作成者従業員番号
     */
    void setCreatedBy(String createdBy);

    /**
     * 作成日時を取得します.
     * @return 作成日時
     */
    Date getCreatedAt();

    /**
     * 作成日時を設定します.
     * @param createdAt 作成日時
     */
    void setCreatedAt(Date createdAt);

    /**
     * 現在日時を作成日時に設定します.
     * @return 設定した作成日時
     */
    Date setCreatedAtNow();

    /**
     * 更新者従業員番号を取得します.
     * @return 更新者従業員番号
     */
    String getUpdatedBy();

    /**
     * 更新者従業員番号を設定します.
     * @param createdBy 更新者従業員番号
     */
    void setUpdatedBy(String createdBy);
    /**
     * 更新日時を取得します.
     * @return 更新日時
     */
    Date getUpdatedAt();

    /**
     * 更新日時を設定します.
     * @param updatedAt 更新日時
     */
    void setUpdatedAt(Date updatedAt);

    /**
     * 現在日時を更新日時に設定します.
     * @return 設定した更新日時
     */
    Date setUpdatedAtNow();

    /**
     * バージョンNo.を取得します.
     * @return バージョンNo.
     */
    Long getVersionNo();

    /**
     * バージョンNo.を設定します.
     * @param versionNo バージョンNo.
     */
    void setVersionNo(Long versionNo);

    /**
     * 削除No.を取得します.
     * @return 削除No.
     */
    Long getDeleteNo();

    /**
     * 削除No.を設定します.
     * @param deleteNo 削除No.
     */
    void setDeleteNo(Long deleteNo);

    /**
     * 作成者名(姓)を取得します.
     * @return 作成者名(姓)
     */
    String getCreatedByLastNm();

    /**
     * 作成者名(姓)を設定します.
     * @param createdByLastNm 作成者名(姓)
     */
    void setCreatedByLastNm(String createdByLastNm);

    /**
     * 作成者名(名)を取得します.
     * @return 作成者名(名)
     */
    String getCreatedByFrstNm();

    /**
     * 作成者名(名)を設定します.
     * @param createdByFrstNm 作成者名(名)
     */
    void setCreatedByFrstNm(String createdByFrstNm);

    /**
     * 作成者名(英語)を取得します.
     * @return 作成者名(英語)
     */
    String getCreatedByNmE();

    /**
     * 作成者名(英語)を設定します.
     * @param createdByNmE 作成者名(英語)
     */
    void setCreatedByNmE(String createdByNmE);

    /**
     * 作成者名(日本語)を取得します.
     * @return 作成者名(日本語)
     */
    String getCreatedByNmJ();

    /**
     * 作成者名(日本語)を設定します.
     * @param createdByNmJ 作成者名(日本語)
     */
    void setCreatedByNmJ(String createdByNmJ);

    /**
     * 更新者名(姓)を取得します.
     * @return 更新者名(姓)
     */
    String getUpdatedByLastNm();

    /**
     * 更新者名(姓)を設定します.
     * @param updatedByLastNm 更新者名(姓)
     */
    void setUpdatedByLastNm(String updatedByLastNm);

    /**
     * 更新者名(名)を取得します.
     * @return 更新者名(名)
     */
    String getUpdatedByFrstNm();

    /**
     * 更新者名(名)を設定します.
     * @param updatedByFrstNm 更新者名(名)
     */
    void setUpdatedByFrstNm(String updatedByFrstNm);

    /**
     * 更新者名(英語)を取得します.
     * @return 更新者名(英語)
     */
    String getUpdatedByNmE();

    /**
     * 更新者名(英語)を設定します.
     * @param updatedByNmE 更新者名(英語)
     */
    void setUpdatedByNmE(String updatedByNmE);

    /**
     * 更新者名(日本語)を取得します.
     * @return 更新者名(日本語)
     */
    String getUpdatedByNmJ();

    /**
     * 更新者名(日本語)を設定します.
     * @param updatedByNmJ 更新者名(日本語)
     */
    void setUpdatedByNmJ(String updatedByNmJ);

    /**
     * 現在日時を取得します.
     * @return 現在日時
     */
    Date getNowTime();
}
