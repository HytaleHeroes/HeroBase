package gg.hytaleheroes.herobase.extra.profile;

import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.setup.AssetFinalize;
import com.hypixel.hytale.protocol.packets.setup.AssetInitialize;
import com.hypixel.hytale.protocol.packets.setup.AssetPart;
import com.hypixel.hytale.protocol.packets.setup.RequestCommonAssetsRebuild;
import com.hypixel.hytale.server.core.asset.common.CommonAsset;
import com.hypixel.hytale.server.core.asset.common.CommonAssetRegistry;
import com.hypixel.hytale.server.core.io.PacketHandler;
import gg.hytaleheroes.herobase.HeroBase;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class PlayerProfileAsset extends CommonAsset {
    private static final String PATH = "UI/Custom/Pages/Profile/Profile.png";

    byte[] data;

    public PlayerProfileAsset(@Nullable byte[] bytes) {
        super(PATH, "00DEADBEEF000000000000000000000000000013370000000000000000000000", bytes);
        this.data = bytes;
    }

    @Override
    protected CompletableFuture<byte[]> getBlob0() {
        return CompletableFuture.completedFuture(data);
    }

    public static CommonAsset empty() {
        return CommonAssetRegistry.getByName(PATH);
    }

    public static void send(PacketHandler handler, CommonAsset asset) {
        byte[] allBytes = asset.getBlob().join();
        byte[][] parts = ArrayUtil.split(allBytes, 2621440);
        Packet[] packets = new Packet[2 + parts.length];
        packets[0] = new AssetInitialize(asset.toPacket(), allBytes.length);

        for(int partIndex = 0; partIndex < parts.length; ++partIndex) {
            packets[1 + partIndex] = new AssetPart(parts[partIndex]);
        }

        packets[packets.length - 1] = new AssetFinalize();
        handler.write(packets);
        handler.writeNoCache(new RequestCommonAssetsRebuild());
    }

    public static PlayerProfileAsset load(String username) {
        var folder = HeroBase.get().getDataDirectory().resolve("profile_caches");
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Path file = folder.resolve(username + ".png");
        if (Files.exists(file)) {
            try {
                return new PlayerProfileAsset(Files.readAllBytes(file));
            } catch (IOException ignored) {
            }
        }

        String encoded = URLEncoder.encode(username, StandardCharsets.UTF_8);
        URI uri = URI.create("https://hyvatar.io/render/" + encoded + "?size=256");

        try {
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", "HeroBase");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream in = conn.getInputStream()) {
                    byte[] bytes = in.readAllBytes();
                    try {
                        Files.write(file, bytes);
                    } catch (IOException ignored) {
                    }
                    return new PlayerProfileAsset(bytes);
                }
            }
        } catch (IOException ignored) {
        }

        CommonAsset fallback = CommonAssetRegistry.getByName(PATH);
        if (fallback != null) {
            return new PlayerProfileAsset(fallback.getBlob().join());
        }

        return new PlayerProfileAsset(null);
    }

    public static void deleteCache(String username) {
        var folder = HeroBase.get().getDataDirectory().resolve("profile_caches");
        Path file = folder.resolve(username + ".png");
        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
        }
    }
}
