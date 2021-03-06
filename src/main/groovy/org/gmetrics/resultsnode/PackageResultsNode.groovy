/*
 * Copyright 2012 the original author or authors.
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
package org.gmetrics.resultsnode

import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.MetricResult
import org.gmetrics.metric.Metric

/**
 * Represents a package node in the hierarchy of metric result nodes
 *
 * @author Chris Mair
 */
class PackageResultsNode implements ResultsNode {

    final String name
    final String packageName
    final String path
    final MetricLevel level = MetricLevel.PACKAGE
    final List<MetricResult> metricResults = []

    private Map<String, ResultsNode> children = [:]

    PackageResultsNode(String name, String packageName, String path) {
        this.name = name
        this.packageName = packageName
        this.path = path
    }

    Map<String, ResultsNode> getChildren() {
        return children
    }

    boolean containsClassResults() {
        return children.values().find { node -> node.containsClassResults() }
    }

    MetricResult getMetricResult(Metric metric) {
        assert metric
        return metricResults.find { metricResult -> metricResult.metric == metric }
    }

    void addChildIfNotEmpty(String name, ResultsNode child) {
        assert name
        assert child
        if (child.metricResults) {
            children[name] = child
        }
    }

    void addChild(String name, ResultsNode child) {
        assert name
        assert child
        children[name] = child
    }

    void applyMetric(Metric metric) {
        this.children = children.asImmutable()
        def childMetricResultsForMetric = []
        children.values().each { child ->
            def metricResult = child.getMetricResult(metric)
            if (metricResult) {
                childMetricResultsForMetric << metricResult
            }
        }
        def packageMetricResult = metric.applyToPackage(path, packageName, childMetricResultsForMetric)
        if (packageMetricResult) {
            metricResults << packageMetricResult
        }
    }

    String toString() {
        return "PackageResultsNode[path=$path, packageName=$packageName, metricResults=$metricResults, children=$children]"
    }
}
