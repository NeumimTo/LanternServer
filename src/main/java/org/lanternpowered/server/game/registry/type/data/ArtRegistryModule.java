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

import org.lanternpowered.server.data.type.LanternArt;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.spongepowered.api.data.type.Art;
import org.spongepowered.api.data.type.Arts;

public class ArtRegistryModule extends PluginCatalogRegistryModule<Art> {

    public ArtRegistryModule() {
        super(Arts.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternArt("minecraft", "kebab", "Kebab", 0, 1, 1));
        register(new LanternArt("minecraft", "aztec", "Aztec", 1, 1, 1));
        register(new LanternArt("minecraft", "alban", "Alban", 2, 1, 1));
        register(new LanternArt("minecraft", "aztec_2", "Aztec2", 3, 1, 1));
        register(new LanternArt("minecraft", "bomb", "Bomb", 4, 1, 1));
        register(new LanternArt("minecraft", "plant", "Plant", 5, 1, 1));
        register(new LanternArt("minecraft", "wasteland", "Wasteland", 6, 1, 1));
        register(new LanternArt("minecraft", "pool", "Pool", 7, 2, 1));
        register(new LanternArt("minecraft", "courbet", "Courbet", 8, 2, 1));
        register(new LanternArt("minecraft", "sea", "Sea", 9, 2, 1));
        register(new LanternArt("minecraft", "sunset", "Sunset", 10, 2, 1));
        register(new LanternArt("minecraft", "creebet", "Creebet", 11, 2, 1));
        register(new LanternArt("minecraft", "wanderer", "Wanderer", 12, 1, 2));
        register(new LanternArt("minecraft", "graham", "Graham", 13, 1, 2));
        register(new LanternArt("minecraft", "match", "Match", 14, 2, 2));
        register(new LanternArt("minecraft", "bust", "Bust", 15, 2, 2));
        register(new LanternArt("minecraft", "stage", "Stage", 16, 2, 2));
        register(new LanternArt("minecraft", "void", "Void", 17, 2, 2));
        register(new LanternArt("minecraft", "skull_and_roses", "SkullAndRoses", 18, 2, 2));
        register(new LanternArt("minecraft", "wither", "Wither", 19, 2, 2));
        register(new LanternArt("minecraft", "fighters", "Fighters", 20, 4, 2));
        register(new LanternArt("minecraft", "pointer", "Pointer", 21, 4, 4));
        register(new LanternArt("minecraft", "pigscene", "Pigscene", 22, 4, 4));
        register(new LanternArt("minecraft", "burning_skull", "BurningSkull", 23, 4, 4));
        register(new LanternArt("minecraft", "skeleton", "Skeleton", 24, 4, 3));
        register(new LanternArt("minecraft", "donkey_kong", "DonkeyKong", 25, 4, 3));
    }
}
