package de.healthhub.auth.security;

import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

@ApplicationScoped
public class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH_BITS = 256;

    public String hash(String clearTextPassword) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);

            byte[] hash = pbkdf2(clearTextPassword, salt, ITERATIONS, KEY_LENGTH_BITS);

            return "pbkdf2$"
                    + ITERATIONS
                    + "$"
                    + Base64.getEncoder().encodeToString(salt)
                    + "$"
                    + Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new IllegalStateException("Could not hash password", e);
        }
    }

    public boolean matches(String clearTextPassword, String storedHash) {
        try {
            String[] parts = storedHash.split("\\$");

            if (parts.length != 4) {
                return false;
            }

            String algorithmMarker = parts[0];
            if (!"pbkdf2".equalsIgnoreCase(algorithmMarker)) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            byte[] actualHash = pbkdf2(
                    clearTextPassword,
                    salt,
                    iterations,
                    expectedHash.length * 8
            );

            return constantTimeEquals(actualHash, expectedHash);

        } catch (Exception e) {
            return false;
        }
    }

    private byte[] pbkdf2(String clearTextPassword, byte[] salt, int iterations, int keyLengthBits) throws Exception {
        KeySpec spec = new PBEKeySpec(
                clearTextPassword.toCharArray(),
                salt,
                iterations,
                keyLengthBits
        );

        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        return factory.generateSecret(spec).getEncoded();
    }

    private boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null || a.length != b.length) {
            return false;
        }

        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}