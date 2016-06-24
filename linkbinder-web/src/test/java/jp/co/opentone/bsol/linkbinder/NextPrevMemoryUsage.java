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
package jp.co.opentone.bsol.linkbinder;

import java.util.ArrayList;
import java.util.List;

import jp.co.opentone.bsol.linkbinder.dto.Correspon;

/**
 * 前/次リンク追加でコレポン文書IDのリストをメモリ上に保持すると
 * どのくらいのサイズになるかを確認する.
 * @author opentone
 */
public class NextPrevMemoryUsage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int size = 30000;

        long total = getTotalMemory();
        long beforeUsage = getMemoryUsage(total);
        printMemoryUsage(size, total, beforeUsage);

//        List<?> list = createCorresponList(size);
        List<?> list = createIdList(size);

        total = getTotalMemory();
        long afterUsage = getMemoryUsage(total);
        printMemoryUsage(size, total, afterUsage - beforeUsage);

    }

    static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }
    static long getMemoryUsage(long total) {
        return total - Runtime.getRuntime().freeMemory();
    }

    static void printMemoryUsage(int size, long total, long usage) {
        System.out.printf("---------- size: %d%n", size);
        System.out.printf("  total: %dMB, usage: %dkb (%dMB)%n",
                    total/1024,
                    usage/1024,
                    usage/1024/1024);

    }
    static List<Long> createIdList(int size) {
        List<Long> list = new ArrayList<Long>();
        for (int i = 0; i < size; i++)
            list.add(new Long(i));

        return list;
    }

    static List<Correspon> createCorresponList(int size) {
        List<Correspon> list = new ArrayList<Correspon>();
        for (int i = 0; i < size; i++) {
            Correspon c = new Correspon();
            c.setId(new Long(i));
            list.add(c);
        }
        return list;
    }
}
