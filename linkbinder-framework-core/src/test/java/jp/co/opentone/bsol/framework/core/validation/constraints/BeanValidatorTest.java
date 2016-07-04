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

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Test;

/**
 * JSR-303 (Bean Validation)のサンプルコード.
 * @author opentone
 */
public class BeanValidatorTest {

    @Test
    public void testValidation() throws Exception {
        // 検証対象の値を格納したBean
        InputBean bean = new InputBean();
        bean.setCode("123456");

        //  Validatorの生成
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator v = factory.getValidator();

        //  検証結果を取得、検証メッセージを表示
        Set<ConstraintViolation<InputBean>> result = v.validate(bean);
        for (ConstraintViolation<InputBean> cv : result) {
            System.out.println(cv.getMessage());
            System.out.println(cv.getConstraintDescriptor().getAnnotation());
            System.out.println(cv.getPropertyPath());
        }
    }
}
