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
package jp.co.opentone.bsol.linkbinder.view.validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.web.validation.constraints.impl.FacesByteLengthValidator;

/**
 * カスタムフィールドの値を検証するValidator.
 * @author opentone
 */
public class CustomFieldValueValidator implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8985384168635722736L;
    /**
     * 入力可能な最大桁数.
     */
    public static final int MAX_LENGTH = 100;

    /**
     * カスタムフィールド値の区切り文字.
     */
    private String delim;
    /**
     * 値の区切り文字を指定してインスタンス化する.
     * @param delim 区切り文字
     */
    public CustomFieldValueValidator(String delim) {
        this.delim = delim;
    }

    /**
     * カスタムフィールド値を検証する.
     * <p>
     * このメソッドはJSPのValidationフェーズで呼び出される.
     * </p>
     * @param context FacesContext
     * @param component 入力コンポーネント
     * @param value 入力値
     */
    public void validate(FacesContext context, UIComponent component, Object value) {
        ArgumentValidator.validateNotNull(context);
        ArgumentValidator.validateNotNull(component);

        List<String> values = createValueList((String) value);
        validateLength(context, component, values);
    }

    private List<String> createValueList(String value) {
        List<String> values = new ArrayList<String>();
        if (StringUtils.isNotEmpty(value)) {
            String[] a = value.split(delim);
            for (String v : a) {
                values.add(v);
            }
        }
        return values;
    }

    /**
     * カスタムフィールド値の桁数を検証する.
     * @param context FacesContext
     * @param component 入力コンポーネント
     * @param values 入力値
     */
    public void validateLength(FacesContext context, UIComponent component, List<String> values) {
        FacesByteLengthValidator validator = new FacesByteLengthValidator(MAX_LENGTH);
        for (String v : values) {
            validator.validateChar(context, component, v);
        }
    }
}
