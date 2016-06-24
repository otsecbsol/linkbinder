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
package jp.co.opentone.bsol.linkbinder.view.util.help;

import java.io.Serializable;

/**
 * ヘルプ文書の内容.
 * @author opentone
 */
public class HelpContent implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 5917563618740559513L;

    /** このヘルプの名前. */
    private String name;
    /** このヘルプの内容. */
    private String content;

    /**
     * 名前と内容を指定してインスタンス化する.
     * @param name 名前
     * @param content 内容
     */
    public HelpContent(String name, String content) {
        this.setName(name);
        this.setContent(content);
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }
}
