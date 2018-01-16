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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import com.flowpowered.math.vector.Vector3f;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.objects.Types;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnParticle;

public final class CodecPlayOutSpawnParticle implements Codec<MessagePlayOutSpawnParticle> {

    private final static int BASE_LENGTH = Integer.BYTES * 2 + Byte.BYTES + Float.BYTES * 7;

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutSpawnParticle message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer(BASE_LENGTH);
        buf.writeInteger(message.getParticleId());
        buf.writeBoolean(message.isLongDistance());
        final Vector3f position = message.getPosition();
        buf.writeFloat(position.getX());
        buf.writeFloat(position.getY());
        buf.writeFloat(position.getZ());
        final Vector3f offset = message.getOffset();
        buf.writeFloat(offset.getX());
        buf.writeFloat(offset.getY());
        buf.writeFloat(offset.getZ());
        buf.writeFloat(message.getData());
        buf.writeInteger(message.getCount());
        final MessagePlayOutSpawnParticle.Data extra = message.getExtra();
        if (extra != null) {
            if (extra instanceof MessagePlayOutSpawnParticle.ItemData) {
                buf.write(Types.ITEM_STACK, ((MessagePlayOutSpawnParticle.ItemData) extra).getItemStack());
            } else if (extra instanceof MessagePlayOutSpawnParticle.BlockData) {
                buf.writeVarInt(((MessagePlayOutSpawnParticle.BlockData) extra).getBlockState());
            } else if (extra instanceof MessagePlayOutSpawnParticle.DustData) {
                final MessagePlayOutSpawnParticle.DustData data = (MessagePlayOutSpawnParticle.DustData) extra;
                buf.writeFloat(data.getRed());
                buf.writeFloat(data.getGreen());
                buf.writeFloat(data.getBlue());
                buf.writeFloat(data.getScale());
            } else {
                throw new EncoderException("Unsupported extra data type: " + extra.getClass().getName());
            }
        }
        return buf;
    }
}
