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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author opentone
 */
public class ElasticsearchConfigurationTest {

    @Test
    public void test() {
        String host = "192.168.99.100";
        int port = 9200;
        String index = "test-es-config";

        ElasticsearchConfiguration config = ElasticsearchConfiguration.builder()
            .address(host, port)
            .index(index)
            .build();

        assertThat(config, notNullValue());
        assertThat(config.getIndex(), is(index));
        assertThat(config.getAddresses().size(), is(1));
        config.getAddresses().stream().forEach(a -> {
            assertThat(a.getHost(), is(host));
            assertThat(a.getPort(), is(port));
        });
    }

    @Test(expected = ElasticsearchException.class)
    public void testThrowException() {
        ElasticsearchConfiguration.builder()
            .address("hoge", 9200)
            .index("test-es-config")
            .build();
    }
}
