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

import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;

/**
 * 値が半角英数字であるか検証するValidator.
 * @author opentone
 */
public class AlphanumericValidator implements ConstraintValidator<Alphanumeric, String> {

    /**
     * 許容する文字(半角英数字、記号)の正規表現文字列.
     */
    public static final String ALLOW = "^[a-zA-Z0-9 -/:-@\\[-\\`\\{-\\~]+$";
    /**
     * 許容する文字(記号を含まない半角英数字)の正規表現文字列.
     */
    public static final String ALLOW_ALPHANUMERIC_ONLY = "^[a-zA-Z0-9]+$";
    /**
     * 半角英数字のみ(記号除く)許容する場合はtrue.
     */
    private boolean allowAlphaNumericOnly;
    /**
     * 大文字のみ許容する場合はtrue.
     */
    private boolean allowUpperCaseOnly;

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(Alphanumeric constraintAnnotation) {
        allowAlphaNumericOnly = constraintAnnotation.allowAlphaNumericOnly();
        allowUpperCaseOnly = constraintAnnotation.allowUpperCaseOnly();
    }

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator
     *      #isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.length() == 0) {
            return true;
        }
        String pattern =
            allowAlphaNumericOnly
                ? ALLOW_ALPHANUMERIC_ONLY
                : ALLOW;

        if (!value.matches(pattern)) {
            return false;
        }

        if (allowUpperCaseOnly && !value.equals(value.toUpperCase())) {
            return false;
        }
        return true;
    }
}
