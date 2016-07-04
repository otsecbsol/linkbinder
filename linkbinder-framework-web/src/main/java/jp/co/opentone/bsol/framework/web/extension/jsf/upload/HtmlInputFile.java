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
package jp.co.opentone.bsol.framework.web.extension.jsf.upload;

import javax.faces.component.html.HtmlInputText;

/**
 * Faces component for <code>input type="file"</code> field.
 * <p>
 * @author opentone
 */
public class HtmlInputFile extends HtmlInputText {

    // CHECKSTYLE:OFF
    private final static String RENDERER_TYPE = "UploadFileRenderer";
    private final static String COMPONENT_FAMILY = "HtmlInputFileFamily";
    // CHECKSTYLE:ON

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponentBase#getRendererType()
     */
    @Override
    public String getRendererType() {
        return RENDERER_TYPE;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIInput#getFamily()
     */
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
}
