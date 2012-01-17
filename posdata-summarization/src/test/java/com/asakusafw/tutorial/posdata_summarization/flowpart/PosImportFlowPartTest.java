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
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;

/**
 * POSデータ取込フローパートのテスト
 */
public class PosImportFlowPartTest {

    /**
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Test
    public void testPosImportFlowPart() {
        String testDataSheet = "PosImportFlowPart.xls";
        String dumpDir = "target/PosImportFlowPartTest";

        FlowPartTester tester = new FlowPartTester(getClass());

        tester.setBatchArg("TARGET_DATE", "20120101");

        In<PosItem> in = tester.input("in", PosItem.class).prepare(testDataSheet + "#posItem");

        Out<PosItem> valid = tester.output("valid", PosItem.class)
                .verify(testDataSheet + "#valid", testDataSheet + "#validRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/valid.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/valid.html"));

        Out<ErrorPosItem> invalid = tester.output("invalid", ErrorPosItem.class)
                .verify(testDataSheet + "#invalid", testDataSheet + "#invalidRule")
                .dumpActual(new ExcelSheetSinkFactory(dumpDir + "/invalid.xls"))
                .dumpDifference(new HtmlDifferenceSinkFactory(dumpDir + "/invalid.html"));

        tester.runTest(new PosImportFlowPart(in, valid, invalid));
    }
}
