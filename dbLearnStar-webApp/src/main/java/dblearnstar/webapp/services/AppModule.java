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

package dblearnstar.webapp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.commons.Configuration;
import org.apache.tapestry5.commons.MappedConfiguration;
import org.apache.tapestry5.commons.OrderedConfiguration;
import org.apache.tapestry5.hibernate.HibernateEntityPackageManager;
import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.http.services.RequestGlobals;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Decorate;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.modules.Bootstrap4Module;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStateCreator;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.hibernate.Session;
import org.slf4j.Logger;

import dblearnstar.model.entities.Person;
import dblearnstar.model.entities.PersonRole;
import dblearnstar.model.model.UserInfo;
import dblearnstar.model.model.UserInfo.UserRole;
import dblearnstar.webapp.util.AppConfig;

@ImportModule(Bootstrap4Module.class)
public class AppModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(AccessControllerRequestFilter.class);
		binder.bind(DigestService.class);
		binder.bind(GenericDao.class);
		binder.bind(GenericService.class);
		binder.bind(GroupManager.class);
		binder.bind(PersonDao.class);
		binder.bind(PersonManager.class);
		binder.bind(ActivityManager.class);
		binder.bind(ActivityDao.class);
		binder.bind(EvaluationService.class);
		binder.bind(TestManager.class);
		binder.bind(TranslationService.class);
		binder.bind(SystemConfigService.class);
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.override(SymbolConstants.APPLICATION_VERSION, AppConfig.getString("app.version"));
		configuration.override(SymbolConstants.PRODUCTION_MODE, false);
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en,mk");
		configuration.add(SymbolConstants.HMAC_PASSPHRASE,
				AppConfig.getString("tapestry.hmac-passphrase") + UUID.randomUUID());
		configuration.add(SymbolConstants.ENABLE_HTML5_SUPPORT, true);
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, false);

		configuration.add("tapestry.hibernate.early-startup", true);

		configuration.add(SymbolConstants.JAVASCRIPT_INFRASTRUCTURE_PROVIDER, "jquery");

		configuration.add(SymbolConstants.EXCEPTION_REPORTS_DIR,
				AppConfig.getString("additionalFiles.path") + AppConfig.getString("exceptionReports.path"));
	}

	@Contribute(HibernateEntityPackageManager.class)
	public static void addHibernateEntityPackageManager(Configuration<String> configuration) {
		configuration.add(Person.class.getPackageName());
	}

	@Match({ "*Service", "*Dao", "*Manager" })
	public static void adviseEnableTransactions(HibernateTransactionAdvisor advisor, MethodAdviceReceiver receiver) {
		advisor.addTransactionCommitAdvice(receiver);
	}

	@Decorate(serviceInterface = ThreadLocale.class)
	public ThreadLocale decorateThreadLocale(final ThreadLocale threadLocale, final PersistentLocale persistentLocale) {
		return new ThreadLocale() {
			@Override
			public void setLocale(Locale locale) {
				threadLocale.setLocale(locale);
			}

			@Override
			public Locale getLocale() {
				if (!persistentLocale.isSet()) {
					setLocale(new Locale("mk"));
					persistentLocale.set(new Locale("mk"));
				}
				return threadLocale.getLocale();
			}
		};
	}

	public static final void contributeComponentRequestHandler(
			OrderedConfiguration<ComponentRequestFilter> configuration,
			ComponentRequestFilter accessControllerRequestFilter, ApplicationStateManager asm,
			ComponentSource componentSource) {
		configuration.add("AccessControllerRequestFilter", accessControllerRequestFilter, "before:*");
	}

	public void contributeApplicationStateManager(
			MappedConfiguration<Class, ApplicationStateContribution> configuration, Session session,
			PersonManager personManager, RequestGlobals requestGlobals, Logger logger) {
		ApplicationStateCreator<UserInfo> userInfoCreator = new ApplicationStateCreator<UserInfo>() {
			public UserInfo create() {
				logger.debug("userInfoCreator.create entered");

				UserInfo userInfo = new UserInfo();
				userInfo.setUserRoles(null);
				userInfo.setPersonId(null);
				userInfo.setUserName(null);

				try {
					String userName = requestGlobals.getHTTPServletRequest().getRemoteUser();
					userInfo.setUserName(userName);
					logger.info("Login by user: " + userName);

					Person loggedInPerson = (Person) session.getSession()
							.createQuery("from Person p where userName=:userName").setParameter("userName", userName)
							.getSingleResult();

					if (loggedInPerson == null) {
						userInfo.setUserRoles(null);
						userInfo.setPersonId(null);
					} else {
						logger.debug("Login personId: {}", loggedInPerson.getPersonId());

						List<UserInfo.UserRole> userRoles = new ArrayList<UserRole>();

						if (!(loggedInPerson.getStudents().isEmpty())) {
							logger.debug("Login user is student");
							userRoles.add(UserRole.STUDENT);
						}

						for (PersonRole pr : personManager.getPersonRolesForPerson(loggedInPerson.getPersonId())) {
							if (pr.getRole().getName().equals("ADMINISTRATOR")) {
								logger.debug("Login user is administrator");
								userRoles.add(UserRole.ADMINISTRATOR);
							} else if (pr.getRole().getName().equals("INSTRUCTOR")) {
								logger.debug("Login user is instructor");
								userRoles.add(UserRole.INSTRUCTOR);
							}
						}

						if (userRoles.size() == 0) {
							logger.debug("Login user role is set to NONE");
							userRoles.add(UserRole.NONE);
						}

						logger.debug("Login user has {} roles", userRoles.size());

						userInfo.setUserName(userName);
						userInfo.setPersonId(loggedInPerson.getPersonId());
						userInfo.setUserRoles(userRoles);
						logger.debug("userInfo is now initialized");

					}

					return userInfo;

				} catch (Exception e) {
					if (userInfo.getUserName() != null) {
						logger.error("userName {} is not found", userInfo.getUserName());
					} else {
						logger.error("userName is empty");
					}
					// throw new NoSuchUserException();
					return userInfo;
				}
			}
		};
		configuration.add(UserInfo.class, new ApplicationStateContribution("session", userInfoCreator));
	}

	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration) {
		configuration.add("webjars", "META-INF/resources/webjars");
	}

	public Logger buildLogger(final Logger logger) {
		return logger;
	}

}
