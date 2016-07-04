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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import ognl.ClassResolver;

/**
 * ClassResolver loading from multiple ClassLoader in Ognl.
 *
 * @author opentone
 */
public class MultipleLoaderClassResolver implements ClassResolver {

    /**
     * Loads class from multiple ClassLoader.
     *
     * If this method can not load target class,
     * it tries to add package java.lang(default package)
     *  and load target class.
     * Still, if it can not the class, throws ClassNotFoundException.
     * (behavior is put together on DefaultClassResolver.)
     *
     * @param className class name --  that wants to load it.
     * @param loaderMap map -- that has VALUES ClassLoader (KEYS are arbitary).
     * @return loaded class from ClassLoader defined loaderMap.
     */
    @SuppressWarnings("unchecked")
    public Class classForName(String className, Map loaderMap)
            throws ClassNotFoundException {
        Collection<ClassLoader> loaders = null;
        // add context-classloader
        loaders = new HashSet<ClassLoader>();
        loaders.add(Thread.currentThread().getContextClassLoader());
        if (loaderMap != null && !loaderMap.isEmpty()) {
            loaders.addAll(loaderMap.values());
        }
        Class clazz = null;
        ClassNotFoundException lastCause = null;
        for (Iterator<ClassLoader> it = loaders.iterator(); it.hasNext();) {
            ClassLoader loader = it.next();
            try {
                clazz = loader.loadClass(className);
                return clazz;
            } catch (ClassNotFoundException fqcnException) {
                lastCause = fqcnException;
                try {
                    if (className.indexOf('.') == -1) {
                        clazz = loader.loadClass("java.lang." + className);
                        return clazz;
                    }
                } catch (ClassNotFoundException defaultClassException) {
                    lastCause = defaultClassException;
                    // try next loader.
                }
            }
        }
        // can't load class at end.
        throw lastCause;
    }
}
