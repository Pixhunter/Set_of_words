package com.pixhunter;

public class VariablesConfig {
    // Params for SQLite database
    public static final String DB_NAME = "Words.db";
    public static final String DB_PATH = "/data/data/com.pixhunter.setofwords_the_lastes/" + DB_NAME;
    public static final String SQL_QUERY_LIKE = "SELECT LETTERS FROM ALLWORDS WHERE LETTERS LIKE ?";

    // Params for words view in app
    public static int ALL_COUNT_WORDS = 63;
    public static int COLUMN_WIDTH = 3;
}
