/**
 * Copyright 2012 Asakusa Framework Team.
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
package com.asakusafw.tutorial.posdata_summarization.jobflow;

import org.junit.Test;

import com.asakusafw.testdriver.JobFlowTester;
import com.asakusafw.testdriver.excel.ExcelSheetSinkFactory;
import com.asakusafw.testdriver.html.HtmlDifferenceSinkFactory;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Category;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorConsumptionTax;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.FormLayout;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summary;

/**
 * POSデータ集計ジョブフローのテスト
 */
public class SummarizeJobFlowTest {

    /**
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Test
    public void testSummarizeJobFlow() {
        String testDataSheet = "SummarizeJobFlow.xls";
        String dumpDir = "target/SummarizeJobFlowTest";

        JobFlowTester tester = new JobFlowTester(getClass());

        tester.input("checkpoint", PosItem.class).prepare(testDataSheet + "#checkpoint");
        tester.input("category", Category.class).prepare(testDataSheet + "#category");

        tester.output("summary", Summary.class).verify(testDataSheet + "#summary", testDataSheet + "#summaryRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/summary.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/summary.html"));

        tester.output("formLayout", FormLayout.class)
                .verify(testDataSheet + "#formLayout", testDataSheet + "#formLayoutRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/formLayout.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/formLayout.html"));

        tester.output("categoryCodeError", ErrorPosItem.class)
                .verify(testDataSheet + "#categoryCodeError", testDataSheet + "#categoryCodeErrorRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/categoryCodeError.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/categoryCodeError.html"));

        tester.output("consumptionTaxError", ErrorConsumptionTax.class)
                .verify(testDataSheet + "#consumptionTaxError", testDataSheet + "#consumptionTaxErrorRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/consumptionTaxError.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/consumptionTaxError.html"));

        tester.runTest(SummarizeJobFlow.class);
    }
}
