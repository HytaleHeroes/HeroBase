package gg.hytaleheroes.herobase.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerFirstJoinTrackerFile extends BlockingDiskFile {
    public static String MAIN_PATH = Constants.UNIVERSE_PATH.resolve("HeroBase").toAbsolutePath().toString();
    public static String FILE_PATH = MAIN_PATH + File.separator + "Joined.json";

    private Set<UUID> tracker;

    public PlayerFirstJoinTrackerFile() {
        super(Path.of(FILE_PATH));
        this.tracker = ConcurrentHashMap.newKeySet();

        var file = new File(MAIN_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        if (!Files.exists(Path.of(FILE_PATH))) {
            try {
                Files.createFile(Path.of(MAIN_PATH));
            } catch (IOException e) {

            }
        }
    }

    @Override
    protected void read(BufferedReader bufferedReader) {
        var rootElement = JsonParser.parseReader(bufferedReader);
        if (rootElement == null || !rootElement.isJsonObject()) return;
        var root = rootElement.getAsJsonObject();
        JsonArray valuesArray = root.getAsJsonArray("Values");
        if (valuesArray == null) return;
        this.tracker = ConcurrentHashMap.newKeySet();
        valuesArray.forEach(jsonElement -> {
            var playerObj = jsonElement.getAsString();
            this.tracker.add(UUID.fromString(playerObj));
        });
    }

    @Override
    protected void write(BufferedWriter bufferedWriter) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray valuesArray = new JsonArray();
        for (UUID playerUuid : this.tracker) {
            valuesArray.add(new JsonPrimitive(playerUuid.toString()));
        }
        root.add("Values", valuesArray);
        bufferedWriter.write(root.toString());
    }

    @Override
    protected void create(BufferedWriter bufferedWriter) throws IOException {
        this.tracker = ConcurrentHashMap.newKeySet();
        write(bufferedWriter);
    }

    public Set<UUID> getJoined() {
        return this.tracker;
    }

    public boolean add(UUID uuid) {
        return this.tracker.add(uuid);
    }
}