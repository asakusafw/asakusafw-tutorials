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

import com.asakusafw.runtime.core.BatchContext;
import com.asakusafw.runtime.value.Date;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.vocabulary.operator.Branch;

/**
 * POSデータの取込チェックを行うオペレータ
 */
public abstract class PosCheckOperator {

    /**
     * POSデータの取り込みチェックを行う。
     *
     * @param posItem チェック対象となるレシートデータ
     * @return チェック結果 (VALID or INVALID)
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Branch
    public PosStatus check(PosItem posItem) {
        if (posItem.getStoreCodeOption().isNull() || posItem.getStoreCode() <= 0 || posItem.getStoreCode() >= 9999) {
            return PosStatus.INVALID;
        }
        // 集計対象日付をBatchContextから取得
        Date targetDate = Date.valueOf(BatchContext.get("TARGET_DATE"), Date.Format.SIMPLE);
        if (posItem.getIssueDateOption().isNull() || !posItem.getIssueDate().equals(targetDate)) {
            return PosStatus.INVALID;
        }
        if (posItem.getIdOption().isNull() || posItem.getId() <= 0) {
            return PosStatus.INVALID;
        }
        if (posItem.getApprovalIdOption().isNull()) {
            return PosStatus.INVALID;
        }

        if (posItem.getReceiptIdOption().isNull() || posItem.getReceiptId() <= 0) {
            return PosStatus.INVALID;
        }
        if (posItem.getLineNumberOption().isNull() || posItem.getLineNumber() <= 0) {
            return PosStatus.INVALID;
        }
        if (posItem.getJanCodeOption().isNull() || Long.toString(posItem.getJanCode()).length() != 14) {
            return PosStatus.INVALID;
        }
        if (posItem.getItemNameOption().isNull()) {
            return PosStatus.INVALID;
        }
        if (posItem.getPriceOption().isNull() || posItem.getPrice() < 0) {
            return PosStatus.INVALID;
        }
        if (posItem.getQuantityOption().isNull() || posItem.getQuantity() < 0) {
            return PosStatus.INVALID;
        }
        if (posItem.getAmountOption().isNull() || posItem.getAmount() < 0) {
            return PosStatus.INVALID;
        }

        if (posItem.getTotalAmountOption().isNull() || posItem.getTotalAmount() < 0) {
            return PosStatus.INVALID;
        }
        if (posItem.getTotalConsumptionTaxOption().isNull() || posItem.getTotalConsumptionTax() < 0) {
            return PosStatus.INVALID;
        }
        if (posItem.getTotalReceivedAmountOption().isNull() || posItem.getTotalReceivedAmount() < 0) {
            return PosStatus.INVALID;
        }

        return PosStatus.VALID;
    }

    /**
     * 取り込みチェック結果に応じた分岐先
     */
    public static enum PosStatus {

        /**
         * 正常  
         */
        VALID,

        /**
          * 異常
          */
        INVALID
    }

}
