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
package jp.co.opentone.bsol.linkbinder.dto;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import jp.co.opentone.bsol.linkbinder.dto.UpdateMode.ModeHolder;


/**
 * {@link UpdateMode}のテストケース.
 * @author opentone
 */
public class UpdateModeTest {

    /**
     * {@link UpdateMode#setUpdateMode(List, UpdateMode)}の検証.
     */
    @Test
    public void testSetUpdateMode() {
        UpdateMode[] modes = UpdateMode.values();
        int length = modes.length;

        List<Value> values = new ArrayList<Value>();
        for (int i = 0; i < 10; i++) {
            values.add(new Value(modes[i % length]));
        }

        UpdateMode.setUpdateMode(values, UpdateMode.NEW);
        for (Value v : values) {
            assertEquals(UpdateMode.NEW, v.getMode());
        }
    }

    static class Value implements ModeHolder {

        private UpdateMode mode;

        Value(UpdateMode mode) {
            this.mode = mode;
        }
        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.dto.UpdateMode.ModeHolder#getMode()
         */
        public UpdateMode getMode() {
            return mode;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.dto.UpdateMode.ModeHolder#setMode(jp.co.opentone.bsol.linkbinder.dto.UpdateMode)
         */
        public void setMode(UpdateMode mode) {
            this.mode = mode;
        }
    }
}
