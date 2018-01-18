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

import org.lanternpowered.server.data.type.record.LanternMusicDisc;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.SoundTypeRegistryModule;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.effect.sound.music.MusicDisc;
import org.spongepowered.api.effect.sound.music.MusicDiscs;
import org.spongepowered.api.registry.util.RegistrationDependency;

@RegistrationDependency(SoundTypeRegistryModule.class)
public class MusicDiscRegistryModule extends InternalPluginCatalogRegistryModule<MusicDisc> {

    private static final MusicDiscRegistryModule INSTANCE = new MusicDiscRegistryModule();

    public static MusicDiscRegistryModule get() {
        return INSTANCE;
    }

    private MusicDiscRegistryModule() {
        super(MusicDiscs.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternMusicDisc("minecraft", "thirteen", "item.record.13.desc", 0, SoundTypes.RECORD_13));
        register(new LanternMusicDisc("minecraft", "cat", "item.record.cat.desc", 1, SoundTypes.RECORD_CAT));
        register(new LanternMusicDisc("minecraft", "blocks", "item.record.blocks.desc", 2, SoundTypes.RECORD_BLOCKS));
        register(new LanternMusicDisc("minecraft", "chirp", "item.record.chirp.desc", 3, SoundTypes.RECORD_CHIRP));
        register(new LanternMusicDisc("minecraft", "far", "item.record.far.desc", 4, SoundTypes.RECORD_FAR));
        register(new LanternMusicDisc("minecraft", "mall", "item.record.mall.desc", 5, SoundTypes.RECORD_MALL));
        register(new LanternMusicDisc("minecraft", "mellohi", "item.record.mellohi.desc", 6, SoundTypes.RECORD_MELLOHI));
        register(new LanternMusicDisc("minecraft", "stal", "item.record.stal.desc", 7, SoundTypes.RECORD_STAL));
        register(new LanternMusicDisc("minecraft", "strad", "item.record.strad.desc", 8, SoundTypes.RECORD_STRAD));
        register(new LanternMusicDisc("minecraft", "ward", "item.record.ward.desc", 9, SoundTypes.RECORD_WARD));
        register(new LanternMusicDisc("minecraft", "eleven", "item.record.11.desc", 10, SoundTypes.RECORD_11));
        register(new LanternMusicDisc("minecraft", "wait", "item.record.wait.desc", 11, SoundTypes.RECORD_WAIT));
    }
}
