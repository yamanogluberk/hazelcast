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

package com.hazelcast.sql.impl.expression.aggregate;

import com.hazelcast.sql.HazelcastSqlException;
import com.hazelcast.sql.impl.QueryContext;
import com.hazelcast.sql.impl.exec.agg.AggregateCollector;
import com.hazelcast.sql.impl.expression.Expression;
import com.hazelcast.sql.impl.type.DataType;
import com.hazelcast.sql.impl.type.GenericType;

/**
 * MIN expression.
 */
public class MinAggregateExpression extends AbstractSingleOperandAggregateExpression<Comparable<?>> {
    public MinAggregateExpression() {
        // No-op.
    }

    private MinAggregateExpression(Expression<?> operand, DataType resType, boolean distinct) {
        super(operand, resType, distinct);
    }

    public static MinAggregateExpression create(Expression<?> operand, boolean distinct) {
        DataType operandType = operand.getType();

        if (operandType.getType() == GenericType.LATE) {
            throw new HazelcastSqlException(-1, "Operand type cannot be resolved: " + operandType);
        }

        return new MinAggregateExpression(operand, operandType, distinct);
    }

    @Override
    public AggregateCollector newCollector(QueryContext ctx) {
        return new MinMaxAggregateCollector(true);
    }

    @Override
    protected boolean isIgnoreNull() {
        return true;
    }
}
