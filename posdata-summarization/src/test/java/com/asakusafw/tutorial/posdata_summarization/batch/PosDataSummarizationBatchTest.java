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
package com.asakusafw.tutorial.posdata_summarization.batch;

import org.junit.Test;

import com.asakusafw.testdriver.BatchTester;
import com.asakusafw.testdriver.excel.ExcelSheetSinkFactory;
import com.asakusafw.testdriver.html.HtmlDifferenceSinkFactory;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Category;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorConsumptionTax;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.FormLayout;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Item;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Store;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summary;

/**
 * POSデータ集計バッチのテスト
 */
public class PosDataSummarizationBatchTest {

    /**
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Test
    public void testPosDataSummarizationBatch() {
        String testDataSheet = "PosDataSummarizationBatch.xls";
        String dumpDir = "target/PosDataSummarizationBatchTest";

        BatchTester tester = new BatchTester(getClass());

        tester.setBatchArg("TARGET_DATE", "20120101");

        // CleaningJobFlow
        tester.jobflow("CleaningJobFlow").input("posItem", PosItem.class).prepare(testDataSheet + "#posItem");
        tester.jobflow("CleaningJobFlow").input("store", Store.class).prepare(testDataSheet + "#store");
        tester.jobflow("CleaningJobFlow").input("item", Item.class).prepare(testDataSheet + "#item");

        tester.jobflow("CleaningJobFlow").output("checkpoint", PosItem.class)
                .verify(testDataSheet + "#checkpoint", testDataSheet + "#checkpointRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/checkpoint.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/checkpoint.html"));

        tester.jobflow("CleaningJobFlow").output("posItemError", ErrorPosItem.class)
                .verify(testDataSheet + "#posItemError", testDataSheet + "#posItemErrorRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/posItemError.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/posItemError.html"));

        // SummarizeJobFlow
        tester.jobflow("SummarizeJobFlow").input("category", Category.class).prepare(testDataSheet + "#category");

        tester.jobflow("SummarizeJobFlow").output("summary", Summary.class)
                .verify(testDataSheet + "#summary", testDataSheet + "#summaryRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/summary.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/summary.html"));

        tester.jobflow("SummarizeJobFlow").output("formLayout", FormLayout.class)
                .verify(testDataSheet + "#formLayout", testDataSheet + "#formLayoutRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/formLayout.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/formLayout.html"));

        tester.jobflow("SummarizeJobFlow").output("categoryCodeError", ErrorPosItem.class)
                .verify(testDataSheet + "#categoryCodeError", testDataSheet + "#categoryCodeErrorRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/categoryCodeError.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/categoryCodeError.html"));

        tester.jobflow("SummarizeJobFlow").output("consumptionTaxError", ErrorConsumptionTax.class)
                .verify(testDataSheet + "#consumptionTaxError", testDataSheet + "#consumptionTaxErrorRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/consumptionTaxError.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/consumptionTaxError.html"));

        tester.runTest(PosDataSummarizationBatch.class);
    }

}
