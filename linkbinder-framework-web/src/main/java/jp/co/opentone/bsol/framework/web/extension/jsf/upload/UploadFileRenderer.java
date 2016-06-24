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

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.TextRenderer;

/**
 * Custom render for <code>input type="file"</code> field.
 * <p>
 * @author opentone
 */
public class UploadFileRenderer extends TextRenderer {

    // CHECKSTYLE:OFF
    private static final Attribute[] INPUT_ATTRIBUTES
        = AttributeManager.getAttributes(AttributeManager.Key.INPUTTEXT);
    private static final String EMPTY_VALUE = "";
    // CHECKSTYLE:ON

    /* (non-Javadoc)
     * @see com.sun.faces.renderkit.html_basic.TextRenderer
     *      #getEndTextToRender(javax.faces.context.FacesContext,
     *                          javax.faces.component.UIComponent,
     *                          java.lang.String)
     */
    @Override
    protected void getEndTextToRender(FacesContext context,
                                      UIComponent component,
                                      String currentValue) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        final String elementName = "input";
        writer.startElement(elementName, component);
        writeIdAttributeIfNecessary(context, writer, component);
        writer.writeAttribute("type", "file", null);
        writer.writeAttribute("name", component.getClientId(context), "clientId");

        String styleClass = (String) component.getAttributes().get("styleClass");
        if (null != styleClass) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }

        RenderKitUtils.renderPassThruAttributes(
                context, writer, component, INPUT_ATTRIBUTES, getNonOnChangeBehaviors(component));
        RenderKitUtils.renderXHTMLStyleBooleanAttributes(writer, component);
        RenderKitUtils.renderOnchange(context, component, false);
        writer.endElement(elementName);
    }

    /* (non-Javadoc)
     * @see com.sun.faces.renderkit.html_basic.HtmlBasicRenderer
     *      #decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @Override
    public void decode(FacesContext context, UIComponent component) {
        rendererParamsNotNull(context, component);
        if (!shouldDecode(component)) {
            return;
        }
        String clientId = decodeBehaviors(context, component);
        if (null == clientId) {
            clientId = component.getClientId(context);
        }
        MultipartRequest multiReq = (MultipartRequest) context.getExternalContext().getRequest();
        MultipartFile file = multiReq.getFile(clientId);
        Object submittedValue = (null != file) ? file : EMPTY_VALUE;
        ((UIInput) component).setSubmittedValue(submittedValue);
    }

    /* (non-Javadoc)
     * @see com.sun.faces.renderkit.html_basic.HtmlBasicInputRenderer
     *      #getConvertedValue(javax.faces.context.FacesContext,
     *                         javax.faces.component.UIComponent,
     *                         java.lang.Object)
     */
    @Override
    public Object getConvertedValue(FacesContext context,
                                    UIComponent component,
                                    Object submittedValue) throws ConverterException {
        return (EMPTY_VALUE != submittedValue) ? submittedValue : null;
    }
}
