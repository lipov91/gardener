package com.survivingwithandroid.actionbartabnavigation.parsers;

import com.survivingwithandroid.actionbartabnavigation.model.Flower;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lipov91 on 2015-07-06.
 */
public class FlowerXMLParser {

    static boolean inDataItemTag = false;
    static String currentTagName = "";
    static Flower flower = null;
    static List<Flower> flowerList = new ArrayList<Flower>();
    static XmlPullParserFactory factory;
    static XmlPullParser parser;
    static int eventType;

    public static List<Flower> parseFeed(String content) {

        try {

            inDataItemTag = false;
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            parser.setInput(new StringReader(content));
            eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {

                    case XmlPullParser.START_TAG:

                        currentTagName = parser.getName();

                        if (currentTagName.equals("product")) {

                            inDataItemTag = true;
                            flower = new Flower();
                            flowerList.add(flower);
                        }

                        break;

                    case XmlPullParser.END_TAG:

                        if (parser.getName().equals("product")) {

                            inDataItemTag = false;
                        }

                        currentTagName = "";

                        break;

                    case XmlPullParser.TEXT:

                        if (inDataItemTag && flower != null) {

                            if (currentTagName.equals("productId")) {

                                flower.setProductId(Integer.parseInt(parser.getText()));

                            } else if (currentTagName.equals("name")) {

                                flower.setName(parser.getText());

                            } else if (currentTagName.equals("instructions")) {

                                flower.setInstructions(parser.getText());

                            } else if (currentTagName.equals("category")) {

                                flower.setCategory(parser.getText());

                            } else if (currentTagName.equals("price")) {

                                flower.setPrice(Double.parseDouble(parser.getText()));

                            } else if (currentTagName.equals("photo")) {

                                flower.setPhoto(parser.getText());

                            }
                        }

                        break;
                }

                eventType = parser.next();
            }

            return flowerList;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }
}
