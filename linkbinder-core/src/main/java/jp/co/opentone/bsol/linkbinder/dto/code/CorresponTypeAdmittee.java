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
package jp.co.opentone.bsol.linkbinder.dto.code;

import java.util.EnumSet;

import jp.co.opentone.bsol.framework.core.dto.Code;

/**
 * CorresponType を利用可能なユーザーの種別.
 *
 * @author opentone
 *
 */
public enum CorresponTypeAdmittee implements Code {

    /**
     * ProjectAdmin.
     */
    PROJECT_ADMIN(4, "プロジェクト管理者"),

    /**
     * GroupAdmin.
     */
    GROUP_ADMIN(2, "グループ管理者"),

    /**
     * NormalUser.
     */
    NORMAL_USER(1, "一般ユーザー");


    /**
     * ラベル.
     */
    private final int value;

    /**
     * 値.
     */
    private final String label;

    /**
     * 値とラベルを指定してインスタンス化する.
     * @param value 値
     * @param label ラベル
     */
    private CorresponTypeAdmittee(int value, String label) {
        this.value = value;
        this.label = label;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.framework.extension.ibatis.EnumValue#getValue()
     */
    public Integer getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dto.code.Code#getLabel()
     */
    public String getLabel() {
        return label;
    }


    /**
     * int 値 (DB に永続化される 0 から 255 の整数) に含まれる  Correspondence Type
     * を利用可能なユーザー権限の EnumSet を返す.
     * @param val  0 から 255 の整数
     * @return アクセス可能な権限を示す CorresponTypeAdmittee 列挙子の set
     */
    public static EnumSet<CorresponTypeAdmittee> getEnumSetFromDbValue(int val) {
        EnumSet<CorresponTypeAdmittee> result =
            EnumSet.noneOf(CorresponTypeAdmittee.class);

        for (CorresponTypeAdmittee uat : CorresponTypeAdmittee.values()) {
            int itemValue = uat.getValue();
            if ((val & itemValue) == itemValue ) { result.add(uat); }
        }

        return result;
    }


    /**
     * CorresponTypeAdmittee 列挙子の set から int 値 (DB に永続化される 0 から 255
     * の整数) を算出して返す.
     * @param enumSet アクセス可能な権限を示す CorresponTypeAdmittee 列挙子の set
     * @return 0 から 255 の整数
     */
    public static int getIntValueFromEnumSet(
            EnumSet<CorresponTypeAdmittee> enumSet) {

        int result = 0;
        for (CorresponTypeAdmittee uat : enumSet) {
            result |= uat.getValue();
        }
        return result;
    }

} // end of enum CorresponTypeAdmittee
