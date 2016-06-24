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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.util.LogUtil;
import jp.co.opentone.bsol.framework.web.validation.constraints.FileSize;

/**
 * アップロードされたファイルサイズを検証するValidator.
 * @author opentone
 */
public class FileSizeValidator implements ConstraintValidator<FileSize, MultipartFile> {

    /** logger. */
    private static Logger log = LogUtil.getLogger();

    /**
     * アップロード可能最大ファイルサイズ取得キー.
     */
    public static final String KEY_UPLOAD_MAX_SIZE = "upload.max.size";

    /** ファイルサイズの上限値. */
    private long size;

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(FileSize constraintAnnotation) {
        this.size = constraintAnnotation.value();
        if (this.size == constraintAnnotation.defaultValue()) {
            try {
                this.size = Long.parseLong(SystemConfig.getValue(KEY_UPLOAD_MAX_SIZE));
            } catch (Exception ignore) {
                log.warn("{} not defined.", KEY_UPLOAD_MAX_SIZE);
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator
     *      #isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.getSize() <= size;
    }
}
