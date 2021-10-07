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

package dblearnstar.webapp.components;

import java.util.Locale;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.http.services.RequestGlobals;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PersistentLocale;
import org.slf4j.Logger;

import dblearnstar.model.entities.Person;
import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.PublicPage;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.PersonManager;

@Import(stylesheet = { "classpath:META-INF/resources/webjars/fontsource-fira-sans/3.0.5/index.css",
		"classpath:META-INF/resources/webjars/fontsource-fira-sans/3.0.5/500.css",
		"classpath:META-INF/resources/webjars/fontsource-fira-sans/3.0.5/700.css",
		"classpath:META-INF/resources/webjars/firacode/1.205.0/distr/fira_code.css",
		"site-overrides.css" }, module = { "bootstrap/dropdown", "bootstrap/collapse", "zoneUpdateEffect" })
@PublicPage
public class Layout {
	@Property
	@SessionState
	private UserInfo userInfo;

	@Inject
	private ComponentResources resources;
	@Inject
	private RequestGlobals requestGlobals;
	@Inject
	private Request request;

	@Property
	@Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
	private String title;

	@Property
	private String pageName;

	@Property
	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	private String appVersion;

	@Inject
	private Logger logger;
	@Inject
	private GenericService genericService;

	@Inject
	private PersonManager personManager;

	public String getClassForPageName() {
		logger.debug("respgname:{}", resources.getPageName() + " " + resources.getCompleteId());
		if (resources.getPageName().equalsIgnoreCase(pageName)) {
			return "active";
		} else {
			return " ";
		}
	}

	public String[] getStudentPageNames() {
		return new String[] { "ExamsAndTasksOverview", "SolutionComparator", "OpenDiscussions", "PersonalAnalytics" };
	}

	public String[] getAdminPageNames() {
		if (userInfo.isAdministrator()) {
			return new String[] { "admin/ManagePersons", "admin/GroupManagement", "admin/StudentGroupProgress",
					"admin/ManageModels", "admin/TestAdmin", "admin/Reevaluation", "admin/SubmissionLogViewer",
					"admin/TestCollectionManagement", "admin/ActiveUsers" };
		} else {
			return null;
		}
	}

	public String getLoggedInUserName() {
		if (userInfo == null) {
			return "NOT LOGGED IN";
		} else {
			return personManager.getPersonFullNameWithId(genericService.getByPK(Person.class, userInfo.getPersonId()));
		}
	}

	@Inject
	private Messages messages;

	public String getPageNameTitle() {
		return messages.get(pageName + "-pagelink");
	}

	@Inject
	private PersistentLocale persistentLocale;

	void onActionFromLocaleToggle() {
		if (persistentLocale.isSet()) {
			if ("mk".equalsIgnoreCase(persistentLocale.get().getLanguage())) {
				persistentLocale.set(new Locale("en"));
			} else {
				persistentLocale.set(new Locale("mk"));
			}
		} else {
			persistentLocale.set(new Locale("mk"));
		}
	}

	public String getDisplayLanguage() {
		Locale loc = persistentLocale.get();
		if (loc == null)
			return "N/A";
		else
			return loc.getLanguage().toUpperCase();
	}

}
