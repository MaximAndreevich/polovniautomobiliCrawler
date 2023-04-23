package com.max.autoLookup.exception;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@AllArgsConstructor
public class PageParsingException extends Throwable{
    String text;
}
