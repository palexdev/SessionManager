package io.github.palexdev.sessionmanager.utils.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Custom exclusion strategy for Gson. Fields annotated with {@link Exclude} won't be serialized.
 */
public class ExcludeAnnotationStrategy implements ExclusionStrategy {
	//================================================================================
	// Properties
	//================================================================================
	private static final ExcludeAnnotationStrategy strategy = new ExcludeAnnotationStrategy();

	//================================================================================
	// Constructors
	//================================================================================
	private ExcludeAnnotationStrategy() {
	}

	//================================================================================
	// Getters
	//================================================================================
	public static ExcludeAnnotationStrategy instance() {
		return strategy;
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	public boolean shouldSkipField(FieldAttributes field) {
		return field.getAnnotation(Exclude.class) != null;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}
}
