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
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.LearningLabelCorrespon;
import jp.co.opentone.bsol.linkbinder.service.correspon.LearningCorresponService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            // nodeを登録
            List<LearningCorresponNode> list = new ArrayList<>();
            Map<String, LearningCorresponNode> parents = new HashMap<>();

            for (LearningLabelCorrespon lc : learningLabelCorresponList) {
                String myLabel = getMyLabel(lc.getLabel());
                String parentLabel = getParentLabel(lc.getLabel());
                LearningCorresponNode node;

                if (StringUtils.isEmpty(parentLabel)) {
                    node = new LearningCorresponNode(null, myLabel, null);
                    node.addChildren(
                            lc.getCorresponList().stream()
                                    .map(c -> new LearningCorresponNode(c.getId(), c.getSubject(), toCorresponUrl(c)))
                                    .collect(Collectors.toList()));
                    parents.put(lc.getLabel(), node);
                    list.add(node);
                } else {
                    LearningCorresponNode parent = null;
                    if (parents.containsKey(parentLabel)) {
                        parent = parents.get(parentLabel);
                    } else {
                        // 親階層を追加
                        String delimiter = SystemConfig.getValue(Constants.KEY_LEARNING_LABEL_DELIMITER);
                        StringBuilder strPath = new StringBuilder();
                        String[] path = StringUtils.split(parentLabel, delimiter);
                        for (int i = 0; i < path.length; i++) {
                            LearningCorresponNode n = new LearningCorresponNode(null, path[i], null);
                            if (i == 0) {
                                list.add(n);
                            } else {
                                list.get(list.size() - 1).addChild(n);
                            }
                            String label = StringUtils.join(path, delimiter, 0, i);
                            parents.put(label, n);

                            if (i == path.length - 1) {
                                parent = n;
                            }
                        }
                    }
                    LearningCorresponNode me = new LearningCorresponNode(null, myLabel, null);
                    if (parent != null) {
                        parent.addChild(me);
                        me.addChildren(
                                lc.getCorresponList().stream()
                                        .map(c -> new LearningCorresponNode(c.getId(), c.getSubject(), toCorresponUrl(c)))
                                        .collect(Collectors.toList()));

                        parents.put(lc.getLabel(), me);
                    }
                }
            }
            return list;
        }

        private String getMyLabel(String label) {
            String delimiter = SystemConfig.getValue(Constants.KEY_LEARNING_LABEL_DELIMITER);
            int i = label.lastIndexOf(delimiter);
            if (i == -1) {
                return label;
            }

            return label.substring(i + 1);
        }

        private String getParentLabel(String label) {
            String delimiter = SystemConfig.getValue(Constants.KEY_LEARNING_LABEL_DELIMITER);
            int i = label.lastIndexOf(delimiter);
            if (i == -1) {
                return null;
            }

            return label.substring(0, i);
        }

        private String toCorresponUrl(Correspon c) {
            return String.format(page.viewHelper.getContextPath()
                    + "/correspon/correspon.jsf?id=%d&projectId=%s", c.getId(), c.getProjectId());
        }

        private String toJson(List<LearningCorresponNode> list) {
            ObjectMapper m = new ObjectMapper();
            try {
                return m.writeValueAsString(list);
            } catch (JsonProcessingException e) {
                throw new ApplicationFatalRuntimeException(e);
            }
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
            this.children.add(child);
        }

        public void addChildren(List<LearningCorresponNode> children) {
            this.children.addAll(children);
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
