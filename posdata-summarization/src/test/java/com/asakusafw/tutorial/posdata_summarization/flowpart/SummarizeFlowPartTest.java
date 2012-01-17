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
package com.asakusafw.tutorial.posdata_summarization.flowpart;

import org.junit.Test;

import com.asakusafw.testdriver.FlowPartTester;
import com.asakusafw.testdriver.excel.ExcelSheetSinkFactory;
import com.asakusafw.testdriver.html.HtmlDifferenceSinkFactory;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PreparedPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summarized;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;

/**
 * 集計フローパートのテスト
 */
public class SummarizeFlowPartTest {

    /**
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Test
    public void testSummarizeFlowPart() {
        String testDataSheet = "SummarizeFlowPart.xls";
        String dumpDir = "target/SummarizeFlowPartTest";

        FlowPartTester tester = new FlowPartTester(getClass());

        In<PreparedPosItem> preparedPosItemIn = tester.input("preparedPosItem", PreparedPosItem.class).prepare(
                testDataSheet + "#preparedPosItem");

        Out<Summarized> summarizedAllStore = tester.output("allStore", Summarized.class)
                .verify(testDataSheet + "#allStore", testDataSheet + "#allStoreRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/allStore.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/allStore.html"));

        Out<Summarized> summarizedAllStoreSection = tester.output("allStoreSection", Summarized.class)
                .verify(testDataSheet + "#allStoreSection", testDataSheet + "#allStoreSectionRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/allStoreSection.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/allStoreSection.html"));

        Out<Summarized> summarizedAllStoreCategory1 = tester.output("allStoreCategory1", Summarized.class)
                .verify(testDataSheet + "#allStoreCategory1", testDataSheet + "#allStoreCategory1Rule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/allStoreCategory1.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/allStoreCategory1.html"));

        Out<Summarized> summarizedAllStoreCategory2 = tester.output("allStoreCategory2", Summarized.class)
                .verify(testDataSheet + "#allStoreCategory2", testDataSheet + "#allStoreCategory2Rule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/allStoreCategory2.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/allStoreCategory2.html"));

        Out<Summarized> summarizedAllStoreSku = tester.output("allStoreSku", Summarized.class)
                .verify(testDataSheet + "#allStoreSku", testDataSheet + "#allStoreSkuRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/allStoreSku.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/allStoreSku.html"));

        Out<Summarized> summarizedEachStore = tester.output("eachStore", Summarized.class)
                .verify(testDataSheet + "#eachStore", testDataSheet + "#eachStoreRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/eachStore.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/eachStore.html"));

        Out<Summarized> summarizedEachStoreSection = tester.output("eachStoreSection", Summarized.class)
                .verify(testDataSheet + "#eachStoreSection", testDataSheet + "#eachStoreSectionRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/eachStoreSection.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/eachStoreSection.html"));

        Out<Summarized> summarizedEachStoreCategory1 = tester.output("eachStoreCategory1", Summarized.class)
                .verify(testDataSheet + "#eachStoreCategory1", testDataSheet + "#eachStoreCategory1Rule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/eachStoreCategory1.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/eachStoreCategory1.html"));

        Out<Summarized> summarizedEachStoreCategory2 = tester.output("eachStoreCategory2", Summarized.class)
                .verify(testDataSheet + "#eachStoreCategory2", testDataSheet + "#eachStoreCategory2Rule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/eachStoreCategory2.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/eachStoreCategory2.html"));

        Out<Summarized> summarizedEachStoreSku = tester.output("eachStoreSku", Summarized.class)
                .verify(testDataSheet + "#eachStoreSku", testDataSheet + "#eachStoreSkuRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/eachStoreSku.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/eachStoreSku.html"));

        tester.runTest(new SummarizeFlowPart(preparedPosItemIn, summarizedAllStore, summarizedAllStoreSection,
                summarizedAllStoreCategory1, summarizedAllStoreCategory2, summarizedAllStoreSku, summarizedEachStore,
                summarizedEachStoreSection, summarizedEachStoreCategory1, summarizedEachStoreCategory2,
                summarizedEachStoreSku));
    }

}
