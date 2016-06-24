/**
 *
 */
package jp.co.opentone.bsol.linkbinder.util;

import java.util.ArrayList;
import java.util.List;

import jp.co.opentone.bsol.framework.core.validation.constraints.impl.AlphanumericValidator;


/**
 * 取り込んだ値を検証するValidator.
 * @author opentone
 */
public class CSVMessageFormatter {

    /**
     * メッセージコード.
     */
    public static final String MSG_ERROR = "ERROR";
    public static final String MSG_WARN = "WARN";
    public static final String MSG_INFO = "INFO";
    public static final String MSG_UNDEFINED = "UNDEFINED";


    // TODO : この辺りはプロパティに設定したい
    /**
     * 最大桁数を越えた場合のメッセージ.
     */
    public static final String MSG_MAXIMUM = "最大桁数を超えています。";
    /**
     * 必須項目が未入力の場合のメッセージ.
     */
    public static final String MSG_REQUIRE = "必須項目が入力されていません。";

    /**
     * 入力項目が過多の場合のメッセージ.
     */
    public static final String MSG_EXCESS = "入力されている項目が指定されている項目数を超えています。";

    /**
     * 指定したIDが存在しない場合のメッセージ
     */
    public static final String MSG_NO_ID = "指定されたIDは存在しませんでした。";
    /**
     * 指定したPJが存在しない場合のメッセージ
     */
    public static final String MSG_NO_PJ = "指定されたプロジェクト存在しませんでした。";
    /**
     * 入力した形式が間違っている場合のメッセージ（英数字のみ）
     */
    public static final String CHK_NO_KANA = "項目に入力されている形式が間違っています。";

    /**
     * 空のインスタンスを生成する.
     */
    public CSVMessageFormatter() {
    }



    /**
     * 値を検証する.
     * @param value
     */
    public List<String> validate(String value, boolean require, boolean chkNoKana, int countLine, String colName) {
        List<String> result =  new ArrayList<>();
        if (value.length() > 0){
            //英数字のみ
            if(chkNoKana) {
                AlphanumericValidator cls  = new AlphanumericValidator();
                if (!cls.isValid(value, null)){
                    result = setMessage(MSG_ERROR, CHK_NO_KANA, countLine, colName);
                }
            }
        } else {
          //必須チェック
            if (require) {
                result = setMessage(MSG_ERROR, MSG_REQUIRE, countLine, colName);
            }
        }
        return result;
    }

    public List<String> validateMax(int value, int max, int countLine) {
        List<String> result =  new ArrayList<>();
        if (value > max){
            result = setMessage(MSG_ERROR, MSG_EXCESS, countLine, "");
        }
        return result;
    }


    /**
     * メッセージを作成する.
     * @param messageType メッセージのタイプ
     * @param message 表示する内容
     * @param countLine 対応行数
     * @param colName 対象項目
     * @return List<String>
     */
    public List<String> setMessage( String messageType, String message, int countLine, String colName) {
        List<String> result = new ArrayList<>();
        String msg = null;
        if  ( messageType == null) {
            messageType = "";
        }
        if  ( message == null) {
            message = "";
        }
        if ( messageType == "" && message == "") {
            return result;
        } else {
            if (countLine > 0) {
                if (colName.length() > 0) {
                    msg = countLine + "行目 " + "[" + colName + "]" + message;
                } else {
                    msg = countLine + "行目 " + message;
                }
            } else {
                msg = message;
            }
            result.add(messageType);
            result.add(msg);
            result.add(message);
        }
        return result;

    }
    public String createMessage(String message, String messageCode) {
        String result = "";
        result = "<p class=\""+ messageCode + "\">";
        result +=  message;
        result += "</p>";

        return result;
    }


}
