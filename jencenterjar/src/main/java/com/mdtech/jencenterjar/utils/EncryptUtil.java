package com.mdtech.jencenterjar.utils;


import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil {
    private static final String ENCRYPT_TYPE = "";

    public static String encrypt(String plainText) {
        byte[] encrypt = null;
        try {
            Key key = generateKey("KEY");
            Cipher cipher = Cipher.getInstance(ENCRYPT_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypt = cipher.doFinal(plainText.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(encrypt, Base64.DEFAULT);
    }

    public static String decrypt(String keyStr, String encryptData) {
        byte[] decrypt = null;
        try {
            Key key = generateKey(keyStr);
            Cipher cipher = Cipher.getInstance(ENCRYPT_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypt = cipher.doFinal(Base64.decode(encryptData, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (decrypt != null)
            return new String(decrypt).trim();
        else return "";
    }

    private static Key generateKey(String key) throws Exception {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), ENCRYPT_TYPE);
            return keySpec;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

}
