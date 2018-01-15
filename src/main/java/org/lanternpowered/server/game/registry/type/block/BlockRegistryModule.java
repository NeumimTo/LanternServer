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
package org.lanternpowered.server.game.registry.type.block;

import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.block.provider.property.PropertyProviderCollections.INSTANT_BROKEN;
import static org.lanternpowered.server.block.provider.property.PropertyProviderCollections.PASSABLE;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.blastResistance;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.flammableInfo;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.hardness;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.lightEmission;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.replaceable;
import static org.lanternpowered.server.item.PropertyProviders.equipmentType;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.block.BlockTypeBuilder;
import org.lanternpowered.server.block.BlockTypeBuilderImpl;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.aabb.BoundingBoxes;
import org.lanternpowered.server.block.behavior.simple.BlockSnapshotProviderPlaceBehavior;
import org.lanternpowered.server.block.behavior.simple.SimpleBlockDropsProviderBehavior;
import org.lanternpowered.server.block.behavior.simple.SimpleBreakBehavior;
import org.lanternpowered.server.block.behavior.simple.SimplePlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.AxisRotationPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.ChestBreakBehavior;
import org.lanternpowered.server.block.behavior.vanilla.ChestInteractionBehavior;
import org.lanternpowered.server.block.behavior.vanilla.ChestPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.CraftingTableInteractionBehavior;
import org.lanternpowered.server.block.behavior.vanilla.EnderChestInteractionBehavior;
import org.lanternpowered.server.block.behavior.vanilla.HopperPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.HorizontalRotationPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.JukeboxInteractionBehavior;
import org.lanternpowered.server.block.behavior.vanilla.NoteBlockInteractionBehavior;
import org.lanternpowered.server.block.behavior.vanilla.OpenableContainerInteractionBehavior;
import org.lanternpowered.server.block.behavior.vanilla.OppositeFaceDirectionalPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.RotationPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.ShulkerBoxInteractionBehavior;
import org.lanternpowered.server.block.provider.property.PropertyProviderCollections;
import org.lanternpowered.server.block.trait.LanternBooleanTraits;
import org.lanternpowered.server.block.trait.LanternEnumTraits;
import org.lanternpowered.server.block.trait.LanternIntegerTraits;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternBedPart;
import org.lanternpowered.server.data.type.LanternChestAttachment;
import org.lanternpowered.server.data.type.LanternInstrumentType;
import org.lanternpowered.server.data.type.LanternPortionType;
import org.lanternpowered.server.data.type.LanternRailDirection;
import org.lanternpowered.server.data.type.LanternSlabPortion;
import org.lanternpowered.server.data.type.RedstoneConnectionType;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.InternalIDRegistries;
import org.lanternpowered.server.game.registry.type.data.KeyRegistryModule;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule;
import org.lanternpowered.server.inventory.InventorySnapshot;
import org.lanternpowered.server.item.behavior.vanilla.SlabItemInteractionBehavior;
import org.lanternpowered.server.item.behavior.vanilla.TorchInteractionBehavior;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntityTypes;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Direction;

import java.util.Optional;

@RegistrationDependency({
        KeyRegistryModule.class,
        EquipmentTypeRegistryModule.class
})
public final class BlockRegistryModule extends AdditionalPluginCatalogRegistryModule<BlockType> implements BlockRegistry {

    private static final BlockRegistryModule INSTANCE = new BlockRegistryModule();

    public static BlockRegistryModule get() {
        return INSTANCE;
    }

    private final Int2ObjectMap<BlockState> blockStateByInternalId = new Int2ObjectOpenHashMap<>();
    private final Object2IntMap<BlockState> internalIdByBlockState = new Object2IntOpenHashMap<>();

    public BlockRegistryModule() {
        super(BlockTypes.class);
        this.internalIdByBlockState.defaultReturnValue(-1);
    }

    @Override
    public void register(BlockType blockType) {
        int internalId = InternalIDRegistries.BLOCK_TYPE_IDS.getInt(blockType.getId());
        checkState(internalId != -1, "No internal id could be found for the block id: " + blockType.getId());
        super.register(blockType);
        final LanternBlockType type = (LanternBlockType) blockType;
        for (BlockState blockState : type.getBlockStateBase().getBlockStates()) {
            final int id = internalId++;
            this.blockStateByInternalId.put(id, blockState);
            this.internalIdByBlockState.put(blockState, id);
        }
        final BlockStateRegistryModule blockStateRegistryModule = Lantern.getRegistry()
                .getRegistryModule(BlockStateRegistryModule.class).get();
        blockType.getAllBlockStates().forEach(blockStateRegistryModule::registerState);
        blockType.getItem().ifPresent(itemType -> ItemRegistryModule.get().register(itemType));
        Lantern.getGame().getPropertyRegistry().registerBlockPropertyStores(type.getPropertyProviderCollection());
    }

    @Override
    public Optional<BlockState> getStateByInternalId(int internalId) {
        return Optional.ofNullable(this.blockStateByInternalId.get(internalId));
    }

    @Override
    public int getStateInternalId(BlockState blockState) {
        return this.internalIdByBlockState.get(blockState);
    }

    @Override
    public void registerDefaults() {
        // @formatter:off

        ///////////////////
        ///    Air      ///
        ///////////////////
        register(builder()
                .properties(PropertyProviderCollections.DEFAULT_GAS)
                .build("minecraft", "air"));
        ///////////////////
        ///    Stone    ///
        ///////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(1.5))
                        .add(blastResistance(30.0)))
                .build("minecraft", "stone"));
        //////////////////////////
        ///    Smooth Stone    ///
        //////////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "smooth_stone"));
        /////////////////////
        ///    Granite    ///
        /////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(1.5))
                        .add(blastResistance(30.0)))
                .build("minecraft", "granite"));
        //////////////////////////////
        ///    Polished Granite    ///
        //////////////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(1.5))
                        .add(blastResistance(30.0)))
                .build("minecraft", "polished_granite"));
        /////////////////////
        ///    Diorite    ///
        /////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(1.5))
                        .add(blastResistance(30.0)))
                .build("minecraft", "diorite"));
        //////////////////////////////
        ///    Polished Diorite    ///
        //////////////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(1.5))
                        .add(blastResistance(30.0)))
                .build("minecraft", "polished_diorite"));
        //////////////////////
        ///    Andesite    ///
        //////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(1.5))
                        .add(blastResistance(30.0)))
                .build("minecraft", "andesite"));
        ///////////////////////////////
        ///    Polished Andesite    ///
        ///////////////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(1.5))
                        .add(blastResistance(30.0)))
                .build("minecraft", "polished_andesite"));
        ///////////////////
        ///    Grass    ///
        ///////////////////
        register(simpleBuilder()
                .itemType()
                .trait(LanternBooleanTraits.SNOWY)
                .defaultState(state -> state.withTrait(LanternBooleanTraits.SNOWY, false).get())
                .properties(builder -> builder
                        .add(hardness(0.6))
                        .add(blastResistance(3.0)))
                .build("minecraft", "grass_block"));
        ///////////////////
        ///    Dirt     ///
        ///////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.5))
                        .add(blastResistance(2.5)))
                .build("minecraft", "dirt"));
        //////////////////////////
        ///    Coarse Dirt     ///
        //////////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.5))
                        .add(blastResistance(2.5)))
                .build("minecraft", "coarse_dirt"));
        /////////////////////
        ///    Podzol     ///
        /////////////////////
        register(simpleBuilder()
                .itemType()
                .traits(LanternBooleanTraits.SNOWY)
                .defaultState(state -> state
                        .withTrait(LanternBooleanTraits.SNOWY, false).get())
                .properties(builder -> builder
                        .add(hardness(0.5))
                        .add(blastResistance(2.5)))
                .build("minecraft", "podzol"));
        ///////////////////
        /// Cobblestone ///
        ///////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(3.0)))
                .build("minecraft", "cobblestone"));
        ////////////////
        ///  Planks  ///
        ////////////////
        register(planksBuilder().build("minecraft", "oak_planks"));
        register(planksBuilder().build("minecraft", "spruce_planks"));
        register(planksBuilder().build("minecraft", "birch_planks"));
        register(planksBuilder().build("minecraft", "jungle_planks"));
        register(planksBuilder().build("minecraft", "acacia_planks"));
        register(planksBuilder().build("minecraft", "dark_oak_planks"));
        //////////////////
        ///  Saplings  ///
        //////////////////
        register(saplingBuilder().build("minecraft", "oak_sapling"));
        register(saplingBuilder().build("minecraft", "spruce_sapling"));
        register(saplingBuilder().build("minecraft", "birch_sapling"));
        register(saplingBuilder().build("minecraft", "jungle_sapling"));
        register(saplingBuilder().build("minecraft", "acacia_sapling"));
        register(saplingBuilder().build("minecraft", "dark_oak_sapling"));
        ////////////////////
        ///    Bedrock   ///
        ////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(PropertyProviderCollections.UNBREAKABLE))
                .build("minecraft", "bedrock"));
        ////////////////////
        ///     Sand     ///
        ////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.5))
                        .add(blastResistance(2.5)))
                .build("minecraft", "sand"));
        ////////////////////
        ///   Red Sand   ///
        ////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.5))
                        .add(blastResistance(2.5)))
                .build("minecraft", "red_sand"));
        // TODO: Sand physics behavior
        ////////////////////
        ///    Gravel    ///
        ////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.6))
                        .add(blastResistance(3.0)))
                .build("minecraft", "gravel"));
        // TODO: Gravel physics behavior
        ////////////////////
        ///   Gold Ore   ///
        ////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(3.0))
                        .add(blastResistance(15.0)))
                .build("minecraft", "gold_ore"));
        ////////////////////
        ///   Iron Ore   ///
        ////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(3.0))
                        .add(blastResistance(15.0)))
                .build("minecraft", "iron_ore"));
        ////////////////////
        ///   Coal Ore   ///
        ////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(3.0))
                        .add(blastResistance(15.0)))
                .build("minecraft", "coal_ore"));
        ///////////////////
        ///    Logs     ///
        ///////////////////
        register(logBuilder().build("minecraft", "oak_log"));
        register(logBuilder().build("minecraft", "spruce_log"));
        register(logBuilder().build("minecraft", "birch_log"));
        register(logBuilder().build("minecraft", "jungle_log"));
        register(logBuilder().build("minecraft", "acacia_log"));
        register(logBuilder().build("minecraft", "dark_oak_log"));
        ///////////////////////
        ///    Log bark     ///
        ///////////////////////
        register(barkBuilder().build("minecraft", "oak_bark"));
        register(barkBuilder().build("minecraft", "spruce_bark"));
        register(barkBuilder().build("minecraft", "birch_bark"));
        register(barkBuilder().build("minecraft", "jungle_bark"));
        register(barkBuilder().build("minecraft", "acacia_bark"));
        register(barkBuilder().build("minecraft", "dark_oak_bark"));
        //////////////////
        ///   Leaves   ///
        //////////////////
        register(leavesBuilder().build("minecraft", "oak_leaves"));
        register(leavesBuilder().build("minecraft", "spruce_leaves"));
        register(leavesBuilder().build("minecraft", "birch_leaves"));
        register(leavesBuilder().build("minecraft", "jungle_leaves"));
        register(leavesBuilder().build("minecraft", "acacia_leaves"));
        register(leavesBuilder().build("minecraft", "dark_oak_leaves"));
        ////////////////////
        ///    Sponge    ///
        ////////////////////
        register(simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.6))
                                .add(blastResistance(3.0)))
                        .build("minecraft", "sponge"));
        ////////////////////////
        ///    Wet Sponge    ///
        ////////////////////////
        register(simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.6))
                                .add(blastResistance(3.0)))
                        .build("minecraft", "wet_sponge"));
        ////////////////////
        ///   Lapis Ore  ///
        ////////////////////
        register(simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(15.0)))
                        .build("minecraft", "lapis_ore"));
        ////////////////////
        ///  Lapis Block ///
        ////////////////////
        register(simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(15.0)))
                        .build("minecraft", "lapis_block"));
        ////////////////////
        ///   Dispenser  ///
        ////////////////////
        register(simpleBuilder()
                        .traits(LanternEnumTraits.FACING, LanternBooleanTraits.TRIGGERED)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.FACING, Direction.NORTH).get()
                                .withTrait(LanternBooleanTraits.TRIGGERED, false).get())
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.5))
                                .add(blastResistance(17.5)))
                        // .tileEntityType(() -> TileEntityTypes.DISPENSER)
                        .behaviors(pipeline -> pipeline
                                .add(new RotationPlacementBehavior()))
                        .build("minecraft", "dispenser"));
        ////////////////////
        ///   Sandstone  ///
        ////////////////////
        register(sandstoneBuilder().build("minecraft", "sandstone"));
        register(sandstoneBuilder().build("minecraft", "chiseled_sandstone"));
        register(sandstoneBuilder().build("minecraft", "smooth_sandstone"));
        register(sandstoneBuilder().build("minecraft", "red_sandstone"));
        register(sandstoneBuilder().build("minecraft", "chiseled_red_sandstone"));
        register(sandstoneBuilder().build("minecraft", "cut_red_sandstone"));
        /////////////////////
        ///   Note Block  ///
        /////////////////////
        register(simpleBuilder()
                .traits(LanternEnumTraits.INSTRUMENT, LanternIntegerTraits.NOTE, LanternBooleanTraits.POWERED)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.INSTRUMENT, LanternInstrumentType.HARP).get()
                        .withTrait(LanternIntegerTraits.NOTE, 0).get()
                        .withTrait(LanternBooleanTraits.POWERED, false).get())
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.8))
                        .add(blastResistance(4.0)))
                .behaviors(pipeline -> pipeline
                                .add(new NoteBlockInteractionBehavior()))
                .build("minecraft", "note_block"));
        ////////////////////
        ///     Beds     ///
        ////////////////////
        register(bedBuilder().build("minecraft", "white_bed"));
        register(bedBuilder().build("minecraft", "orange_bed"));
        register(bedBuilder().build("minecraft", "magenta_bed"));
        register(bedBuilder().build("minecraft", "light_blue_bed"));
        register(bedBuilder().build("minecraft", "yellow_bed"));
        register(bedBuilder().build("minecraft", "lime_bed"));
        register(bedBuilder().build("minecraft", "pink_bed"));
        register(bedBuilder().build("minecraft", "gray_bed"));
        register(bedBuilder().build("minecraft", "light_gray_bed"));
        register(bedBuilder().build("minecraft", "cyan_bed"));
        register(bedBuilder().build("minecraft", "purple_bed"));
        register(bedBuilder().build("minecraft", "blue_bed"));
        register(bedBuilder().build("minecraft", "brown_bed"));
        register(bedBuilder().build("minecraft", "green_bed"));
        register(bedBuilder().build("minecraft", "red_bed"));
        register(bedBuilder().build("minecraft", "black_bed"));
        ///////////////////////
        ///   Powered Rail  ///
        ///////////////////////
        register(simpleBuilder()
                .traits(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION, LanternBooleanTraits.POWERED)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION, LanternRailDirection.NORTH_SOUTH).get()
                        .withTrait(LanternBooleanTraits.POWERED, false).get())
                .itemType()
                .boundingBox(BoundingBoxes::rail)
                .properties(builder -> builder
                        .add(PASSABLE)
                        .add(hardness(0.7))
                        .add(blastResistance(3.5)))
                .build("minecraft", "powered_rail"));
        ////////////////////////
        ///   Detector Rail  ///
        ////////////////////////
        register(simpleBuilder()
                .traits(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION, LanternBooleanTraits.POWERED)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION, LanternRailDirection.NORTH_SOUTH).get()
                        .withTrait(LanternBooleanTraits.POWERED, false).get())
                .itemType()
                .boundingBox(BoundingBoxes::rail)
                .properties(builder -> builder
                        .add(PASSABLE)
                        .add(hardness(0.7))
                        .add(blastResistance(3.5)))
                .build("minecraft", "detector_rail"));
        // TODO: 29
        //////////////////
        ///   Cobweb   ///
        //////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(PASSABLE)
                        .add(hardness(4.0))
                        .add(blastResistance(20.0)))
                .build("minecraft", "cobweb"));
        ////////////////////////
        ///   Bushes/Grass   ///
        ////////////////////////
        register(replaceableBushBuilder().build("minecraft", "dead_bush"));
        register(replaceableBushBuilder().build("minecraft", "grass"));
        register(replaceableBushBuilder().build("minecraft", "fern"));
        // TODO: 33
        // TODO: 34
        ///////////////////
        ///     Wool    ///
        ///////////////////
        register(woolBuilder().build("minecraft", "white_wool"));
        register(woolBuilder().build("minecraft", "orange_wool"));
        register(woolBuilder().build("minecraft", "magenta_wool"));
        register(woolBuilder().build("minecraft", "light_blue_wool"));
        register(woolBuilder().build("minecraft", "yellow_wool"));
        register(woolBuilder().build("minecraft", "lime_wool"));
        register(woolBuilder().build("minecraft", "pink_wool"));
        register(woolBuilder().build("minecraft", "gray_wool"));
        register(woolBuilder().build("minecraft", "light_gray_wool"));
        register(woolBuilder().build("minecraft", "cyan_wool"));
        register(woolBuilder().build("minecraft", "purple_wool"));
        register(woolBuilder().build("minecraft", "blue_wool"));
        register(woolBuilder().build("minecraft", "brown_wool"));
        register(woolBuilder().build("minecraft", "green_wool"));
        register(woolBuilder().build("minecraft", "red_wool"));
        register(woolBuilder().build("minecraft", "black_wool"));
        // TODO: 36
        //////////////////
        ///  Flowers   ///
        //////////////////
        register(bushBuilder().build("minecraft", "dandelion"));
        register(bushBuilder().build("minecraft", "poppy"));
        register(bushBuilder().build("minecraft", "blue_orchid"));
        register(bushBuilder().build("minecraft", "allium"));
        register(bushBuilder().build("minecraft", "azure_bluet"));
        register(bushBuilder().build("minecraft", "red_tulip"));
        register(bushBuilder().build("minecraft", "orange_tulip"));
        register(bushBuilder().build("minecraft", "white_tulip"));
        register(bushBuilder().build("minecraft", "pink_tulip"));
        register(bushBuilder().build("minecraft", "oxeye_daisy"));
        //////////////////////////
        ///   Brown Mushroom   ///
        //////////////////////////
        register(simpleBuilder()
                .boundingBox(BoundingBoxes.bush())
                .properties(builder -> builder
                        .add(INSTANT_BROKEN)
                        .add(PASSABLE)
                        .add(lightEmission(1)))
                .build("minecraft", "brown_mushroom"));
        ////////////////////////
        ///   Red Mushroom   ///
        ////////////////////////
        register(simpleBuilder()
                .boundingBox(BoundingBoxes.bush())
                .properties(builder -> builder
                        .add(INSTANT_BROKEN)
                        .add(PASSABLE))
                .build("minecraft", "red_mushroom"));
        //////////////////////
        ///   Gold Block   ///
        //////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(3.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "gold_block"));
        //////////////////////
        ///   Iron Block   ///
        //////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(5.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "iron_block"));
        ///////////////////////////
        ///    Smooth Quartz    ///
        ///////////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "smooth_quartz"));
        /////////////////
        ///   Slabs   ///
        /////////////////
        register(slabBuilder()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "stone_slab"));
        register(slabBuilder()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "sandstone_slab"));
        register(slabBuilder()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "petrified_oak_slab"));
        register(slabBuilder()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "cobblestone_slab"));
        register(slabBuilder()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "brick_slab"));
        register(slabBuilder()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "stone_brick_slab"));
        register(slabBuilder()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "nether_brick_slab"));
        register(slabBuilder()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "quartz_slab"));
        register(slabBuilder()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "red_sandstone_slab"));
        register(slabBuilder()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "purpur_slab"));
        ////////////////////////
        ///   Wooden Slabs   ///
        ////////////////////////
        register(woodenSlabBuilder().build("minecraft", "oak_slab"));
        register(woodenSlabBuilder().build("minecraft", "spruce_slab"));
        register(woodenSlabBuilder().build("minecraft", "birch_slab"));
        register(woodenSlabBuilder().build("minecraft", "jungle_slab"));
        register(woodenSlabBuilder().build("minecraft", "acacia_slab"));
        register(woodenSlabBuilder().build("minecraft", "dark_oak_slab"));
        ///////////////////////
        ///   Brick Block   ///
        ///////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "bricks"));
        ///////////////
        ///   TNT   ///
        ///////////////
        register(simpleBuilder()
                .trait(LanternBooleanTraits.EXPLODE)
                .defaultState(state -> state
                        .withTrait(LanternBooleanTraits.EXPLODE, false).get())
                .itemType()
                .properties(builder -> builder
                        .add(INSTANT_BROKEN))
                .build("minecraft", "tnt"));
        /////////////////////
        ///   Bookshelf   ///
        /////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(1.5))
                        .add(blastResistance(7.5)))
                .build("minecraft", "bookshelf"));
        /////////////////////////////
        ///   Mossy Cobblestone   ///
        /////////////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "mossy_cobblestone"));
        ////////////////////
        ///   Obsidian   ///
        ////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(50.0))
                        .add(blastResistance(2000.0)))
                .build("minecraft", "obsidian"));
        //////////////////////
        ///   Wall Torch   ///
        //////////////////////
        register(simpleBuilder()
                .trait(LanternEnumTraits.HORIZONTAL_FACING)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.HORIZONTAL_FACING, Direction.NORTH).get())
                .properties(builder -> builder
                        .add(INSTANT_BROKEN))
                .boundingBox(BoundingBoxes::wallTorch)
                .build("minecraft", "wall_torch"));
        /////////////////
        ///   Torch   ///
        /////////////////
        register(simpleBuilder()
                .properties(builder -> builder
                        .add(INSTANT_BROKEN))
                .boundingBox(BoundingBoxes.torch())
                .itemType(builder -> builder
                        .behaviors(pipeline -> pipeline
                                .add(new TorchInteractionBehavior())))
                .build("minecraft", "torch"));
        //////////////
        ///  Fire  ///
        //////////////
        register(simpleBuilder()
                .properties(builder -> builder
                        .add(PropertyProviderCollections.PASSABLE)
                        .add(PropertyProviderCollections.INSTANT_BROKEN)
                        .add(lightEmission(15)))
                .boundingBox(BoundingBoxes.NULL)
                .build("minecraft", "fire"));
        /////////////////////
        ///  Mob Spawner  ///
        /////////////////////
        register(simpleBuilder()
                .properties(builder -> builder
                        .add(hardness(5.0))
                        .add(blastResistance(25.0)))
                .build("minecraft", "mob_spawner"));
        // TODO: Oak Stairs
        ////////////////////
        ///     Chest    ///
        ////////////////////
        register(chestBuilder().build("minecraft", "chest"));
        ///////////////////////////
        ///     Redstone Wire   ///
        ///////////////////////////
        register(simpleBuilder()
                .traits(LanternIntegerTraits.POWER,
                        LanternEnumTraits.REDSTONE_NORTH_CONNECTION,
                        LanternEnumTraits.REDSTONE_SOUTH_CONNECTION,
                        LanternEnumTraits.REDSTONE_EAST_CONNECTION,
                        LanternEnumTraits.REDSTONE_WEST_CONNECTION)
                .defaultState(state -> state
                        .withTrait(LanternIntegerTraits.POWER, 0).get()
                        .withTrait(LanternEnumTraits.REDSTONE_NORTH_CONNECTION, RedstoneConnectionType.NONE).get()
                        .withTrait(LanternEnumTraits.REDSTONE_SOUTH_CONNECTION, RedstoneConnectionType.NONE).get()
                        .withTrait(LanternEnumTraits.REDSTONE_EAST_CONNECTION, RedstoneConnectionType.NONE).get()
                        .withTrait(LanternEnumTraits.REDSTONE_WEST_CONNECTION, RedstoneConnectionType.NONE).get())
                .boundingBox(BoundingBoxes.NULL)
                .build("minecraft", "redstone_wire"));
        ///////////////////////
        ///   Diamond Ore   ///
        ///////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(3.0))
                        .add(blastResistance(5.0)))
                .build("minecraft", "diamond_ore"));
        /////////////////////////
        ///   Diamond Block   ///
        /////////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(5.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "diamond_block"));
        //////////////////////////
        ///   Crafting Table   ///
        //////////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(2.5))
                        .add(blastResistance(12.5)))
                .behaviors(pipeline -> pipeline
                        .add(new CraftingTableInteractionBehavior()))
                .build("minecraft", "crafting_table"));
        // TODO: Wheat
        ////////////////////
        ///   Farmland   ///
        ////////////////////
        register(simpleBuilder()
                .boundingBox(BoundingBoxes.farmland())
                .trait(LanternIntegerTraits.MOISTURE)
                .properties(builder -> builder
                        .add(hardness(0.6))
                        .add(blastResistance(3.0)))
                .defaultState(state ->
                        state.withTrait(LanternIntegerTraits.MOISTURE, 0).get())
                .build("minecraft", "farmland"));
        ////////////////////
        ///    Furnace   ///
        ////////////////////
        register(horizontalFacingBuilder()
                .traits(LanternBooleanTraits.LIT)
                .defaultState(state ->
                        state.withTrait(LanternBooleanTraits.LIT, false).get())
                .tileEntityType(() -> TileEntityTypes.FURNACE)
                .itemType()
                .behaviors(pipeline -> pipeline
                        .add(new OpenableContainerInteractionBehavior()))
                .properties(builder -> builder
                        .add(hardness(3.5))
                        .add(blastResistance(17.5))
                        .add(lightEmission((blockState, location, face) ->
                                blockState.getTraitValue(LanternBooleanTraits.LIT).get() ? 13 : 0)))
                .build("minecraft", "furnace"));
        ////////////////////////////
        /// Stone Pressure Plate ///
        ////////////////////////////
        register(pressurePlateBuilder().build("minecraft", "stone_pressure_plate"));
        //////////////////////////////
        /// Wooden Pressure Plates ///
        //////////////////////////////
        register(pressurePlateBuilder().build("minecraft", "oak_pressure_plate"));
        register(pressurePlateBuilder().build("minecraft", "spruce_pressure_plate"));
        register(pressurePlateBuilder().build("minecraft", "birch_pressure_plate"));
        register(pressurePlateBuilder().build("minecraft", "jungle_pressure_plate"));
        register(pressurePlateBuilder().build("minecraft", "acacia_pressure_plate"));
        register(pressurePlateBuilder().build("minecraft", "dark_oak_pressure_plate"));
        ////////////////////
        ///    Jukebox   ///
        ////////////////////
        register(simpleBuilder()
                .itemType()
                .traits(LanternBooleanTraits.HAS_RECORD)
                .defaultState(state -> state
                        .withTrait(LanternBooleanTraits.HAS_RECORD, false).get())
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .tileEntityType(() -> TileEntityTypes.JUKEBOX)
                .behaviors(pipeline -> pipeline
                        .add(new JukeboxInteractionBehavior()))
                .build("minecraft", "jukebox"));
        ////////////////////////
        ///  Carved Pumpkin  ///
        ////////////////////////
        register(pumpkinBuilder()
                .itemType(builder -> builder
                        .properties(properties -> properties
                                .add(equipmentType(EquipmentTypes.HEADWEAR))))
                .build("minecraft", "carved_pumpkin"));
        /////////////////
        ///  Pumpkin  ///
        /////////////////
        register(pumpkinBuilder().build("minecraft", "pumpkin"));
        //////////////////////
        ///   Netherrack   ///
        //////////////////////
        register(simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.4))
                                .add(blastResistance(2.0)))
                        .build("minecraft", "netherrack"));
        ////////////////////////
        ///  Jack o'Lantern  ///
        ////////////////////////
        register(pumpkinBuilder()
                .properties(builder -> builder
                        .add(lightEmission(15)))
                .build("minecraft", "jack_o_lantern"));
        ////////////////
        ///   Glass  ///
        ////////////////
        register(glassBuilder().build("minecraft", "glass"));
        register(glassBuilder().build("minecraft", "white_stained_glass"));
        register(glassBuilder().build("minecraft", "orange_stained_glass"));
        register(glassBuilder().build("minecraft", "magenta_stained_glass"));
        register(glassBuilder().build("minecraft", "light_blue_stained_glass"));
        register(glassBuilder().build("minecraft", "yellow_stained_glass"));
        register(glassBuilder().build("minecraft", "lime_stained_glass"));
        register(glassBuilder().build("minecraft", "pink_stained_glass"));
        register(glassBuilder().build("minecraft", "gray_stained_glass"));
        register(glassBuilder().build("minecraft", "light_gray_stained_glass"));
        register(glassBuilder().build("minecraft", "cyan_stained_glass"));
        register(glassBuilder().build("minecraft", "purple_stained_glass"));
        register(glassBuilder().build("minecraft", "blue_stained_glass"));
        register(glassBuilder().build("minecraft", "brown_stained_glass"));
        register(glassBuilder().build("minecraft", "green_stained_glass"));
        register(glassBuilder().build("minecraft", "red_stained_glass"));
        register(glassBuilder().build("minecraft", "black_stained_glass"));
        ///////////////////
        ///  Iron Bars  ///
        ///////////////////
        register(simpleBuilder()
                // TODO
                .itemType()
                .properties(builder -> builder
                        .add(hardness(5.0))
                        .add(blastResistance(10.0)))
                .build("minecraft", "iron_bars"));
        /////////////////////
        ///   End Stone   ///
        /////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(3.0))
                        .add(blastResistance(15.0)))
                .build("minecraft", "end_stone"));
        /////////////////////
        ///  Ender Chest  ///
        /////////////////////
        register(simpleBuilder()
                .trait(LanternEnumTraits.HORIZONTAL_FACING)
                .defaultState(state -> state.withTrait(LanternEnumTraits.HORIZONTAL_FACING, Direction.NORTH).get())
                .itemType()
                .tileEntityType(() -> TileEntityTypes.ENDER_CHEST)
                .properties(builder -> builder
                        .add(hardness(22.5))
                        .add(blastResistance(3000.0))
                        .add(lightEmission(7)))
                .boundingBox(BoundingBoxes.chest())
                .behaviors(pipeline -> pipeline
                        .add(new HorizontalRotationPlacementBehavior())
                        .add(new EnderChestInteractionBehavior()))
                .build("minecraft", "ender_chest"));
        /////////////////////
        /// Trapped Chest ///
        /////////////////////
        register(chestBuilder().build("minecraft", "trapped_chest"));
        ///////////////////////////////////////
        /// Weighted Pressure Plate (Light) ///
        ///////////////////////////////////////
        register(weightedPressurePlateBuilder().build("minecraft", "light_weighted_pressure_plate"));
        ///////////////////////////////////////
        /// Weighted Pressure Plate (Heavy) ///
        ///////////////////////////////////////
        register(weightedPressurePlateBuilder().build("minecraft", "heavy_weighted_pressure_plate"));
        ///////////////////////
        /// Redstone Block  ///
        ///////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(5.0))
                        .add(blastResistance(30.0)))
                .build("minecraft", "redstone_block"));
        ////////////////////
        ///  Quartz Ore  ///
        ////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(3.0))
                        .add(blastResistance(15.0)))
                .build("minecraft", "nether_quartz_ore"));
        ///////////////////
        ///    Hopper   ///
        ///////////////////
        register(simpleBuilder()
                .traits(LanternEnumTraits.HOPPER_FACING, LanternBooleanTraits.ENABLED)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.HOPPER_FACING, Direction.DOWN).get()
                        .withTrait(LanternBooleanTraits.ENABLED, false).get())
                .itemType()
                .properties(builder -> builder
                        .add(hardness(3.0))
                        .add(blastResistance(8.0)))
                .behaviors(pipeline -> pipeline
                        .add(new HopperPlacementBehavior()))
                .build("minecraft", "hopper"));
        //////////////////////
        ///  Quartz Block  ///
        //////////////////////
        register(quartzBlockBuilder().build("minecraft", "quartz_block"));
        ///////////////////////////////
        ///  Chiseled Quartz Block  ///
        ///////////////////////////////
        register(quartzBlockBuilder().build("minecraft", "chiseled_quartz_block"));
        ///////////////////////
        ///  Quartz Pillar  ///
        ///////////////////////
        register(quartzBlockBuilder()
                .trait(LanternEnumTraits.AXIS)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.AXIS, Axis.X).get())
                .behaviors(pipeline -> pipeline
                        .add(new AxisRotationPlacementBehavior()))
                .build("minecraft", "quartz_pillar"));
        ////////////////////
        ///    Dropper   ///
        ////////////////////
        register(simpleBuilder()
                .traits(LanternEnumTraits.FACING, LanternBooleanTraits.TRIGGERED)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.FACING, Direction.NORTH).get()
                        .withTrait(LanternBooleanTraits.TRIGGERED, false).get())
                .itemType()
                .properties(builder -> builder
                        .add(hardness(3.5))
                        .add(blastResistance(17.5)))
                // .tileEntityType(() -> TileEntityTypes.DROPPER)
                .behaviors(pipeline -> pipeline
                        .add(new RotationPlacementBehavior()))
                .build("minecraft", "dropper"));
        //////////////////////
        ///   Terracotta   ///
        //////////////////////
        register(terracottaBuilder().build("minecraft", "hardened_clay")); // TODO: The wiki was too fast?
        register(terracottaBuilder().build("minecraft", "white_terracotta"));
        register(terracottaBuilder().build("minecraft", "orange_terracotta"));
        register(terracottaBuilder().build("minecraft", "magenta_terracotta"));
        register(terracottaBuilder().build("minecraft", "light_blue_terracotta"));
        register(terracottaBuilder().build("minecraft", "yellow_terracotta"));
        register(terracottaBuilder().build("minecraft", "lime_terracotta"));
        register(terracottaBuilder().build("minecraft", "pink_terracotta"));
        register(terracottaBuilder().build("minecraft", "gray_terracotta"));
        register(terracottaBuilder().build("minecraft", "light_gray_terracotta"));
        register(terracottaBuilder().build("minecraft", "cyan_terracotta"));
        register(terracottaBuilder().build("minecraft", "purple_terracotta"));
        register(terracottaBuilder().build("minecraft", "blue_terracotta"));
        register(terracottaBuilder().build("minecraft", "brown_terracotta"));
        register(terracottaBuilder().build("minecraft", "green_terracotta"));
        register(terracottaBuilder().build("minecraft", "red_terracotta"));
        register(terracottaBuilder().build("minecraft", "black_terracotta"));
        //////////////////
        /// Glass Pane ///
        //////////////////
        register(glassBuilder().build("minecraft", "glass_pane"));
        register(glassBuilder().build("minecraft", "white_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "orange_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "magenta_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "light_blue_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "yellow_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "lime_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "pink_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "gray_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "light_gray_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "cyan_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "purple_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "blue_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "brown_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "green_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "red_stained_glass_pane"));
        register(glassBuilder().build("minecraft", "black_stained_glass_pane"));
        ////////////////////
        ///   Barrier    ///
        ////////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(PropertyProviderCollections.UNBREAKABLE))
                .build("minecraft", "barrier"));
        /////////////////////
        ///     Carpet    ///
        /////////////////////
        register(carpetBuilder().build("minecraft", "white_carpet"));
        register(carpetBuilder().build("minecraft", "orange_carpet"));
        register(carpetBuilder().build("minecraft", "magenta_carpet"));
        register(carpetBuilder().build("minecraft", "light_blue_carpet"));
        register(carpetBuilder().build("minecraft", "yellow_carpet"));
        register(carpetBuilder().build("minecraft", "lime_carpet"));
        register(carpetBuilder().build("minecraft", "pink_carpet"));
        register(carpetBuilder().build("minecraft", "gray_carpet"));
        register(carpetBuilder().build("minecraft", "light_gray_carpet"));
        register(carpetBuilder().build("minecraft", "cyan_carpet"));
        register(carpetBuilder().build("minecraft", "purple_carpet"));
        register(carpetBuilder().build("minecraft", "blue_carpet"));
        register(carpetBuilder().build("minecraft", "brown_carpet"));
        register(carpetBuilder().build("minecraft", "green_carpet"));
        register(carpetBuilder().build("minecraft", "red_carpet"));
        register(carpetBuilder().build("minecraft", "black_carpet"));
        ///////////////////
        ///   End Rod   ///
        ///////////////////
        register(simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.0))
                        .add(blastResistance(0.0))
                        .add(lightEmission(14)))
                .build("minecraft", "end_rod"));
        ///////////////////////
        ///  Shulker Boxes  ///
        ///////////////////////
        register(shulkerBoxBuilder().build("minecraft", "white_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "orange_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "magenta_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "light_blue_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "yellow_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "lime_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "pink_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "gray_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "light_gray_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "cyan_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "purple_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "blue_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "brown_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "green_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "red_shulker_box"));
        register(shulkerBoxBuilder().build("minecraft", "black_shulker_box"));

        // @formatter:on
    }

    private BlockTypeBuilder woolBuilder() {
        return simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.8))
                        .add(blastResistance(4.0)));
    }

    private BlockTypeBuilder replaceableBushBuilder() {
        return bushBuilder()
                .properties(builder -> builder
                        .add(replaceable(true)));
    }

    private BlockTypeBuilder bushBuilder() {
        return simpleBuilder()
                .itemType()
                .boundingBox(BoundingBoxes.bush())
                .properties(builder -> builder
                        .add(INSTANT_BROKEN)
                        .add(PASSABLE));
    }

    private BlockTypeBuilder bedBuilder() {
        return simpleBuilder()
                .itemType()
                .traits(LanternEnumTraits.HORIZONTAL_FACING, LanternEnumTraits.BED_PART, LanternBooleanTraits.OCCUPIED)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.HORIZONTAL_FACING, Direction.NORTH).get()
                        .withTrait(LanternEnumTraits.BED_PART, LanternBedPart.FOOT).get()
                        .withTrait(LanternBooleanTraits.OCCUPIED, false).get())
                .properties(builder -> builder
                        .add(hardness(0.2))
                        .add(blastResistance(1.0)));
    }

    private BlockTypeBuilder saplingBuilder() {
        return simpleBuilder()
                .itemType()
                .traits(LanternIntegerTraits.SAPLING_GROWTH_STAGE)
                .defaultState(state -> state
                        .withTrait(LanternIntegerTraits.SAPLING_GROWTH_STAGE, 0).get())
                .properties(builder -> builder
                        .add(PASSABLE)
                        .add(INSTANT_BROKEN));
    }

    private BlockTypeBuilder planksBuilder() {
        return simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(5.0))
                        .add(flammableInfo(5, 20)));
    }

    private BlockTypeBuilder simpleBuilder() {
        return builder()
                .behaviors(pipeline -> pipeline
                        .add(new BlockSnapshotProviderPlaceBehavior())
                        .add(new SimplePlacementBehavior())
                        .add(new SimpleBreakBehavior()));
        // TODO: Item drops?
    }

    private BlockTypeBuilder builder() {
        return new BlockTypeBuilderImpl();
    }

    private BlockTypeBuilder pressurePlateBuilder() {
        return simpleBuilder()
                .itemType()
                .traits(LanternBooleanTraits.POWERED)
                .boundingBox(BoundingBoxes::pressurePlate)
                .defaultState(state -> state
                        .withTrait(LanternBooleanTraits.POWERED, false).get())
                .properties(builder -> builder
                        .add(hardness(0.5))
                        .add(blastResistance(2.5)));
    }

    private BlockTypeBuilder weightedPressurePlateBuilder() {
        return simpleBuilder()
                .itemType()
                .traits(LanternIntegerTraits.POWER)
                .boundingBox(BoundingBoxes::pressurePlate)
                .defaultState(state -> state
                        .withTrait(LanternIntegerTraits.POWER, 0).get())
                .properties(builder -> builder
                        .add(hardness(0.5))
                        .add(blastResistance(2.5)));
    }

    private BlockTypeBuilder horizontalFacingBuilder() {
        return simpleBuilder()
                .traits(LanternEnumTraits.HORIZONTAL_FACING)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.HORIZONTAL_FACING, Direction.NORTH).get())
                .behaviors(pipeline -> pipeline
                        .add(new HorizontalRotationPlacementBehavior()));
    }

    private BlockTypeBuilder pumpkinBuilder() {
        return horizontalFacingBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(1.0))
                        .add(blastResistance(5.0)));
    }

    private BlockTypeBuilder carpetBuilder() {
        return simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.1))
                        .add(blastResistance(0.5)))
                .boundingBox(BoundingBoxes.carpet());
    }

    private BlockTypeBuilder terracottaBuilder() {
        return simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(1.25))
                        .add(blastResistance(7.0)));
    }

    private BlockTypeBuilder glassBuilder() {
        return simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.3))
                        .add(blastResistance(1.5)));
    }

    private BlockTypeBuilder sandstoneBuilder() {
        return simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.8))
                        .add(blastResistance(4.0)));
    }

    private BlockTypeBuilder quartzBlockBuilder() {
        return simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.8))
                        .add(blastResistance(2.4)));
    }

    /**
     * Generates a leaves block builder.
     *
     * @return The block type builder
     */
    private BlockTypeBuilder leavesBuilder() {
        return simpleBuilder()
                .itemType()
                .traits(LanternBooleanTraits.DECAYABLE, LanternBooleanTraits.CHECK_DECAY)
                .defaultState(state -> state
                        .withTrait(LanternBooleanTraits.DECAYABLE, true).get()
                        .withTrait(LanternBooleanTraits.CHECK_DECAY, true).get())
                .properties(builder -> builder
                        .add(hardness(0.2))
                        .add(blastResistance(1.0))
                        .add(flammableInfo(30, 60)));
    }

    private BlockTypeBuilder logBuilder() {
        return barkLogBuilder()
                .traits(LanternEnumTraits.AXIS)
                .behaviors(pipeline -> pipeline
                        .add(new AxisRotationPlacementBehavior()))
                .defaultState(state -> state.withTrait(LanternEnumTraits.AXIS, Axis.X).get());
    }

    private BlockTypeBuilder barkBuilder() {
        return barkLogBuilder();
    }

    /**
     * Generates a log block builder.
     *
     * @return The block type builder
     */
    private BlockTypeBuilder barkLogBuilder() {
        return simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(5.0))
                        .add(flammableInfo(5, 5)))
                .behaviors(pipeline -> pipeline
                        .add(new SimpleBlockDropsProviderBehavior(/* No items yet? */)));
    }

    private BlockTypeBuilder chestBuilder() {
        return builder()
                .traits(LanternEnumTraits.CHEST_ATTACHMENT, LanternEnumTraits.HORIZONTAL_FACING)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.HORIZONTAL_FACING, Direction.NORTH).get()
                        .withTrait(LanternEnumTraits.CHEST_ATTACHMENT, LanternChestAttachment.SINGLE).get())
                .itemType()
                .tileEntityType(() -> TileEntityTypes.CHEST)
                .boundingBox(BoundingBoxes::doubleChest)
                .properties(builder -> builder
                        .add(hardness(2.5))
                        .add(blastResistance(12.5)))
                .behaviors(pipeline -> pipeline
                        .add(new BlockSnapshotProviderPlaceBehavior())
                        .add(new ChestPlacementBehavior())
                        .add(new ChestInteractionBehavior())
                        .add(new SimpleBreakBehavior())
                        .add(new ChestBreakBehavior()));
        // TODO: Item drops?
    }

    private BlockTypeBuilder shulkerBoxBuilder() {
        return builder()
                .trait(LanternEnumTraits.FACING)
                .defaultState(state -> state.withTrait(LanternEnumTraits.FACING, Direction.UP).get())
                .itemType(builder -> builder
                        .keysProvider(collection -> collection
                                .register(LanternKeys.INVENTORY_SNAPSHOT, InventorySnapshot.EMPTY)
                        )
                        .maxStackQuantity(1)
                )
                .tileEntityType(() -> TileEntityTypes.SHULKER_BOX)
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .behaviors(pipeline -> pipeline
                        .add(new BlockSnapshotProviderPlaceBehavior())
                        .add(new SimplePlacementBehavior())
                        .add(new OppositeFaceDirectionalPlacementBehavior())
                        .add(new ShulkerBoxInteractionBehavior())
                        .add(new SimpleBreakBehavior()));
        // TODO: Item drops?
    }

    private BlockTypeBuilder slabBuilder() {
        return simpleBuilder()
                .traits(LanternEnumTraits.SLAB_PORTION)
                .boundingBox(BoundingBoxes::slab)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.SLAB_PORTION, LanternSlabPortion.BOTTOM).get())
                .itemType(builder -> builder
                        .behaviors(pipeline -> pipeline.add(new SlabItemInteractionBehavior())));
    }

    private BlockTypeBuilder woodenSlabBuilder() {
        return slabBuilder()
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(5.0)));
    }
}
