package com.kaboomroads.molecraft.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.UUID;

public record Skin(String value, String signature) {

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }

    private static final String SKIN_DATA_UUID_DOWNLOAD_URL =
            "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    private static final String PLAYER_UUID_FROM_USERNAME_URL = "https://api.mojang.com/users/profiles/minecraft/%s";

    private static UUID toRealUUID(String mojangUUID) {
        String least = mojangUUID.substring(0, 16);
        String most = mojangUUID.substring(16);
        return new UUID(Long.parseUnsignedLong(least, 16), Long.parseUnsignedLong(most, 16));
    }

    public static Skin download(String username) {
        String url = String.format(PLAYER_UUID_FROM_USERNAME_URL, username);
        try {
            URLConnection connection = new URI(url).toURL().openConnection();
            InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
            JsonObject jsonObject = (JsonObject) JsonParser.parseReader(inputStream);

            String id = jsonObject.get("id").getAsString();
            return downloadFromUUIDString(id);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Skin downloadFromUUIDString(String uuid) {
        String url = String.format(SKIN_DATA_UUID_DOWNLOAD_URL, uuid);
        try {
            URLConnection connection = new URI(url).toURL().openConnection();
            InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());

            JsonObject jsonObject = (JsonObject) JsonParser.parseReader(inputStream);

            JsonArray properties = jsonObject.getAsJsonArray("properties");
            JsonObject property = properties.get(0).getAsJsonObject();

            return new Skin(property.get("value").getAsString(), property.get("signature").getAsString());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Skin download(UUID id) {
        String url = String.format(SKIN_DATA_UUID_DOWNLOAD_URL, id.toString().replace("-", ""));
        try {
            URLConnection connection = new URI(url).toURL().openConnection();
            InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());

            JsonObject jsonObject = (JsonObject) JsonParser.parseReader(inputStream);

            JsonArray properties = jsonObject.getAsJsonArray("properties");
            JsonObject property = properties.get(0).getAsJsonObject();

            return new Skin(property.get("value").getAsString(), property.get("signature").getAsString());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
