package com.loxil.helpstarter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BotListFetcher {

    // TODO: Set the API base URL here
    private static final String API_BASE_URL = "https://real-api.com";

    /**
     * Fetches the JSON file from the API and parses it into a list of strings.
     * Note: the first line should be a comment; the JSON payload starts on the second line.
     *
     * @param username Minecraft username
     * @return list of strings
     */
    public static List<String> fetchListFromApi(String username) throws Exception {
        List<String> result = new ArrayList<String>();

        String apiUrl = API_BASE_URL + "/list/" + username;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("User-Agent", "HelpStarter/0.2.1");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("Failed to fetch API data: HTTP " + responseCode);
        }

        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        try {
            // Skip the first line
            reader.readLine();

            // Read the remaining content
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        } finally {
            reader.close();
            connection.disconnect();
        }

        // Parse JSON
        String jsonContent = jsonBuilder.toString();
        JsonElement element = new JsonParser().parse(jsonContent);

        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement item : array) {
                if (item.isJsonPrimitive()) {
                    result.add(item.getAsString());
                } else {
                    result.add(item.toString());
                }
            }
        }
        else {
            throw new IllegalStateException("Expected JSON array but got: " + element);
        }
        
        return result;
    }

    /**
     * Full flow: get client username and fetch data from API.
     *
     * @return the list of available bots
     */
    public static List<String> fetchList() throws Exception {
        String username = MinecraftUtils.getUsername();
        List<String> list = fetchListFromApi(username);
        return list;
    }

    /**
     * Fetches all bot usernames from the API.
     *
     * Prefer: {@link #fetchAllBotsClassified(String) fetchAllBotsClassified(username)}
     *
     * @return List of bot usernames
     */
    public static List<String> fetchAllBots() throws Exception {
        List<String> usernames = new ArrayList<String>();

        String apiUrl = API_BASE_URL + "/list";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("User-Agent", "HelpStarter/0.2.1");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("Failed to fetch API data: HTTP " + responseCode);
        }

        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        try {
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        } finally {
            reader.close();
            connection.disconnect();
        }

        // Parse JSON
        String jsonContent = jsonBuilder.toString();
        JsonElement element = new JsonParser().parse(jsonContent);

        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement item : array) {
                if (item.isJsonObject()) {
                    JsonObject obj = item.getAsJsonObject();
                    if (obj.has("username")) {
                        usernames.add(obj.get("username").getAsString());
                    }
                }
            }
        } else {
            throw new IllegalStateException("Expected JSON array but got: " + element);
        }

        return usernames;
    }

    /**
     * Classification result for bots based on user access and availability.
     */
    public static class BotClassification {
        public final List<String> unavailable;  // User cannot use these bots
        public final List<String> busy;         // User can use but currently in party
        public final List<String> available;    // User can use and currently free

        public BotClassification() {
            this.unavailable = new ArrayList<String>();
            this.busy = new ArrayList<String>();
            this.available = new ArrayList<String>();
        }
    }

    /**
     * Fetches all bots and classifies them based on user access and availability.
     * 
     * Classification rules:
     * - Unavailable: whitelist without username, or blacklist with username
     * - Busy: user can access but in_party is true
     * - Available: user can access and in_party is false
     *
     * @param username The username to check access for
     * @return BotClassification containing three categorized lists
     */
    public static BotClassification fetchAllBotsClassified(String username) throws Exception {
        BotClassification result = new BotClassification();

        String apiUrl = API_BASE_URL + "/list";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("User-Agent", "HelpStarter/0.2.1");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("Failed to fetch API data: HTTP " + responseCode);
        }

        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        try {
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        } finally {
            reader.close();
            connection.disconnect();
        }

        // Parse JSON
        String jsonContent = jsonBuilder.toString();
        JsonElement element = new JsonParser().parse(jsonContent);

        if (!element.isJsonArray()) {
            throw new IllegalStateException("Expected JSON array but got: " + element);
        }

        JsonArray array = element.getAsJsonArray();
        String usernameLower = username.toLowerCase();

        for (JsonElement item : array) {
            if (!item.isJsonObject()) continue;

            JsonObject obj = item.getAsJsonObject();
            
            // Validate required fields
            if (!obj.has("username")) {
                throw new IllegalStateException("Bot object missing required field: username");
            }
            if (!obj.has("list_type")) {
                throw new IllegalStateException("Bot object missing required field: list_type");
            }
            if (!obj.has("in_party")) {
                throw new IllegalStateException("Bot object missing required field: in_party");
            }
            if (!obj.has("list") || !obj.get("list").isJsonArray()) {
                throw new IllegalStateException("Bot object missing required field or invalid type: list");
            }

            String botName = obj.get("username").getAsString();
            String listType = obj.get("list_type").getAsString();
            boolean inParty = obj.get("in_party").getAsBoolean();
            JsonArray list = obj.get("list").getAsJsonArray();

            // Check if username is in the bot's list
            boolean userInList = false;
            for (JsonElement listItem : list) {
                if (listItem.isJsonPrimitive() && listItem.getAsString().equalsIgnoreCase(usernameLower)) {
                    userInList = true;
                    break;
                }
            }

            // Determine if user can access this bot
            boolean canAccess;
            if (listType.equals("whitelist")) {
                canAccess = userInList;  // Whitelist: must be in list
            } else {
                canAccess = !userInList; // Blacklist: must NOT be in list
            }

            // Classify the bot
            if (!canAccess) {
                result.unavailable.add(botName);
            } else if (inParty) {
                result.busy.add(botName);
            } else {
                result.available.add(botName);
            }
        }

        return result;
    }
}
