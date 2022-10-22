package io.github.palexdev.sessionmanager.utils.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.Reader;
import java.lang.reflect.Type;

public class GsonInstance {
	//================================================================================
	// Properties
	//================================================================================
	private static final Gson gson;

	//================================================================================
	// Initialization
	//================================================================================
	static {
		gson = new GsonBuilder()
				.addSerializationExclusionStrategy(ExcludeAnnotationStrategy.instance())
				.setPrettyPrinting()
				.create();
	}

	//================================================================================
	// Constructors
	//================================================================================
	private GsonInstance() {
	}

	//================================================================================
	// Getters
	//================================================================================
	public static Gson gson() {
		return gson;
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	public static <T> TypeAdapter<T> getAdapter(TypeToken<T> type) {
		return gson.getAdapter(type);
	}

	public static <T> TypeAdapter<T> getDelegateAdapter(TypeAdapterFactory skipPast, TypeToken<T> type) {
		return gson.getDelegateAdapter(skipPast, type);
	}

	public static <T> TypeAdapter<T> getAdapter(Class<T> type) {
		return gson.getAdapter(type);
	}

	public static JsonElement toJsonTree(Object src) {
		return gson.toJsonTree(src);
	}

	public static JsonElement toJsonTree(Object src, Type typeOfSrc) {
		return gson.toJsonTree(src, typeOfSrc);
	}

	public static String toJson(Object src) {
		return gson.toJson(src);
	}

	public static String toJson(Object src, Type typeOfSrc) {
		return gson.toJson(src, typeOfSrc);
	}

	public static void toJson(Object src, Appendable writer) throws JsonIOException {
		gson.toJson(src, writer);
	}

	public static void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
		gson.toJson(src, typeOfSrc, writer);
	}

	public static void toJson(Object src, Type typeOfSrc, JsonWriter writer) throws JsonIOException {
		gson.toJson(src, typeOfSrc, writer);
	}

	public static String toJson(JsonElement jsonElement) {
		return gson.toJson(jsonElement);
	}

	public static void toJson(JsonElement jsonElement, Appendable writer) throws JsonIOException {
		gson.toJson(jsonElement, writer);
	}

	public static void toJson(JsonElement jsonElement, JsonWriter writer) throws JsonIOException {
		gson.toJson(jsonElement, writer);
	}

	public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
		return gson.fromJson(json, classOfT);
	}

	public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
		return gson.fromJson(json, typeOfT);
	}

	public static <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
		return gson.fromJson(json, classOfT);
	}

	public static <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
		return gson.fromJson(json, typeOfT);
	}

	public static <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
		return gson.fromJson(reader, typeOfT);
	}

	public static <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
		return gson.fromJson(json, classOfT);
	}

	public static <T> T fromJson(JsonElement json, Type typeOfT) throws JsonSyntaxException {
		return gson.fromJson(json, typeOfT);
	}
}
