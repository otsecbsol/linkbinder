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
package jp.co.opentone.bsol.framework.web.validation.constraints.impl;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.LengthValidator;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.message.Messages;

/**
 * 値が指定の長さ(バイト長)であるか検証するValidator.
 * @author opentone
 */
public class FacesByteLengthValidator extends LengthValidator {

    /**
     * 最小桁数未満の場合のメッセージID.
     */
    public static final String MSG_ID_MINIMUM = "length_minimum";
    /**
     * 最大桁数を越えた場合のメッセージID.
     */
    public static final String MSG_ID_MAXIMUM = "length_maximum";

    /** logger. */
    private static Logger log = LoggerFactory.getLogger(FacesByteLengthValidator.class);

    /**
     * 空のインスタンスを生成する.
     */
    public FacesByteLengthValidator() {
        super();
    }

    /**
     * 最大桁数を指定してインスタンスを生成する.
     *
     * @param maximum 最大桁数
     */
    public FacesByteLengthValidator(int maximum) {
        super(maximum);
    }

    /**
     * 最小・最大桁数を指定してインスタンスを生成する.
     *
     * @param maximum 最大桁数
     * @param minimum 最小桁数
     */
    public FacesByteLengthValidator(int maximum, int minimum) {
        super(maximum, minimum);
    }

    /*
     * (非 Javadoc)
     * @seejavax.faces.validator.LengthValidator#validate(javax.faces.context.
     * FacesContext, javax.faces.component.UIComponent, java.lang.Object)
     */
    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object value)
            throws ValidatorException {

        if (facesContext == null) {
            throw new NullPointerException("facesContext");
        }
        if (uiComponent == null) {
            throw new NullPointerException("uiComponent");
        }

        if (value == null) {
            return;
        }

        int length = getByteLength(value);

        Integer minimum = getMinimum();
        if (minimum != null) {
            if (length < minimum.intValue()) {
                throw new ValidatorException(getMessage(MSG_ID_MINIMUM, minimum));
            }
        }

        Integer maximum = getMaximum();
        if (maximum != null) {
            if (length > maximum.intValue()) {
                throw new ValidatorException(getMessage(MSG_ID_MAXIMUM, maximum));
            }
        }
    }

    private int getByteLength(Object value) {
        String v;
        if (value instanceof String) {
            v = (String) value;
        } else {
            v = value.toString();
        }

        int length = 0;
        for (int i = 0; i < v.length(); i++) {
            // ASCII範囲内であれば1バイト、その他は2バイト
            // CHECKSTYLE:OFF
            length += (v.charAt(i) <= 0x7f) ? 1 : 2;
            // CHECKSTYLE:ON
        }
        if (log.isDebugEnabled()) {
            log.debug("value:{}, length:{}", v, length);
        }
        return length;
    }

    /*
     * (非 Javadoc)
     * @seejavax.faces.validator.LengthValidator#validate(javax.faces.context.
     * FacesContext, javax.faces.component.UIComponent, java.lang.Object)
     */
    public void validateChar(FacesContext facesContext, UIComponent uiComponent, Object value)
            throws ValidatorException {

        if (facesContext == null) {
            throw new NullPointerException("facesContext");
        }
        if (uiComponent == null) {
            throw new NullPointerException("uiComponent");
        }

        if (value == null) {
            return;
        }

        int length = getLength(value);

        Integer minimum = getMinimum();
        if (minimum != null) {
            if (length < minimum.intValue()) {
                throw new ValidatorException(getMessage(MSG_ID_MINIMUM, minimum));
            }
        }

        Integer maximum = getMaximum();
        if (maximum != null) {
            if (length > maximum.intValue()) {
                throw new ValidatorException(getMessage(MSG_ID_MAXIMUM, maximum));
            }
        }
    }
    private int getLength(Object value) {
        String v;
        if (value instanceof String) {
            v = (String) value;
        } else {
            v = value.toString();
        }

        int length = 0;
        length = v.length();
        if (log.isDebugEnabled()) {
            log.debug("value:{}, length:{}", v, length);
        }
        return length;
    }

    private FacesMessage getMessage(String messageCode, Object... args) {
        Message m = Messages.getMessage(messageCode, args);
        return new FacesMessage(FacesMessage.SEVERITY_ERROR, m.getSummary(), m.getMessage());
    }
}
