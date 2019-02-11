package com.example.basediffadapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSON {
    private static final Gson GSON = new GsonBuilder().
            registerTypeAdapter(Double.class, new JsonSerializer<Double>() {

                @Override
                public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                    if (src == src.longValue())
                        return new JsonPrimitive(src.longValue());
                    return new JsonPrimitive(src);
                }
            })
            .disableHtmlEscaping()
            .create();

    public static String toJSONString(Object obj) {
        try {
            return GSON.toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T[] parseArray(String json, Class<T> clazz) {
        try {
            JsonObject[] jsonObjects = GSON.fromJson(json, new TypeToken<JsonObject[]>() {
            }.getType());
            List<T> list = new ArrayList<>();
            for (JsonObject jsonObject : jsonObjects) {
                list.add(GSON.fromJson(jsonObject, clazz));
            }
            T[] ts = (T[]) Array.newInstance(clazz, list.size());
            return list.toArray(ts);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}