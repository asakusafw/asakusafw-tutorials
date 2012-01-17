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
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Item;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Store;

/**
 * POSデータクリーニングジョブフローのテスト
 */
public class CleaningJobFlowTest {

    /**
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Test
    public void testCleaningJobFlow() {
        String testDataSheet = "CleaningJobFlow.xls";
        String dumpDir = "target/CleaningJobFlowTest";

        JobFlowTester tester = new JobFlowTester(getClass());

        tester.setBatchArg("TARGET_DATE", "20120101");

        tester.input("posItem", PosItem.class).prepare(testDataSheet + "#posItem");
        tester.input("store", Store.class).prepare(testDataSheet + "#store");
        tester.input("item", Item.class).prepare(testDataSheet + "#item");

        tester.output("checkpoint", PosItem.class)
                .verify(testDataSheet + "#checkpoint", testDataSheet + "#checkpointRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/checkpoint.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/checkpoint.html"));

        tester.output("posItemError", ErrorPosItem.class)
                .verify(testDataSheet + "#posItemError", testDataSheet + "#posItemErrorRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/posItemError.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/posItemError.html"));

        tester.runTest(CleaningJobFlow.class);
    }
}
