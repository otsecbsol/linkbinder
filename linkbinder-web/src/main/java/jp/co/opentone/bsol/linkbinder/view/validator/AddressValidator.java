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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

/**
 * コレポン文書の宛先を検証するValidator.
 *
 * @author opentone
 */
public class AddressValidator implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 3157453373170122809L;

    /** logger. */
    private static final Logger log = LoggerFactory.getLogger(AddressValidator.class);

    /**
     * メッセージID:宛先未入力.
     */
    public static final String MSG_ID_REQUIRED = "address_required";
    /**
     * メッセージID:Attention未入力.
     */
    public static final String MSG_ID_ATTENTION_REQUIRED = "address_attention_required";
    /**
     * メッセージID:形式不正.
     */
    public static final String MSG_ID_FORMAT = "address_format";

    /**
     * 入力値をオブジェクトに変換するための型情報.
     */
    private Type mappingType;

    /**
     * To/Cc判定フラグ.
     */
    private boolean to;

    /**
     * To/Ccのいずれのケースを検証するかを指定してインスタンス化する.
     * @param mappingType 検証対象の文字列をObjectとして復元するための型情報
     * @param to Toの場合true
     */
    public AddressValidator(Type mappingType, boolean to) {
        this.mappingType = mappingType;
        this.to = to;
    }

    /**
     * 宛先を検証する.
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

        List<AddressCorresponGroup> addresses = decode((String) value);
        validateRequired(addresses);
   }

    /**
     * 必須の値を検証する.
     * @param addresses 検証対象
     */
    private void validateRequired(List<AddressCorresponGroup> addresses) {
        if (to) {
            if (log.isDebugEnabled()) {
                log.debug("Validation: Addresses not empty");
            }
            if (to && (addresses == null || addresses.size() == 0)) {
                if (log.isDebugEnabled()) {
                    log.debug("Validation Error: Addresses empty");
                }
                throw new ValidatorException(getMessage(MSG_ID_REQUIRED));
            }
            if (addresses != null) {
                int count = 0;  //  登録対象件数
                for (AddressCorresponGroup ag : addresses) {
                    //  削除された宛先は検証対象外
                    if (ag.getMode() == UpdateMode.DELETE) {
                        continue;
                    }
                    count++;
                    List<AddressUser> users = ag.getUsers();
                    if (to && (users == null || users.size() == 0)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Validation Error: Users empty");
                        }
                        throw new ValidatorException(getMessage(MSG_ID_ATTENTION_REQUIRED));
                    }
                }
                // Toは必須
                if (to && count == 0) {
                    throw new ValidatorException(getMessage(MSG_ID_REQUIRED));
                }
            }
        }
    }
    /**
     * 入力値を活動単位とユーザーの関連付けオブジェクトに復元する.
     * @param value 入力値
     * @return 復元結果
     */
    @SuppressWarnings("unchecked")
    private List<AddressCorresponGroup> decode(String value) {
        List<AddressCorresponGroup> result = new ArrayList<AddressCorresponGroup>();
        if (StringUtils.isEmpty(value)) {
            return result;
        }

        try {
            // JSON形式の宛先をオブジェクトに変換
            // 結果をresultに格納する
            List<AddressCorresponGroup> decoded =
                    (List<AddressCorresponGroup>) JSON.decode(value, mappingType);
            result.addAll(decoded);

            return result;
        } catch (JSONException e) {
            if (log.isWarnEnabled()) {
                log.warn("Decode failed. Wrong JSON text format.", e);
            }
            throw new ValidatorException(getMessage(MSG_ID_FORMAT));
        } catch (SecurityException e) {
            if (log.isWarnEnabled()) {
                log.warn("Decode failed. JVM Security error.", e);
            }
            throw new ValidatorException(getMessage(MSG_ID_FORMAT));
        }
    }

    private FacesMessage getMessage(String messageCode, Object... args) {
        Message m = Messages.getMessage(messageCode, args);
        return new FacesMessage(FacesMessage.SEVERITY_ERROR, m.getSummary(), m.getMessage());
    }
}
