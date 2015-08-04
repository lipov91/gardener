package com.survivingwithandroid.actionbartabnavigation.parsers;

import com.survivingwithandroid.actionbartabnavigation.model.Flower;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lipov91 on 2015-07-06.
 */
public class FlowerJSONParser {

    static JSONArray jsonArray;
    static JSONObject jsonObject;
    static List<Flower> flowers;
    static Flower flower;

    public static List<Flower> parseFeed(String content) {

        try {

            jsonArray = new JSONArray(content);
            flowers = new ArrayList<Flower>();

            for (int i = 0; i < jsonArray.length(); i++) {

                jsonObject = jsonArray.getJSONObject(i);
                flower = new Flower();

                flower.setProductId(jsonObject.getInt("productId"));
                flower.setName(jsonObject.getString("name"));
                flower.setCategory(jsonObject.getString("category"));
                flower.setInstructions(jsonObject.getString("instructions"));
                flower.setPrice(jsonObject.getDouble("price"));
                flower.setPhoto(jsonObject.getString("photo"));

                flowers.add(flower);
            }

            return flowers;

        } catch (JSONException e) {

            e.printStackTrace();

            return null;
        }
    }
}
