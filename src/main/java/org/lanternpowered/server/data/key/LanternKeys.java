/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.data.key;

import static org.spongepowered.api.data.key.KeyFactory.makeSingleKey;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.data.value.mutable.Value;

public class LanternKeys {

    public static final Key<Value<Boolean>> INVULNERABLE = makeSingleKey(Boolean.class, Value.class, DataQuery.of("Invulnerable"));
    public static final Key<Value<Integer>> PORTAL_COOLDOWN_TICKS = makeSingleKey(Integer.class, Value.class, DataQuery.of("PortalCooldownTicks"));
    public static final Key<Value<Integer>> SCORE = makeSingleKey(Integer.class, Value.class, DataQuery.of("Score"));
    public static final Key<MutableBoundedValue<Double>> ABSORPTION_AMOUNT = makeSingleKey(Double.class, MutableBoundedValue.class,
            DataQuery.of("AbsorptionAmount"));
    public static final Key<Value<Boolean>> CAN_PICK_UP_LOOT = makeSingleKey(Boolean.class, Value.class, DataQuery.of("CanPickUpLoot"));
}