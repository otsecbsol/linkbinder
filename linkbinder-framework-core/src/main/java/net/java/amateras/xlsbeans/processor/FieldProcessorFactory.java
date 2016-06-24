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

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import net.java.amateras.xlsbeans.annotation.Cell;
import net.java.amateras.xlsbeans.annotation.HorizontalRecords;
import net.java.amateras.xlsbeans.annotation.IterateTables;
import net.java.amateras.xlsbeans.annotation.LabelledCell;
import net.java.amateras.xlsbeans.annotation.SheetName;
import net.java.amateras.xlsbeans.annotation.VerticalRecords;

/**
 *
 * @author opentone
 */
public class FieldProcessorFactory {

	private static ConcurrentHashMap<Class<? extends Annotation>, FieldProcessor>
		map = new ConcurrentHashMap<Class<? extends Annotation>, FieldProcessor>();

	static {
		map.put(Cell.class, new CellProcessor());
		map.put(LabelledCell.class, new LabelledCellProcessor());
		map.put(HorizontalRecords.class, new HorizontalRecordsProcessor());
		map.put(VerticalRecords.class, new VerticalRecordsProcessor());
		map.put(SheetName.class, new SheetNameProcessor());
		map.put(IterateTables.class, new IterateTablesProcessor());

		try {
			InputStream in = FieldProcessorFactory.class.getResourceAsStream(
					"/xlsbeans.properties");
			if(in != null){
				Properties props = new Properties();
				props.load(in);

		        ClassLoader clsLoader = Thread.currentThread().getContextClassLoader();
		        if (clsLoader == null) {
		            clsLoader = FieldProcessorFactory.class.getClassLoader();
		        }

				for(Map.Entry<Object, Object> entry : props.entrySet()){
					try {
						Class<? extends Annotation> annoClazz
							= clsLoader.loadClass((String)entry.getKey()).asSubclass(Annotation.class);

						Class<? extends FieldProcessor> procClazz = clsLoader.loadClass(
			            		(String)entry.getValue()).asSubclass(FieldProcessor.class);

			            map.put(annoClazz, procClazz.newInstance());
					} catch(Exception ex){
						// TODO Logging or throw exception
						ex.printStackTrace();
					}
				}
			}
		} catch(Exception ex){
			// TODO Logging or throw exception
			ex.printStackTrace();
		}
	}

	/**
	 * Registers the annotation and the field processor to this factory.
	 *
	 * @param ann the annotation type
	 * @param processor the field processor which process the given annotation
	 */
	public static void registerProcessor(Class<? extends Annotation> ann,
			FieldProcessor processor){
		map.put(ann, processor);
	}

	/**
	 * Returns the <code>FieldProcessor</code> which corresponds to the given annotation.
	 *
	 * @param ann the field annotation
	 * @return the field processor. If processors which correspond to the
	 *   given annotation are not registered, this method returns <code>null</code>.
	 */
	public static FieldProcessor getProcessor(Annotation ann){
		return map.get(ann.annotationType());
	}

}
