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

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;

/**
 * Elasticsearch利用定義.
 * @author opentone
 */
public class ElasticsearchConfiguration implements Serializable {

    private String index;
    private Set<TransportAddress> addresses;

    public static ElasticsearchConfigurationBuilder builder() {
        return new ElasticsearchConfigurationBuilder();
    }

    public String getIndex() {
        return index;
    }

    public Set<TransportAddress> getAddresses() {
        return addresses;
    }

    public static class ElasticsearchConfigurationBuilder {
        /**
         * 接続先(ホスト名、ポート番号).
         */
        private Map<String, Integer> addresses = new HashMap<>();

        /**
         * インデックス.
         * 複数インデックスはサポートしない.
         */
        private String index;

        /**
         *
         */
        public ElasticsearchConfigurationBuilder() {
        }

        public ElasticsearchConfigurationBuilder address(String host, int port) {
            addresses.put(host, port);
            return this;
        }

        public ElasticsearchConfigurationBuilder index(String index) {
            this.index = index;
            return this;
        }

        public ElasticsearchConfiguration build() {
            validate();

            ElasticsearchConfiguration result = new ElasticsearchConfiguration();
            result.addresses = new HashSet<>();
            try {
                for (Entry<String, Integer> a : addresses.entrySet()) {
                    result.getAddresses().add(new InetSocketTransportAddress(InetAddress.getByName(a
                            .getKey()), a.getValue()));
                }
            } catch (UnknownHostException e) {
                throw new ElasticsearchException("指定されたホストを解決できません", e);
            }
            result.index = index;

            return result;
        }

        private void validate() {
            if (StringUtils.isEmpty(index)) {
                throw new ElasticsearchException("indexが指定されていません");
            }
        }
    }
}
