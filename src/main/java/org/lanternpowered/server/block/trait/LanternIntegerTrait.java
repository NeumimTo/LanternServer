/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.block.trait;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.block.trait.IntegerTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;

import javax.annotation.Nullable;

public final class LanternIntegerTrait<V> extends LanternBlockTrait<Integer, V> implements IntegerTrait {

    private LanternIntegerTrait(String name, Key<? extends Value<V>> key, ImmutableSet<Integer> possibleValues,
            @Nullable KeyTraitValueTransformer<Integer, V> keyTraitValueTransformer) {
        super(name, key, Integer.class, possibleValues, keyTraitValueTransformer);
    }

    /**
     * Creates a new integer trait with the specified name and the possible values.
     *
     * <p>The possible values array may not be empty.</p>
     *
     * @param name the name
     * @param key the key that should be attached to the trait
     * @param possibleValues the possible values
     * @return the integer trait
     */
    public static LanternIntegerTrait<Integer> of(String name, Key<? extends Value<Integer>> key, int... possibleValues) {
        return ofTransformed(name, key, null, possibleValues);
    }

    /**
     * Creates a new integer trait with the specified name and the possible values.
     * 
     * <p>The possible values array may not be empty.</p>
     * 
     * @param name the name
     * @param key the key that should be attached to the trait
     * @param keyTraitValueTransformer The key trait value transformer
     * @param possibleValues the possible values
     * @return the integer trait
     */
    public static <V> LanternIntegerTrait<V> ofTransformed(String name, Key<? extends Value<V>> key,
            @Nullable KeyTraitValueTransformer<Integer, V> keyTraitValueTransformer, int... possibleValues) {
        checkNotNullOrEmpty(name, "name");
        checkNotNull(possibleValues, "possibleValues");
        checkNotNull(key, "key");
        checkState(possibleValues.length != 0, "possibleValues may not be empty");
        final ImmutableSet.Builder<Integer> builder = ImmutableSet.builder();
        for (int possibleValue : possibleValues) {
            builder.add(possibleValue);
        }
        return new LanternIntegerTrait<>(name, key, builder.build(), keyTraitValueTransformer);
    }

    /**
     * Creates a new integer trait with the specified name and the possible values.
     *
     * <p>The possible values array may not be empty.</p>
     *
     * @param name the name
     * @param key the key that should be attached to the trait
     * @param possibleValues the possible values
     * @return the integer trait
     */
    public static <V> LanternIntegerTrait<V> of(String name, Key<? extends Value<V>> key, Iterable<Integer> possibleValues) {
        return ofTransformed(name, key, null, possibleValues);
    }

    /**
     * Creates a new integer trait with the specified name and the possible values.
     * 
     * <p>The possible values array may not be empty.</p>
     * 
     * @param name the name
     * @param key the key that should be attached to the trait
     * @param keyTraitValueTransformer The key trait value transformer
     * @param possibleValues the possible values
     * @return the integer trait
     */
    public static <V> LanternIntegerTrait<V> ofTransformed(String name, Key<? extends Value<V>> key,
            @Nullable KeyTraitValueTransformer<Integer, V> keyTraitValueTransformer, Iterable<Integer> possibleValues) {
        checkNotNullOrEmpty(name, "name");
        checkNotNull(possibleValues, "possibleValues");
        checkNotNull(key, "key");
        checkState(possibleValues.iterator().hasNext(), "possibleValues may not be empty");
        return new LanternIntegerTrait<>(name, key, ImmutableSet.copyOf(possibleValues), keyTraitValueTransformer);
    }

    /**
     * Creates a new integer trait with the specified name and the values between
     * the minimum (inclusive) and the maximum (exclusive) value.
     *
     * <p>The difference between the minimum and the maximum value must
     * be greater then zero.</p>
     *
     * @param name the name
     * @param key the key that should be attached to the trait
     * @param min the minimum value
     * @param max the maximum value
     * @return the integer trait
     */
    public static LanternIntegerTrait<Integer> ofRange(String name, Key<? extends Value<Integer>> key, int min, int max) {
        return ofRangeTransformed(name, key, null, min, max);
    }

    /**
     * Creates a new integer trait with the specified name and the values between
     * the minimum (inclusive) and the maximum (exclusive) value.
     * 
     * <p>The difference between the minimum and the maximum value must
     * be greater then zero.</p>
     * 
     * @param name the name
     * @param key the key that should be attached to the trait
     * @param keyTraitValueTransformer The key trait value transformer
     * @param min the minimum value
     * @param max the maximum value
     * @return the integer trait
     */
    public static <V> LanternIntegerTrait<V> ofRangeTransformed(String name, Key<? extends Value<V>> key,
            @Nullable KeyTraitValueTransformer<Integer, V> keyTraitValueTransformer, int min, int max) {
        checkNotNullOrEmpty(name, "name");
        checkNotNull(key, "key");
        checkState(max - min > 0, "difference between min and max must be greater then zero");
        final ImmutableSet.Builder<Integer> set = ImmutableSet.builder();
        for (int i = min; i <= max; i++) {
            set.add(i);
        }
        return new LanternIntegerTrait<>(name, key, set.build(), keyTraitValueTransformer);
    }

}
