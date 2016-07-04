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

package net.java.amateras.xlsbeans.xml;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author opentone
 */
public class AnnotationInfo {

	private String annotationClass;
	private Map<String, String> annotationAttributes = new HashMap<String, String>();

	public void setAnnotationClass(String annotationClass){
		this.annotationClass = annotationClass;
	}

	public String getAnnotationClass(){
		return this.annotationClass;
	}

	public void addAnnotationAttribute(String name, String value){
		this.annotationAttributes.put(name, value);
	}

	public String getAnnotationAttribute(String name){
		return this.annotationAttributes.get(name);
	}

	public String[] getAnnotationAttributeKeys(){
		return this.annotationAttributes.keySet().toArray(new String[0]);
	}

}
