package io.github.ghusta.retrofit2.converter.validation;

import okhttp3.RequestBody;
import retrofit2.Converter;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

final class ValidationRequestBodyConverter<T> implements Converter<T, RequestBody> {

    private final Converter<Object, RequestBody> delegate;
    private final Validator validator;
    private final Class<?>[] validationGroups;

    ValidationRequestBodyConverter(Converter<Object, RequestBody> delegate, Validator validator, Class<?>... validationGroups) {
        this.delegate = delegate;
        this.validator = validator;
        this.validationGroups = validationGroups;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(value, validationGroups);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
        return delegate.convert(value);
    }

}
