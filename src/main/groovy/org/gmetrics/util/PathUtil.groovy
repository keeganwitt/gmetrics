/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gmetrics.util

/**
 * Contains static utility methods related to file and directory paths.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
 */
class PathUtil {

    private static final SEP = '/'

    static String getName(String filePath) {
        if (filePath == null) {
            return null
        }
        def separator = normalize(filePath).lastIndexOf('/')
        return (separator == -1) ? filePath : filePath[separator+1..-1]
    }

    static String getParent(String filePath) {
        def normalizedPath = normalize(filePath)
        def partList = normalizedPath ? normalizedPath.tokenize(SEP) : []
        if (partList.size() < 2) {
            return null
        }
        def parentList = partList[0..-2]
        return parentList.join(SEP)
    }

    static String normalize(String path) {
        return path ? path.replaceAll('\\\\', SEP) : path
    }

    static String toPackageName(String filePath) {
        if (!filePath) {
            return null
        }
        def normalizedPath = normalize(filePath).trim()
        if (normalizedPath[0] == SEP) {
            normalizedPath = normalizedPath[1..-1]
        }
        if (normalizedPath[-1] == SEP) {
            normalizedPath = normalizedPath[0..-2]
        }
        return normalizedPath.replace(SEP, '.')
    }

    /**
     * Private constructor. All methods are static.
     */
    private PathUtil() { }
}