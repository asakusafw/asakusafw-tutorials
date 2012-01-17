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
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.FormLayout;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summarized;
import com.asakusafw.vocabulary.model.Key;
import com.asakusafw.vocabulary.operator.GroupSort;

/**
 * 伝票イメージを作成するオペレータ
 */
public abstract class FormLayoutOperator {

    /**
     * 日別店舗別の商品別集計データから伝票イメージを作成する。
     * 
     * 日別店舗別部門別にグルーピングして、6行毎に1ページとしてまとめる。
     * ヘッダーと、各行、ページ数を出力する。
     * 
     * @param summarizeds 日別店舗別商品別集計データ
     * @param out 伝票イメージの出力
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @GroupSort
    public void makeFormLayout(
            @Key(group = { "issueDate", "storeCode", "sectionCode" }, order = "janCode") List<Summarized> summarizeds,
            Result<FormLayout> out) {
        Iterator<Summarized> itr = summarizeds.iterator();
        for (int page = 1; itr.hasNext(); page++) {
            Summarized summarized = itr.next();

            FormLayout formLayout = new FormLayout();
            formLayout.setIssueDate(summarized.getIssueDate());
            formLayout.setStoreCode(summarized.getStoreCode());
            formLayout.setSectionCode(summarized.getSectionCode());

            formLayout.setLine1AsString(formatLine(summarized));

            if (itr.hasNext()) {
                formLayout.setLine2AsString(formatLine(itr.next()));
            }
            if (itr.hasNext()) {
                formLayout.setLine3AsString(formatLine(itr.next()));
            }
            if (itr.hasNext()) {
                formLayout.setLine4AsString(formatLine(itr.next()));
            }
            if (itr.hasNext()) {
                formLayout.setLine5AsString(formatLine(itr.next()));
            }
            if (itr.hasNext()) {
                formLayout.setLine6AsString(formatLine(itr.next()));
            }

            formLayout.setPageNumber(page);

            out.add(formLayout);
        }
    }

    /**
     * 集計データから伝票の行を作成する。
     * 
     * @param summarized 集計データ
     * @return 伝票の1行
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    private String formatLine(Summarized summarized) {
        return String.format("%d\t%s\t%d\t%d\t%d", summarized.getJanCode(), summarized.getItemName(),
                summarized.getQuantity(), summarized.getAmount(), summarized.getConsumptionTax());
    }
}
