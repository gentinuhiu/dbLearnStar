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

import java.io.IOException;

import org.apache.tapestry5.http.Link;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.ComponentEventLinkEncoder;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.ComponentEventResultProcessor;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.PersistentLocale;

public final class PersistentLocaleFilter implements ComponentRequestFilter {

	private final PersistentLocale persistentLocale;

	private final ThreadLocale threadLocale;

	private final ComponentEventLinkEncoder componentEventLinkEncoder;

	private final Request request;

	private final ComponentEventResultProcessor<Link> componentEventResultProcessor;

	public PersistentLocaleFilter(final PersistentLocale persistentLocale, final ThreadLocale threadLocale,
			final ComponentEventLinkEncoder componentEventLinkEncoder, final Request request,
			final ComponentEventResultProcessor<Link> componentEventResultProcessor) {
		this.persistentLocale = persistentLocale;
		this.threadLocale = threadLocale;
		this.componentEventLinkEncoder = componentEventLinkEncoder;
		this.request = request;
		this.componentEventResultProcessor = componentEventResultProcessor;
	}

	@Override
	public final void handleComponentEvent(final ComponentEventRequestParameters parameters,
			final ComponentRequestHandler handler) throws IOException {
		setPersistentLocaleIfNecessary();
		handler.handleComponentEvent(parameters);
	}

	@Override
	public final void handlePageRender(final PageRenderRequestParameters parameters,
			final ComponentRequestHandler handler) throws IOException {
		if (setPersistentLocaleIfNecessary()) {
			final Link pageRedirectLink = this.componentEventLinkEncoder.createPageRenderLink(parameters);
			for (final String paramName : this.request.getParameterNames()) {
				pageRedirectLink.addParameter(paramName, this.request.getParameter(paramName));
			}
			this.componentEventResultProcessor.processResultValue(pageRedirectLink);
			return;
		}
		handler.handlePageRender(parameters);
	}

	private final boolean setPersistentLocaleIfNecessary() {
		if (!this.persistentLocale.isSet()) {
			this.persistentLocale.set(this.threadLocale.getLocale());
			return true;
		}
		return false;
	}
}
