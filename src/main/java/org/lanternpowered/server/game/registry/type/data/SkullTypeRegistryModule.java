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
package org.lanternpowered.server.game.registry.type.data;

import org.lanternpowered.server.data.type.LanternSkullType;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.spongepowered.api.data.type.SkullType;
import org.spongepowered.api.data.type.SkullTypes;

public final class SkullTypeRegistryModule extends InternalPluginCatalogRegistryModule<SkullType> {

    private static final SkullTypeRegistryModule INSTANCE = new SkullTypeRegistryModule();

    public static SkullTypeRegistryModule get() {
        return INSTANCE;
    }

    private SkullTypeRegistryModule() {
        super(SkullTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternSkullType("minecraft", "skeleton", "item.skull.skeleton.name", 0));
        register(new LanternSkullType("minecraft", "wither", "item.skull.wither.name", 1));
        register(new LanternSkullType("minecraft", "zombie", "item.skull.zombie.name", 2));
        register(new LanternSkullType("minecraft", "player", "item.skull.char.name", 3));
        register(new LanternSkullType("minecraft", "creeper", "item.skull.creeper.name", 4));
        register(new LanternSkullType("minecraft", "dragon", "item.skull.dragon.name", 5));
    }
}
