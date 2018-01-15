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
package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.data.type.InstrumentType;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;

import java.util.function.Supplier;

import javax.annotation.Nullable;

public enum LanternInstrumentType implements InstrumentType, SimpleCatalogType {

    HARP            ("harp", () -> SoundTypes.BLOCK_NOTE_HARP),
    BASEDRUM        ("bass_drum", () -> SoundTypes.BLOCK_NOTE_BASEDRUM),
    SNARE           ("snare", () -> SoundTypes.BLOCK_NOTE_SNARE),
    HAT             ("high_hat", () -> SoundTypes.BLOCK_NOTE_HAT),
    BASS            ("bass_attack", () -> SoundTypes.BLOCK_NOTE_BASS),
    FLUTE           ("flute", () -> SoundTypes.BLOCK_NOTE_FLUTE),
    BELL            ("bell", () -> SoundTypes.BLOCK_NOTE_BELL),
    GUITAR          ("guitar", () -> SoundTypes.BLOCK_NOTE_GUITAR),
    CHIME           ("chime", () -> SoundTypes.BLOCK_NOTE_CHIME),
    XYLOPHONE       ("xylophone", () -> SoundTypes.BLOCK_NOTE_XYLOPHONE),
    ;

    private final String identifier;
    private final Supplier<SoundType> soundTypeSupplier;

    @Nullable private SoundType soundType;

    LanternInstrumentType(String identifier, Supplier<SoundType> soundTypeSupplier) {
        this.identifier = identifier;
        this.soundTypeSupplier = soundTypeSupplier;
    }

    @Override
    public String getId() {
        return this.identifier;
    }

    public SoundType getSound() {
        if (this.soundType == null) {
            this.soundType = this.soundTypeSupplier.get();
        }
        return this.soundType;
    }
}
