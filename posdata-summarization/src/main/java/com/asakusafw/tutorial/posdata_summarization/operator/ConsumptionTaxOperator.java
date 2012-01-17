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
package com.asakusafw.tutorial.posdata_summarization.operator;

import java.util.Iterator;
import java.util.List;

import com.asakusafw.runtime.core.Result;
import com.asakusafw.tutorial.posdata_summarization.common.BatchError;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorConsumptionTax;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.JoinedPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PreparedPosItem;
import com.asakusafw.vocabulary.model.Key;
import com.asakusafw.vocabulary.operator.CoGroup;

/**
 * 消費税の計算を行うオペレータ
 */
public abstract class ConsumptionTaxOperator {

    /**
     * 店舗コード、日付、ID、レシートIDでグループされたPOSデータから、単品毎の消費税を計算。
     * 
     * @param posItems 店舗、日付、ID、レシートIDでグループされたPOSデータのリスト
     * @param adjusted 消費税計算後のPOSデータ結果
     * @param error 消費税計算時のエラーデータ結果
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @CoGroup
    public void adjustConsumptionTax(
            @Key(group = { "storeCode", "issueDate", "id", "receiptId" }, order = "amount") List<JoinedPosItem> posItems,
            Result<PreparedPosItem> adjusted, Result<ErrorConsumptionTax> error) {
        int totalConsumptionTax = 0;
        int items = 0;
        Iterator<JoinedPosItem> itr = posItems.iterator();
        while (itr.hasNext()) {
            JoinedPosItem posItem = itr.next();
            items++;

            PreparedPosItem prepared = new PreparedPosItem();
            prepared.setStoreCode(posItem.getStoreCode());
            prepared.setIssueDate(posItem.getIssueDate());

            prepared.setReceiptId(posItem.getReceiptId());
            prepared.setJanCode(posItem.getJanCode());
            prepared.setItemNameAsString(posItem.getItemNameAsString());

            prepared.setSectionCode(posItem.getSectionCode());

            prepared.setCategoryCode1(posItem.getCategoryCode1());
            prepared.setCategoryCode2(posItem.getCategoryCode2());

            // 消費税計算
            int unitConsumptionTax = (int) (posItem.getPrice() - posItem.getPrice() / 1.05); // TODO remove hard coding
            int consumptionTax = unitConsumptionTax * posItem.getQuantity();

            prepared.setPrice(posItem.getPrice() - unitConsumptionTax);
            prepared.setQuantity(posItem.getQuantity());
            prepared.setAmount(posItem.getAmount() - consumptionTax);

            prepared.setConsumptionTax(consumptionTax);

            totalConsumptionTax += consumptionTax;

            if (!itr.hasNext()) {
                int diff = posItem.getTotalConsumptionTax() - totalConsumptionTax;

                // 消費税の誤差は一番価格の大きいもの(= 最後のもの)につける
                prepared.setConsumptionTax(prepared.getConsumptionTax() + diff);

                // 誤差が大きい場合には警告として出力しておく
                if (Math.abs(diff) > items) { // ヒットした件数x1円
                    ErrorConsumptionTax errorConsumptionTax = new ErrorConsumptionTax();
                    errorConsumptionTax.setErrorCode(BatchError.SALES_TAX_ERROR.errorCode);

                    errorConsumptionTax.setAdjustedConsumptionTax(totalConsumptionTax);
                    errorConsumptionTax.setReceiptConsumptionTax(posItem.getTotalConsumptionTax());

                    errorConsumptionTax.setStoreCode(posItem.getStoreCode());
                    errorConsumptionTax.setIssueDate(posItem.getIssueDate());
                    errorConsumptionTax.setReceiptId(posItem.getReceiptId());

                    errorConsumptionTax.setTotalAmount(posItem.getTotalAmount());
                    errorConsumptionTax.setTotalReceivedAmount(posItem.getTotalReceivedAmount());
                    errorConsumptionTax.setDescriptionAsString("ERROR"); // TODO エラーメッセージ

                    error.add(errorConsumptionTax);
                }
            }

            adjusted.add(prepared);
        }
    }
}
