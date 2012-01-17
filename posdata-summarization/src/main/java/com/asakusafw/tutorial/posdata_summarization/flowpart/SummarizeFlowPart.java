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

import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PreparedPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summarized;
import com.asakusafw.tutorial.posdata_summarization.operator.SummarizeOperatorFactory;
import com.asakusafw.tutorial.posdata_summarization.operator.SummarizeOperatorFactory.SetInitialCustomers;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.FlowPart;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;
import com.asakusafw.vocabulary.flow.util.CoreOperatorFactory;
import com.asakusafw.vocabulary.flow.util.CoreOperatorFactory.Extend;

/**
 * 集計フローパート
 */
@FlowPart
public class SummarizeFlowPart extends FlowDescription {

    private final CoreOperatorFactory coreOp = new CoreOperatorFactory();

    private final SummarizeOperatorFactory summarizeOp = new SummarizeOperatorFactory();

    private final In<PreparedPosItem> preparedPosItemIn;

    private final Out<Summarized> summarizedAllStore;

    private final Out<Summarized> summarizedAllStoreSection;

    private final Out<Summarized> summarizedAllStoreCategory1;

    private final Out<Summarized> summarizedAllStoreCategory2;

    private final Out<Summarized> summarizedAllStoreSku;

    private final Out<Summarized> summarizedEachStore;

    private final Out<Summarized> summarizedEachStoreSection;

    private final Out<Summarized> summarizedEachStoreCategory1;

    private final Out<Summarized> summarizedEachStoreCategory2;

    private final Out<Summarized> summarizedEachStoreSku;

    /**
     * @param preparedPosItemIn 集計元POSデータ
     * @param summarizedAllStore 全店全商品集計結果出力
     * @param summarizedAllStoreSection 全店部門別集計結果出力
     * @param summarizedAllStoreCategory1 全店カテゴリ1別集計結果出力
     * @param summarizedAllStoreCategory2 全店カテゴリ2別集計結果出力
     * @param summarizedAllStoreSku 全店商品別集計結果出力
     * @param summarizedEachStore 店舗別全商品集計結果出力
     * @param summarizedEachStoreSection 店舗別部門別集計結果出力
     * @param summarizedEachStoreCategory1 店舗別カテゴリ1別集計結果出力
     * @param summarizedEachStoreCategory2 店舗別カテゴリ2別集計結果出力
     * @param summarizedEachStoreSku 店舗別商品別集計結果出力
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    public SummarizeFlowPart(In<PreparedPosItem> preparedPosItemIn, Out<Summarized> summarizedAllStore,
            Out<Summarized> summarizedAllStoreSection, Out<Summarized> summarizedAllStoreCategory1,
            Out<Summarized> summarizedAllStoreCategory2, Out<Summarized> summarizedAllStoreSku,
            Out<Summarized> summarizedEachStore, Out<Summarized> summarizedEachStoreSection,
            Out<Summarized> summarizedEachStoreCategory1, Out<Summarized> summarizedEachStoreCategory2,
            Out<Summarized> summarizedEachStoreSku) {
        this.preparedPosItemIn = preparedPosItemIn;
        this.summarizedAllStore = summarizedAllStore;
        this.summarizedAllStoreSection = summarizedAllStoreSection;
        this.summarizedAllStoreCategory1 = summarizedAllStoreCategory1;
        this.summarizedAllStoreCategory2 = summarizedAllStoreCategory2;
        this.summarizedAllStoreSku = summarizedAllStoreSku;
        this.summarizedEachStore = summarizedEachStore;
        this.summarizedEachStoreSection = summarizedEachStoreSection;
        this.summarizedEachStoreCategory1 = summarizedEachStoreCategory1;
        this.summarizedEachStoreCategory2 = summarizedEachStoreCategory2;
        this.summarizedEachStoreSku = summarizedEachStoreSku;
    }

    @Override
    protected void describe() {
        Extend<Summarized> extend = coreOp.extend(preparedPosItemIn, Summarized.class);
        SetInitialCustomers setInitialCustomers = summarizeOp.setInitialCustomers(extend);

        // 全店集計
        summarizedAllStore.add(summarizeOp.summarizeAllStore(setInitialCustomers.out).out);
        summarizedAllStoreSection.add(summarizeOp.summarizeAllStoreSection(setInitialCustomers.out).out);
        summarizedAllStoreCategory1.add(summarizeOp.summarizeAllStoreCategory1(setInitialCustomers.out).out);
        summarizedAllStoreCategory2.add(summarizeOp.summarizeAllStoreCategory2(setInitialCustomers.out).out);
        summarizedAllStoreSku.add(summarizeOp.summarizeAllStoreSku(setInitialCustomers.out).out);

        // 店舗集計
        summarizedEachStore.add(summarizeOp.summarizeEachStore(setInitialCustomers.out).out);
        summarizedEachStoreSection.add(summarizeOp.summarizeEachStoreSection(setInitialCustomers.out).out);
        summarizedEachStoreCategory1.add(summarizeOp.summarizeEachStoreCategory1(setInitialCustomers.out).out);
        summarizedEachStoreCategory2.add(summarizeOp.summarizeEachStoreCategory2(setInitialCustomers.out).out);
        summarizedEachStoreSku.add(summarizeOp.summarizeEachStoreSku(setInitialCustomers.out).out);
    }

}
