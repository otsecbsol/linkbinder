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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * 文字列を取り扱うためのCustom Converter.
 * <p>
 * @author opentone
 */
public class StringOmissionConverter implements Converter {

    /**
     * 省略する文字数.
     */
    private static final int DEFAULT_NUM = 10;
    /**
     * 省略文字.
     */
    private static final String OMISSION_VALUE = "...";
    /**
     * 指定省略文字数を取得するキー.
     */
    private static final String PARAM_NUM = "num";
    /* (non-Javadoc)
     * @see javax.faces.convert.Converter#getAsObject(
     *      javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
     */
    @Override
    public Object getAsObject(
            FacesContext context, UIComponent component, String value) {
        // 指定した省略文字数を取得
        String num = (String) component.getAttributes().get(PARAM_NUM);

        // 取得できない場合はデフォルト省略文字数を適用
        Integer number = (num == null) ? DEFAULT_NUM : Integer.valueOf(num);

        String result = "";
        if (null == context || null == component) {
            throw new IllegalArgumentException("arg[context] or arg[component] is null.");
        }
        if (null != value && 0 < value.length()) {
            if (value.length() > number) {
                result = value.substring(0, number);
                result += OMISSION_VALUE;
            } else {
                result = value;
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see javax.faces.convert.Converter#getAsString(
     *      javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
     */
    @Override
    public String getAsString(
            FacesContext context, UIComponent component, Object value) {

        // 指定した省略文字数を取得
        String num = (String) component.getAttributes().get(PARAM_NUM);

        // 取得できない場合はデフォルト省略文字数を適用
        Integer number = (num == null) ? DEFAULT_NUM : Integer.valueOf(num);

        String result = "";
        if (null == context || null == component) {
            throw new IllegalArgumentException("arg[context] or arg[component] is null.");
        }
        if (null != value && 0 < value.toString().length()) {
            if (value.toString().length() > number) {
                result = value.toString().substring(0, number);
                result += OMISSION_VALUE;
            } else {
                result = value.toString();
            }
        }
        return result;
    }
}
