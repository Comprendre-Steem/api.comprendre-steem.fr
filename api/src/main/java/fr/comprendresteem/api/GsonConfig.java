package fr.comprendresteem.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonConfig {

	private static Gson GSON = GsonConfig.getBuilder().create();

	public static Gson GSON() {
		return GSON;
	}

    public static synchronized GsonBuilder getBuilder() {
    	GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    	return builder;
    }

}