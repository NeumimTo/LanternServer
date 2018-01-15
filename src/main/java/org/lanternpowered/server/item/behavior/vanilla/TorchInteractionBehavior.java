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
package org.lanternpowered.server.item.behavior.vanilla;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.property.SolidSideProperty;
import org.lanternpowered.server.block.trait.LanternEnumTraits;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class TorchInteractionBehavior implements InteractWithItemBehavior {

    private static Direction[] getHorizontalDirections(Vector3d vector) {
        Direction start = Direction.getClosestHorizontal(vector, Direction.Division.CARDINAL).getOpposite();
        Direction second;
        if (start == Direction.WEST || start == Direction.EAST) {
            final double d = vector.getZ();
            if (d > 0) {
                second = Direction.NORTH;
            } else {
                second = Direction.SOUTH;
            }
        } else {
            final double d = vector.getX();
            if (d > 0) {
                second = Direction.WEST;
            } else {
                second = Direction.EAST;
            }
        }
        return new Direction[] { start, second, start.getOpposite(), second.getOpposite() };
    }


    @Nullable
    private BlockSnapshotBuilder tryPlaceAt(LanternPlayer player, Location<World> location) {
        Direction facing;
        if (player != null) {
            facing = player.getDirection(Direction.Division.CARDINAL).getOpposite();
            // The up direction cannot be used
            if (facing == Direction.DOWN) {
                facing = player.getHorizontalDirection(Direction.Division.CARDINAL).getOpposite();
            }
        } else {
            facing = Direction.UP;
        }
        Direction direction = null;
        if (facing == Direction.UP && location.getExtent().getProperty(
                location.getBlockRelative(Direction.DOWN).getBlockPosition(), Direction.UP, SolidSideProperty.class).get().getValue()) {
            direction = Direction.UP;
        }
        if (direction == null) {
            for (Direction dir : getHorizontalDirections(player.getHorizontalDirectionVector())) {
                if (location.getExtent().getProperty(location.getBlockRelative(dir.getOpposite()).getBlockPosition(),
                        dir, SolidSideProperty.class).get().getValue()) {
                    direction = dir;
                    break;
                }
            }
        }
        if (direction == null) {
            return null;
        }
        return createBuilder(direction);
    }

    private BlockSnapshotBuilder createBuilder(Direction direction) {
        final BlockSnapshotBuilder builder = BlockSnapshotBuilder.create();
        if (direction == Direction.UP) {
            return builder.blockState(BlockTypes.TORCH.getDefaultState());
        } else {
            return builder.blockState(BlockTypes.WALL_TORCH.getDefaultState()
                    .withTrait(LanternEnumTraits.HORIZONTAL_FACING, direction).get());
        }
    }

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Direction face = context.requireContext(ContextKeys.INTERACTION_FACE);
        final LanternPlayer player = (LanternPlayer) context.getContext(ContextKeys.PLAYER).orElse(null);
        BlockSnapshotBuilder builder = null;
        Location<World> location = context.requireContext(ContextKeys.INTERACTION_LOCATION);
        // Check if the block can be replaced
        if (location.getExtent().getProperty(location.getBlockPosition(), ReplaceableProperty.class).get().getValue()) {
            builder = tryPlaceAt(player, location);
        } else {
            final Location<World> relLocation = location.getBlockRelative(face.getOpposite());
            if (relLocation.getExtent().getProperty(relLocation.getBlockPosition(), ReplaceableProperty.class).get().getValue()) {
                // Check if the clicked face is solid, if so, place the block there
                if (face != Direction.UP && location.getExtent().getProperty(
                        location.getBlockPosition(), face, SolidSideProperty.class).get().getValue()) {
                    builder = createBuilder(face.getOpposite());
                } else {
                    // Use the first valid face
                    builder = tryPlaceAt(player, relLocation);
                }
                location = relLocation;
            }
        }
        if (builder != null) {
            context.addBlockChange(builder.location(location).build());
            return BehaviorResult.SUCCESS;
        }
        return BehaviorResult.FAIL;
    }
}
