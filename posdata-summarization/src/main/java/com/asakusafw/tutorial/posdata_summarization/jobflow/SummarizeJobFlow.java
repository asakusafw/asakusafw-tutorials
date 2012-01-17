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

import com.asakusafw.tutorial.posdata_summarization.flowpart.FlattenSummaryFlowPartFactory;
import com.asakusafw.tutorial.posdata_summarization.flowpart.FlattenSummaryFlowPartFactory.FlattenSummaryFlowPart;
import com.asakusafw.tutorial.posdata_summarization.flowpart.PrepareSummaryFlowPartFactory;
import com.asakusafw.tutorial.posdata_summarization.flowpart.PrepareSummaryFlowPartFactory.PrepareSummaryFlowPart;
import com.asakusafw.tutorial.posdata_summarization.flowpart.SummarizeFlowPartFactory;
import com.asakusafw.tutorial.posdata_summarization.flowpart.SummarizeFlowPartFactory.SummarizeFlowPart;
import com.asakusafw.tutorial.posdata_summarization.gateway.CategoryCodeErrorToCsv;
import com.asakusafw.tutorial.posdata_summarization.gateway.CategoryFromCsv;
import com.asakusafw.tutorial.posdata_summarization.gateway.CheckpointFromCsv;
import com.asakusafw.tutorial.posdata_summarization.gateway.ConsumptionTaxErrorToCsv;
import com.asakusafw.tutorial.posdata_summarization.gateway.FormLayoutToCsv;
import com.asakusafw.tutorial.posdata_summarization.gateway.SummaryToCsv;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Category;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorConsumptionTax;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.FormLayout;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summary;
import com.asakusafw.tutorial.posdata_summarization.operator.FormLayoutOperatorFactory;
import com.asakusafw.tutorial.posdata_summarization.operator.FormLayoutOperatorFactory.MakeFormLayout;
import com.asakusafw.vocabulary.flow.Export;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.Import;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.JobFlow;
import com.asakusafw.vocabulary.flow.Out;

/**
 * POSデータ集計ジョブフロー
 */
@JobFlow(name = "SummarizeJobFlow")
public class SummarizeJobFlow extends FlowDescription {

    private final PrepareSummaryFlowPartFactory prepare = new PrepareSummaryFlowPartFactory();

    private final SummarizeFlowPartFactory summarize = new SummarizeFlowPartFactory();

    private final FlattenSummaryFlowPartFactory flatten = new FlattenSummaryFlowPartFactory();

    private final FormLayoutOperatorFactory formLayoutOp = new FormLayoutOperatorFactory();

    private final In<PosItem> checkpointIn;

    private final In<Category> categoryIn;

    private final Out<Summary> summaryOut;

    private final Out<FormLayout> formLayoutOut;

    private final Out<ErrorPosItem> categoryCodeErrorOut;

    private final Out<ErrorConsumptionTax> consumptionTaxErrorOut;

    /**
     * @param checkpointIn POSデータ入力
     * @param categoryIn カテゴリマスタ
     * @param summaryOut カテゴリデータ展開結果出力
     * @param formLayoutOut 伝票イメージ出力
     * @param categoryCodeErrorOut カテゴリコードエラー出力
     * @param consumptionTaxErrorOut 消費税計算エラー出力
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    public SummarizeJobFlow(
            @Import(name = "checkpoint", description = CheckpointFromCsv.class) In<PosItem> checkpointIn,
            @Import(name = "category", description = CategoryFromCsv.class) In<Category> categoryIn,
            @Export(name = "summary", description = SummaryToCsv.class) Out<Summary> summaryOut,
            @Export(name = "formLayout", description = FormLayoutToCsv.class) Out<FormLayout> formLayoutOut,
            @Export(name = "categoryCodeError", description = CategoryCodeErrorToCsv.class) Out<ErrorPosItem> categoryCodeErrorOut,
            @Export(name = "consumptionTaxError", description = ConsumptionTaxErrorToCsv.class) Out<ErrorConsumptionTax> consumptionTaxErrorOut) {
        this.checkpointIn = checkpointIn;
        this.categoryIn = categoryIn;
        this.summaryOut = summaryOut;
        this.formLayoutOut = formLayoutOut;
        this.categoryCodeErrorOut = categoryCodeErrorOut;
        this.consumptionTaxErrorOut = consumptionTaxErrorOut;
    }

    @Override
    protected void describe() {
        // 集計元データ作成
        PrepareSummaryFlowPart prepared = prepare.create(checkpointIn, categoryIn);
        categoryCodeErrorOut.add(prepared.categoryCodeError); // カテゴリコードエラー
        consumptionTaxErrorOut.add(prepared.consumptionTaxError); // 消費税計算エラー

        // 集計
        SummarizeFlowPart summarizeFlowPart = summarize.create(prepared.prepared);

        // カテゴリデータへの展開
        FlattenSummaryFlowPart flattenSummaryFlowPart = flatten.create(summarizeFlowPart.summarizedAllStore,
                summarizeFlowPart.summarizedAllStoreSection, summarizeFlowPart.summarizedAllStoreCategory1,
                summarizeFlowPart.summarizedAllStoreCategory2, summarizeFlowPart.summarizedAllStoreSku,
                summarizeFlowPart.summarizedEachStore, summarizeFlowPart.summarizedEachStoreSection,
                summarizeFlowPart.summarizedEachStoreCategory1, summarizeFlowPart.summarizedEachStoreCategory2,
                summarizeFlowPart.summarizedEachStoreSku);
        summaryOut.add(flattenSummaryFlowPart.summary);

        // 伝票イメージ作成
        MakeFormLayout formLayout = formLayoutOp.makeFormLayout(summarizeFlowPart.summarizedEachStoreSku);
        formLayoutOut.add(formLayout.out);
    }
}
