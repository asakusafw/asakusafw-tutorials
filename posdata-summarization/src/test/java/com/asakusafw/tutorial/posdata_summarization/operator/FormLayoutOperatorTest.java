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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.asakusafw.runtime.testing.MockResult;
import com.asakusafw.runtime.value.Date;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.FormLayout;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Summarized;

/**
 * 伝票イメージを作成するオペレータのテスト
 */
public class FormLayoutOperatorTest extends FormLayoutOperator {

    /**
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Test
    public void testMakeFormLayout() {
        FormLayoutOperator operator = new FormLayoutOperatorImpl();

        List<Summarized> summarizeds = new ArrayList<Summarized>();
        summarizeds.add(newObject(12345678900001L, "商品1", 10, 100, 5, 1));
        summarizeds.add(newObject(12345678900002L, "商品2", 20, 200, 10, 2));
        summarizeds.add(newObject(12345678900003L, "商品3", 30, 300, 15, 3));
        summarizeds.add(newObject(12345678900004L, "商品4", 40, 400, 20, 4));
        summarizeds.add(newObject(12345678900005L, "商品5", 50, 500, 25, 5));
        summarizeds.add(newObject(12345678900006L, "商品6", 60, 600, 30, 6));
        summarizeds.add(newObject(12345678900007L, "商品7", 70, 700, 35, 7));
        summarizeds.add(newObject(12345678900008L, "商品8", 80, 800, 40, 8));
        summarizeds.add(newObject(12345678900009L, "商品9", 90, 900, 45, 9));

        MockResult<FormLayout> formLayoutResult = new MockResult<FormLayout>();

        operator.makeFormLayout(summarizeds, formLayoutResult);

        assertThat(formLayoutResult.getResults().size(), is(2));

        FormLayout page1 = formLayoutResult.getResults().get(0);
        assertThat(page1.getIssueDate(), is(Date.valueOf("20111212", Date.Format.SIMPLE)));
        assertThat(page1.getStoreCode(), is(101));
        assertThat(page1.getSectionCode(), is(100));
        assertThat(page1.getLine1AsString(), is("12345678900001\t商品1\t10\t100\t5"));
        assertThat(page1.getLine2AsString(), is("12345678900002\t商品2\t20\t200\t10"));
        assertThat(page1.getLine3AsString(), is("12345678900003\t商品3\t30\t300\t15"));
        assertThat(page1.getLine4AsString(), is("12345678900004\t商品4\t40\t400\t20"));
        assertThat(page1.getLine5AsString(), is("12345678900005\t商品5\t50\t500\t25"));
        assertThat(page1.getLine6AsString(), is("12345678900006\t商品6\t60\t600\t30"));
        assertThat(page1.getPageNumber(), is(1));

        FormLayout page2 = formLayoutResult.getResults().get(1);
        assertThat(page2.getIssueDate(), is(Date.valueOf("20111212", Date.Format.SIMPLE)));
        assertThat(page2.getStoreCode(), is(101));
        assertThat(page2.getSectionCode(), is(100));
        assertThat(page2.getLine1AsString(), is("12345678900007\t商品7\t70\t700\t35"));
        assertThat(page2.getLine2AsString(), is("12345678900008\t商品8\t80\t800\t40"));
        assertThat(page2.getLine3AsString(), is("12345678900009\t商品9\t90\t900\t45"));
        assertTrue(page2.getLine4Option().isNull());
        assertTrue(page2.getLine5Option().isNull());
        assertTrue(page2.getLine6Option().isNull());
        assertThat(page2.getPageNumber(), is(2));
    }

    /**
     * テスト用の{@code Summarized}オブジェクトを作成する。
     * 発行日、店舗コード、部門は固定、その他パラメータを指定する。
     * 
     * @param janCode JANコード
     * @param itemName 商品コード
     * @param quantity 数量
     * @param amount 売価
     * @param consumptionTax 消費税
     * @param customers 客数
     * @return テスト用の{@code Summarized}オブジェクト
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    private Summarized newObject(long janCode, String itemName, int quantity, int amount, int consumptionTax,
            int customers) {
        Summarized summarized = new Summarized();
        summarized.setIssueDate(Date.valueOf("20111212", Date.Format.SIMPLE));
        summarized.setStoreCode(101);
        summarized.setSectionCode(100);
        summarized.setJanCode(janCode);
        summarized.setItemNameAsString(itemName);
        summarized.setQuantity(quantity);
        summarized.setAmount(amount);
        summarized.setConsumptionTax(consumptionTax);
        summarized.setCustomers(customers);
        return summarized;
    }
}
