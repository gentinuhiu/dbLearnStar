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

package dblearnstar.webapp.mixins;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

public class WebEditor {

	@InjectContainer
	private TextArea field;

	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	String areaType;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Inject
	@Path("ckeditor-custom.css")
	private Asset asset;

	public void afterRender() {
		JSONObject json = new JSONObject();
		json.put("id", field.getClientId());
		json.put("csspath", asset.toClientURL());
		if (areaType == null || areaType.equals("CK")) {
			javaScriptSupport.require("ckeditor-config").with(json);
		} else if (areaType.equals("CKADMIN")) {
			javaScriptSupport.require("ckeditor-config-admin").with(json);
		}
	}
}
