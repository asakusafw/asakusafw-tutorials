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

import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Category;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.JoinCategory;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.vocabulary.operator.MasterJoin;
import com.asakusafw.vocabulary.operator.Update;

/**
 * POSデータにカテゴリマスタをJOINするためのオペレータ
 */
public abstract class CategoryJoinOperator {

    /**
     * POSデータにカテゴリマスタをJOINする。
     * 
     * @param category カテゴリマスタ
     * @param posItem POSデータ
     * @return カテゴリマスタをJOINしたオブジェクト
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @MasterJoin
    public abstract JoinCategory doJoinCategory(Category category, PosItem posItem);

    /**
     * カテゴリマスタとJOIN出来なかったPOSデータに対してデフォルト値（"その他"のコード）をセットする。
     * 
     * @param joinCategory カテゴリマスタとJOIN出来なかったPOSデータ
     * @param sectionCode デフォルト部門コード
     * @param categoryCode1 デフォルトカテゴリコード1
     * @param categoryCode2 デフォルトカテゴリコード2
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Update
    public void setDefaultCategory(JoinCategory joinCategory, int sectionCode, int categoryCode1, int categoryCode2) {
        joinCategory.setSectionCode(sectionCode);
        joinCategory.setCategoryCode1(categoryCode1);
        joinCategory.setCategoryCode2(categoryCode2);
    }

}
