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
package jp.co.opentone.bsol.linkbinder.dao;

import java.util.List;

import jp.co.opentone.bsol.framework.core.dao.Dao;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroup;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroupCreate;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroupDelete;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroupUpdate;

/**
 * Distributionテンプレート活動単位を操作するDao.
 *
 * @author opentone
 *
 */
public interface DistTemplateGroupDao extends Dao {

    /**
     * 指定されたDistributionテンプレートヘッダーIDを条件にDistributionテンプレート活動単位を取得する.
     * @param id DistributionテンプレートヘッダーID
     * @return Distributionテンプレート活動単位
     */
    List<DistTemplateGroup> findByDistTemplateHeaderId(Long id);

    /**
     * レコードを1件追加する.
     * @param distTemplateGroup 追加する情報
     * @return 新規追加したレコードID値
     */
    Long create(DistTemplateGroupCreate distTemplateGroup);

    /**
     * レコードを1件論理削除する.
     * @param distTemplateGroup 削除する情報
     * @return 論理削除件数
     */
    Integer delete(DistTemplateGroupDelete distTemplateGroup);

    /**
     * レコードを1件更新する.
     * @param distTemplateGroup 更新する情報.
     * @return 1件だけ更新できた場合はtrue
     */
    boolean update(DistTemplateGroupUpdate distTemplateGroup);
}
