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
package jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.apache.commons.lang.StringUtils;

import com.orangesignal.csv.Csv;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvListHandler;
import com.orangesignal.csv.handlers.StringArrayListHandler;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportProcessType;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportType;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.validation.groups.CreateGroup;

/**
 * マスタデータ一括登録/削除モジュール
 * @author opentone
 */
public abstract class MasterDataImportModule<T> implements Serializable {

    /**
     * 一括登録/削除を実行する.
     * @param page 対象のページオブジェクト
     * @throws ServiceAbortException 処理に失敗
     */
    public void execute(MasterDataImportablePage page) throws ServiceAbortException {
        MasterDataImportParseResult<T> result =
                parse(page.getImportFileInfo(), page.getImportProcessType());
        if (result.isSuccess()) {
            executeImport(page.getImportProcessType(), result.getList());
            page.importSucceeded();
        } else {
            page.importParseFailed(result.getMessages());
        }
    }

    /**
     * 取込ファイルを解析しその結果を返す.
     * @param file 取込ファイル
     * @return 解析結果
     * @throws ServiceAbortException 解析に失敗
     */
    public MasterDataImportParseResult<T> parse(AttachmentInfo file,
                                                MasterDataImportProcessType processType)
                                                        throws ServiceAbortException {
        try {
            List<T> list = loadCsv(file.getSourcePath());
            List<ValidationErrorInfo> violations = validate(file.getSourcePath(), list, processType);

            return new MasterDataImportParseResult<>(
                    violations.isEmpty(),
                    list,
                    toMessageList(violations));
        } catch (IndexOutOfBoundsException e) {
            // 列数が定義に満たない場合はこの例外が発生する
            throw new ServiceAbortException(ApplicationMessageCode.ERROR_PARSING_IMPORT_FILE);
        } catch (IOException e) {
            throw new ServiceAbortException(ApplicationMessageCode.ERROR_PARSING_IMPORT_FILE);
        }
    }

    /**
     * CSV読込定義情報を生成する.
     * @return CSV読込定義情報
     */
    protected CsvConfig createCsvConfig() {
        CsvConfig config = new CsvConfig();
        config.setQuoteDisabled(false);             // 囲み文字を有効化
        config.setIgnoreLeadingWhitespaces(true);   // 値前の空白を除去
        config.setIgnoreTrailingWhitespaces(true);  // 値後の空白を除去
        config.setIgnoreEmptyLines(true);           // 空行を無視
        config.setSkipLines(1);                     // 先頭行(タイトル行)をスキップ
//        config.setVariableColumns(false);

        return config;
    }

    /**
     * CSVを読み込み、オブジェクトに変換して返す.
     * @param file CSVファイル
     * @return 変換結果
     * @throws IOException 読込失敗
     */
    protected List<T> loadCsv(String file) throws IOException {
        CsvConfig config = createCsvConfig();
        CsvListHandler<T> handler = createCsvListHandler();

        List<T> list = Csv.load(newInputStream(file),
                SystemConfig.getValue(Constants.KEY_CSV_ENCODING),
                config,
                handler);

        return list;
    }

    private InputStream newInputStream(String file) throws IOException {
        return Files.newInputStream(Paths.get(file));
    }

    /**
     * 入力値を検証し結果を返す.
     * @param file 取込ファイル
     * @param list 入力値が格納されたオブジェクト.
     * @return 検証結果
     * @throws IOException
     */
    protected List<ValidationErrorInfo> validate(String file,
                                                List<T> list,
                                                MasterDataImportProcessType processType)
                                                        throws IOException {
        List<ValidationErrorInfo> result = new ArrayList<>();

        // 列数を検証
        CsvConfig config = createCsvConfig();
        List<String[]> lines = Csv.load(
                    newInputStream(file),
                    SystemConfig.getValue(Constants.KEY_CSV_ENCODING),
                    config,
                    new StringArrayListHandler());
        int expectedCount = getCsvColumnCount();
        String msgInvalidColumnCount =
                Messages.getMessage(ApplicationMessageCode.ERROR_INVALID_COLUMN_COUNT)
                    .getMessage();
        IntStream.range(0, lines.size()).forEach(i -> {
            int rowNum = i + 1;
            if (lines.get(i).length != expectedCount) {
                result.add(new ValidationErrorInfo(
                                    rowNum,
                                    null,
                                    null,
                                    msgInvalidColumnCount));
            }
        });
        if (!result.isEmpty()) {
            return result;
        }


        // 各項目の検証
        Class<?>[] validationGroups;
        switch (processType) {
        case CREATE_OR_UPDATE:
            validationGroups = new Class<?>[] { Default.class, CreateGroup.class };
            break;
        case DELETE:
            validationGroups = new Class<?>[] { Default.class};
            break;
        default:
            validationGroups = new Class<?>[] { Default.class};
        }

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        IntStream.range(0, list.size()).forEach(i -> {
            int rowNum = i + 1;
            Set<ConstraintViolation<T>> violations = validator.validate(list.get(i), validationGroups);
            List<ValidationErrorInfo> infoList = violations.stream()
                .map(v -> new ValidationErrorInfo(
                                    rowNum,
                                    toViewName(v.getPropertyPath().toString()),
                                    v.getInvalidValue(),
                                    v.getMessage()))
                .collect(Collectors.toList());

            result.addAll(infoList);
        });

        return result;
    }

    /**
     * 検証エラー情報からエラーメッセージ文言に変換する.
     * @param violations 検証エラー情報
     * @return 検証エラー毎のエラーメッセージ
     */
    protected List<String> toMessageList(List<ValidationErrorInfo> violations) {
        return violations.stream()
                .map(v -> v.format())
                .collect(Collectors.toList());
    }

    /**
     * CSV解析ハンドラを生成して返す.
     * @return CSV解析ハンドラ
     */
    public abstract CsvListHandler<T> createCsvListHandler();

    /**
     * 取込のメイン処理.
     * @param processType 登録処理種別
     * @param list 取込対象
     * @throws ServiceAbortException 取込に失敗
     */
    public abstract void executeImport(MasterDataImportProcessType processType, List<T> list)
                throws ServiceAbortException;

    /**
     * 指定された取込データをこのオブジェクトが処理できる場合はtrueを返す.
     * @param type 取込データ種別
     * @return 判定結果
     */
    public abstract boolean accept(MasterDataImportType type);

    /**
     * Beanフィールド名を表示名に変換する.
     * @param fieldName フィールド名
     * @return 表示名
     */
    protected abstract String toViewName(String fieldName);

    /**
     * CSV列数を返す.
     * @return 列数
     */
    protected abstract int getCsvColumnCount();

    public static class ValidationErrorInfo {
        public final int rowNum;
        public final String fieldName;
        public final Object value;
        public final String message;

        public ValidationErrorInfo(int rowNum, String fieldName, Object value, String message) {
            this.rowNum = rowNum;
            this.fieldName = fieldName;
            this.value = value;
            this.message = message;
        }

        public String format() {
            if (StringUtils.isNotEmpty(fieldName)) {
                return String.format("%d行目 [%s]: %s", rowNum, fieldName, message);
            } else {
                return String.format("%d行目 : %s", rowNum, message);
            }
        }
    }
}
