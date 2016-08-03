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
package jp.co.opentone.bsol.framework.core.filestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ファイルストアの操作を行うクラス.
 * @author opentone
 */
public class FileStoreClient implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8606090263277430589L;
    /** logger. */
    private static final Logger log = LoggerFactory.getLogger(FileStoreClient.class);

    /** ファイル格納ディレクトリ. */
    private static final String KEY_ATTACHMENT_DIR = "attachment.dir";

    /**
     * ファイルを作成しそのファイルIDを返す.
     * @param sourcePath 対象ファイルのフルパス
     * @return ファイルID
     */
    public String createFile(String sourcePath) {
        String id = generateId();
        try (FileInputStream in = new FileInputStream(sourcePath);
            FileOutputStream out = new FileOutputStream(getFile(id))) {
            IOUtils.copy(in, out);
            return id;
        } catch (IOException e) {
            throw new FileStoreException(e);
        }
    }

    /**
     * ファイルIDが示すファイルを返す.
     * @param id ファイルID
     * @return ファイル
     */
    protected File getFile(String id) {
        return new File(SystemConfig.getValue(KEY_ATTACHMENT_DIR), id);
    }

    /**
     * ファイルIDを生成する.
     * @return ファイルID
     */
    protected String generateId() {
        String result = UUID.randomUUID().toString();
        return result.replace(':', '_');
    }

    /**
     * fileIdに対応するファイルの中身をバイナリデータとして返す.
     * 指定されたファイルパスにファイルをコピーする.
     * ファイルは呼び出し側で削除処理を行う事.
     *
     * @param fileId ファイルID
     * @param filePath ファイルパス
     * @return ファイルのバイナリデータ
     * @throws FileStoreException ファイル情報の取得に失敗
     */
    public byte[] getFileContent(String fileId, String filePath) throws FileStoreException {
        ArgumentValidator.validateNotEmpty(fileId);
        ArgumentValidator.validateNotEmpty(filePath);

        File source = getFile(fileId);
        File dest = new File(filePath);
        // 出力
        try (FileInputStream in = new FileInputStream(source);
            FileOutputStream out = new FileOutputStream(dest)) {
            IOUtils.copy(in, out);
        } catch (IOException e) {
            throw new FileStoreException(e);
        }

        // 出力結果を取得
        try (FileInputStream in = new FileInputStream(dest)) {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new FileStoreException(e);
        }
    }
}
