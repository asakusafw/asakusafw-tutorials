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
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summarized;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summary;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;

/**
 * カテゴリデータへの展開フローパートのテスト
 */
public class FlattenSummaryFlowPartTest {

    /**
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Test
    public void testFlattenSummaryFlowPart() {
        String testDataSheet = "FlattenSummaryFlowPart.xls";
        String dumpDir = "target/FlattenSummaryFlowPartTest";

        FlowPartTester tester = new FlowPartTester(getClass());

        In<Summarized> summarizedAllStoreIn = tester.input("allStore", Summarized.class).prepare(
                testDataSheet + "#allStore");
        In<Summarized> summarizedAllStoreSectionIn = tester.input("allStoreSection", Summarized.class).prepare(
                testDataSheet + "#allStoreSection");
        In<Summarized> summarizedAllStoreCategory1In = tester.input("allStoreCategory1", Summarized.class).prepare(
                testDataSheet + "#allStoreCategory1");
        In<Summarized> summarizedAllStoreCategory2In = tester.input("allStoreCategory2", Summarized.class).prepare(
                testDataSheet + "#allStoreCategory2");
        In<Summarized> summarizedAllStoreSkuIn = tester.input("allStoreSku", Summarized.class).prepare(
                testDataSheet + "#allStoreSku");
        In<Summarized> summarizedEachStoreIn = tester.input("eachStore", Summarized.class).prepare(
                testDataSheet + "#eachStore");
        In<Summarized> summarizedEachStoreSectionIn = tester.input("eachStoreSection", Summarized.class).prepare(
                testDataSheet + "#eachStoreSection");
        In<Summarized> summarizedEachStoreCategory1In = tester.input("eachStoreCategory1", Summarized.class).prepare(
                testDataSheet + "#eachStoreCategory1");
        In<Summarized> summarizedEachStoreCategory2In = tester.input("eachStoreCategory2", Summarized.class).prepare(
                testDataSheet + "#eachStoreCategory2");
        In<Summarized> summarizedEachStoreSkuIn = tester.input("eachStoreSku", Summarized.class).prepare(
                testDataSheet + "#eachStoreSku");

        Out<Summary> summary = tester.output("summary", Summary.class)
                .verify(testDataSheet + "#summary", testDataSheet + "#summaryRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/summary.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/summary.html"));

        tester.runTest(new FlattenSummaryFlowPart(summarizedAllStoreIn, summarizedAllStoreSectionIn,
                summarizedAllStoreCategory1In, summarizedAllStoreCategory2In, summarizedAllStoreSkuIn,
                summarizedEachStoreIn, summarizedEachStoreSectionIn, summarizedEachStoreCategory1In,
                summarizedEachStoreCategory2In, summarizedEachStoreSkuIn, summary));
    }

}
