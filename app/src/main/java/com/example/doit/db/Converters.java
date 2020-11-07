package com.example.doit.db;

import androidx.room.TypeConverter;

import com.example.doit.model.AnswerFireStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Converters
{
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static List<String> fromString(String value) {
        if(value == null)
            return null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(value, String[].class));
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        if(list == null)
            return null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(list);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static List<AnswerFireStore> stringToAnswers(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<AnswerFireStore>>() {}.getType();
        List<AnswerFireStore> measurements = gson.fromJson(json, type);
        return measurements;
    }

    @TypeConverter
    public static String answersToString(List<AnswerFireStore> list) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<AnswerFireStore>>() {}.getType();
        String json = gson.toJson(list, type);
        return json;
    }
}