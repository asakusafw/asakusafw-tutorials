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
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Category;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorConsumptionTax;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.JoinCategory;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.JoinedPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PreparedPosItem;
import com.asakusafw.tutorial.posdata_summarization.operator.CategoryJoinOperatorFactory;
import com.asakusafw.tutorial.posdata_summarization.operator.CategoryJoinOperatorFactory.DoJoinCategory;
import com.asakusafw.tutorial.posdata_summarization.operator.CategoryJoinOperatorFactory.SetDefaultCategory;
import com.asakusafw.tutorial.posdata_summarization.operator.ConsumptionTaxOperatorFactory;
import com.asakusafw.tutorial.posdata_summarization.operator.ConsumptionTaxOperatorFactory.AdjustConsumptionTax;
import com.asakusafw.tutorial.posdata_summarization.operator.ErrorOperatorFactory;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.FlowPart;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;
import com.asakusafw.vocabulary.flow.util.CoreOperatorFactory;

/**
 * 集計元データ作成フローパート
 */
@FlowPart
public class PrepareSummaryFlowPart extends FlowDescription {

    private final CoreOperatorFactory coreOp = new CoreOperatorFactory();

    private final ErrorOperatorFactory errorOp = new ErrorOperatorFactory();

    private final CategoryJoinOperatorFactory joinOp = new CategoryJoinOperatorFactory();

    private final ConsumptionTaxOperatorFactory consumptionTaxOp = new ConsumptionTaxOperatorFactory();

    private final In<PosItem> posItemIn;

    private final In<Category> categoryIn;

    private final Out<PreparedPosItem> prepared;

    private final Out<ErrorPosItem> categoryCodeError;

    private final Out<ErrorConsumptionTax> consumptionTaxError;

    /**
     * @param posItemIn クリーニング済のPOSデータ
     * @param categoryIn カテゴリマスタ
     * @param prepared 集計元POSデータ出力
     * @param categoryCodeError カテゴリコードエラー出力
     * @param consumptionTaxError 消費税計算エラー出力
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    public PrepareSummaryFlowPart(In<PosItem> posItemIn, In<Category> categoryIn, Out<PreparedPosItem> prepared,
            Out<ErrorPosItem> categoryCodeError, Out<ErrorConsumptionTax> consumptionTaxError) {
        this.posItemIn = posItemIn;
        this.categoryIn = categoryIn;
        this.prepared = prepared;
        this.categoryCodeError = categoryCodeError;
        this.consumptionTaxError = consumptionTaxError;
    }

    @Override
    protected void describe() {
        // カテゴリがJOINできなければエラーに出力しつつ「その他」カテゴリとして引き続き処理する
        DoJoinCategory doJoinCategory = joinOp.doJoinCategory(categoryIn, posItemIn);
        categoryCodeError.add(errorOp.setErrorCode(coreOp.extend(doJoinCategory.missed, ErrorPosItem.class),
                BatchError.MISSING_CATEGORY_ERROR.errorCode).out);

        // 「その他」カテゴリーをセット
        SetDefaultCategory setDefaultCategory = joinOp.setDefaultCategory(
                coreOp.extend(doJoinCategory.missed, JoinCategory.class), Integer.MAX_VALUE, Integer.MAX_VALUE,
                Integer.MAX_VALUE); // TODO 各デフォルト値は仮

        // 消費税を計算
        AdjustConsumptionTax adjusted = consumptionTaxOp.adjustConsumptionTax(coreOp.extend(
                coreOp.confluent(doJoinCategory.joined, setDefaultCategory.out), JoinedPosItem.class));
        prepared.add(adjusted.adjusted);
        consumptionTaxError.add(adjusted.error);
    }
}
