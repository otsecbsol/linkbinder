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
package jp.co.opentone.bsol.linkbinder.view.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.service.common.HomeService;
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
     * ホームサービス.
     */
    @Resource
    private HomeService homeService;

    private String learningCorresponListJson;

    /**
     * 空のインスタンスを生成する.
     */
    public LearningCorresponListPage() {
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. ページの初期化に必要な情報を準備する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    public String getList() {
        return learningCorresponListJson;
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
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
        public InitializeAction(LearningCorresponListPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            //FIXME 検索結果をJSON化
            List<LearningCorresponNode> list = new ArrayList<>();
            createDummyData(list);

            ObjectMapper m = new ObjectMapper();
            try {
                page.learningCorresponListJson = m.writeValueAsString(list);
            } catch (JsonProcessingException e) {
                throw new ApplicationFatalRuntimeException(e);
            }
        }

        private void createDummyData(List<LearningCorresponNode> list) {
            list.add(new LearningCorresponNode(null, "基本的事項"));

            LearningCorresponNode p = new LearningCorresponNode(null, "現場における安全管理事項について");
            p.addChild(new LearningCorresponNode(2L, "飛来物・落下物への注意点"));
            list.add(p);
        }
    }

    /**
     * 学習用文書.
     */
    public static class LearningCorresponNode implements Serializable {
        private Long id;
        private String name;
        private List<LearningCorresponNode> children = new ArrayList<>();

        public LearningCorresponNode() {
        }

        public LearningCorresponNode(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public void addChild(LearningCorresponNode child) {
            children.add(child);
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
    }
}
