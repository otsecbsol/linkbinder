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
package jp.co.opentone.bsol.linkbinder.view.exception;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * View層まで伝播した例外をハンドリングする {@link ExceptionHandler} を生成する.
 * @author opentone
 */
public class LinkBinderExceptionHandlerFactory extends ExceptionHandlerFactory {

    /** このオブジェクトの親にあたる {@link ExceptionHandlerFactory}. */
    private ExceptionHandlerFactory parent;

    public LinkBinderExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    /* (non-Javadoc)
     * @see javax.faces.context.ExceptionHandlerFactory#getExceptionHandler()
     */
    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler org = parent.getExceptionHandler();
        return new LinkBinderExceptionHandler(org);
    }
}
