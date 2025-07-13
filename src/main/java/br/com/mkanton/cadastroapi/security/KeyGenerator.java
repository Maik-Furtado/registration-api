package br.com.mkanton.cadastroapi.security;


import java.security.SecureRandom;
import java.util.Base64;

/**
 * This class provides methods for generating cryptographic keys of various lengths and encodings.
 */
public class KeyGenerator {

    // Defining constants for encoding types to avoid hardcoding strings
    public enum EncodingType {HEX, BASE64}

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a random cryptographic key with the given length and encoding type.
     *
     * @param length       The number of bytes to generate.
     * @param encodingType The desired encoding type.
     * @return The generated key as a string.
     * @throws IllegalArgumentException if the parameters are invalid.
     */
    public static String generateKey(int length, EncodingType encodingType) {
        if (length <= 0) {
            throw new IllegalArgumentException("Key length must be a positive integer.");
        }

        byte[] keyBytes = new byte[length];
        SECURE_RANDOM.nextBytes(keyBytes);

        switch (encodingType) {
            case HEX:
                return byteToHex(keyBytes);
            case BASE64:
                return Base64.getEncoder().encodeToString(keyBytes);
            default:
                throw new IllegalArgumentException("Unsupported encoding type: " + encodingType);
        }
    }

    /**
     * Converts a byte array to its hexadecimal string representation.
     *
     * @param bytes The byte array to be converted.
     * @return The hexadecimal string representation of the byte array.
     */
    private static String byteToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(String.format("%02x", hex));//update for better hex conversion efficiency
        }
        return hexString.toString();
    }

}
