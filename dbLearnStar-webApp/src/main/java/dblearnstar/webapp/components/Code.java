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

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Code {

	@Property
	@Parameter(required = true, allowNull = true)
	private String value;
	@Property
	@Parameter(required = false, allowNull = true)
	private String additionalClass;

	@Inject
	private ComponentResources componentResources;

	public String getId() {
		return componentResources.getId();
	}

	public Boolean getNullValue() {
		if (value == null) {
			return true;
		} else {
			return false;
		}
	}

	public String getAllClasses() {
		if (additionalClass != null) {
			return additionalClass;
		} else {
			return "";
		}
	}
}
