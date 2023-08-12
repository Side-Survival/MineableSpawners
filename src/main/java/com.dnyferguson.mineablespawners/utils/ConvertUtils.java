package com.dnyferguson.mineablespawners.utils;

import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertUtils {

    public static final Gson gson = new Gson();
    private static final Pattern HEX_PATTERN = Pattern.compile("&(#[a-f0-9]{6})", 2);

    public static List<String> color(List<String> input) {
        for (int i = 0; i < input.size(); i++) {
            input.set(i, color(input.get(i)));
        }

        return input;
    }

    public static String[] color(String[] input) {
        for (int i = 0; i < input.length; i++) {
            input[i] = color(input[i]);
        }

        return input;
    }

    public static String color(String input) {
        Matcher m = HEX_PATTERN.matcher(input);
        try {
            ChatColor.class.getDeclaredMethod("of", new Class[]{String.class});
            while (m.find())
                input = input.replace(m.group(), ChatColor.of(m.group(1)).toString());
        } catch (Exception e) {
            while (m.find())
                input = input.replace(m.group(), "");
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
