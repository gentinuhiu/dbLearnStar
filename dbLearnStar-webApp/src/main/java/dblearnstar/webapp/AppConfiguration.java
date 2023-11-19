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

package dblearnstar.webapp;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;

import org.apache.tapestry5.TapestryFilter;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import dblearnstar.webapp.services.UTF8Filter;
import dblearnstar.webapp.util.AppConfig;

@Configuration
@ComponentScan({ "dblearnstar.webapp" })
public class AppConfiguration {

	@Bean
	public ServletContextInitializer initializer() {
		return new ServletContextInitializer() {
			@Override
			public void onStartup(ServletContext ctx) throws ServletException {
				// Tapestry init parameters
				ctx.setInitParameter("tapestry.app-package", "dblearnstar.webapp");
				ctx.setInitParameter("tapestry.development-modules", "dblearnstar.webapp.services.DevelopmentModule");
				ctx.setInitParameter("tapestry.qa-modules", "dblearnstar.webapp.services.QaModule");

				ctx.setInitParameter("artifactParameterName", "ticket");

				// Apereo CAS init parameters
				ctx.setInitParameter("casServerLogoutUrl", AppConfig.getString("cas.server") + "/cas/logout");
				ctx.setInitParameter("casServerLoginUrl", AppConfig.getString("cas.server") + "/cas/login");
				ctx.setInitParameter("casServerUrlPrefix", AppConfig.getString("cas.server") + "/cas");
				ctx.setInitParameter("service", AppConfig.getString("app.server") + ctx.getContextPath());

				// Filters

				EnumSet<DispatcherType> esDTs = EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR);

				ctx.addFilter("encodingFilter", UTF8Filter.class)
					.addMappingForUrlPatterns(esDTs, false, "/*");

				ctx.addFilter("CAS Single Sign Out Filter", SingleSignOutFilter.class)
					.addMappingForUrlPatterns(esDTs, false, "/*");
				ctx.addFilter("CAS Authentication Filter", AuthenticationFilter.class)
					.addMappingForUrlPatterns(esDTs, false, "/*");
				ctx.addFilter("CAS Validation Filter", Cas20ProxyReceivingTicketValidationFilter.class)
					.addMappingForUrlPatterns(esDTs, false, "/*");
				ctx.addFilter("CAS HttpServletRequest Wrapper Filter", HttpServletRequestWrapperFilter.class)
					.addMappingForUrlPatterns(esDTs, false, "/*");

				ctx.addFilter("app", TapestryFilter.class)
					.addMappingForUrlPatterns(esDTs, false, "/*");

				ctx.addListener(SingleSignOutHttpSessionListener.class);

				ctx.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
			}
		};
	}

	@Bean
	public ConfigurableServletWebServerFactory webServerFactory() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error404"));
		return factory;
	}

}
