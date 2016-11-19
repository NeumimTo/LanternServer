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
package org.lanternpowered.server.statistic;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.statistic.ItemStatistic;
import org.spongepowered.api.statistic.StatisticFormat;
import org.spongepowered.api.statistic.StatisticGroup;
import org.spongepowered.api.text.translation.Translation;

import javax.annotation.Nullable;

public class LanternItemStatisticBuilder extends AbstractStatisticBuilder<ItemStatistic, ItemStatistic.Builder>
        implements ItemStatistic.Builder {

    private ItemType itemType;

    @Override
    public ItemStatistic.Builder item(ItemType item) {
        this.itemType = checkNotNull(item, "item");
        return this;
    }

    @Override
    public LanternItemStatisticBuilder from(ItemStatistic value) {
        super.from(value);
        this.itemType = value.getItemType();
        return this;
    }

    @Override
    public LanternItemStatisticBuilder reset() {
        super.reset();
        this.itemType = null;
        return this;
    }


    @Override
    ItemStatistic build(String pluginId, String id, String name, Translation translation, StatisticGroup group,
            @Nullable StatisticFormat format, String internalId) {
        checkState(this.itemType != null, "The itemType must be set");
        return new LanternItemStatistic(pluginId, id, name, translation, group, format, this.itemType, internalId);
    }
}
