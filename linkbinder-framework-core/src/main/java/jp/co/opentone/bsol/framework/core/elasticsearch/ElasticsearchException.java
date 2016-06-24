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
package jp.co.opentone.bsol.framework.core.elasticsearch;

/**
 * Elasticsearch関連処理で発生した例外.
 * @author opentone
 */
public class ElasticsearchException extends RuntimeException {

    public ElasticsearchException() {
        super();
    }

    public ElasticsearchException(String message) {
        this(message, null);
    }

    public ElasticsearchException(Throwable cause) {
        this(null, cause);
    }

    public ElasticsearchException(String message, Throwable cause) {
        super(message, cause);
    }

}
