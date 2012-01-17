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

import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.Err;
import com.asakusafw.vocabulary.operator.Update;

/**
 * エラー処理のためのオペレータ
 */
public abstract class ErrorOperator {

    /**
     * エラーデータに対してエラーコードをセットする。
     * 
     * @param error エラーデータ
     * @param errorCode エラーコード
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    @Update
    public <E extends Err> void setErrorCode(E error, int errorCode) {
        error.setErrorCode(errorCode);
    }

}
