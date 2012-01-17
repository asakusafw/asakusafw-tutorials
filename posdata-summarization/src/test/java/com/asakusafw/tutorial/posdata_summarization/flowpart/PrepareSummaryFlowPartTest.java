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
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Category;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorConsumptionTax;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PreparedPosItem;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;

/**
 * 集計元データ作成フローパートのテスト
 */
public class PrepareSummaryFlowPartTest {

    /**
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Test
    public void testPrepareSummaryFlowPart() {
        String testDataSheet = "PrepareSummaryFlowPart.xls";
        String dumpDir = "target/PrepareSummaryFlowPartTest";

        FlowPartTester tester = new FlowPartTester(getClass());

        In<PosItem> posItemIn = tester.input("posItem", PosItem.class).prepare(testDataSheet + "#posItem");
        In<Category> categoryIn = tester.input("category", Category.class).prepare(testDataSheet + "#category");

        Out<PreparedPosItem> prepared = tester.output("prepared", PreparedPosItem.class)
                .verify(testDataSheet + "#prepared", testDataSheet + "#preparedRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/prepared.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/prepared.html"));

        Out<ErrorPosItem> categoryCodeError = tester.output("categoryCodeError", ErrorPosItem.class)
                .verify(testDataSheet + "#categoryCodeError", testDataSheet + "#categoryCodeErrorRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/categoryCodeError.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/categoryCodeError.html"));

        Out<ErrorConsumptionTax> consumptionTaxError = tester.output("consumptionTaxError", ErrorConsumptionTax.class)
                .verify(testDataSheet + "#consumptionTaxError", testDataSheet + "#consumptionTaxErrorRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/consumptionTaxError.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/consumptionTaxError.html"));

        tester.runTest(new PrepareSummaryFlowPart(posItemIn, categoryIn, prepared, categoryCodeError,
                consumptionTaxError));
    }
}
