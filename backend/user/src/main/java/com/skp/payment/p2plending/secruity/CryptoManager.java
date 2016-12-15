package com.skp.payment.p2plending.secruity;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;

import static java.lang.System.arraycopy;

public class CryptoManager {
    private static final String PBKDF2_ALGORITHM = "HmacSHA256";
    private static final String HMAC_SHA256 = "SHA-256";
    private static final String HMAC_SHA512 = "SHA-512";
    private static final String ENC_TYPE = "utf-8";
    private static final String ENC_FORMAT = "AES/CBC/PKCS5Padding";

    public static String pbkdf2(String password, String salt, int c, int dkLen) throws GeneralSecurityException {
        byte[] S = salt.getBytes();

        return pbkdf2(password, S, c, dkLen);
    }

    public static String pbkdf2(String password, byte[] salt, int c, int dkLen) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(PBKDF2_ALGORITHM);
        byte[] DK = new byte[dkLen];

        mac.init(new SecretKeySpec(password.getBytes(), PBKDF2_ALGORITHM));

        int hLen = mac.getMacLength();
        if (dkLen > (Math.pow(2, 32) - 1) * hLen) {
            throw new GeneralSecurityException("Requested key length too long");
        }

        byte[] U = new byte[hLen];
        byte[] T = new byte[hLen];
        byte[] block1 = new byte[salt.length + 4];

        int l = (int) Math.ceil((double) dkLen / hLen);
        int r = dkLen - (l - 1) * hLen;

        arraycopy(salt, 0, block1, 0, salt.length);
        for (int i = 1; i <= l; i++) {
            block1[salt.length + 0] = (byte) (i >> 24 & 0xff);
            block1[salt.length + 1] = (byte) (i >> 16 & 0xff);
            block1[salt.length + 2] = (byte) (i >> 8 & 0xff);
            block1[salt.length + 3] = (byte) (i >> 0 & 0xff);
            mac.update(block1);
            mac.doFinal(U, 0);
            arraycopy(U, 0, T, 0, hLen);

            for (int j = 1; j < c; j++) {
                mac.update(U);
                mac.doFinal(U, 0);
                for (int k = 0; k < hLen; k++) {
                    T[k] ^= U[k];
                }
            }
            arraycopy(T, 0, DK, (i - 1) * hLen, (i == l ? r : hLen));
        }

        return toHex(DK);
    }

    public static String getSecurePassword(String passwordToHash) {
        String generatedPassword = null;
        try {
            String salt = "";
            MessageDigest md = MessageDigest.getInstance(HMAC_SHA512);
            md.update(salt.getBytes());
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return generatedPassword;
    }

    public static String getSalt16() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return toHex(salt);
    }

    public static String getSalt32() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[32];
        sr.nextBytes(salt);
        return toHex(salt);
    }

    public static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }

        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return ba;
    }

    public static String getSha256hash(String cleartext) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(HMAC_SHA256);
            digest.update(cleartext.getBytes("UTF-8"));
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toString((messageDigest[i] & 0xff) + 0x100, 16).substring(1));
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public final static String getSha512Hash(String target) {
        String result = null;
        try {
            MessageDigest sh = MessageDigest.getInstance(HMAC_SHA512);
            sh.update(target.getBytes());

            StringBuffer sb = new StringBuffer();
            for (byte b : sh.digest()) {
                sb.append(Integer.toHexString(0xff & b));
            }

            result = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String makeHmacData(String input, String key) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes("UTF-8"), mac.getAlgorithm()));
        mac.update(input.getBytes("UTF-8"));

        byte[] signValue = mac.doFinal();
        return Base64.encodeBase64URLSafeString(signValue);
    }
}