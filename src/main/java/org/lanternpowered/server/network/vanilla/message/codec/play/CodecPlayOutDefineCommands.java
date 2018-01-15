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

import io.netty.handler.codec.CodecException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.command.SuggestionType;
import org.lanternpowered.server.network.vanilla.command.argument.ArgumentAndType;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDefineCommands;
import org.lanternpowered.server.network.vanilla.command.Node;
import org.lanternpowered.server.network.vanilla.command.RootNode;
import org.lanternpowered.server.network.vanilla.command.ArgumentNode;
import org.lanternpowered.server.network.vanilla.command.LiteralNode;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public final class CodecPlayOutDefineCommands implements Codec<MessagePlayOutDefineCommands> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutDefineCommands message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        final RootNode rootNode = message.getRootNode();

        // Collect all the commands nested within the root node
        final Object2IntMap<Node> nodeToIndexMap = new Object2IntOpenHashMap<>();
        final List<Node> nodes = new ArrayList<>();
        collectChildren(rootNode, nodeToIndexMap, nodes);

        // Write all the nodes
        buf.writeVarInt(nodes.size());
        for (Node node : nodes) {
            byte flags = 0;
            final Node redirect = node.getRedirect();
            if (redirect != null) {
                flags |= 0x8;
            }
            final String command = node.getCommand();
            if (command != null) {
                flags |= 0x4;
            }
            if (node instanceof ArgumentNode) {
                final ArgumentNode argumentNode = (ArgumentNode) node;
                flags |= 0x2;
                if (argumentNode.getCustomSuggestions() != null) {
                    flags |= 0x10;
                }
            } else if (node instanceof LiteralNode) {
                flags |= 0x1;
            }
            // Writes the flags
            buf.writeByte(flags);
            // Write the children indexes
            final List<Node> children = node.getChildren();
            buf.writeVarInt(children.size()); // The amount of children
            children.forEach(child -> buf.writeVarInt(nodeToIndexMap.getInt(child)));
            // Write the redirect node index
            if (redirect != null) {
                buf.writeVarInt(nodeToIndexMap.getInt(redirect));
            }
            if (node instanceof ArgumentNode) {
                final ArgumentNode argumentNode = (ArgumentNode) node;
                final ArgumentAndType argumentAndType = argumentNode.getArgumentAndType();
                buf.writeString(argumentNode.getName());
                // Write the type of the argument
                buf.writeString(argumentAndType.getType().getId());
                // Write extra argument flags
                argumentAndType.getType().getCodec().encode(buf, argumentAndType.getArgument());
                final SuggestionType suggestions = argumentNode.getCustomSuggestions();
                if (suggestions != null) {
                    buf.writeString(suggestions.getId());
                }
            } else if (node instanceof LiteralNode) {
                final LiteralNode literalNode = (LiteralNode) node;
                buf.writeString(literalNode.getLiteral());
            }
        }
        buf.writeVarInt(0); // The root should always be on index 0
        return buf;
    }

    private static void collectChildren(Node node, Object2IntMap<Node> nodeToIndexMap, List<Node> nodes) {
        final int index = nodeToIndexMap.size();
        if (nodeToIndexMap.putIfAbsent(node, index) == null) {
            nodes.add(node);
        }
        final Node redirect = node.getRedirect();
        if (redirect != null) {
            collectChildren(redirect, nodeToIndexMap, nodes);
        }
        node.getChildren().forEach(child -> collectChildren(child, nodeToIndexMap, nodes));
    }
}
