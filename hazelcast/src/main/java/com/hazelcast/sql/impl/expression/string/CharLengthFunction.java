/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.sql.impl.expression.string;

import com.hazelcast.sql.impl.expression.Expression;
import com.hazelcast.sql.impl.expression.UniCallExpression;
import com.hazelcast.sql.impl.row.Row;
import com.hazelcast.sql.impl.type.DataType;

public class CharLengthFunction extends UniCallExpression<Integer> {
    public CharLengthFunction() {
        // No-op.
    }

    private CharLengthFunction(Expression<?> operand) {
        super(operand);
    }

    public static CharLengthFunction create(Expression<?> operand) {
        operand.ensureCanConvertToVarchar();

        return new CharLengthFunction(operand);
    }

    @Override
    public Integer eval(Row row) {
        String value = operand.evalAsVarchar(row);

        return StringExpressionUtils.charLength(value);
    }

    @Override
    public DataType getType() {
        return DataType.INT;
    }
}
