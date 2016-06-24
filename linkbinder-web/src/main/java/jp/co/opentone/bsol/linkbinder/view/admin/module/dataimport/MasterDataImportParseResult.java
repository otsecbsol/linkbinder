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
package jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport;

import java.io.Serializable;
import java.util.List;

/**
 * マスタデータ取込用ファイルの解析結果.
 * @author opentone
 */
public class MasterDataImportParseResult<T> implements Serializable {

    private boolean success;
    private List<T> list;
    private List<String> messages;

    public MasterDataImportParseResult(boolean success) {
        this.success = success;
    }
    public MasterDataImportParseResult(boolean success, List<T> list) {
        this.success = success;
        this.list = list;
    }
    public MasterDataImportParseResult(boolean success, List<T> list, List<String> messages) {
        this.success = success;
        this.list = list;
        this.setMessages(messages);
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public List<T> getList() {
        return list;
    }
    public void setList(List<T> list) {
        this.list = list;
    }
    public List<String> getMessages() {
        return messages;
    }
    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
