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
package jp.co.opentone.bsol.linkbinder.view.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.LearningLabelCorrespon;
import jp.co.opentone.bsol.linkbinder.service.correspon.LearningCorresponService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import org.springframework.context.annotation.Scope;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ログインユーザーが閲覧可能な学習用文書の一覧.
 * @author opentone
 */
@ManagedBean
@Scope("request")
public class LearningCorresponListPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -1911947820084637576L;

    /**
     * 学習用文書サービス.
     */
    @Resource
    private LearningCorresponService service;

    private String learningCorresponListJson;

    /**
     * 空のインスタンスを生成する.
     */
    public LearningCorresponListPage() {
    }

    public String getList() {
        handler.handleAction(new ListAction(this));
        return learningCorresponListJson;
    }

    /**
     * 検索アクション.
     * @author opentone
     */
    static class ListAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -4973116494274359233L;
        /** アクション発生元ページ. */
        private LearningCorresponListPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ListAction(LearningCorresponListPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            List<LearningCorresponNode> list =
                    convertLearningCorresponNode(page.service.findAll());
            page.learningCorresponListJson = toJson(list);
        }

        private List<LearningCorresponNode> convertLearningCorresponNode(
                        List<LearningLabelCorrespon> learningLabelCorresponList) {
            //FIXME
            List<LearningCorresponNode> list = new ArrayList<>();
            for (LearningLabelCorrespon lc : learningLabelCorresponList) {
                LearningCorresponNode n = new LearningCorresponNode(null, lc.getLabel(), null);
                for (Correspon c : lc.getCorresponList()) {
                    n.addChild(new LearningCorresponNode(c.getId(), c.getSubject(), toCorresponUrl(c)));
                }

                list.add(n);
            }

            return list;
        }

        private String toCorresponUrl(Correspon c) {
            return String.format(page.viewHelper.getContextPath()
                    + "/correspon/correspon.jsf?id=%d&projectId=%s", c.getId(), c.getProjectId());
        }

        private void add(List<LearningCorresponNode> list, LearningLabelCorrespon lc) {
            for (LearningCorresponNode n : list) {
                if (n.getName().equals(lc.getLabel())) {

                }
            }
        }
        private String toJson(List<LearningCorresponNode> list) {
            ObjectMapper m = new ObjectMapper();
            try {
                return m.writeValueAsString(list);
            } catch (JsonProcessingException e) {
                throw new ApplicationFatalRuntimeException(e);
            }
        }

        private void createDummyData(List<LearningCorresponNode> list) {
            list.add(new LearningCorresponNode(null, "基本的事項", null));

            LearningCorresponNode p = new LearningCorresponNode(null, "現場における安全管理事項について", null);
            p.addChild(new LearningCorresponNode(2L, "飛来物・落下物への注意点", "/correspon/correspon.jsf?id=115&projectId=A0000000001"));
            list.add(p);
        }
    }

    /**
     * 学習用文書.
     */
    public static class LearningCorresponNode implements Serializable {
        private Long id;
        private String name;
        private String url;
        private List<LearningCorresponNode> children = new ArrayList<>();

        public LearningCorresponNode() {
        }

        public LearningCorresponNode(Long id, String name, String url) {
            this.id = id;
            this.name = name;
            this.url = url;
        }

        public void addChild(LearningCorresponNode child) {
            children.add(child);
        }

        public void addChildren(List<LearningCorresponNode> children) {
            children.addAll(children);
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<LearningCorresponNode> getChildren() {
            return children;
        }

        public void setChildren(List<LearningCorresponNode> children) {
            this.children = children;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
