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
package org.lanternpowered.server.command;

import org.lanternpowered.api.world.weather.WeatherUniverse;
import org.lanternpowered.server.world.LanternWorldProperties;
import org.lanternpowered.server.world.weather.LanternWeather;
import org.lanternpowered.server.world.weather.WeatherOptions;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.weather.Weathers;

public class CommandToggleDownfall extends CommandProvider {

    public CommandToggleDownfall() {
        super(2, "toggledownfall");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.flags()
                                .valueFlag(GenericArguments.world(CommandHelper.WORLD_KEY), "-world", "w")
                                .buildWith(GenericArguments.none())
                )
                .executor((src, args) -> {
                    final LanternWorldProperties world = CommandHelper.getWorldProperties(src, args);
                    final WeatherUniverse weatherUniverse = world.getWorld().get().getWeatherUniverse().orElse(null);
                    final LanternWeather weather = (LanternWeather) weatherUniverse.getWeather();
                    if (weather.getOptions().getOrDefault(WeatherOptions.RAIN_STRENGTH).get() > 0) {
                        weatherUniverse.setWeather(Weathers.CLEAR);
                    } else {
                        weatherUniverse.setWeather(Weathers.RAIN);
                    }
                    return CommandResult.success();
                });
    }
}
