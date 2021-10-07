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

import javax.inject.Inject;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(module = { "ModalBox" })
public class ModalBox implements ClientElement {
	@Inject
	private JavaScriptSupport javaScriptSupport;
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	@Inject
	private Request request;

	@Parameter(name = "componentClientId", value = "prop:componentResources.id", defaultPrefix = BindingConstants.LITERAL)
	private String componentClientId;

	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL, allowNull = true)
	private String additionalClass;
	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL, allowNull = true)
	private String additionalClassDialog;

	@Override
	public String getClientId() {
		return componentClientId;
	}

	void setupRender() {
		JSONObject json = new JSONObject();
		json.put("keyboard", true);
		json.put("backdrop", "static");
		json.put("focus", true);
		javaScriptSupport.require("ModalBox").invoke("activate").with(componentClientId, json);
	}

	public void hide() {
		if (request.isXHR()) {
			ajaxResponseRenderer.addCallback(makeScriptToHideModal());
		}
	}

	private JavaScriptCallback makeScriptToHideModal() {
		return new JavaScriptCallback() {
			public void run(JavaScriptSupport javascriptSupport) {
				javaScriptSupport.require("ModalBox").invoke("hide").with(componentClientId);
			}
		};
	}

}
