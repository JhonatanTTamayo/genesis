package com.breaze.genesis.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationErrorItem {

    private String field;
    private String message;
}