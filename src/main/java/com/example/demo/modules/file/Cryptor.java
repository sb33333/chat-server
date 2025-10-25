package com.example.demo.modules.file;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class Cryptor {
    private final byte[] SECRET_KEY;
    private final String CIPHER_INSTANCE_NAME = "AES/GCM/NoPadding";
    
    private SecretKey getSecretKey() {
        return new SecretKeySpec(SECRET_KEY, "AES");
    }

    public byte[] encrypt(byte[] data) {
        try {

            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_NAME);
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), spec);
            byte[] encrypted = cipher.doFinal(data);

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
            buffer.put(iv);
            buffer.put(encrypted);
            return buffer.array();

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed.", e);
        }
    }

    public byte[] decrypt (byte[] encryptedWithIv) {
        try {

            ByteBuffer buffer = ByteBuffer.wrap(encryptedWithIv);
            byte[] iv = new byte[12];
            buffer.get(iv);
            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);
            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_NAME);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), new GCMParameterSpec(128, iv));
            return cipher.doFinal(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("Decryption failed.", e);
        }

    } // end of decrypt()
}
