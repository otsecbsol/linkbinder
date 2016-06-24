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
package jp.co.opentone.bsol.framework.core.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;


/**
 * @author opentone
 */
public class PropertyGetUtilTest {

    @Test
    public void testGetNestedProperty(){
        Someone someone1 = new Someone("someone1");
        someone1.setSomething(new Something("something1"));
        Someone someone2 = new Someone("someone2");
        someone2.setArraySomethings(Arrays.asList(
                new Something("something2-1"),
                new Something("something2-2",
                        new Something(null)),
                new Something("something2-3",
                      new Something("something2-3-1"),
                      new Something("something2-3-2"))
                ).toArray(new Something[0]));

        assertEquals("something1",
                (String)PropertyGetUtil.getNestedProperty(someone1,
                        "something.name"));
        assertNull(PropertyGetUtil.getNestedProperty(someone2,
                        "arraySomethings[0].somethings[0].name"));
        assertEquals("something2-2",
                (String)PropertyGetUtil.getNestedProperty(someone2,
                        "arraySomethings[1].name"));
        assertNull(PropertyGetUtil.getNestedProperty(someone2,
                        "arraySomethings[1].somethings[0].name"));
        assertEquals("something2-3-2",
                (String)PropertyGetUtil.getNestedProperty(someone2,
                        "arraySomethings[2].somethings[1].name"));
    }

    public static class Someone {
        private String name;
        private Something something;
        private Something[] arraySomethings;
        private List<Something> listSomethings;
        public Someone(String name){
            setName(name);
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Something getSomething() {
            return something;
        }
        public void setSomething(Something something) {
            this.something = something;
        }
        public Something[] getArraySomethings() {
            return arraySomethings;
        }
        public void setArraySomethings(Something[] arraySomethings) {
            this.arraySomethings = arraySomethings;
        }

        public void setListSomethings(List<Something> listSomethings) {
            this.listSomethings = listSomethings;
        }

        public List<Something> getListSomethings() {
            return listSomethings;
        }

    }

    public static class Something {
        private String name;
        private Something[] somethings;

        public Something(String name){
            setName(name);
        }
        public Something(String name, Something... somethings){
            setName(name);
            setSomethings(somethings);
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setSomethings(Something[] somethings) {
            this.somethings = somethings;
        }

        public Something[] getSomethings() {
            return somethings;
        }
    }

}
