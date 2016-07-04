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

/**
 * 操作ユーザー情報DTOです.
 *
 * @author opentone
 *
 */
public class DistTemplateOperateUser extends Object implements Cloneable, Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -8992199460907538874L;

    /**
     * 従業員番号.
     */
    private String empNo;

    /**
     * 従業員名(姓).
     */
    private String lastName;

    /**
     * 従業員名(名).
     */
    private String firstName;
    /**
     * 従業員名(英語).
     */
    private String nameE;

    /**
     * 従業員名(日本語).
     */
    private String nameJ;

    /**
     * 表示用従業員名.
     */
    private String empNameAndNo;

    /**
     * 従業員番号.を取得します.
     * @return 従業員番号.
     */
    public String getEmpNo() {
        return empNo;
    }

    /**
     * 従業員番号.を設定します.
     * @param empNo 従業員番号.
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * 従業員名(姓).を取得します.
     * @return 従業員名(姓).
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * 従業員名(姓).を設定します.
     * @param lastName 従業員名(姓).
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * 従業員名(名).を取得します.
     * @return 従業員名(名).
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * 従業員名(名).を設定します.
     * @param firstName 従業員名(名).
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * 従業員名(英語).を取得します.
     * @return 従業員名(英語).
     */
    public String getNameE() {
        return nameE;
    }

    /**
     * 従業員名(英語).を設定します.
     * @param nameE 従業員名(英語).
     */
    public void setNameE(String nameE) {
        this.nameE = nameE;
    }

    /**
     * 従業員名(日本語).を取得します.
     * @return 従業員名(日本語).
     */
    public String getNameJ() {
        return nameJ;
    }

    /**
     * 従業員名(日本語).を設定します.
     * @param nameJ 従業員名(日本語).
     */
    public void setNameJ(String nameJ) {
        this.nameJ = nameJ;
    }

    /**
     * 表示用従業員名.を取得します.
     * @return 表示用従業員名.
     */
    public String getEmpNameAndNo() {
        return empNameAndNo;
    }

    /**
     * 表示用従業員名.を設定します.
     * @param empNameAndNo 表示用従業員名.
     */
    public void setEmpNameAndNo(String empNameAndNo) {
        this.empNameAndNo = empNameAndNo;
    }

    /* (非 Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        DistTemplateOperateUser clone = new DistTemplateOperateUser();
        clone.setEmpNo(empNo);
        clone.setLastName(lastName);
        clone.setFirstName(firstName);
        clone.setNameE(nameE);
        clone.setNameJ(nameJ);
        clone.setEmpNameAndNo(empNameAndNo);
        return clone;
    }
}
