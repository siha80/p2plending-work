package com.skp.payment.p2plending.secruity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class UserPasswordEncoder {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserPasswordEncoder.class.getName());
	private static final String SALT_FOR_HASHING = "";

	public String getSalt(int length) throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[length];
		sr.nextBytes(salt);
		return CryptoManager.toHex(salt);
	}

	public String encodePassword(String rawPass, String salt) {
		try {
			return CryptoManager.getSecurePassword(SCrypt.scrypt(rawPass, salt.toString()));
		} catch (GeneralSecurityException e) {
			LOGGER.warn("", e);
			return "";
		}
    }

	public boolean isPasswordValid(String encPass, String rawPass, String salt) {
		try {
			if (salt == null || salt.toString().isEmpty()) {
				LOGGER.debug("user's salt is null or is empty");
				return false;
			}
			return CryptoManager.getSecurePassword(SCrypt.scrypt(rawPass, salt.toString())).equals(encPass);
		} catch (GeneralSecurityException e) {
			LOGGER.warn("", e);
			return false;
		}
	}
}
