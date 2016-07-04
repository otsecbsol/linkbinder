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

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.attachment.UploadedFile;

/**
 * 添付ファイルの値を検証するValidator.
 * @author opentone
 */
public class AttachmentValidator implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8985384168635722736L;

    /**
     * 最大桁数を越えた場合のメッセージID.
     */
    public static final String MSG_ID_MAXIMUM = "length_maximum";

    /**
     * 空のインスタンスを生成する.
     */
    public AttachmentValidator() {
    }

    /**
     * 値を検証する.
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

        if (value != null) {
            validateAttachment(context, component, (String) value);
        }
    }

    /**
     * 添付ファイルを検証する.
     * @param context FacesContext
     * @param component 入力コンポーネント
     * @param attachmentKey 添付キー
     */
    public void validateAttachment(FacesContext context, UIComponent component,
            String attachmentKey) {
        // ファイル名が最大長以下であるか
        if (UploadedFile.KEY_FILENAME_OVER.equals(attachmentKey)) {
            throw new ValidatorException(getMessage(MSG_ID_MAXIMUM, getFilenameMaxLength()));
        } else if (UploadedFile.KEY_SIZE_OVER.equals(attachmentKey)) {
            // 添付ファイルサイズオーバー処理
        } else if (UploadedFile.KEY_SIZE_ZERO.equals(attachmentKey)) {
            // 添付ファイルサイズ０処理
        }
    }

    private int getFilenameMaxLength() {
        String s = SystemConfig.getValue(Constants.KEY_FILENAME_MAX_LENGTH);
        return Integer.valueOf(s).intValue();
    }

    private FacesMessage getMessage(String messageCode, Object... args) {
        Message m = Messages.getMessage(messageCode, args);
        return new FacesMessage(FacesMessage.SEVERITY_ERROR, m.getSummary(), m.getMessage());
    }
}
