package com.app.persistence.validator;

import com.app.persistence.validator.exception.ValidatorException;

import java.util.Map;
import java.util.stream.Collectors;

public interface Validator<T> {
    // Metoda validate zwraca mape, gdzie kluczem bedzie nazwa pola np name
    // a wartoscia komunikat co z tym polem jest nie tak
    Map<String, String> validate(T t); //abstrakcyjna

    static <T> void validate(T t, Validator<T> validator) {
        var errors = validator.validate(t); //klasa CarV przygotowuje implement.

        if (!errors.isEmpty()) {
            throw new ValidatorException(errors
                    .entrySet()
                    .stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .collect(Collectors.joining(", ")));
        }

    }
}
