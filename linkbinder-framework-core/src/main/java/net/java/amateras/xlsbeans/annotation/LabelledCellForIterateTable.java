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

package net.java.amateras.xlsbeans.annotation;

import java.lang.annotation.Annotation;

/**
 *
 * @author opentone
 */
public class LabelledCellForIterateTable implements LabelledCell{

    private String _label = null;
    private int _labelColumn = -1;
    private int _labelRow = -1;
    private boolean _optional = false;
    private int _range = -1;
    private LabelledCellType _type = null;
    private Class<? extends Annotation> _annotationType = null;
    private String _headerLabel = null;
    private int _skip = 0;

    public LabelledCellForIterateTable (LabelledCell labelledCell, int labelRow, int labelColumn) {
        this._label = "";
        this._labelColumn = labelColumn;
        this._labelRow = labelRow;
        this._optional = labelledCell.optional();
        this._range = labelledCell.range();
        this._type = labelledCell.type();
        this._annotationType = labelledCell.annotationType();
        this._headerLabel = labelledCell.headerLabel();
        this._skip = labelledCell.skip();
    }

    public String label() {
        return this._label;
    }

    public int labelColumn() {
        return this._labelColumn;
    }

    public int labelRow() {
        return this._labelRow;
    }

    public boolean optional() {
        return this._optional;
    }

    public int range() {
        return this._range;
    }

    public LabelledCellType type() {
        return this._type;
    }

    public Class<? extends Annotation> annotationType() {
        return this._annotationType;
    }

    public String headerLabel(){
        return this._headerLabel;
    }

    public int skip() {
    	return this._skip;
    }
}
