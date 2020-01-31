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

package com.hazelcast.sql.impl.expression;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.sql.impl.type.DataType;

import java.io.IOException;
import java.util.Objects;

public abstract class BiCallExpressionWithType<T> extends BiCallExpression<T> {
    /** Result type. */
    protected DataType resultType;

    protected BiCallExpressionWithType() {
        // No-op.
    }

    // TODO: Remove this after refactoring.
    protected BiCallExpressionWithType(Expression<?> operand1, Expression<?> operand2) {
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    protected BiCallExpressionWithType(Expression<?> operand1, Expression<?> operand2, DataType resultType) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.resultType = resultType;
    }

    @Override
    public DataType getType() {
        return resultType;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);

        out.writeObject(resultType);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);

        resultType = in.readObject();
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand1, operand2, resultType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BiCallExpressionWithType<?> that = (BiCallExpressionWithType<?>) o;

        return Objects.equals(operand1, that.operand1) && Objects.equals(operand2, that.operand2)
                   && Objects.equals(resultType, that.resultType);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{operand1=" + operand1 + ", operand2=" + operand2 + ", resType=" + resultType + '}';
    }
}
