package com.survivingwithandroid.actionbartabnavigation.database;

import android.provider.BaseColumns;

/**
 * Created by lipov91 on 2015-07-23.
 */
public final class FlowersDatabase {

    private FlowersDatabase() {}


    public static final class Flowers implements BaseColumns {

        private Flowers() {}

        public static final String FLOWERS_TABLE_NAME = "table_flowers";
        public static final String FLOWER_NAME = "flower_name";
        public static final String FLOWER_CATEGORY_ID = "flower_category_id";
        public static final String DEFAULT_SORT_ORDER = "flower_name ASC";
    }


    public static final class Categories implements BaseColumns {

        private Categories() {}

        public static final String CATEGORIES_TABLE_NAME = "table_categories";
        public static final String CATEGORY_NAME = "category_name";
        public static final String DEFAULT_SORT_ORDER = "category_name ASC";
    }
}
