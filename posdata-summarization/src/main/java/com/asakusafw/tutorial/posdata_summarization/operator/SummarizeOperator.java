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

import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summarized;
import com.asakusafw.vocabulary.model.Key;
import com.asakusafw.vocabulary.operator.Fold;
import com.asakusafw.vocabulary.operator.Update;

/**
 * 全店舗、もしくは店舗別の各階層での集計を行うオペレータ
 */
public abstract class SummarizeOperator {

    /**
     * 集計元のPOSデータに客数の初期値1をセットする。
     * 
     * 集計対象が1件の場合には@Foldオペレータが呼ばれず、客数が0扱いとなってしまうため、この時点で客数は1としておく。
     * 
     * @param summarized 集計元のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Update
    public void setInitialCustomers(Summarized summarized) {
        summarized.setCustomers(1);
    }

    /**
     * 日別全店の集計を行う。
     * 客数計算のために{@code storeCode}、{@code receiptId}で並べ替えしておく。
     * 
     * 実際の処理は{@link #summarize(Summarized, Summarized)}メソッドで。
     * 
     * @param sum 集計値の積み上げ
     * @param each 個々のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Fold
    public void summarizeAllStore(@Key(group = { "issueDate" }, order = { "storeCode", "receiptId" }) Summarized sum,
            Summarized each) {
        summarize(sum, each);
    }

    /**
     * 日別全店の部門別集計を行う。
     * 客数計算のために{@code storeCode}、{@code receiptId}で並べ替えしておく。
     * 
     * 実際の処理は{@link #summarize(Summarized, Summarized)}メソッドで。
     * 
     * @param sum 集計値の積み上げ
     * @param each 個々のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Fold
    public void summarizeAllStoreSection(@Key(group = { "issueDate", "sectionCode" }, order = { "storeCode",
            "receiptId" }) Summarized sum, Summarized each) {
        summarize(sum, each);
    }

    /**
     * 日別全店のカテゴリ1別集計を行う。
     * 客数計算のために{@code storeCode}、{@code receiptId}で並べ替えしておく。
     * 
     * 実際の処理は{@link #summarize(Summarized, Summarized)}メソッドで。
     * 
     * @param sum 集計値の積み上げ
     * @param each 個々のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Fold
    public void summarizeAllStoreCategory1(@Key(group = { "issueDate", "categoryCode1" }, order = { "storeCode",
            "receiptId" }) Summarized sum, Summarized each) {
        summarize(sum, each);
    }

    /**
     * 日別全店のカテゴリ2別集計を行う。
     * 客数計算のために{@code storeCode}、{@code receiptId}で並べ替えしておく。
     * 
     * 実際の処理は{@link #summarize(Summarized, Summarized)}メソッドで。
     * 
     * @param sum 集計値の積み上げ
     * @param each 個々のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Fold
    public void summarizeAllStoreCategory2(@Key(group = { "issueDate", "categoryCode2" }, order = { "storeCode",
            "receiptId" }) Summarized sum, Summarized each) {
        summarize(sum, each);
    }

    /**
     * 日別全店の商品別集計を行う。
     * 客数計算のために{@code storeCode}、{@code receiptId}で並べ替えしておく。
     * 
     * 実際の処理は{@link #summarize(Summarized, Summarized)}メソッドで。
     * 
     * @param sum 集計値の積み上げ
     * @param each 個々のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Fold
    public void summarizeAllStoreSku(
            @Key(group = { "issueDate", "janCode" }, order = { "storeCode", "receiptId" }) Summarized sum,
            Summarized each) {
        summarize(sum, each);
    }

    /**
     * 日別店舗別の集計を行う。
     * 客数計算のために{@code receiptId}で並べ替えしておく。
     * 
     * 実際の処理は{@link #summarize(Summarized, Summarized)}メソッドで。
     * 
     * @param sum 集計値の積み上げ
     * @param each 個々のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Fold
    public void summarizeEachStore(@Key(group = { "issueDate", "storeCode" }, order = "receiptId") Summarized sum,
            Summarized each) {
        summarize(sum, each);
    }

    /**
     * 日別店舗別の部門別集計を行う。
     * 客数計算のために{@code receiptId}で並べ替えしておく。
     * 
     * 実際の処理は{@link #summarize(Summarized, Summarized)}メソッドで。
     * 
     * @param sum 集計値の積み上げ
     * @param each 個々のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Fold
    public void summarizeEachStoreSection(
            @Key(group = { "issueDate", "storeCode", "sectionCode" }, order = "receiptId") Summarized sum,
            Summarized each) {
        summarize(sum, each);
    }

    /**
     * 日別店舗別のカテゴリ1別集計を行う。
     * 客数計算のために{@code receiptId}で並べ替えしておく。
     * 
     * 実際の処理は{@link #summarize(Summarized, Summarized)}メソッドで。
     * 
     * @param sum 集計値の積み上げ
     * @param each 個々のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Fold
    public void summarizeEachStoreCategory1(
            @Key(group = { "issueDate", "storeCode", "categoryCode1" }, order = "receiptId") Summarized sum,
            Summarized each) {
        summarize(sum, each);
    }

    /**
     * 日別店舗別のカテゴリ2別集計を行う。
     * 客数計算のために{@code receiptId}で並べ替えしておく。
     * 
     * 実際の処理は{@link #summarize(Summarized, Summarized)}メソッドで。
     * 
     * @param sum 集計値の積み上げ
     * @param each 個々のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Fold
    public void summarizeEachStoreCategory2(
            @Key(group = { "issueDate", "storeCode", "categoryCode2" }, order = "receiptId") Summarized sum,
            Summarized each) {
        summarize(sum, each);
    }

    /**
     * 日別店舗別の商品別集計を行う。
     * 客数計算のために{@code receiptId}で並べ替えしておく。
     * 
     * 実際の処理は{@link #summarize(Summarized, Summarized)}メソッドで。
     * 
     * @param sum 集計値の積み上げ
     * @param each 個々のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Fold
    public void summarizeEachStoreSku(
            @Key(group = { "issueDate", "storeCode", "janCode" }, order = "receiptId") Summarized sum, Summarized each) {
        summarize(sum, each);
    }

    /**
     * 売上金額、売上数量、消費税の足し上げ集計を行う。
     * また、客数（＝日別店舗別のレシートIDのユニークカウントとする）の集計を行う。
     * 
     * @param sum 集計値の積み上げ
     * @param each 個々のPOSデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    private void summarize(Summarized sum, Summarized each) {
        sum.setAmount(sum.getAmount() + each.getAmount());
        sum.setQuantity(sum.getQuantity() + each.getQuantity());
        sum.setConsumptionTax(sum.getConsumptionTax() + each.getConsumptionTax());

        if (sum.getStoreCode() != each.getStoreCode() || sum.getReceiptId() != each.getReceiptId()) {
            // 店舗コード、レシートIDが切り替わったら客数をカウントアップ
            sum.setCustomers(sum.getCustomers() + 1);
            sum.setStoreCode(each.getStoreCode());
            sum.setReceiptId(each.getReceiptId());
        }
    }

}
