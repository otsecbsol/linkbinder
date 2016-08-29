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
package jp.co.opentone.bsol.framework.core.google.vision;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Google Cloud Vision API利用定義.
 * @author opentone
 */
public class GoogleVisionApiConfiguration implements Serializable {

    private boolean use;
    private String accountFilePath;
    private String applicationName;
    private int maxResult;

    public static GoogleVisionApiConfigurationBuilder builder() {
        return new GoogleVisionApiConfigurationBuilder();
    }

    public String getAccountFilePath() {
        return accountFilePath;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public boolean isUse() {
        return use;
    }

    public static class GoogleVisionApiConfigurationBuilder {

        private boolean use;
        private String accountFilePath;
        private String applicationName;
        private int maxResult;

        /**
         *
         */
        public GoogleVisionApiConfigurationBuilder() {
        }

        public GoogleVisionApiConfigurationBuilder use(boolean use) {
            this.use = use;
            return this;
        }

        public GoogleVisionApiConfigurationBuilder maxResult(int maxResult) {
            this.maxResult = maxResult;
            return this;
        }

        public GoogleVisionApiConfigurationBuilder accountFilePath(String accountFilePath) {
            this.accountFilePath = accountFilePath;
            return this;
        }

        public GoogleVisionApiConfigurationBuilder applicationName(String applicationName) {
            this.applicationName = applicationName;
            return this;
        }

        public GoogleVisionApiConfiguration build() {
            validate();

            GoogleVisionApiConfiguration result = new GoogleVisionApiConfiguration();
            result.use = use;
            result.accountFilePath = accountFilePath;
            result.applicationName = applicationName;
            result.maxResult = maxResult > 0 ? maxResult : 6;

            return result;
        }

        private void validate() {
            if (StringUtils.isEmpty(accountFilePath)) {
                throw new GoogleVisionApiException("認証ファイルパスが指定されていません");
            }
            if (StringUtils.isEmpty(applicationName)) {
                throw new GoogleVisionApiException("アプリケーション名が指定されていません");
            }
        }
    }
}
