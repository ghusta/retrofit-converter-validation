package org.husta.retrofit2.converter.validation;

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

    ValidationRequestBodyConverter(Converter<Object, RequestBody> delegate, Validator validator) {
        this.delegate = delegate;
        this.validator = validator;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(value);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
        return delegate.convert(value);
    }

}
