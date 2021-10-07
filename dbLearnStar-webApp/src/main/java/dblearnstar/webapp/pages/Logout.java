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

package dblearnstar.webapp.pages;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.http.services.RequestGlobals;
import org.apache.tapestry5.http.services.Session;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.slf4j.Logger;

import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.PublicPage;
import dblearnstar.webapp.util.AppConfig;

@PublicPage
@Import(stylesheet = { "classpath:META-INF/resources/webjars/fontsource-fira-sans/3.0.5/index.css",
		"site-overrides.css" }, module = { "bootstrap/dropdown", "bootstrap/collapse" })
public class Logout {
	@Inject
	private Logger logger;

	@Inject
	private Request request;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private Cookies cookies;

	@Persist
	@Property
	private UserInfo userInfo;

	@Property
	private String casServer;
	@Property
	private String appServer;
	@Property
	private String logoutRedirectToServer;
	@Property
	private String casLogoutLink;

	@Inject
	private Messages messages;

	void onActivate() {
		logoutRedirectToServer = AppConfig.getString("logout.redirectToServer");

		casLogoutLink = AppConfig.getString("cas.server") + "/cas/logout?service=" + AppConfig.getString("app.server");

		Session session = request.getSession(false);
		if (session != null) {
			session.invalidate();
			userInfo = null;
			logger.debug("Session successfully invalidated!");
		}

		clearCookie();
	}

	private void clearCookie() {
	}

}
