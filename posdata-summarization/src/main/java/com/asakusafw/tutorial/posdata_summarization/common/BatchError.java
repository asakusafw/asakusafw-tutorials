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
package com.asakusafw.tutorial.posdata_summarization.common;

/**
 * バッチエラーコード
 */
public enum BatchError {

    /**
     * POSインポートエラー
     */
    POS_IMPORT_ERROR(1),

    /**
     * 店舗コードエラー
     */
    MISSING_STORE_ERROR(2),

    /**
     * 商品コードエラー
     */
    MISSING_ITEM_ERROR(3),

    /**
     * カテゴリコードエラー
     */
    MISSING_CATEGORY_ERROR(4),

    /**
     * 消費税計算エラー
     */
    SALES_TAX_ERROR(5);

    /**
     * エラーコード
     */
    public final int errorCode;

    /**
     * @param errorCode エラーコード
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    private BatchError(int errorCode) {
        this.errorCode = errorCode;
    }
}
