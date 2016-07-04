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
package jp.co.opentone.bsol.framework.web.extension.jsf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.exception.MethodInvocationRuntimeException;
import jp.co.opentone.bsol.framework.core.exception.ReflectionRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.util.MethodUtil;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Prerender;
import jp.co.opentone.bsol.framework.web.view.Page;
import jp.co.opentone.bsol.framework.web.view.PagePropertyUtil;
import jp.co.opentone.bsol.framework.web.view.flash.Flash;

/**
 * JSFのライフサイクルイベントで フレームワーク独自処理を組み込むクラス.
 * @author opentone
 */
public class ExtendedPhaseListener implements PhaseListener {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6716377912430880014L;

    /**
     * logger.
     */
    private Logger log = LoggerFactory.getLogger(ExtendedPhaseListener.class);

    /**
     * リクエスト間で有効な値を保持するコンテナ.
     */
    private Flash flash = new Flash();

    /**
     * 拡張NavigationHandler.
     */
    private ExtendedNavigationHandler navHandler;

    /*
     * (非 Javadoc)
     * @see
     * javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
     */
    public void beforePhase(PhaseEvent event) {
        FacesHelper helper = new FacesHelper(event.getFacesContext());
        if (event.getPhaseId().equals(PhaseId.RESTORE_VIEW)) {
            log.debug("before phase: RESTORE_VIEW");
            setup(helper);
        } else if (event.getPhaseId().equals(PhaseId.APPLY_REQUEST_VALUES)) {
            log.debug("before phase: APPLY_REQUEST_VALUES");
            processPopulateFlashValues(helper);
            processPageSetUp(helper);

        } else if (event.getPhaseId().equals(PhaseId.RENDER_RESPONSE)) {
            log.debug("before phase: RENDER_RESPONSE");
            process(helper);
        }
    }

    /*
     * (非 Javadoc)
     * @see
     * javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
     */
    public void afterPhase(PhaseEvent event) {
        FacesHelper helper = new FacesHelper(event.getFacesContext());
        if (event.getPhaseId().equals(PhaseId.PROCESS_VALIDATIONS)) {
            processAfterValidation(helper);
        } else if (event.getPhaseId().equals(PhaseId.RENDER_RESPONSE)) {
            processTransferFlashValues(helper);
        }
    }

    /**
     * 現在のViewIdに対応するPageオブジェクトを返す.
     * @return pageオブジェクト
     */
    private Page getPage() {
        FacesHelper helper = new FacesHelper(FacesContext.getCurrentInstance());
        String viewId = helper.getViewId();
        return helper.getPage(helper.getPageName(viewId));
    }

    /**
     * フレームワーク独自処理のための準備.
     * @param helper
     *            JSFヘルパー
     */
    private void setup(FacesHelper helper) {
        synchronized (this) {
            if (navHandler == null) {
                // 拡張NavigationHandlerをセットアップし
                // アクションメソッドの戻り値から遷移先を
                // 推測できるようにする
                navHandler = new ExtendedNavigationHandler();
                navHandler.setUp(helper);
            }
        }
    }

    /**
     * RENDER_RESPONSEフェーズでのフレームワーク拡張処理.
     * @param helper
     *            JSFヘルパー
     */
    protected void process(FacesHelper helper) {
        String viewId = helper.getViewId();
        if (StringUtils.isEmpty(viewId)) {
            log.warn("viewRoot is null");
            return;
        }

        String pageName = helper.getPageName(viewId);
        Page page = helper.getPage(pageName);
        if (log.isDebugEnabled()) {
            log.debug("viewId={}, pageName={}, page={}", new Object[]{viewId, pageName, page});
        }

        if (processInitialize(helper, page)) {
            processPrerender(helper, page);
        }
    }

    /**
     * ページを初期化する処理を起動する.
     * <p>
     * 初期化処理は、最初にページが表示される時に起動される. 具体的には、現在のページが別のページからGETリクエストで起動された時.
     * forwardポストバックでのページ起動の場合は、初期化処理は起動されない.
     * </p>
     * ページクラスの、{@link Initialize}アノテーションが付与されたメソッドを 初期化処理メソッドとして起動する.
     * @see Initialize
     * @param helper
     *            JSFヘルパー
     * @param page
     *            ページオブジェクト
     * @return 初期化終了後、後続の処理を続ける場合はtrue、処理を行わない場合はfalse
     */
    protected boolean processInitialize(FacesHelper helper, Page page) {
        boolean processContinue = true;
        if (helper.isGetRequest()) {
            Method[] initialize =
                    MethodUtil.getMethodsWithAnnotation(page.getClass(), Initialize.class);
            if (initialize.length > 1) {
                throw new MethodInvocationRuntimeException("initialize method is ambiguous.");
            }
            // 前ページから引き継いだ値とリクエストパラメーターをpageに適用する
            populateFlashValues(helper, page);
            populateRequestValues(helper, page);

            if (initialize.length > 0) {
                Object nextPage = MethodUtil.invoke(page, initialize[0]);
                if (nextPage != null && nextPage instanceof String) {
                    NavigationHandler nh = helper.getNavigationHandler();
                    nh.handleNavigation(helper.getContext(), null, (String) nextPage);
                    processContinue = false;
                }
            }
            // 前ページから引き継いだメッセージを表示する
            FacesMessage fMessage = flash.getValue(Page.KEY_FLASH_MASSAGE);
            if (fMessage != null) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, fMessage);
                flash.setValue(Page.KEY_FLASH_MASSAGE, null);
            }
        }
        return processContinue;
    }

    /**
     * pageオブジェクトに、Flashに格納された前回リクエスト時の値を適用する.
     * @param helper
     *            helper
     */
    protected void processPopulateFlashValues(FacesHelper helper) {
        Page page = getPage();
        if (page != null) {
            populateFlashValues(helper, page);
        }
    }

    /**
     * pageオブジェクトに、page自身の直前のリクエスト時に保存した値を適用する.
     * @param helper
     *            helper
     */
    protected void processTransferFlashValues(FacesHelper helper) {
//        Page page = getPage();
        //TODO RequestScopeからViewScopeに変更する場合はこの処理は不要
//        if (page != null) {
//            // pageオブジェクト内の、同ページ間での引き継ぎ対象の値をFlashに保存
//            Map<String, Object> values = PagePropertyUtil.collectTransferValues(page);
//            flash.setValue(page.getClass().getName(), values);
//        }

        // 前のリクエストの値をクリアする
        flash.setValue(Page.KEY_TRANSFER, null);
        flash.refresh();
    }

    /**
     * pageオブジェクトの処理開始前の初期化処理を行う.
     * @param helper helper
     */
    protected void processPageSetUp(FacesHelper helper) {
        Page page = getPage();
        if (page != null) {
            page.setUp();
        }
    }

    /**
     * pageオブジェクト自身の、前回リクエスト時にFlashに保存した値と前画面のpageオブジェクトから引き継いだ値を適用する.
     * @param helper
     *            viewHelper
     * @param page
     *            pageオブジェクト
     */
    @SuppressWarnings("unchecked")
    private void populateFlashValues(FacesHelper helper, Page page) {
        //TODO RequestScopeからViewScopeに変更する場合はこの処理は不要
//        PagePropertyUtil.copyProperties(page, (Map<String, Object>) flash.getValue(page.getClass()
//                                                                                       .getName()));

        PagePropertyUtil.copyProperties(page,
                                        (Map<String, Object>) flash.getValue(Page.KEY_TRANSFER));
    }

    /**
     * リクエストコンテキストに格納された値をpageオブジェクトに適用する.
     * @param helper
     *            viewHelper
     * @param page
     *            pageオブジェクト
     */
    private void populateRequestValues(FacesHelper helper, Page page) {
        Map<String, String> params =
                helper.getContext().getExternalContext().getRequestParameterMap();
        try {
            BeanUtils.copyProperties(page, params);
        } catch (IllegalAccessException e) {
            throw new ReflectionRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ReflectionRuntimeException(e);
        }
    }

    /**
     * ページのレンダリング直前の処理を起動する.
     * <p>
     * GETリクエスト・forward・ポストバックの際に処理を起動する.
     * </p>
     * ページクラスの、{@link Prerender}アノテーションが付与されたメソッドを レンダリング直前処理メソッドとして起動する.
     * @see Prerender
     * @param helper
     *            JSFヘルパー
     * @param page
     *            ページオブジェクト
     */
    protected void processPrerender(FacesHelper helper, Object page) {
        Method[] prerender = MethodUtil.getMethodsWithAnnotation(page.getClass(), Prerender.class);
        if (prerender.length > 1) {
            throw new MethodInvocationRuntimeException("prerender method is ambiguous.");
        }
        if (prerender.length > 0) {
            MethodUtil.invoke(page, prerender[0]);
        }
    }

    @SuppressWarnings("rawtypes")
    protected void processAfterValidation(FacesHelper helper) {
        boolean existValidationError = false;
        for (Iterator it = helper.getContext().getClientIdsWithMessages();
                it.hasNext();) {
            existValidationError = true;
            break;
        }

        if (existValidationError) {
            Message m = Messages.getMessage(MessageCode.E_INVALID_INPUT);
            FacesMessage fm = new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    m.getSummary(),
                                    m.getMessage());
            helper.getContext().addMessage(null, fm);
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
}
