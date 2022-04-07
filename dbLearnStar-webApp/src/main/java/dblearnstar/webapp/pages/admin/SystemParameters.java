/*******************************************************************************
 * Copyright (C) 2021 Vangel V. Ajanovski
 *     
 * This file is part of the EPRMS - Educational Project and Resource 
 * Management System (hereinafter: EPRMS).
 *     
 * EPRMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *     
 * EPRMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *     
 * You should have received a copy of the GNU General Public License
 * along with EPRMS.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package dblearnstar.webapp.pages.admin;

import java.util.List;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import dblearnstar.model.entities.SystemParameter;
import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.annotations.InstructorPage;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.UsefulMethods;

@InstructorPage
@AdministratorPage
public class SystemParameters {
	@SessionState
	@Property
	private UserInfo userInfo;

	@Inject
	private GenericService genericService;

	@Property
	private SystemParameter systemParameter;

	@Persist
	@Property
	private SystemParameter editSystemParameter;

	public List<SystemParameter> getSystemParameters() {
		return (List<SystemParameter>) UsefulMethods.castList(SystemParameter.class,
				genericService.getAll(SystemParameter.class));
	}

	void onActionFromNewSystemParameter() {
		editSystemParameter = new SystemParameter();
	}

	void onActionFromEditParameter(SystemParameter sp) {
		editSystemParameter = sp;
	}

	@CommitAfter
	void onActionFromDeleteParameter(SystemParameter sp) {
		genericService.delete(sp);
	}

	@CommitAfter
	void onSuccessFromEditSystemParameter() {
		genericService.saveOrUpdate(editSystemParameter);
		editSystemParameter = null;
	}
}
