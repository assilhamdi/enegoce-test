package com.enegoce.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class XValidator implements ConstraintValidator<XType, String> {


    //a string containing all the allowed characters for type X
    private static final String ALLOWED_CHARS_X = "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/-?:().,'+ \r\n";


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //check if the given value contains only characters from the ALLOWED_CHARS_X set
        // and if it doesn't contain any invalid CrLf sequences

        if (value == null) {
            return true; // Allow null values
        }
        if (!containsOnlyAllowedChars(value)) {
            throw new IllegalArgumentException("Invalid characters found in input for type X");

        }
        if (containsInvalidCrLfSequence(value)) {
            throw new IllegalArgumentException("Invalid CrLf sequence found in input for type X");
        }
        return true;
    }

    private boolean containsOnlyAllowedChars(String value) {
        //iterates over each character in the input value and checks if it exists in the ALLOWED_CHARS_X string
        for (char c : value.toCharArray()) {
            if (ALLOWED_CHARS_X.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }


    private boolean containsInvalidCrLfSequence(String value) {
        //checks for any invalid CrLf sequences
        int crIndex = value.indexOf('\r');
        if (crIndex == -1) {
            return false;
        }
        return crIndex != value.lastIndexOf('\r')
                || crIndex + 1 == value.length()
                || value.charAt(crIndex + 1) != '\n';
    }


}
