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
package org.gmetrics.metric

/**
 * Enum representing the level at which a metric is applied - either method, class or package
 *
 * @author Chris Mair
 */
enum MetricLevel {
    METHOD('method'),
    CLASS('class'),
    PACKAGE('package')

    static MetricLevel parse(String name) {
        return valueOf(name.toUpperCase())
    }

    static List<MetricLevel> parseCommaSeparatedList(String names) {
        def tokens = names.tokenize(',')
        return tokens.collect { name ->
            parse(name.trim())
        }
    }

    static List getNames() {
        return MetricLevel.values().collect { it.name }
    }

    final String name

    String toString() {
        return name
    }

    private MetricLevel(String name) {
        this.name = name
    }
}