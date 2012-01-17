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

import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summarized;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summary;
import com.asakusafw.tutorial.posdata_summarization.operator.FlattenSummaryOperatorFactory;
import com.asakusafw.tutorial.posdata_summarization.operator.FlattenSummaryOperatorFactory.Convert;
import com.asakusafw.tutorial.posdata_summarization.operator.FlattenSummaryOperatorFactory.SetAllStoreSkuTotal;
import com.asakusafw.tutorial.posdata_summarization.operator.FlattenSummaryOperatorFactory.SetCategory1Total;
import com.asakusafw.tutorial.posdata_summarization.operator.FlattenSummaryOperatorFactory.SetCategory2Total;
import com.asakusafw.tutorial.posdata_summarization.operator.FlattenSummaryOperatorFactory.SetSectionTotal;
import com.asakusafw.tutorial.posdata_summarization.operator.FlattenSummaryOperatorFactory.SetTotal;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.FlowPart;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;
import com.asakusafw.vocabulary.flow.util.CoreOperatorFactory;

/**
 * カテゴリデータへの展開フローパート
 */
@FlowPart
public class FlattenSummaryFlowPart extends FlowDescription {

    private final CoreOperatorFactory coreOp = new CoreOperatorFactory();

    private final FlattenSummaryOperatorFactory flattenOp = new FlattenSummaryOperatorFactory();

    private final In<Summarized> summarizedAllStoreIn;

    private final In<Summarized> summarizedAllStoreSectionIn;

    private final In<Summarized> summarizedAllStoreCategory1In;

    private final In<Summarized> summarizedAllStoreCategory2In;

    private final In<Summarized> summarizedAllStoreSkuIn;

    private final In<Summarized> summarizedEachStoreIn;

    private final In<Summarized> summarizedEachStoreSectionIn;

    private final In<Summarized> summarizedEachStoreCategory1In;

    private final In<Summarized> summarizedEachStoreCategory2In;

    private final In<Summarized> summarizedEachStoreSkuIn;

    private final Out<Summary> summary;

    /**
     * @param summarizedAllStoreIn 全店全商品集計結果
     * @param summarizedAllStoreSectionIn 全店部門別集計結果
     * @param summarizedAllStoreCategory1In 全店カテゴリ1別集計結果
     * @param summarizedAllStoreCategory2In 全店カテゴリ2別集計結果
     * @param summarizedAllStoreSkuIn 全店商品別集計結果
     * @param summarizedEachStoreIn 店舗別全商品集計結果
     * @param summarizedEachStoreSectionIn 店舗別部門別集計結果
     * @param summarizedEachStoreCategory1In 店舗別カテゴリ1別集計結果
     * @param summarizedEachStoreCategory2In 店舗別カテゴリ2別集計結果
     * @param summarizedEachStoreSkuIn 店舗別商品別集計結果
     * @param summary カテゴリデータ展開結果出力
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    public FlattenSummaryFlowPart(In<Summarized> summarizedAllStoreIn, In<Summarized> summarizedAllStoreSectionIn,
            In<Summarized> summarizedAllStoreCategory1In, In<Summarized> summarizedAllStoreCategory2In,
            In<Summarized> summarizedAllStoreSkuIn, In<Summarized> summarizedEachStoreIn,
            In<Summarized> summarizedEachStoreSectionIn, In<Summarized> summarizedEachStoreCategory1In,
            In<Summarized> summarizedEachStoreCategory2In, In<Summarized> summarizedEachStoreSkuIn, Out<Summary> summary) {
        this.summarizedAllStoreIn = summarizedAllStoreIn;
        this.summarizedAllStoreSectionIn = summarizedAllStoreSectionIn;
        this.summarizedAllStoreCategory1In = summarizedAllStoreCategory1In;
        this.summarizedAllStoreCategory2In = summarizedAllStoreCategory2In;
        this.summarizedAllStoreSkuIn = summarizedAllStoreSkuIn;
        this.summarizedEachStoreIn = summarizedEachStoreIn;
        this.summarizedEachStoreSectionIn = summarizedEachStoreSectionIn;
        this.summarizedEachStoreCategory1In = summarizedEachStoreCategory1In;
        this.summarizedEachStoreCategory2In = summarizedEachStoreCategory2In;
        this.summarizedEachStoreSkuIn = summarizedEachStoreSkuIn;
        this.summary = summary;
    }

    @Override
    protected void describe() {
        Convert converted = flattenOp.convert(summarizedEachStoreSkuIn);
        coreOp.stop(converted.original);

        SetAllStoreSkuTotal setAllStoreSkuTotal = flattenOp.setAllStoreSkuTotal(summarizedAllStoreSkuIn, converted.out);
        coreOp.stop(setAllStoreSkuTotal.missed);

        SetTotal setTotal = flattenOp
                .setTotal(summarizedAllStoreIn, summarizedEachStoreIn, setAllStoreSkuTotal.updated);
        SetSectionTotal setSectionTotal = flattenOp.setSectionTotal(summarizedAllStoreSectionIn,
                summarizedEachStoreSectionIn, setTotal.out);
        SetCategory1Total setCategory1Total = flattenOp.setCategory1Total(summarizedAllStoreCategory1In,
                summarizedEachStoreCategory1In, setSectionTotal.out);
        SetCategory2Total setCategory2Total = flattenOp.setCategory2Total(summarizedAllStoreCategory2In,
                summarizedEachStoreCategory2In, setCategory1Total.out);
        summary.add(setCategory2Total.out);
    }

}
