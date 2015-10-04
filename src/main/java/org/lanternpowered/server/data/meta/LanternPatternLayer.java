package org.lanternpowered.server.data.meta;

import static org.spongepowered.api.data.DataQuery.of;

import java.util.Optional;

import org.spongepowered.api.Game;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.service.persistence.DataBuilder;
import org.spongepowered.api.service.persistence.InvalidDataException;

public final class LanternPatternLayer implements PatternLayer {

    private static final DataQuery BANNER_SHAPE = of("BannerShapeId");
    private static final DataQuery DYE_COLOR = of("DyeColor");

    private final BannerPatternShape shape;
    private final DyeColor color;

    public LanternPatternLayer(BannerPatternShape shape, DyeColor color) {
        this.shape = shape;
        this.color = color;
    }

    @Override
    public BannerPatternShape getShape() {
        return this.shape;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer().set(BANNER_SHAPE, this.shape.getId()).set(DYE_COLOR, this.color.getName());
    }

    public static class Builder implements DataBuilder<PatternLayer> {

        private final Game game;

        public Builder(Game game) {
            this.game = game;
        }

        @Override
        public Optional<PatternLayer> build(DataView container) throws InvalidDataException {
            String bannerShape = container.getString(BANNER_SHAPE).orElse(null);
            String dyeColor = container.getString(DYE_COLOR).orElse(null);
            if (bannerShape == null || dyeColor == null) {
                return Optional.empty();
            }
            BannerPatternShape shape = this.game.getRegistry().getType(BannerPatternShape.class, bannerShape).orElse(null);
            DyeColor color = this.game.getRegistry().getType(DyeColor.class, dyeColor).orElse(null);
            if (shape == null || color == null) {
                return Optional.empty();
            }
            return Optional.of(new LanternPatternLayer(shape, color));
        }
    }

}