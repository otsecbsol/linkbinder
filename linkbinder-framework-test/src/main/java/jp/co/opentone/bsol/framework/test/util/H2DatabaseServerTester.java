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

package jp.co.opentone.bsol.framework.test.util;

import org.h2.tools.Server;

public class H2DatabaseServerTester {

    /**
     * H2 Databaseのサーバー.
     * <p>
     * {@link ServiceActionHandler}は、トランザクション制御を行うオブジェクトに
     * 依存しているのでデータベースが起動している必要がある.
     * </p>
     */
    private static Server s;

    public void setUp() throws Exception {
        try {
            s = Server.createTcpServer(new String[] { "-baseDir", "./data", "-tcpPort", "9092" });
            s.start();

        } catch (Exception ignore) {}
    }

    public void tearDown() throws Exception {
        s.shutdown();
    }

    public static void main(String[] args) throws Exception {
        new H2DatabaseServerTester().setUp();
    }
}
