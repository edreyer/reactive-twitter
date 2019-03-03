package com.liquidsoftware.reactivetwitter.utils;

import com.liquidsoftware.reactivetwitter.domain.Tweet;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.stream.IntStream;

public class CryptoUtils {

    private static final Logger LOG = LoggerFactory.getLogger(Tweet.class);

    private static MessageDigest messageDigest;

    static {
        messageDigest = Try.of(() -> MessageDigest.getInstance("SHA-256"))
            .onFailure(ex -> LOG.error("Failed to create MessageDigest", ex))
            .get();
    }


    public static String encode(String unencoded) {
        final byte[] hashbytes = messageDigest.digest(
            unencoded.getBytes(StandardCharsets.UTF_8)
        );
        return bytesToHex(hashbytes);
    }

    // adapted from: https://www.baeldung.com/sha-256-hashing-java
    private static String bytesToHex(byte[] hash) {
        return IntStream.range(0, hash.length)
            .mapToObj(i -> Integer.toHexString(0xff & hash[i]))
            .map(hex -> hex.length() == 1 ? hex + "0" : hex)
            .reduce("", String::concat);
    }}
