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
package jp.co.opentone.bsol.framework.core.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import jp.co.opentone.bsol.framework.core.validation.constraints.impl.ByteLengthValidator;

/**
 * 指定の長さ(バイト長)であるか検証する.
 * @author opentone
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ByteLengthValidator.class)
public @interface ByteLength {

    /** 最小バイト数. */
    int min() default 0;
    /** 最大バイト数. */
    int max() default Integer.MAX_VALUE;

    String message()
        default "{ByteLength.message}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

    /**
     * アノテーションを複数定義するためのアノテーション.
     * @author opentone
     */
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ByteLength[] values();
    }
}
