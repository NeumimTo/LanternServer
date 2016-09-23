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
package org.lanternpowered.server.world;

import org.lanternpowered.server.component.AttachableTo;
import org.lanternpowered.server.component.Component;
import org.lanternpowered.server.component.Locked;
import org.lanternpowered.server.component.OnAttach;
import org.lanternpowered.server.data.world.MoonPhase;
import org.lanternpowered.server.inject.Inject;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldTime;
import org.lanternpowered.server.world.rules.RuleTypes;

@Locked
@AttachableTo(LanternWorld.class)
public class TimeUniverse implements Component {

    public static final int TICKS_IN_A_DAY = 24000;
    private static final int SYNC_DELAY = 6;

    // The world this weather universe is attached to
    @Inject private LanternWorld world;
    private TimeData timeData;
    private int syncCounter = 0;
    private MoonPhase lastMoonPhase;
    private boolean lastDoDaylightCycle;

    @OnAttach
    private void onAttach() {
        this.timeData = this.world.getProperties().getTimeData();
        this.lastDoDaylightCycle = this.world.getOrCreateRule(RuleTypes.DO_DAYLIGHT_CYCLE).getValue();
        this.lastMoonPhase = this.timeData.getMoonPhase();
    }

    void pulse() {
        this.timeData.setAge(this.timeData.getAge() + 1);
        final boolean doDaylightCycle = this.world.getOrCreateRule(RuleTypes.DO_DAYLIGHT_CYCLE).getValue();
        long time = this.timeData.getDayTime();
        if (doDaylightCycle) {
            time++;
            this.timeData.setDayTime(time);
        }
        final MoonPhase moonPhase = this.timeData.getMoonPhase();
        if (this.syncCounter-- < 0 || this.lastDoDaylightCycle != doDaylightCycle || this.lastMoonPhase != moonPhase) {
            this.syncCounter = SYNC_DELAY;
            this.lastDoDaylightCycle = doDaylightCycle;
            this.lastMoonPhase = moonPhase;
            final long time1 = time;
            this.world.broadcast(() -> new MessagePlayOutWorldTime(moonPhase, this.timeData.getAge(), (int) time1, doDaylightCycle));
        }
    }
}
