/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.service.persistence;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import org.lanternpowered.server.configuration.DataSerializableTypeSerializer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.util.persistence.DataBuilder;
import org.spongepowered.api.util.persistence.SerializationManager;

import java.util.Map;
import java.util.Optional;

public class LanternSerializationService implements SerializationManager {

    static {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(DataSerializable.class),
                new DataSerializableTypeSerializer());
    }

    private final Map<Class<?>, DataBuilder<?>> builders = Maps.newHashMap();
    private boolean registrationComplete = false;

    public void completeRegistration() {
        checkState(!this.registrationComplete);
        this.registrationComplete = true;
    }

    @Override
    public <T extends DataSerializable> void registerBuilder(Class<T> clazz, DataBuilder<T> builder) {
        checkNotNull(clazz);
        checkNotNull(builder);
        checkState(!this.registrationComplete);
        if (!this.builders.containsKey(clazz)) {
            this.builders.put(clazz, builder);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DataSerializable> Optional<DataBuilder<T>> getBuilder(Class<T> clazz) {
        checkNotNull(clazz);
        if (this.builders.containsKey(clazz)) {
            return Optional.of((DataBuilder<T>) this.builders.get(clazz));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public <T extends DataSerializable> Optional<T> deserialize(Class<T> clazz, DataView dataView) {
        Optional<DataBuilder<T>> optional = this.getBuilder(clazz);
        if (optional.isPresent()) {
            return optional.get().build(dataView);
        } else {
            return Optional.empty();
        }
    }
}
