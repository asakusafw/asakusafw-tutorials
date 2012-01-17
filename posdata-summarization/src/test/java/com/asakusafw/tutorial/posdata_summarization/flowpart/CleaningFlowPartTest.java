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
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Item;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Store;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;

/**
 * POSデータのクリーニングを行うフローパートのテスト
 */
public class CleaningFlowPartTest {

    /**
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Test
    public void testCleaningFlowPart() {
        String testDataSheet = "CleaningFlowPart.xls";
        String dumpDir = "target/CleaningFlowPartTest";

        FlowPartTester tester = new FlowPartTester(getClass());

        tester.setBatchArg("TARGET_DATE", "20120101");

        In<PosItem> posItemIn = tester.input("posItem", PosItem.class).prepare(testDataSheet + "#posItem");
        In<Store> storeIn = tester.input("store", Store.class).prepare(testDataSheet + "#store");
        In<Item> itemIn = tester.input("item", Item.class).prepare(testDataSheet + "#item");

        Out<PosItem> cleaned = tester.output("cleaned", PosItem.class)
                .verify(testDataSheet + "#cleaned", testDataSheet + "#cleanedRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/cleaned.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/cleaned.html"));

        Out<ErrorPosItem> storeCodeError = tester.output("storeCodeError", ErrorPosItem.class)
                .verify(testDataSheet + "#storeCodeError", testDataSheet + "#storeCodeErrorRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/storeCodeError.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/storeCodeError.html"));

        Out<ErrorPosItem> itemCodeError = tester.output("itemCodeError", ErrorPosItem.class)
                .verify(testDataSheet + "#itemCodeError", testDataSheet + "#itemCodeErrorRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/itemCodeError.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/itemCodeError.html"));

        tester.runTest(new CleaningFlowPart(posItemIn, storeIn, itemIn, cleaned, storeCodeError, itemCodeError));
    }
}
