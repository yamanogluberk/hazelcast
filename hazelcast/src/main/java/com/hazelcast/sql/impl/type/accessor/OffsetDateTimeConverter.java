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

package com.hazelcast.sql.impl.type.accessor;

import com.hazelcast.sql.impl.type.GenericType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Converter for {@link OffsetDateTime} type.
 */
public final class OffsetDateTimeConverter extends Converter {
    /** Singleton instance. */
    public static final OffsetDateTimeConverter INSTANCE = new OffsetDateTimeConverter();

    private OffsetDateTimeConverter() {
        // No-op.
    }

    @Override
    public Class<?> getValueClass() {
        return OffsetDateTime.class;
    }

    @Override
    public GenericType getGenericType() {
        return GenericType.TIMESTAMP_WITH_TIMEZONE;
    }

    @Override
    public String asVarchar(Object val) {
        return cast(val).toString();
    }

    @Override
    public LocalDate asDate(Object val) {
        return asTimestamp(val).toLocalDate();
    }

    @Override
    public LocalTime asTime(Object val) {
        return asTimestamp(val).toLocalTime();
    }

    @Override
    public LocalDateTime asTimestamp(Object val) {
        Instant instant = cast(val).toInstant();

        return LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());
    }

    @Override
    public OffsetDateTime asTimestampWithTimezone(Object val) {
        return cast(val);
    }

    @Override
    public Object convertToSelf(Converter valConverter, Object val) {
        return valConverter.asTimestampWithTimezone(val);
    }

    private OffsetDateTime cast(Object val) {
        return ((OffsetDateTime) val);
    }
}
