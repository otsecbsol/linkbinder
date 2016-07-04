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
package jp.co.opentone.bsol.linkbinder.view.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.co.opentone.bsol.linkbinder.dto.Workflow;

/**
 * ワークフローリスト表示のユーティリティクラス.
 * @author opentone
 */
public class WorkflowListUtil implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7439484833629218578L;

    /**
     * コンストラクタ.
     */
    private WorkflowListUtil() {
    }

    /**
     * ワークフローナンバーを振りなおす.
     * @param workflows ワークフローリスト
     * @return ワークフローナンバーを振りなおしたリスト
     */
    public static List<Workflow> renumberWorkflowNo(List<Workflow> workflows) {
        List<Workflow> workflowList = new ArrayList<Workflow>();
        // ワークフローナンバーを振りなおす
        long no = 1;

        for (Workflow w : workflows) {
            w.setWorkflowNo(no++);
            workflowList.add(w);
        }

        return workflowList;
    }
}
