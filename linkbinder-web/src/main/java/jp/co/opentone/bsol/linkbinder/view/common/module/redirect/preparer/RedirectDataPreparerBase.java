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
package jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessException;

/**
 * リダイレクト先画面用の必要データ準備ベースクラス.
 * @author opentone
 */
public abstract class RedirectDataPreparerBase implements RedirectDataPreparer {
    /** リダイレクト処理モジュール. */
    //CHECKSTYLE:OFF サブクラスから利用のため
    protected RedirectModule module;
    //CHECKSTYLE:ON

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.TransitionDataPreparer
     *  #process(jp.co.opentone.bsol.linkbinder.view.common.module.RedirectModule,java.util.Map)
     */
    public void process(RedirectModule aModule, Map<String, String> parameterMap)
        throws RedirectProcessException {
        this.module = aModule;
        store(find(parameterMap));
    }

    /**
     * 画面遷移必要データを検索する.
     * @param parameterMap
     * @return 検索結果データ
     * @throws RedirectProcessException
     *             データ検索に失敗した場合
     */
    abstract Object find(Map<String, String> parameterMap) throws RedirectProcessException;

    /**
     * 検索結果データを格納する.
     * @param foundObject
     *            検索結果データ
     * @throws RedirectProcessException データ格納に失敗した場合
     */
    abstract void store(Object foundObject) throws RedirectProcessException;

    /**
     * @return the module.
     */
    public RedirectModule getModule() {
        return module;
    }

    /**
     * @param module the module to set.
     */
    public void setModule(RedirectModule module) {
        this.module = module;
    }

    /**
     * カレントユーザ情報を取得する.
     * @return カレントユーザ情報
     * @throws RedirectProcessException カレントユーザ情報が存在しない場合
     */
    protected User getCurrentUser() throws RedirectProcessException {
        User curUser = module.getCurrentUser();
        if (curUser == null || StringUtils.isBlank(curUser.getEmpNo())) {
            throw new RedirectProcessException(
                RedirectProcessException.ErrorCode.CURRENT_USER_IS_NULL);
        }
        return curUser;
    }

    /**
     * カレントプロジェクト情報を取得する.
     * @return カレントプロジェクト情報
     * @throws RedirectProcessException カレントプロジェクト情報が存在しない場合
     */
    protected Project getCurrentProject() throws RedirectProcessException {
        Project curProject = module.getCurrentProject();
        if (curProject == null) {
            throw new RedirectProcessException(
                RedirectProcessException.ErrorCode.CURRENT_PROJECT_IS_NULL);
        }
        return curProject;
    }
}
