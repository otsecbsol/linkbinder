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
package jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Pattern;

import org.h2.util.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orangesignal.csv.CsvListHandler;
import com.orangesignal.csv.handlers.ColumnPositionMappingBeanListHandler;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.DateString;
import jp.co.opentone.bsol.framework.core.validation.constraints.MailAddress;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.linkbinder.dto.SysUsers;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportProcessType;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportType;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.validation.groups.CreateGroup;
import jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.UserDataImportModule.UserCsvRecord;

/**
 * ユーザーデータを取り込むmodule.
 * @author opentone
 */
@Component
public class UserDataImportModule extends MasterDataImportModule<UserCsvRecord>
        implements Serializable {

    /** service. */
    @Autowired
    private UserService service;

    private Map<String, String> nameMappings;

    @PostConstruct
    public void initialize() {
        nameMappings = new HashMap<String, String>();
        nameMappings.put("empNo", "ユーザーID");
        nameMappings.put("lastName", "ユーザー姓（英語）");
        nameMappings.put("nameE", "ユーザーフルネーム（英語）");
        nameMappings.put("nameJ", "ユーザーフルネーム（日本語）");
        nameMappings.put("password", "パスワード");
        nameMappings.put("sysAdmFlg", "システム管理者");
        nameMappings.put("userRegistAprvFlg", "利用可否");
        nameMappings.put("mailAddress", "メールアドレス");
        nameMappings.put("pjId", "プロジェクトID");
        nameMappings.put("pjAdmFlg", "プロジェクト管理者");
        nameMappings.put("userIdAt", "ユーザーID利用開始日");
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport
     * .MasterDataImportModule#createCsvListHandler()
     */
    @Override
    public CsvListHandler<UserCsvRecord> createCsvListHandler() {
        return new ColumnPositionMappingBeanListHandler<>(UserCsvRecord.class)
                .addColumn(0, "empNo")
                .addColumn(1, "lastName")
                .addColumn(2, "nameE")
                .addColumn(3, "nameJ")
                .addColumn(4, "password")
                .addColumn(5, "mailAddress")
                .addColumn(6, "sysAdmFlg")
                .addColumn(7, "userRegistAprvFlg")
                .addColumn(8, "pjId")
                .addColumn(9, "pjAdmFlg")
                .addColumn(10, "userIdAt")
                ;
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport
     *      .MasterDataImportModule#getCsvColumnCount()
     */
    @Override
    protected int getCsvColumnCount() {
        return nameMappings.size();
    }

    @Override
    public void executeImport(MasterDataImportProcessType processType, List<UserCsvRecord> list)
                    throws ServiceAbortException {
        List<SysUsers> userList = convert(list);
        List<SysUsers> resultList;
        switch (processType) {
        case CREATE_OR_UPDATE:
            resultList = service.save(userList);
            break;
        case DELETE:
            resultList = service.delete(userList);
            break;
        default:
            throw new UnsupportedOperationException();
        }
    }

    private List<SysUsers> convert(List<UserCsvRecord> list) {
        return list.stream().map(r -> r.toSysUser()).collect(Collectors.toList());
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport
     * .MasterDataImportModule#accept(jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportType)
     */
    @Override
    public boolean accept(MasterDataImportType type) {
        return MasterDataImportType.USER == type;
    }

    public static class UserCsvRecord implements Serializable {

        /**
         * Emp no.
         */
        @Required
        @Length(max = 5)
        @Alphanumeric
        private String empNo;

        /**
         * Last name.
         */
        @Length(max = 15)
        private String lastName;

        /**
         * Name e.
         */
        @Length(max = 40)
        private String nameE;

        /**
         * Name j.
         */
        @Length(max = 20)
        private String nameJ;

        /**
         * password.
         */
        @Length(max = 31)
        @Alphanumeric
        private String password;

        /**
         * mailAddress
         */
        @Length(max = 60)
        @MailAddress
        private String mailAddress;

        /**
         * sysAdmFlg.
         */
        @Pattern(regexp = "^○{0,1}$")
        private String sysAdmFlg;

        /**
         * userRegistAprvFlg.
         */
        @Pattern(regexp = "^○{0,1}$")
        private String userRegistAprvFlg;

        /**
         * securityLevel.
         * <p>
         * [sys_users.security_level]
         * </p>
         */
//        @Length(max = 2)
        private String securityLevel;

        /**
         * プロジェクトID.
         */
        @Length(max = 11)
        @Alphanumeric
        private String pjId;

        /**
         * プロジェクト管理者フラグ.
         */
        @Pattern(regexp = "^○{0,1}$")
        private String pjAdmFlg;

        /**
         * userIdAt.
         * <p>
         * [sys_users.USER_ID_VALID_DT]
         * </p>
         */
        @Required(groups = CreateGroup.class)
        @DateString(groups = CreateGroup.class)
        private String userIdAt;

        public SysUsers toSysUser() {
            SysUsers u = new SysUsers();

            u.setEmpNo(empNo);
            u.setLastName(lastName);
            u.setNameE(nameE);
            u.setNameJ(nameJ);
            u.setPassword(password);
            u.setMailAddress(mailAddress);
            if (StringUtils.equals("○", sysAdmFlg)) {
                u.setSysAdmFlg("X");
            }
            if (StringUtils.equals("○", userRegistAprvFlg)) {
                u.setUserRegistAprvFlg("X");
            }
            u.setPjId(pjId);
            if (StringUtils.equals("○", pjAdmFlg)) {
                u.setPjAdmFlg("X");
            }
            u.setUserIdAt(userIdAt);

            return u;
        }

        /**
         * @return empNo
         */
        public String getEmpNo() {
            return empNo;
        }

        /**
         * @param empNo セットする empNo
         */
        public void setEmpNo(String empNo) {
            this.empNo = empNo;
        }

        /**
         * @return lastName
         */
        public String getLastName() {
            return lastName;
        }

        /**
         * @param lastName セットする lastName
         */
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        /**
         * @return nameE
         */
        public String getNameE() {
            return nameE;
        }

        /**
         * @param nameE セットする nameE
         */
        public void setNameE(String nameE) {
            this.nameE = nameE;
        }

        /**
         * @return nameJ
         */
        public String getNameJ() {
            return nameJ;
        }

        /**
         * @param nameJ セットする nameJ
         */
        public void setNameJ(String nameJ) {
            this.nameJ = nameJ;
        }

        /**
         * @return password
         */
        public String getPassword() {
            return password;
        }

        /**
         * @param password セットする password
         */
        public void setPassword(String password) {
            this.password = password;
        }

        /**
         * @return sysAdmFlg
         */
        public String getSysAdmFlg() {
            return sysAdmFlg;
        }

        /**
         * @param sysAdmFlg セットする sysAdmFlg
         */
        public void setSysAdmFlg(String sysAdmFlg) {
            this.sysAdmFlg = sysAdmFlg;
        }

        /**
         * @return userRegistAprvFlg
         */
        public String getUserRegistAprvFlg() {
            return userRegistAprvFlg;
        }

        /**
         * @param userRegistAprvFlg セットする userRegistAprvFlg
         */
        public void setUserRegistAprvFlg(String userRegistAprvFlg) {
            this.userRegistAprvFlg = userRegistAprvFlg;
        }

        /**
         * @return securityLevel
         */
        public String getSecurityLevel() {
            return securityLevel;
        }

        /**
         * @param securityLevel セットする securityLevel
         */
        public void setSecurityLevel(String securityLevel) {
            this.securityLevel = securityLevel;
        }

        /**
         * @return pjId
         */
        public String getPjId() {
            return pjId;
        }

        /**
         * @param pjId セットする pjId
         */
        public void setPjId(String pjId) {
            this.pjId = pjId;
        }

        /**
         * @return pjAdmFlg
         */
        public String getPjAdmFlg() {
            return pjAdmFlg;
        }

        /**
         * @param pjAdmFlg セットする pjAdmFlg
         */
        public void setPjAdmFlg(String pjAdmFlg) {
            this.pjAdmFlg = pjAdmFlg;
        }

        /**
         * @return mailAddress
         */
        public String getMailAddress() {
            return mailAddress;
        }

        /**
         * @param mailAddress セットする mailAddress
         */
        public void setMailAddress(String mailAddress) {
            this.mailAddress = mailAddress;
        }

        /**
         * @return userIdAt
         */
        public String getUserIdAt() {
            return userIdAt;
        }

        /**
         * @param userIdAt セットする userIdAt
         */
        public void setUserIdAt(String userIdAt) {
            this.userIdAt = userIdAt;
        }
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin
     *  .module.dataimport.MasterDataImportModule#toViewName(java.lang.String)
     */
    @Override
    protected String toViewName(String fieldName) {
        return nameMappings.get(fieldName);
    }
}
