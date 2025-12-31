package com.loxil.helpstarter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BotListFetcher {

    /**
     * Fetches the JSON file from the API and parses it into a list of strings.
     * Note: the first line should be a comment; the JSON payload starts on the second line.
     *
     * @param username Minecraft username
     * @return list of strings
     */
    public static List<String> fetchListFromApi(String username) throws Exception {
        List<String> result = new ArrayList<String>();

        // TODO: Set your API base URL here
        String apiUrl = "https://real-api.com" + "/list/" + username;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("User-Agent", "HelpStarter/0.1.0 (Minecraft Forge 1.8.9)");

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
     * Full flow: get username, fetch data from API.
     *
     * @return the list of available bots
     */
    public static List<String> fetchList() throws Exception {
        String username = MinecraftUtils.getUsername();
        List<String> list = fetchListFromApi(username);
        return list;
    }
}
