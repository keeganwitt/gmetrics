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
package org.gmetrics.result

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel

/**
 * Tests for AggregateNumberMetricResults
 *
 * @author Chris Mair
 */
class AggregateNumberMetricResultTest extends AbstractTestCase {

    private static final DEFAULT_FUNCTIONS = ['total', 'average', 'minimum', 'maximum']
    private static final METRIC = [getName:{'TestMetric'}, getFunctions:{ DEFAULT_FUNCTIONS }] as Metric
    private static final BD = [0.23, 5.01, 3.67]
    private static final LINE_NUM = 67

    private aggregateNumberMetricResult

    void testConstructorThrowsExceptionForNullMetricParameter() {
        shouldFailWithMessageContaining('metric') { new AggregateNumberMetricResult(null, MetricLevel.METHOD, [], LINE_NUM) }
    }

    void testConstructorThrowsExceptionForNullMetricLevelParameter() {
        shouldFailWithMessageContaining('metricLevel') { new AggregateNumberMetricResult(METRIC, null, [], LINE_NUM) }
    }

    void testConstructorThrowsExceptionForNullChildrenParameter() {
        shouldFailWithMessageContaining('children') { new AggregateNumberMetricResult(METRIC, MetricLevel.METHOD, null, null) }
    }

    void testConstructorSetsMetricProperly() {
        def mr = new AggregateNumberMetricResult(METRIC, MetricLevel.METHOD, [], LINE_NUM)
        assert mr.metric == METRIC
    }

    void testConstructorSetsMetricLevelProperly() {
        def mr = new AggregateNumberMetricResult(METRIC, MetricLevel.METHOD, [], LINE_NUM)
        assert mr.metricLevel == MetricLevel.METHOD
    }

    void testGetLineNumberIsSameValuePassedIntoConstructor() {
        def result = new AggregateNumberMetricResult(METRIC, MetricLevel.METHOD, [], 67)
        assert result.getLineNumber() == 67
    }

    // Tests for no children

    void testFunctionValuesForNoChildrenAreAllZero() {
        initializeNoChildMetricResults()
        assert aggregateNumberMetricResult['average'] == 0
        assert aggregateNumberMetricResult['total'] == 0
        assert aggregateNumberMetricResult['minimum'] == 0
        assert aggregateNumberMetricResult['maximum'] == 0
    }

    void testCountForNoChildrenIsZero() {
        initializeNoChildMetricResults()
        assert aggregateNumberMetricResult.count == 0
    }

    void testFunctionValuesForChildrenNullChildFunctionValues() {
        def children = [new StubMetricResult(metric:METRIC, metricLevel:MetricLevel.METHOD)]
        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, MetricLevel.METHOD, children, null)
        assert aggregateNumberMetricResult['average'] == 0
        assert aggregateNumberMetricResult['total'] == 0
        assert aggregateNumberMetricResult['minimum'] == null
        assert aggregateNumberMetricResult['maximum'] == null
    }

    // Tests for a single child

    void testFunctionValuesForASingleMetricAreAllThatMetricValue() {
        initializeOneChildMetricResult(99.5)
        assert aggregateNumberMetricResult['average'] == 99.5
        assert aggregateNumberMetricResult['total'] == 99.5
        assert aggregateNumberMetricResult['minimum'] == 99.5
        assert aggregateNumberMetricResult['maximum'] == 99.5
    }

    // Tests for several children

    void testAverageValueForSeveralIntegerMetricsIsTheAverageOfTheMetricValues() {
        initializeThreeIntegerChildMetricResults()
        assert aggregateNumberMetricResult['average'] == scale(25 / 3)
    }

    void testTotalValueForSeveralIntegerMetricsIsTheSumOfTheMetricValues() {
        initializeThreeIntegerChildMetricResults()
        assert aggregateNumberMetricResult['total'] == 25
    }

    void testMinimumValueForSeveralIntegerMetrics() {
        initializeThreeIntegerChildMetricResults()
        assert aggregateNumberMetricResult['minimum'] == 1
    }

    void testMaximumValueForSeveralIntegerMetrics() {
        initializeThreeIntegerChildMetricResults()
        assert aggregateNumberMetricResult['maximum'] == 21
    }

    void testTotalValueForSeveralBigDecimalMetricsIsTheSumOfTheMetricValues() {
        initializeThreeBigDecimalChildMetricResults()
        assert aggregateNumberMetricResult['total'] == BD[0] + BD[1] + BD[2]
    }

    void testAverageValueForSeveralBigDecimalMetricsIsTheAverageOfTheMetricValues() {
        initializeThreeBigDecimalChildMetricResults()
        def sum = (BD[0] + BD[1] + BD[2])
        assert aggregateNumberMetricResult['average'] == scale(sum / 3)
    }

    void testMinimumValueForSeveralBigDecimalMetrics() {
        initializeThreeBigDecimalChildMetricResults()
        assert aggregateNumberMetricResult['minimum'] == BD.min()
    }

    void testMaximumValueForSeveralBigDecimalMetrics() {
        initializeThreeBigDecimalChildMetricResults()
        assert aggregateNumberMetricResult['maximum'] == BD.max()
    }

    void testCorrectCountForSeveralChildResults() {
        initializeThreeIntegerChildMetricResults()
        assert aggregateNumberMetricResult.count == 3
    }

    void testCorrectCountForChildResultsWithCountsGreaterThanOne() {
        def children = [new StubMetricResult(count:3, total:0), new StubMetricResult(count:7, total:0)]
        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, MetricLevel.METHOD, children, null)
        assert aggregateNumberMetricResult.count == 10
    }

    // Tests for predefinedValues

    void testPredefinedValues_OnlyUsesPredefinedValueThatWasSpecified() {
        initializeOneChildMetricResult(99.5, [total:66])
        assert aggregateNumberMetricResult['average'] == 99.5
        assert aggregateNumberMetricResult['total'] == 66
        assert aggregateNumberMetricResult['minimum'] == 99.5
        assert aggregateNumberMetricResult['maximum'] == 99.5
    }

    void testPredefinedValues_UsesAllPredefinedValues() {
        initializeOneChildMetricResult(99.5, [total:66, average:55, minimum:44, maximum:88])
        assert aggregateNumberMetricResult['average'] == 55
        assert aggregateNumberMetricResult['total'] == 66
        assert aggregateNumberMetricResult['minimum'] == 44
        assert aggregateNumberMetricResult['maximum'] == 88
    }

    // Other tests

    void testDefaultScaleIsAppliedToAverageValue() {
        def children = [new StubMetricResult(count:3, total:10)]
        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, MetricLevel.METHOD, children, null)
        assert aggregateNumberMetricResult['average'] == scale(10/3)
    }

    void testGetAt_NoSuchFunctionName_ReturnsNull() {
        initializeOneChildMetricResult(99.5)
        assert aggregateNumberMetricResult['xxx'] == null
    }

//    void testConfiguredScaleIsAppliedToAverageValue() {
//        def children = [new StubMetricResult(count:3, total:10)]
//        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, children)
//        aggregateNumberMetricResult.scale = 3
//        assert aggregateNumberMetricResult['average'] == scale(10/3, 3)
//    }

    void testUsesFunctionNamesFromMetric() {
        final FUNCTION_NAMES = ['average', 'maximum']
        def metric = [getName:{'TestMetric'}, getFunctions:{ FUNCTION_NAMES }] as Metric
        aggregateNumberMetricResult = new AggregateNumberMetricResult(metric, MetricLevel.METHOD, [], LINE_NUM)
        assert aggregateNumberMetricResult['average'] != null
        assert aggregateNumberMetricResult['maximum'] != null 
        assert aggregateNumberMetricResult['total'] == null
        assert aggregateNumberMetricResult['minimum'] == null
    }

    //--------------------------------------------------------------------------
    // Helper Methods
    //--------------------------------------------------------------------------
    
    private void initializeNoChildMetricResults() {
        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, MetricLevel.METHOD, [], LINE_NUM)
    }

    private void initializeOneChildMetricResult(value, Map predefinedValues=null) {
        def children = [new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, value)]
        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, MetricLevel.METHOD, children, LINE_NUM, predefinedValues)
    }

    private void initializeThreeIntegerChildMetricResults() {
        initializeThreeChildMetricResults(21, 1, 3)
    }

    private void initializeThreeBigDecimalChildMetricResults() {
        initializeThreeChildMetricResults(BD[0], BD[1], BD[2])
    }

    private void initializeThreeChildMetricResults(x, y, z) {
        def children = [new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, x),new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, y), new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, z)]
        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, MetricLevel.METHOD, children, LINE_NUM)
    }

}