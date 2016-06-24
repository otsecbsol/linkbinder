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
package jp.co.opentone.bsol.linkbinder.view.correspon.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * コレポン文書の宛先-ユーザーが任命した担当者1件を表す表示用クラス.
 * @author opentone
 */
public class PersonInChargeModel implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 5632218950047033618L;

    /**
     * 担当者.
     */
    private PersonInCharge personInCharge;
    /**
     * 担当者を指定してインスタンス化する.
     * @param personInCharge 担当者
     */
    PersonInChargeModel(PersonInCharge personInCharge) {
        this.personInCharge = personInCharge;
    }

    /**
     * 担当者を設定する.
     * @param personInCharge 担当者
     */
    public void setPersonInCharge(PersonInCharge personInCharge) {
        this.personInCharge = personInCharge;
    }

    /**
     * 担当者を取得する.
     * @return 担当者
     */
    public PersonInCharge getPersonInCharge() {
        return personInCharge;
    }

    /**
     * {@link PersonInCharge}への委譲メソッド.
     *
     * @return {@link PersonInCharge}の同名メソッドを参照のこと.
     */
    public User getUser() {
        return personInCharge.getUser();
    }

    /**
     * 担当者のリストをこのクラスのリストに変換して返す.
     *
     * @param personInCharges 担当者のリスト
     * @return 担当者のリスト
     */
    public static List<PersonInChargeModel> createPersonInChargeModelList(
        List<PersonInCharge> personInCharges) {
        List<PersonInChargeModel> result = new ArrayList<PersonInChargeModel>();
        if (personInCharges == null) {
            return result;
        }

        for (PersonInCharge pic : personInCharges) {
            result.add(new PersonInChargeModel(pic));
        }
        return result;
    }

    /**
     * 担当者の従業員番号を表す文字列をこのクラスのリストに変換して返す.
     *
     * @param empNos 担当者の従業員番号をカンマ区切りで表した文字列
     * @param dsUsers 従業員番号とユーザーオブジェクトのマップ
     * @return 担当者のリスト
     * @throws ServiceAbortException
     */
    public static List<PersonInChargeModel> createPersonInChargeModelList(
        String empNos, Map<String, User> dsUsers) {
        List<PersonInChargeModel> result = new ArrayList<PersonInChargeModel>();
        if (StringUtils.isEmpty(empNos)) {
            return result;
        }

        for (String empNo : empNos.split(",")) {
            User u = dsUsers.get(empNo);
            PersonInCharge pic = new PersonInCharge();
            pic.setUser(u);

            result.add(new PersonInChargeModel(pic));
        }
        return result;
    }
}
