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
package jp.co.opentone.bsol.linkbinder.view.logo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.linkbinder.Constants;

/**
 * プロジェクトロゴ管理クラス.
 * @author opentone
 */
public class ProjectLogoManager  implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 2715763431053922629L;

    /**
     * logger.
     */
    private static Logger log = LoggerFactory.getLogger(ProjectLogoManager.class);

    /**
     * ディレクトリ セパレータ.
     */
    private static final String DIR_SEPARETOR = "/";

    /**
     * コンストラクタ.
     */
    public ProjectLogoManager() {
    }

    /**
     * 渡されたprojectIdよりロゴファイル情報を取得する.
     *
     * @param projectId プロジェクトID
     * @return ロゴファイル情報(ファイルの中身、最終更新日時)
     */
    public ProjectLogo get(String projectId) {
        String logoFile = detectLogoFile(projectId);
        if (log.isDebugEnabled()) {
            log.debug("logoFile[" + logoFile + "]");
        }

        return getLogoData(logoFile);
    }

    /**
     * プロジェクトロゴファイルが変更されたか否かを判断する.
     *
     * @param projectId プロジェクトID
     * @param ifModifiedSince If-Modified-Since値
     * @return true:変更された false:変更されていない
     */
    public boolean isModified(String projectId, String ifModifiedSince) {
        if (log.isDebugEnabled()) {
            log.debug("projectId[" + projectId + "]");
            log.debug("ifModifiedSince[" + ifModifiedSince + "]");
        }

        if (StringUtils.isEmpty(ifModifiedSince)) {
            // If-Modified-Sinceヘッダ(値)が無いならばtrue
            return true;
        }

        long ifModifiedSinceLongValue = 0;
        try {
            Date ifModifiedSinceDateValue = DateUtil.parseDate(ifModifiedSince);
            ifModifiedSinceLongValue = ifModifiedSinceDateValue.getTime();
            if (log.isDebugEnabled()) {
                log.debug("ifModifiedSinceDateValue[" + ifModifiedSinceDateValue + "]");
                log.debug("ifModifiedSinceLongValue[" + ifModifiedSinceLongValue + "]");
            }
        } catch (DateParseException e) {
            // 日付解析失敗なので変更されたと見なす
            log.warn("If-Modified-Since parse error[" + ifModifiedSince + "]", e);
            return true;
        }

        String logoFile = detectLogoFile(projectId);
        File f = new File(logoFile);
        if (log.isDebugEnabled()) {
            log.debug("f.exists[" + f.exists() + "]");
            log.debug("f.lastModified[" + f.lastModified() + "]");
        }

        // ミリ秒の値を秒単位で比較するため1/1000する(12:34:56.789と12:34:56.000は同じと見なす)
        //CHECKSTYLE:OFF
        if (!f.exists()
              || f.lastModified() / 1000 != ifModifiedSinceLongValue / 1000) {
            return true;
        }
        //CHECKSTYLE:ON

        if (log.isDebugEnabled()) {
            log.debug("return false");
        }
        return false;
    }

    /**
     * 渡されたprojectIdよりロゴファイルを決定する.
     *
     * @param projectId プロジェクトID
     * @return ロゴファイル情報(ファイルの中身、最終更新日時)
     */
    private String detectLogoFile(String projectId) {
        String projectLogoDir = SystemConfig.getValue(Constants.KEY_PROJECT_LOGO_DIR);
        String projectLogoExtension = SystemConfig.getValue(Constants.KEY_PROJECT_LOGO_EXTENSION);
        String projectLogoDefault = SystemConfig.getValue(Constants.KEY_PROJECT_LOGO_DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("projectId[" + projectId + "]");
            log.debug("projectLogoDir[" + projectLogoDir + "]");
            log.debug("projectLogoExtension[" + projectLogoExtension + "]");
            log.debug("projectLogoDefault[" + projectLogoDefault + "]");
        }

        String result;
        if (StringUtils.isEmpty(projectId)) {
            // projectIdが無効な場合にはデフォルトのLogo
            result = projectLogoDir + DIR_SEPARETOR + projectLogoDefault;
        } else {
            // projectIdが有効な場合には${projectId}.png
            result = projectLogoDir + DIR_SEPARETOR + projectId + projectLogoExtension;

            // ただし、${projectId}.pngが存在しない場合にはデフォルトのLogoを使用する
            File f = new File(result);
            if (!f.exists()) {
                if (log.isDebugEnabled()) {
                    log.debug("File not found[" + result + "]");
                }
                result =  projectLogoDir + DIR_SEPARETOR + projectLogoDefault;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("result[" + result + "]");
        }
        return result;
    }

    /**
     * 渡されたロゴファイルより情報(ファイルの中身、最終更新日時)を取得する.
     *
     * @param logoFile ロゴファイル名
     * @return ロゴファイル情報(ファイルの中身、最終更新日時)
     */
    private ProjectLogo getLogoData(String logoFile) {
        ProjectLogo result = null;
        BufferedInputStream bis = null;

        try {
            File f = new File(logoFile);
            long lastModifiled = f.lastModified();
            byte[] imageData = new byte[(int) f.length()];
            bis = new BufferedInputStream(new FileInputStream(logoFile));
            bis.read(imageData);

            result = new ProjectLogo();
            result.setImage(imageData);
            result.setLastModified(lastModifiled);
        } catch (FileNotFoundException e) {
            log.warn("プロジェクトロゴファイルが見つかりません [" + logoFile + "]");
        } catch (IOException e) {
            log.error("File I/O error[" + logoFile + "]", e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    log.error("File close error.[" + logoFile + "]", e);
                }
            }
        }
        return result;
    }
}
