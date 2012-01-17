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
package com.asakusafw.tutorial.posdata_summarization.flowpart;

import com.asakusafw.tutorial.posdata_summarization.common.BatchError;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.ErrorPosItem;
import com.asakusafw.tutorial.posdata_summarization.modelgen.dmdl.model.PosItem;
import com.asakusafw.tutorial.posdata_summarization.operator.ErrorOperatorFactory;
import com.asakusafw.tutorial.posdata_summarization.operator.PosCheckOperatorFactory;
import com.asakusafw.tutorial.posdata_summarization.operator.PosCheckOperatorFactory.Check;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.FlowPart;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;
import com.asakusafw.vocabulary.flow.util.CoreOperatorFactory;

/**
 * POSデータ取込フローパート
 * 取り込んだデータの入力チェックを行う。
 */
@FlowPart
public class PosImportFlowPart extends FlowDescription {

    private final CoreOperatorFactory coreOp = new CoreOperatorFactory();

    private final ErrorOperatorFactory errorOp = new ErrorOperatorFactory();

    private final PosCheckOperatorFactory posCheckOp = new PosCheckOperatorFactory();

    private final In<PosItem> in;

    private final Out<PosItem> valid;

    private final Out<ErrorPosItem> invalid;

    /**
     * @param in POSデータ
     * @param valid 正常データ出力
     * @param invalid 異常データ出力
     * @throws IllegalArgumentException if any parameter is {@code null}
     */
    public PosImportFlowPart(In<PosItem> in, Out<PosItem> valid, Out<ErrorPosItem> invalid) {
        this.in = in;
        this.valid = valid;
        this.invalid = invalid;
    }

    @Override
    protected void describe() {
        // 不正なPOSデータはエラー出力
        Check checked = posCheckOp.check(in);
        valid.add(checked.valid);
        invalid.add(errorOp.setErrorCode(coreOp.extend(checked.invalid, ErrorPosItem.class),
                BatchError.POS_IMPORT_ERROR.errorCode).out);
    }
}
