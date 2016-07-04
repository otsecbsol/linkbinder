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

import java.io.Serializable;
import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * 操作情報を表すDTO.
 *
 * @author opentone
 *
 */
@Component
public class DistTemplateOperation extends Object implements Cloneable, Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 6695210506490848523L;

    /**
     * 操作ユーザー.
     */
    private DistTemplateOperateUser operateBy = new DistTemplateOperateUser();

    /**
     * 操作日時.
     */
    private Date operateAt;

    /**
     * 操作ユーザー.を取得します.
     * @return 操作ユーザー.
     */
    public DistTemplateOperateUser getOperateBy() {
        return operateBy;
    }

    /**
     * 操作ユーザー.を設定します.
     * @param operateBy 操作ユーザー.
     */
    public void setOperateBy(DistTemplateOperateUser operateBy) {
        this.operateBy = operateBy;
    }

    /**
     * 操作日時.を取得します.
     * @return 操作日時.
     */
    public Date getOperateAt() {
        return operateAt;
    }

    /**
     * 操作日時.を設定します.
     * @param operateAt 操作日時.
     */
    public void setOperateAt(Date operateAt) {
        this.operateAt = operateAt;
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.mer.dto.MerDtoBase#clone()
     */
    @Override
    public Object clone() {
        DistTemplateOperation obj = new DistTemplateOperation();
        obj.setOperateBy((DistTemplateOperateUser) operateBy.clone());
        if (null != operateAt) {
            obj.setOperateAt(new Date(operateAt.getTime()));
        }
        return obj;
    }
}
