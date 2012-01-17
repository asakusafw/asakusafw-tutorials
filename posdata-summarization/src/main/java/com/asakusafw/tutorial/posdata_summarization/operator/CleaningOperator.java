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

import java.util.List;

import com.asakusafw.runtime.core.BatchContext;
import com.asakusafw.runtime.value.Date;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Item;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Store;
import com.asakusafw.vocabulary.model.Key;
import com.asakusafw.vocabulary.operator.MasterCheck;
import com.asakusafw.vocabulary.operator.MasterJoinUpdate;
import com.asakusafw.vocabulary.operator.MasterSelection;

/**
 * POSデータのクリーニングを行うオペレータ
 * 
 * 店舗マスタ、商品マスタの存在チェックを行う
 */
public abstract class CleaningOperator {

    /**
     * 店舗マスタに有効なデータがあるかどうかを確認する。
     * 
     * {@code BatchContext}から処理対象年月日を取得して、マスタデータの適用期間と照会する。
     * 適応期間内であれば有効、期間外であれば無効となる。
     * 
     * @param stores 確認対象の店舗マスタのリスト
     * @param posItem 結合対象となるPOSデータ
     * @return 有効な店舗マスタ、もしくは有効な店舗マスタがなかった場合には{@code null}
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @MasterSelection
    public Store selectStore(List<Store> stores, PosItem posItem) {
        // 集計対象日付をBatchContextから取得
        Date targetDate = Date.valueOf(BatchContext.get("TARGET_DATE"), Date.Format.SIMPLE);
        for (Store store : stores) {
            if (targetDate.compareTo(store.getApplyStartDate()) >= 0
                    && targetDate.compareTo(store.getApplyEndDate()) < 0) {
                return store;
            }
        }
        return null;
    }

    /**
     * POSデータに対応する店舗マスタの存在チェックを行う。
     * 店舗マスタは{@code selectStore}メソッドで選択されたマスタを利用する。
     * 
     * @param store {@code selectStore}メソッドで選択された店舗マスタ
     * @param posItem チェック対象のPOSデータ
     * @return 有効な店舗マスタが存在すれば{@code true}、存在しなければ{@code false}
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @MasterCheck(selection = "selectStore")
    public abstract boolean existsStore(@Key(group = "storeCode") Store store, @Key(group = "storeCode") PosItem posItem);

    /**
     * 商品マスタに有効なデータがあるかどうかを確認する。
     * 
     * {@code BatchContext}から処理対象年月日を取得して、マスタデータの適用期間と照会する。
     * 適応期間内であれば有効、期間外であれば無効となる。
     * 
     * @param items 確認対象の商品マスタのリスト
     * @param posItem 結合対象となるPOSデータ
     * @return 有効な商品マスタ、もしくは有効な商品マスタがなかった場合には{@code null}
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @MasterSelection
    public Item selectItem(List<Item> items, PosItem posItem) {
        // 集計対象日付をBatchContextから取得
        Date targetDate = Date.valueOf(BatchContext.get("TARGET_DATE"), Date.Format.SIMPLE);
        for (Item item : items) {
            if (targetDate.compareTo(item.getApplyStartDate()) >= 0 && targetDate.compareTo(item.getApplyEndDate()) < 0) {
                return item;
            }
        }
        return null;
    }

    /**
     * POSデータに対応する商品マスタの存在チェックを行う。
     * 商品マスタは{@code selectItem}メソッドで選択されたマスタを利用する。
     * 
     * 商品マスタが存在した場合には、POSデータの商品名をマスタの商品名で上書きする。
     * 
     * @param item {@code selectItem}メソッドで選択された商品マスタ
     * @param posItem チェック対象のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @MasterJoinUpdate(selection = "selectItem")
    public void checkItem(@Key(group = { "storeCode", "gtin" }) Item item,
            @Key(group = { "storeCode", "janCode" }) PosItem posItem) {
        posItem.setItemName(item.getItemName());
    }

}
