/*******************************************************************************
 * Copyright (C) 2021 Vangel V. Ajanovski
 *     
 * This file is part of the dbLearnStar system (hereinafter: dbLearn*).
 *     
 * dbLearn* is free software: you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *     
 * dbLearn* is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
 * details.
 *     
 * You should have received a copy of the GNU General Public License along 
 * with dbLearn*.  If not, see <https://www.gnu.org/licenses/>.
 * 
 ******************************************************************************/

package dblearnstar.webapp.util;

import java.io.UnsupportedEncodingException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class AppConfig {
	private static final String BUNDLE_NAME = "AppConfig";

	@Inject
	private static Logger logger;

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private AppConfig() {
	}

	public static String getString(String key) {
		try {
			return new String(RESOURCE_BUNDLE.getString(key).getBytes("ISO-8859-1"), "UTF-8");
		} catch (MissingResourceException e) {
			logger.error("Missing resource for " + key);
			throw e;
		} catch (UnsupportedEncodingException e) {
			return RESOURCE_BUNDLE.getString(key);
		}
	}

	public static Float getFloat(String key) {
		return Float.parseFloat(getString(key));
	}

	public static Long getLong(String key) {
		return Long.parseLong(getString(key));
	}

	public static Integer getInteger(String key) {
		return Integer.parseInt(getString(key));
	}
}
