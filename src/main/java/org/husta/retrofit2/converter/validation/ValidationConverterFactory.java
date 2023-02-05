package org.husta.retrofit2.converter.validation;

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
import java.lang.reflect.ParameterizedType;
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
        hasBodyAnnotation(parameterAnnotations);
        Class<?> rawType = getRawType(type);
        Type innerType = getParameterUpperBound(0, (ParameterizedType) type);
        // See : retrofit2.OptionalConverterFactory
        Converter<Object, RequestBody> delegate = retrofit.nextRequestBodyConverter(this, type, parameterAnnotations, methodAnnotations);
        Objects.requireNonNull(delegate);
        // methodAnnotations should contains @POST or @PUT ?
        if ((hasBodyAnnotation(parameterAnnotations)) && hasValidAnnotation(parameterAnnotations)) {
            return new ValidationRequestBodyConverter<>(delegate, validator);
        } else {
            return null;
        }
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return null;
    }

    private boolean hasBodyAnnotation(Annotation[] parameterAnnotations) {
        return Arrays.stream(parameterAnnotations).anyMatch(Body.class::isInstance);
    }

    private boolean hasValidAnnotation(Annotation[] parameterAnnotations) {
        return Arrays.stream(parameterAnnotations).anyMatch(Valid.class::isInstance);
    }

}
