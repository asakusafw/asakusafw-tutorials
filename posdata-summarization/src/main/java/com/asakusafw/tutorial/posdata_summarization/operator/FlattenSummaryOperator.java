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
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summarized;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summary;
import com.asakusafw.vocabulary.model.Key;
import com.asakusafw.vocabulary.operator.CoGroup;
import com.asakusafw.vocabulary.operator.Convert;
import com.asakusafw.vocabulary.operator.MasterJoinUpdate;

/**
 * 日別店舗別商品別集計のデータに、店舗別集計、上位カテゴリ集計をフラットにアタッチするオペレータ
 */
public abstract class FlattenSummaryOperator {

    /**
     * 店舗別商品別の集計データをフラットなモデルに変換する。
     * 
     * @param summarized 店舗別商品別集計データ
     * @return 集計値をフラットに持つためのデータ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Convert
    public Summary convert(Summarized summarized) {
        Summary summary = new Summary();

        summary.setStoreCode(summarized.getStoreCode());
        summary.setIssueDate(summarized.getIssueDate());
        summary.setJanCode(summarized.getJanCode());
        summary.setItemName(summarized.getItemName());

        summary.setSectionCode(summarized.getSectionCode());
        summary.setCategoryCode1(summarized.getCategoryCode1());
        summary.setCategoryCode2(summarized.getCategoryCode2());

        summary.setEachStoreSkuTotalAmount(summarized.getAmount());
        summary.setEachStoreSkuTotalQuantity(summarized.getQuantity());
        summary.setEachStoreSkuTotalConsumptionTax(summarized.getConsumptionTax());
        summary.setEachStoreSkuTotalCustomers(summarized.getCustomers());

        return summary;
    }

    /**
     * 全店の商品別集計データをアタッチする。
     * 
     * @param summarized 集計値をフラットに持つデータ
     * @param summary 全店の商品別集計データ
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @MasterJoinUpdate
    public void setAllStoreSkuTotal(@Key(group = { "issueDate", "janCode" }) Summarized summarized, @Key(group = {
            "issueDate", "janCode" }) Summary summary) {
        summary.setAllStoreSkuTotalAmount(summarized.getAmount());
        summary.setAllStoreSkuTotalQuantity(summarized.getQuantity());
        summary.setAllStoreSkuTotalConsumptionTax(summarized.getConsumptionTax());
        summary.setAllStoreSkuTotalCustomers(summarized.getCustomers());
    }

    /**
     * 全店、店舗別の集計データをアタッチする。
     * 
     * @param allStoreSummarizeds 全店の集計データ
     * @param eachStoreSummarizeds 店舗別の集計データ
     * @param summaries フラットデータ
     * @param out アタッチ結果
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @CoGroup
    public void setTotal(@Key(group = { "issueDate" }) List<Summarized> allStoreSummarizeds,
            @Key(group = { "issueDate" }, order = "storeCode") List<Summarized> eachStoreSummarizeds,
            @Key(group = { "issueDate" }, order = "storeCode") List<Summary> summaries, Result<Summary> out) {
        Summarized allStoreSummarized = allStoreSummarizeds.get(0); // 集計時のグループと同じグループなので、1件しか来ない。
        Iterator<Summarized> itr = eachStoreSummarizeds.iterator();
        Summarized eachStoreSummarized = itr.next();
        for (Summary summary : summaries) {
            // 全店の集計データをアタッチ
            summary.setAllStoreTotalAmount(allStoreSummarized.getAmount());
            summary.setAllStoreTotalQuantity(allStoreSummarized.getQuantity());
            summary.setAllStoreTotalConsumptionTax(allStoreSummarized.getConsumptionTax());
            summary.setAllStoreTotalCustomers(allStoreSummarized.getCustomers());

            // 店舗別の集計データをアタッチ
            if (eachStoreSummarized.getStoreCode() != summary.getStoreCode()) {
                // フラットデータの店舗コードが切り替わったら店舗別の集計データも切り替える
                eachStoreSummarized = itr.next();
            }
            summary.setEachStoreTotalAmount(eachStoreSummarized.getAmount());
            summary.setEachStoreTotalQuantity(eachStoreSummarized.getQuantity());
            summary.setEachStoreTotalConsumptionTax(eachStoreSummarized.getConsumptionTax());
            summary.setEachStoreTotalCustomers(eachStoreSummarized.getCustomers());

            out.add(summary);
        }
    }

    /**
     * 全店、店舗別の部門別集計データをアタッチする。
     * 
     * @param allStoreSummarizeds 全店の集計データ
     * @param eachStoreSummarizeds 店舗別の集計データ
     * @param summaries フラットデータ
     * @param out アタッチ結果
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @CoGroup
    public void setSectionTotal(@Key(group = { "issueDate", "sectionCode" }) List<Summarized> allStoreSummarizeds,
            @Key(group = { "issueDate", "sectionCode" }, order = "storeCode") List<Summarized> eachStoreSummarizeds,
            @Key(group = { "issueDate", "sectionCode" }, order = "storeCode") List<Summary> summaries,
            Result<Summary> out) {
        Summarized allStoreSummarized = allStoreSummarizeds.get(0); // 集計時のグループと同じグループなので、1件しか来ない。
        Iterator<Summarized> itr = eachStoreSummarizeds.iterator();
        Summarized eachStoreSummarized = itr.next();
        for (Summary summary : summaries) {
            // 全店の集計データをアタッチ
            summary.setAllStoreSectionTotalAmount(allStoreSummarized.getAmount());
            summary.setAllStoreSectionTotalQuantity(allStoreSummarized.getQuantity());
            summary.setAllStoreSectionTotalConsumptionTax(allStoreSummarized.getConsumptionTax());
            summary.setAllStoreSectionTotalCustomers(allStoreSummarized.getCustomers());

            // 店舗別の集計データをアタッチ
            if (eachStoreSummarized.getStoreCode() != summary.getStoreCode()) {
                // フラットデータの店舗コードが切り替わったら店舗別の集計データも切り替える
                eachStoreSummarized = itr.next();
            }
            summary.setEachStoreSectionTotalAmount(eachStoreSummarized.getAmount());
            summary.setEachStoreSectionTotalQuantity(eachStoreSummarized.getQuantity());
            summary.setEachStoreSectionTotalConsumptionTax(eachStoreSummarized.getConsumptionTax());
            summary.setEachStoreSectionTotalCustomers(eachStoreSummarized.getCustomers());

            out.add(summary);
        }
    }

    /**
     * 全店、店舗別のカテゴリ1別集計データをアタッチする。
     * 
     * @param allStoreSummarizeds 全店の集計データ
     * @param eachStoreSummarizeds 店舗別の集計データ
     * @param summaries フラットデータ
     * @param out アタッチ結果
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @CoGroup
    public void setCategory1Total(@Key(group = { "issueDate", "categoryCode1" }) List<Summarized> allStoreSummarizeds,
            @Key(group = { "issueDate", "categoryCode1" }, order = "storeCode") List<Summarized> eachStoreSummarizeds,
            @Key(group = { "issueDate", "categoryCode1" }, order = "storeCode") List<Summary> summaries,
            Result<Summary> out) {
        Summarized allStoreSummarized = allStoreSummarizeds.get(0); // 集計時のグループと同じグループなので、1件しか来ない。
        Iterator<Summarized> itr = eachStoreSummarizeds.iterator();
        Summarized eachStoreSummarized = itr.next();
        for (Summary summary : summaries) {
            // 全店の集計データをアタッチ
            summary.setAllStoreCategory1TotalAmount(allStoreSummarized.getAmount());
            summary.setAllStoreCategory1TotalQuantity(allStoreSummarized.getQuantity());
            summary.setAllStoreCategory1TotalConsumptionTax(allStoreSummarized.getConsumptionTax());
            summary.setAllStoreCategory1TotalCustomers(allStoreSummarized.getCustomers());

            // 店舗別の集計データをアタッチ
            if (eachStoreSummarized.getStoreCode() != summary.getStoreCode()) {
                // フラットデータの店舗コードが切り替わったら店舗別の集計データも切り替える
                eachStoreSummarized = itr.next();
            }
            summary.setEachStoreCategory1TotalAmount(eachStoreSummarized.getAmount());
            summary.setEachStoreCategory1TotalQuantity(eachStoreSummarized.getQuantity());
            summary.setEachStoreCategory1TotalConsumptionTax(eachStoreSummarized.getConsumptionTax());
            summary.setEachStoreCategory1TotalCustomers(eachStoreSummarized.getCustomers());

            out.add(summary);
        }
    }

    /**
     * 全店、店舗別のカテゴリ2別集計データをアタッチする。
     * 
     * @param allStoreSummarizeds 全店の集計データ
     * @param eachStoreSummarizeds 店舗別の集計データ
     * @param summaries フラットデータ
     * @param out アタッチ結果
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @CoGroup
    public void setCategory2Total(@Key(group = { "issueDate", "categoryCode2" }) List<Summarized> allStoreSummarizeds,
            @Key(group = { "issueDate", "categoryCode2" }, order = "storeCode") List<Summarized> eachStoreSummarizeds,
            @Key(group = { "issueDate", "categoryCode2" }, order = "storeCode") List<Summary> summaries,
            Result<Summary> out) {
        Summarized allStoreSummarized = allStoreSummarizeds.get(0); // 集計時のグループと同じグループなので、1件しか来ない。
        Iterator<Summarized> itr = eachStoreSummarizeds.iterator();
        Summarized eachStoreSummarized = itr.next();
        for (Summary summary : summaries) {
            // 全店の集計データをアタッチ
            summary.setAllStoreCategory2TotalAmount(allStoreSummarized.getAmount());
            summary.setAllStoreCategory2TotalQuantity(allStoreSummarized.getQuantity());
            summary.setAllStoreCategory2TotalConsumptionTax(allStoreSummarized.getConsumptionTax());
            summary.setAllStoreCategory2TotalCustomers(allStoreSummarized.getCustomers());

            // 店舗別の集計データをアタッチ
            if (eachStoreSummarized.getStoreCode() != summary.getStoreCode()) {
                // フラットデータの店舗コードが切り替わったら店舗別の集計データも切り替える
                eachStoreSummarized = itr.next();
            }
            summary.setEachStoreCategory2TotalAmount(eachStoreSummarized.getAmount());
            summary.setEachStoreCategory2TotalQuantity(eachStoreSummarized.getQuantity());
            summary.setEachStoreCategory2TotalConsumptionTax(eachStoreSummarized.getConsumptionTax());
            summary.setEachStoreCategory2TotalCustomers(eachStoreSummarized.getCustomers());

            out.add(summary);
        }
    }

}
