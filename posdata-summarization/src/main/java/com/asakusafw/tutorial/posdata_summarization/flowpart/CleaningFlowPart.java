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

import com.asakusafw.tutorial.posdata_summarization.common.BatchError;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Item;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Store;
import com.asakusafw.tutorial.posdata_summarization.operator.CleaningOperatorFactory;
import com.asakusafw.tutorial.posdata_summarization.operator.CleaningOperatorFactory.CheckItem;
import com.asakusafw.tutorial.posdata_summarization.operator.CleaningOperatorFactory.ExistsStore;
import com.asakusafw.tutorial.posdata_summarization.operator.ErrorOperatorFactory;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.FlowPart;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;
import com.asakusafw.vocabulary.flow.util.CoreOperatorFactory;

/**
 * POSデータのクリーニングを行うフローパート
 */
@FlowPart
public class CleaningFlowPart extends FlowDescription {

    private final CoreOperatorFactory coreOp = new CoreOperatorFactory();

    private final ErrorOperatorFactory errorOp = new ErrorOperatorFactory();

    private final CleaningOperatorFactory cleaningOp = new CleaningOperatorFactory();

    private final In<PosItem> posItemIn;

    private final In<Store> storeIn;

    private final In<Item> itemIn;

    private final Out<PosItem> cleaned;

    private final Out<ErrorPosItem> storeCodeError;

    private final Out<ErrorPosItem> itemCodeError;

    /**
     * @param posItemIn 入力チェック済のPOSデータ
     * @param storeIn 店舗マスタ
     * @param itemIn 商品マスタ
     * @param cleaned クリーニング済のPOSデータ出力
     * @param storeCodeError 店舗コードエラー出力
     * @param itemCodeError 商品コードエラー出力
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    public CleaningFlowPart(In<PosItem> posItemIn, In<Store> storeIn, In<Item> itemIn, Out<PosItem> cleaned,
            Out<ErrorPosItem> storeCodeError, Out<ErrorPosItem> itemCodeError) {
        this.posItemIn = posItemIn;
        this.storeIn = storeIn;
        this.itemIn = itemIn;
        this.cleaned = cleaned;
        this.storeCodeError = storeCodeError;
        this.itemCodeError = itemCodeError;
    }

    @Override
    protected void describe() {
        // StoreがJOINできなかった場合にはエラー出力
        ExistsStore existsStore = cleaningOp.existsStore(storeIn, posItemIn);
        storeCodeError.add(errorOp.setErrorCode(coreOp.extend(existsStore.missed, ErrorPosItem.class),
                BatchError.MISSING_STORE_ERROR.errorCode).out);

        // ItemがJOINできなかった場合にはエラー出力にも流しつつ、処理は続行
        CheckItem checkItem = cleaningOp.checkItem(itemIn, existsStore.found);
        cleaned.add(coreOp.confluent(checkItem.updated, checkItem.missed));
        itemCodeError.add(errorOp.setErrorCode(coreOp.extend(checkItem.missed, ErrorPosItem.class),
                BatchError.MISSING_ITEM_ERROR.errorCode).out);
    }
}
