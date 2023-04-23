package com.max.autoLookup.util;

import com.max.autoLookup.exception.PageParsingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CommonUtils {

    public static Long getUniqueAdNumberFromAdLink(String adUrl) throws PageParsingException {
        String result = "";
        if (StringUtils.hasText(adUrl)) {
            Pattern digitPattern = null;
            if (adUrl.matches(".*\\d{9,}.*")) {
                digitPattern = Pattern.compile("\\d{9}");
            } else if (adUrl.matches(".*\\d{8,}.*")) {
                digitPattern = Pattern.compile("\\d{8}");
            } else {
                log.error("Failed to get adNumber from URL");
                throw new PageParsingException("failed to parse url: " + adUrl);
            }

            Matcher match = digitPattern.matcher(adUrl);
            if (match.find()) {
                result = match.group(0);
            } else {
                log.error("Failed to get adNumber from URL");
                throw new PageParsingException("failed to parse url: " + adUrl);
            }
        }
        return Long.parseLong(result);
    }
}
