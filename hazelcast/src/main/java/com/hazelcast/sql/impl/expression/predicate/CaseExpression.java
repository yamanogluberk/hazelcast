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

package com.hazelcast.sql.impl.expression.predicate;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.sql.impl.expression.CastExpression;
import com.hazelcast.sql.impl.expression.Expression;
import com.hazelcast.sql.impl.row.Row;
import com.hazelcast.sql.impl.type.DataType;
import com.hazelcast.sql.impl.type.DataTypeUtils;

import java.io.IOException;
import java.util.List;

/**
 * CASE-WHEN expression.
 */
public class CaseExpression<T> implements Expression<T> {
    /** Conditions. */
    private Expression<Boolean>[] conditions;

    /** Results. */
    private Expression<?>[] results;

    /** Return type. */
    private DataType resultType;

    public CaseExpression() {
        // No-op.
    }

    private CaseExpression(Expression<Boolean>[] conditions, Expression<Boolean>[] results, DataType resultType) {
        this.conditions = conditions;
        this.results = results;
        this.resultType = resultType;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CaseExpression<?> create(List<Expression<?>> expressions) {
        // Split conditions and expressions.
        assert expressions != null;
        assert expressions.size() % 2 == 1;

        int conditionCount = expressions.size() / 2;

        Expression<Boolean>[] conditions = new Expression[conditionCount];
        Expression<?>[] results = new Expression[conditionCount + 1];

        int idx = 0;

        for (int i = 0; i < conditionCount; i++) {
            conditions[i] = (Expression<Boolean>) expressions.get(idx++);
            results[i] = expressions.get(idx++);
        }

        // Last expression might be null.
        results[results.length - 1] = expressions.size() == idx + 1 ? expressions.get(idx) : null;

        // Determine the result type and perform coercion.
        DataType resType = DataTypeUtils.compare(results);

        for (int i = 0; i < results.length; i++) {
            results[i] = CastExpression.coerce(results[i], resType);
        }

        // Done.
        return new CaseExpression(conditions, results, resType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T eval(Row row) {
        for (int i = 0; i < conditions.length; i++) {
            Expression<Boolean> condition = conditions[i];

            Boolean conditionRes = condition.evalAsBit(row);

            if (conditionRes != null && conditionRes) {
                return (T) results[i].eval(row);
            }
        }

        // Return the last result if none conditions were met.
        Expression<?> lastResult = results[results.length - 1];

        if (lastResult != null) {
            return (T) lastResult.eval(row);
        } else {
            return null;
        }
    }

    @Override
    public DataType getType() {
        return resultType;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(conditions.length);

        for (int i = 0; i < conditions.length; i++) {
            out.writeObject(conditions[i]);
            out.writeObject(results[i]);
        }

        out.writeObject(results[results.length - 1]);

        out.writeObject(resultType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int len = in.readInt();

        conditions = new Expression[len];
        results = new Expression[len + 1];

        for (int i = 0; i < len; i++) {
            conditions[i] = in.readObject();
            results[i] = in.readObject();
        }

        results[len] = in.readObject();

        resultType = in.readObject();
    }
}
