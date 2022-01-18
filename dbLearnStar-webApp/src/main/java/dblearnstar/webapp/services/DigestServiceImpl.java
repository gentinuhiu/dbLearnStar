package dblearnstar.webapp.services;

import org.apache.commons.codec.digest.DigestUtils;

import dblearnstar.webapp.util.AppConfig;

public class DigestServiceImpl implements DigestService {

	@Override
	public String obfuscate(String text) {
		String message = AppConfig.getString("tapestry.hmac-passphrase") + text;
		return DigestUtils.sha1Hex(message);
	}

}
