package validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public abstract class Rod {

    static void notNullField(String fieldName) {
        throw new IllegalArgumentException(fieldName + " não pode ser vazio ou nulo.");
    }

    static void email(String email) {

        if (email == null || email.isBlank()) {
            notNullField("E-mail");
        }

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (!email.matches(regex)) {
            throw new IllegalArgumentException("E-mail inválido: " + email);
        }
    }

    static void string(String value, String fieldName, boolean optional, Integer min, Integer max) {

        if (!optional) {
            if (value == null || value.isBlank()) {
                notNullField(fieldName);
            }
        }

        if (value == null || value.isBlank()) {
            return;
        }

        if (min != null && value.length() < min) {
            throw new IllegalArgumentException(
                    fieldName + " deve ter no mínimo " + min + " caracteres.");
        }

        if (max != null && value.length() > max) {
            throw new IllegalArgumentException(
                    fieldName + " deve ter no máximo " + max + " caracteres.");
        }
    }

    static void number(Number value, String fieldName, boolean optional, Double min, Double max) {
    if (!optional && value == null) {
        notNullField(fieldName);
    }

    if (value == null) {
        return;
    }

    double v = value.doubleValue();

    if (min != null && v < min) {
        throw new IllegalArgumentException(
                fieldName + " deve ser no mínimo " + min + "."
        );
    }

    if (max != null && v > max) {
        throw new IllegalArgumentException(
                fieldName + " deve ser no máximo " + max + "."
        );
    }
}


    static void ValidEnum(ArrayList<String> values, String value, String fieldName) {
        if (value == null || value.isBlank()) {
            notNullField(fieldName);
        }

        Integer passed = 0;

        for (String i : values) {
            if (i.equals(value)) {
                passed++;
            }
        }

        if (passed > 0)
            return;
        throw new IllegalArgumentException(
                fieldName + " tem uma valor que não corresponde ao enum [ " + values + " ]");
    }

    static LocalDate BrasillianDate(String dateStr, Boolean optional) {
        if (!optional) {
            if (dateStr == null || dateStr.isBlank()) {
                notNullField("Data");
            }
        }

        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida. Use o formato yyyy/MM/dd.");
        }
    }

}
