package com.example.bankcards.util;

import java.util.Base64;
import java.util.UUID;

public class Util {

    public static String encrypt(String number) {
        return Base64.getEncoder().encodeToString(number.getBytes());
    }

    public static String decrypt(String encrypted) {
        return new String(Base64.getDecoder().decode(encrypted));
    }

    public static String generateCardNumber() {
        return UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 16);
    }

    public static String getMaskedNumber(String encrypted) {
        String maskedNumber = "**** **** **** ";
        try {
            String decoded = Util.decrypt(encrypted);
            maskedNumber += decoded.substring(decoded.length() - 4);
        } catch (Exception e) {
            maskedNumber = "**** **** **** ????";
        }

        return maskedNumber;
    }
}
