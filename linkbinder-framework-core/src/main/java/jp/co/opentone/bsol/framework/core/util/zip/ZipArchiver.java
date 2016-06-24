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
package jp.co.opentone.bsol.framework.core.util.zip;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;


/**
 * 複数のファイルをまとめてひとつのzipファイルを生成するクラス.
 * <h3>使い方</h3>
 * <pre>
 * ZipArchiver zip = new ZipArchiver();
 * zip.add(filename1, byte1);
 * zip.add(filename2, byte2);
 * zip.add(filename3, byte3);
 * zip.archive("foo.zip");
 * byte[] zipdata = zip.toByte();
 * </pre>
 *
 * <p>
 * <code>zip.toByte()</code>または<code>zip.archive("foo.zip")</code>を実行後に
 * <code>zip.add()</code>を実行すると、例外(<code>ZipArchiverRuntimeException</code>)発生。
 * </p>
 * <p>
 * ただし、<code>zip.toByte()</code>または<code>zip.archive("foo.zip")</code>を実行後に
 * <code>zip.add()</code>ではなく<code>zip.toByte()</code>や<code>zip.archive()</code>の実行はOK。
 * </p>
 * <p>
 * 例外が発生した後、そのインスタンスの処理は保証しない。
 * </p>
 * @author opentone
 *
 */
public class ZipArchiver extends ZipExecutor {

    /**
     * インスタンス終了フラグ.
     * このインスタンスが終了している(これ以上add()を受け付けない)かを判定
     */
    private boolean isFinished = false;

    /**
     * 出力ストリーム(ByteArray).
     */
    private ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

    /**
     * 出力ストリーム(Zip。ByteArrayをフィルタ).
     */
    private ZipOutputStream zip = new ZipOutputStream(byteArray);

    /**
     * インスタンスを生成する.
     */
    public ZipArchiver() {
        zip.setEncoding(getSystemConfigZipEncoding());
    }

    /**
     * zipファイルにエントリーを追加する.
     * @param entryName
     *            zipファイルに追加されるファイルの名前
     * @param data
     *            追加されるデータ
     */
    public void add(String entryName, byte[] data) {
        // インスタンスが終了しているか否かを判定
        if (isFinished) {
            // 一度zipを確定させた後に再追加はさせない
            throw new ZipUtilRuntimeException("ZipArchiver has been finished.");
        }

        // 入力パラメータ(entryName)チェック
        if (StringUtils.isEmpty(entryName)) {
            throw new ZipUtilRuntimeException("wrong entryName. entryName is null or \"\".");
        }

        // 入力パラメータ(data)チェック
        if (data == null) {
            throw new ZipUtilRuntimeException("wrong data. data is null.");
        }

        try {
            // zipストリームにファイル名を設定して追加開始を宣言
            zip.putNextEntry(new ZipEntry(entryName));
            // 追加圧縮
            zip.write(data, 0, data.length);
            // 追加終了
            zip.closeEntry();
        } catch (IOException e) {
            // 上記zip操作3つのどれかで例外発生
            throw new ZipUtilRuntimeException(e);
        }
    }

    /**
     * このオブジェクトが保持するzipデータをファイルに出力する.
     * @param filename
     *            出力ファイル名
     */
    public void archive(String filename) {
        try {
            // バイナリ形式でzipを取得
            byte[] buf = this.toByte();

            // 指定ファイルに書き込み
            FileOutputStream f = new FileOutputStream(filename);
            f.write(buf);
            f.close();
        } catch (FileNotFoundException e) {
            // new FileOutputStream()で例外発生
            throw new ZipUtilRuntimeException(e);
        } catch (IOException e) {
            // write(), close()で例外発生
            throw new ZipUtilRuntimeException(e);
        }

    }

    /**
     * このオブジェクトが保持するzipデータをバイナリ形式で返す.
     * @return zipデータ
     */
    public byte[] toByte() {
        try {
            // 二重close()防止。二重closeしても問題は無いようだが一応。
            if (!isFinished) {
                // インスタンス終了フラグを立てる
                isFinished = true;
                // zipデータ作成完了
                zip.close();
            }
        } catch (IOException e) {
            // close()で例外発生
            throw new ZipUtilRuntimeException(e);
        }

        // 作成したzipデータ(ストリーム)をbyte[]にしてreturn
        return byteArray.toByteArray();
    }
}
