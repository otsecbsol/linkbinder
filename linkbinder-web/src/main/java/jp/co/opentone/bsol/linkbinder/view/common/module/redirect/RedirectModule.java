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
package jp.co.opentone.bsol.linkbinder.view.common.module.redirect;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.processor.RedirectProcessor;

/**
 * ログインからの画面リダイレクト遷移に関する処理を行うモジュールクラス.
 * @author opentone
 */
@Component
@Scope("request")
public class RedirectModule implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -3049996262060723727L;

    /**
     * プロジェクトに関する処理を提供するサービス.
     */
    @Resource
    // CHECKSTYLE:OFF
    protected ProjectService projectService;
    // CHECKSTYLE:ON

    /**
     * ユーザ情報に関する処理を提供するサービス.
     */
    @Resource
    // CHECKSTYLE:OFF
    protected UserService userService;
    // CHECKSTYLE:ON

    /**
     * 呼び出しページ.
     */
    //CHECKSTYLE:OFF
    protected AbstractPage basePage;
    //CHECKSTYLE:ON


    /**
     * 指定された遷移先画面に対応するリダイレクト用のデータ準備とリダイレクト状態設定を行う.
     *
     * @param redirectTo 遷移先の画面ID
     * @param redirectProcessParam 遷移処理パラメータ
     *  パラメータに設定する値の詳細はRedirectScreenId#createRedirectProcessor(RedirectScreenId, Map)
     *  を参照のこと
     * @throws RedirectProcessException
     *             遷移処理に失敗した場合
     * @see RedirectScreenId#createRedirectProcessor(RedirectScreenId, Map)
     */
    public void setupRedirect(RedirectScreenId redirectTo,
        Map<String, String> redirectProcessParam) throws RedirectProcessException {
        ArgumentValidator.validateNotNull(redirectTo);
        ArgumentValidator.validateNotNull(redirectProcessParam);
        RedirectProcessor processor = RedirectScreenId.createRedirectProcessor(
                                                redirectTo, redirectProcessParam);
        processor.setupRedirect(this);
    }

    /**
     * @param basePage the basePage to set
     */
    public void setBasePage(AbstractPage basePage) {
        this.basePage = basePage;
    }

    /**
     * @return the viewHelper
     */
    public ViewHelper getViewHelper() {
        return basePage.getViewHelper();
    }

    /**
     * @return the projectService
     */
    public ProjectService getProjectService() {
        return projectService;
    }

    /**
     * @return the userService
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * 現在ログイン中のユーザ情報を返す.
     * @return 現在ログイン中のユーザ情報
     */
    public User getCurrentUser() {
        return basePage.getCurrentUser();
    }

    /**
    * 現在選択中のプロジェクト情報を返す.
    * @return プロジェクト情報
    * @see AbstractPage.getCurrentProject()
    */
   public Project getCurrentProject() {
       return basePage.getCurrentProject();
   }

    /**
    * 現在選択中のプロジェクト情報をセットする.
    * @param project プロジェクト情報
    * @see AbstractPage.setCurrentProjectInfo()
    */
   public void setCurrentProjectInfo(Project project) {
       basePage.setCurrentProjectInfo(project);
       basePage.setLoginProjectId(project.getProjectId());
   }

   /**
    * 現在処理中のページを返す.
    * @return 処理中ページ
    */
   public AbstractPage getBasePage() {
       return basePage;
   }
}
