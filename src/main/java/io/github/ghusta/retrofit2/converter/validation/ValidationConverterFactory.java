package io.github.ghusta.retrofit2.converter.validation;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.Body;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * A {@linkplain Converter.Factory converter} which uses Bean Validation.
 * <p>
 * It should be declared first in the converter factory list.
 *
 * @see Retrofit#converterFactories()
 */
public final class ValidationConverterFactory extends Converter.Factory {

    private final Validator validator;

    public static ValidationConverterFactory create() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            return create(validator);
        }
    }

    public static ValidationConverterFactory create(Validator validator) {
        Objects.requireNonNull(validator);
        return new ValidationConverterFactory(validator);
    }

    private ValidationConverterFactory(Validator validator) {
        this.validator = validator;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(type);
        // Type innerType = getParameterUpperBound(0, (ParameterizedType) type);
        // See : retrofit2.OptionalConverterFactory
        Converter<Object, RequestBody> delegate = retrofit.nextRequestBodyConverter(this, type, parameterAnnotations, methodAnnotations);
        Objects.requireNonNull(delegate);
        // methodAnnotations should contain @POST or @PUT or something else
        if ((hasAnnotation(parameterAnnotations, Body.class)) && hasAnnotation(parameterAnnotations, Valid.class)) {
            ValidationGroups validationGroupsAnnotation = findFirstAnnotation(parameterAnnotations, ValidationGroups.class);
            if (validationGroupsAnnotation == null) {
                return new ValidationRequestBodyConverter<>(delegate, validator);
            } else {
                return new ValidationRequestBodyConverter<>(delegate, validator, validationGroupsAnnotation.groups());
            }
        } else {
            return null;
        }
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return null;
    }

    private boolean hasAnnotation(Annotation[] parameterAnnotations, Class<? extends Annotation> annotationType) {
        return Arrays.stream(parameterAnnotations).anyMatch(annotationType::isInstance);
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation> A findFirstAnnotation(Annotation[] parameterAnnotations, Class<A> annotationClass) {
        for (Annotation annotation : parameterAnnotations) {
            if (annotation.annotationType().equals(annotationClass)) {
                return (A) annotation;
            }
        }
        return null;
    }

}
