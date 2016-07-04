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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * 日付型を取り扱うためのCustom Converter.
 * <p>
 * @author opentone
 */
public class DateConverter extends CustomDateExtension implements Converter {

    /* (non-Javadoc)
     * @see javax.faces.convert.Converter#getAsObject(
     *      javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
     */
    @Override
    public Object getAsObject(
            FacesContext context, UIComponent component, String value) {
        Object convObj = null;
        if (null == context || null == component) {
            throw new IllegalArgumentException("arg[context] or arg[component] is null.");
        }
        if (null != value && 0 < value.length()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                convObj = sdf.parse(value);
            } catch (ParseException e) {
                throw new ConverterException(e);
            }
        }
        return convObj;
    }

    /* (non-Javadoc)
     * @see javax.faces.convert.Converter#getAsString(
     *      javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
     */
    @Override
    public String getAsString(
            FacesContext context, UIComponent component, Object value) {
        String result = null;
        if (null == context || null == component) {
            throw new IllegalArgumentException("arg[context] or arg[component] is null.");
        }
        if (null != value && 0 < value.toString().length()) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            result = sdf.format((Date) value);
        }
        return result;
    }
}
