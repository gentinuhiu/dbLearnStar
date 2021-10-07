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

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.slf4j.Logger;

import dblearnstar.model.entities.Person;
import dblearnstar.model.entities.SolutionAssessment;
import dblearnstar.model.entities.Student;
import dblearnstar.model.entities.StudentStartedTest;
import dblearnstar.model.entities.StudentSubmitSolution;
import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.model.StudentStartedTestModel;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.TestManager;

@AdministratorPage
public class SolutionEvaluationReview {
	@Property
	@SessionState
	private UserInfo userInfo;

	@Inject
	private Logger logger;
	@Inject
	private SelectModelFactory selectModelFactory;
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	@Inject
	private GenericService genericService;
	@Inject
	private TestManager testManager;

	@InjectComponent
	private Zone studentStartedTestZone;

	@Persist
	@Property
	StudentStartedTest selectedStudentStartedTest;
	@Property
	StudentSubmitSolution studentSubmitSolution;
	@Property
	SolutionAssessment solutionAssessment;

	public void onActivate() {
		if (selectedStudentStartedTest != null) {
			selectedStudentStartedTest = genericService.getByPK(StudentStartedTest.class,
					selectedStudentStartedTest.getStudentStartedTestId());
		}
	}

	public SelectModel getAllStudentStartedTests() {
		Person p = genericService.getByPK(Person.class, userInfo.getPersonId());
		List<StudentStartedTest> list = new ArrayList<StudentStartedTest>();
		for (Student s : p.getStudents()) {
			list.addAll(s.getStudentStartedTests());
		}
		return new StudentStartedTestModel(list);
	}

	public void onValueChangedFromSelect(StudentStartedTest newStudentStartedTest) {
		selectedStudentStartedTest = newStudentStartedTest;
		ajaxResponseRenderer.addRender(studentStartedTestZone);
	}

}
