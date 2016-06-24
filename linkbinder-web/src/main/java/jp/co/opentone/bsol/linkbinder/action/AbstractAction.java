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
package jp.co.opentone.bsol.linkbinder.action;

import jp.co.opentone.bsol.framework.web.view.Page;
import jp.co.opentone.bsol.framework.web.view.action.Action;

/**
 * 当システムの業務処理を行うActionの抽象クラス.
 * @author opentone
 */
public abstract class AbstractAction implements Action {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 8809629699624218350L;

    /**
     * このアクションの名前.
     */
    private String name;

    /**
     * ページを指定してインスタンス化する.
     * @param page
     *            ページオブジェクト
     */
    public AbstractAction(Page page) {
        this.name = page.getActionName();
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
