package com.learn2code.spring.kafka.common;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

import static com.learn2code.spring.kafka.common.Constants.*;

public class Utils {

    public static String getMetadata(Headers headers) {
        String metadata = null;
        if (headers.toArray().length > 0) {
            Iterator<Header> it = headers.headers(Constants.METADATA).iterator();
            if (it.hasNext())
                metadata = new String(it.next().value(), StandardCharsets.UTF_8);
            return metadata;
        }
        return metadata;
    }

    public static String getIdentifierValue(String metadata, String identifier) {
        String identifierValue = "";
        if (metadata != null) {
            String[] identifiers = metadata.split(Constants.TILDE_DELIMITER);
            if (identifiers.length > 0)
                switch (identifier) {

                }
        }
        return identifierValue;
    }

}
