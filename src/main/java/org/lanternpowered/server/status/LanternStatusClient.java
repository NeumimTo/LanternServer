package org.lanternpowered.server.status;

import java.net.InetSocketAddress;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.status.StatusClient;

public class LanternStatusClient implements StatusClient {

    private final InetSocketAddress address;
    private final Optional<InetSocketAddress> virtualHost;
    private final MinecraftVersion version;

    public LanternStatusClient(InetSocketAddress address, MinecraftVersion version, @Nullable InetSocketAddress virtualHost) {
        this.virtualHost = Optional.ofNullable(virtualHost);
        this.address = address;
        this.version = version;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.address;
    }

    @Override
    public MinecraftVersion getVersion() {
        return this.version;
    }

    @Override
    public Optional<InetSocketAddress> getVirtualHost() {
        return this.virtualHost;
    }
}