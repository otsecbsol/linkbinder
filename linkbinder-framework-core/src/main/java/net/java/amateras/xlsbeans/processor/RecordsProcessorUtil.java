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

package net.java.amateras.xlsbeans.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import net.java.amateras.xlsbeans.Utils;
import net.java.amateras.xlsbeans.XLSBeansException;
import net.java.amateras.xlsbeans.annotation.Column;
import net.java.amateras.xlsbeans.xml.AnnotationReader;

/**
 * Provides generic utility methods for {@link HorizontalRecordsProcessor} and
 * {@link VerticalRecordsProcessor}.
 *
 * @author opentone
 */
public class RecordsProcessorUtil {

	public static void checkColumns(Class<?> recordClass,
			List<HeaderInfo> headers, AnnotationReader reader) throws Exception {

		for(Object property: Utils.getColumnProperties(recordClass.newInstance(), null, reader)){
			Column column = null;
			if(property instanceof Method){
				column = reader.getAnnotation(recordClass, (Method) property, Column.class);
			} else if(property instanceof Field){
				column = reader.getAnnotation(recordClass, (Field) property, Column.class);
			}

			if(!column.optional()){
				String columnName = column.columnName();
				boolean find = false;
				for(HeaderInfo info: headers){
					if(info.getHeaderLabel().equals(columnName)){
						find = true;
						break;
					}
				}
				if(!find){
					throw new XLSBeansException("Column '" + columnName + "' doesn't exist.");
				}
			}
		}
	}

}
