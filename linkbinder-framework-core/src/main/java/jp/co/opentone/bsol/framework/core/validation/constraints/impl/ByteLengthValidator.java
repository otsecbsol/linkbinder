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
package jp.co.opentone.bsol.framework.core.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.validation.constraints.ByteLength;

/**
 * 値が指定の長さ(バイト長)であるか検証するValidator.
 * @author opentone
 */
public class ByteLengthValidator implements ConstraintValidator<ByteLength, Object> {

    /** logger. */
    private static Logger log = LoggerFactory.getLogger(ByteLengthValidator.class);

    /** 最小バイト数. */
    private int min;
    /** 最大バイト数. */
    private int max;

    /**
     * 空のインスタンスを生成する.
     */
    public ByteLengthValidator() {
        super();
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
     * (non-Javadoc)
     * @see
     * javax.validation.ConstraintValidator#initialize(java.lang.annotation.
     * Annotation)
     */
    @Override
    public void initialize(ByteLength constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    /*
     * (non-Javadoc)
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
     * javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        int length = getByteLength(value);
        if (min != 0 && length < min) {
            return false;
        }
        if (max != Integer.MAX_VALUE && length > max) {
            return false;
        }
        return true;
    }
}
