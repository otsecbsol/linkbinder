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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.h2.util.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orangesignal.csv.CsvListHandler;
import com.orangesignal.csv.handlers.ColumnPositionMappingBeanListHandler;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportProcessType;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportType;
import jp.co.opentone.bsol.linkbinder.event.EventBus;
import jp.co.opentone.bsol.linkbinder.event.ProjectCreated;
import jp.co.opentone.bsol.linkbinder.event.ProjectDeleted;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectService;
import jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.ProjectDataImportModule.ProjectCsvRecord;

/**
 * プロジェクトデータを取り込むmodule.
 * @author opentone
 */
@Component
public class ProjectDataImportModule extends MasterDataImportModule<ProjectCsvRecord>
        implements Serializable {

    /** service. */
    @Autowired
    private ProjectService service;

    @Autowired
    private EventBus eventBus;

    private Map<String, String> nameMappings;

    @PostConstruct
    public void initialize() {
        nameMappings = new HashMap<String, String>();
        nameMappings.put("projectId", "プロジェクトID");
        nameMappings.put("clientNameE", "クライアント英語名称");
        nameMappings.put("clientNameJ", "クライアント日本語名称");
        nameMappings.put("nameE", "プロジェクト英語名称");
        nameMappings.put("nameJ", "プロジェクト日本語名称");
        nameMappings.put("useApprovedFlg", "利用可否");
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport
     * .MasterDataImportModule#createCsvListHandler()
     */
    @Override
    public CsvListHandler<ProjectCsvRecord> createCsvListHandler() {
        return new ColumnPositionMappingBeanListHandler<>(ProjectCsvRecord.class)
                .addColumn(0, "projectId")
                .addColumn(1, "clientNameE")
                .addColumn(2, "clientNameJ")
                .addColumn(3, "nameE")
                .addColumn(4, "nameJ")
                .addColumn(5, "useApprovedFlg")
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
    public void executeImport(MasterDataImportProcessType processType, List<ProjectCsvRecord> list)
                    throws ServiceAbortException {
        List<Project> projectList = convert(list);
        List<Project> resultList;
        switch (processType) {
        case CREATE_OR_UPDATE:
            resultList = service.save(projectList);
            break;
        case DELETE:
            resultList = service.delete(projectList);
            break;
        default:
            throw new UnsupportedOperationException();
        }

        // イベント発火
        for (Project p :resultList) {
            if (p.getImportResultStatus() != null) {
                switch (p.getImportResultStatus()) {
                case CREATED:
                    eventBus.raiseEvent(new ProjectCreated(p.getProjectId()));
                    break;
                case DELETED:
                    eventBus.raiseEvent(new ProjectDeleted(p.getProjectId()));
                    break;
                case UPDATED:
                case NONE:
                case SKIPPED:
                    // 何もしない
                }
            }
        }
    }

    private List<Project> convert(List<ProjectCsvRecord> list) {
        return list.stream().map(r -> r.toProject()).collect(Collectors.toList());
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport
     * .MasterDataImportModule#accept(jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportType)
     */
    @Override
    public boolean accept(MasterDataImportType type) {
        return MasterDataImportType.PROJECT == type;
    }

    public static class ProjectCsvRecord implements Serializable {

        @Required
        @Length(max = 11)
        @Alphanumeric
        private String projectId;

        @Length(max = 30)
        @Alphanumeric
        private String clientNameE;

        @Length(max = 30)
        private String clientNameJ;

        @Length(max = 40)
        @Alphanumeric
        private String nameE;

        @Length(max = 40)
        private String nameJ;

        @Pattern(regexp = "^○{0,1}$")
        private String useApprovedFlg;

        /* (非 Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return new ToStringBuilder(this).toString();
        }

        /**
         * @return projectId
         */
        public String getProjectId() {
            return projectId;
        }

        /**
         * @param projectId セットする projectId
         */
        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        /**
         * @return clientNameE
         */
        public String getClientNameE() {
            return clientNameE;
        }

        /**
         * @param clientNameE セットする clientNameE
         */
        public void setClientNameE(String clientNameE) {
            this.clientNameE = clientNameE;
        }

        /**
         * @return clientNameJ
         */
        public String getClientNameJ() {
            return clientNameJ;
        }

        /**
         * @param clientNameJ セットする clientNameJ
         */
        public void setClientNameJ(String clientNameJ) {
            this.clientNameJ = clientNameJ;
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
         * @return useApprovedFlg
         */
        public String getUseApprovedFlg() {
            return useApprovedFlg;
        }

        /**
         * @param useApprovedFlg セットする useApprovedFlg
         */
        public void setUseApprovedFlg(String useApprovedFlg) {
            this.useApprovedFlg = useApprovedFlg;
        }

        public Project toProject() {
            Project p = new Project();
            p.setProjectId(projectId);
            p.setClientNameE(clientNameE);
            p.setClientNameJ(clientNameJ);
            p.setNameE(nameE);
            p.setNameJ(nameJ);

            if (StringUtils.equals("○", useApprovedFlg)) {
                p.setUseApprovedFlg("X");
            }

            return p;
        }
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin.module
     *  .dataimport.MasterDataImportModule#toViewName(java.lang.String)
     */
    @Override
    protected String toViewName(String fieldName) {
        return nameMappings.get(fieldName);
    }
}
