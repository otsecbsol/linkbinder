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
import javax.validation.constraints.Digits;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.impl.DigitsValidatorForString;

/**
 * 空文字列に対応した {@link DigitsValidatorForString}.
 * @author opentone
 */
public class ExtendedDigitsValidatorForString
        extends DigitsValidatorForString
        implements ConstraintValidator<Digits, String> {
    /** 長さの最小値. */
    private int maxIntegerLength;
    /** 長さの最大値. */
    private int maxFractionLength;

    /*
     * (non-Javadoc)
     * @see org.hibernate.validator.constraints.impl.DigitsValidatorForString
     *      #initialize(javax.validation.constraints.Digits)
     */
    @Override
    public void initialize(Digits constraintAnnotation) {
        this.maxIntegerLength = constraintAnnotation.integer();
        this.maxFractionLength = constraintAnnotation.fraction();
        validateParameters();
        super.initialize(constraintAnnotation);
    }

    /*
     * (non-Javadoc)
     * @see org.hibernate.validator.constraints.impl.DigitsValidatorForString
     *      #isValid(java.lang.String, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(String str, ConstraintValidatorContext constraintValidatorContext) {
        //  空の文字列を許容します
        //  親クラスとの違いはこの1点のみです
        if (StringUtils.isEmpty(str)) {
            return true;
        }
        return super.isValid(str, constraintValidatorContext);
    }

    private void validateParameters() {
        if (maxIntegerLength < 0) {
            throw new IllegalArgumentException(
                    "The length of the integer part cannot be negative.");
        }
        if (maxFractionLength < 0) {
            throw new IllegalArgumentException(
                    "The length of the fraction part cannot be negative.");
        }
    }
}
