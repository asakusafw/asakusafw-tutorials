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

import com.asakusafw.tutorial.posdata_summarization.flowpart.CleaningFlowPartFactory;
import com.asakusafw.tutorial.posdata_summarization.flowpart.CleaningFlowPartFactory.CleaningFlowPart;
import com.asakusafw.tutorial.posdata_summarization.flowpart.PosImportFlowPartFactory;
import com.asakusafw.tutorial.posdata_summarization.flowpart.PosImportFlowPartFactory.PosImportFlowPart;
import com.asakusafw.tutorial.posdata_summarization.gateway.CheckpointToCsv;
import com.asakusafw.tutorial.posdata_summarization.gateway.ItemFromCsv;
import com.asakusafw.tutorial.posdata_summarization.gateway.PosItemErrorToCsv;
import com.asakusafw.tutorial.posdata_summarization.gateway.PosItemFromCsv;
import com.asakusafw.tutorial.posdata_summarization.gateway.StoreFromCsv;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Item;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Store;
import com.asakusafw.vocabulary.flow.Export;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.Import;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.JobFlow;
import com.asakusafw.vocabulary.flow.Out;

/**
 * POSデータクリーニングジョブフロー
 */
@JobFlow(name = "CleaningJobFlow")
public class CleaningJobFlow extends FlowDescription {

    private final PosImportFlowPartFactory posImport = new PosImportFlowPartFactory();

    private final CleaningFlowPartFactory cleaning = new CleaningFlowPartFactory();

    private final In<PosItem> posItemIn;

    private final In<Store> storeIn;

    private final In<Item> itemIn;

    private final Out<PosItem> checkpointOut;

    private final Out<ErrorPosItem> posItemErrorOut;

    /**
     * @param posItemIn POSデータ入力
     * @param storeIn 店舗マスタ
     * @param itemIn 商品マスタ
     * @param checkpointOut クリーニング後データチェックポイント出力
     * @param posItemErrorOut POSデータエラー出力
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    public CleaningJobFlow(@Import(name = "posItem", description = PosItemFromCsv.class) In<PosItem> posItemIn,
            @Import(name = "store", description = StoreFromCsv.class) In<Store> storeIn,
            @Import(name = "item", description = ItemFromCsv.class) In<Item> itemIn,
            @Export(name = "checkpoint", description = CheckpointToCsv.class) Out<PosItem> checkpointOut,
            @Export(name = "posItemError", description = PosItemErrorToCsv.class) Out<ErrorPosItem> posItemErrorOut) {
        this.posItemIn = posItemIn;
        this.storeIn = storeIn;
        this.itemIn = itemIn;
        this.checkpointOut = checkpointOut;
        this.posItemErrorOut = posItemErrorOut;
    }

    /* (non-Javadoc)
     * @see com.asakusafw.vocabulary.flow.FlowDescription#describe()
     */
    @Override
    protected void describe() {
        // POSデータ取込、入力チェック
        PosImportFlowPart imported = posImport.create(posItemIn);
        posItemErrorOut.add(imported.invalid); // 入力チェックエラー

        // データクリーニング
        CleaningFlowPart cleaned = cleaning.create(imported.valid, storeIn, itemIn);
        posItemErrorOut.add(cleaned.storeCodeError); // 店舗コードエラー
        posItemErrorOut.add(cleaned.itemCodeError); // 商品コードエラー

        checkpointOut.add(cleaned.cleaned);
    }

}
