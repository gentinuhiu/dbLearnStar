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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;

import dblearnstar.model.entities.Task;
import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.model.entities.TaskIsOfType;
import dblearnstar.model.entities.TestCollection;
import dblearnstar.model.entities.TestInstance;
import dblearnstar.model.entities.TestType;
import dblearnstar.model.model.ComparatorTestCollection;
import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.annotations.StudentPage;
import dblearnstar.webapp.model.TestCollectionSelectModel;
import dblearnstar.webapp.pages.admin.SubmissionEvaluations;
import dblearnstar.webapp.services.DigestService;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.PersonManager;
import dblearnstar.webapp.services.TestManager;
import dblearnstar.webapp.services.TranslationService;
import dblearnstar.webapp.services.UsefulMethods;

@AdministratorPage
@StudentPage
@Import(stylesheet = "ExamsAndTasksOverviewPage.css", module = "bootstrap/collapse")

public class ExamsAndTasksOverviewPage {
	@Inject
	private Logger logger;
	@Inject
	private JavaScriptSupport javaScriptSupport;
	@Inject
	private PersistentLocale persistentLocale;
	@Inject
	private SelectModelFactory selectModelFactory;
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	@Inject
	private DigestService digestService;

	@SessionState
	@Property
	private UserInfo userInfo;

	@Inject
	private PersonManager pm;
	@Inject
	private GenericService genericService;
	@Inject
	private TestManager testManager;
	@Inject
	private TranslationService translationService;

	@InjectPage
	private QueryTest queryTest;
	@InjectPage
	private SubmissionEvaluations submissionEvaluations;

	@Property
	private TestType testType;
	@Property
	private TestInstance testInstance;
	@Property
	private TaskInTestInstance taskInTestInstance;
	@Property
	private TaskIsOfType taskIsOfType;
	@Persist
	@Property
	private TestCollection testCollection;

	@InjectComponent
	private Zone collectionZone;

	private long studentId;

	public void onActivate() {
		studentId = pm.getStudentsByPersonId(userInfo.getPersonId()).get(0).getStudentId();
		if (testCollection == null) {
			testCollection = getTestCollections().stream().findFirst().orElse(null);
		}
	}

	public Date getCurrentTime() {
		return new Date();
	}

	public List<TestType> getTestTypes() {
		return UsefulMethods.castList(TestType.class, genericService.getAll(TestType.class));
	}

	public String getHashedTestInstanceId() {
		return digestService.obfuscate(Long.toString(testInstance.getTestInstanceId()));
	}

	/* selectTestCollection Form */

	public List<TestCollection> getTestCollections() {
		List<TestCollection> list = testManager.getTestCollectionsWithTestInstances();
		ComparatorTestCollection c = new ComparatorTestCollection();
		Collections.sort(list, c);
		return list;
	}

	public SelectModel getTestCollectionModel() {
		List<TestCollection> list = getTestCollections();
		if (list == null) {
			return null;
		} else {
			return new TestCollectionSelectModel(list, translationService, persistentLocale);
		}
	}

	public void onValueChanged(TestCollection newTestCollection) {
		testCollection = newTestCollection;
		ajaxResponseRenderer.addRender(collectionZone);
	}

	public List<TestInstance> getTestInstances() {
		List<TestInstance> list;
		if (userInfo.isAdministrator()) {
			if (testCollection == null) {
				list = testManager.getAllTestInstancesByTestType(testType.getTestTypeId());
			} else {
				list = testManager.getAllTestInstancesByTestTypeAndCollection(testType.getTestTypeId(),
						testCollection.getTestCollectionId());
			}
		} else if (userInfo.isStudent()) {
			if (testCollection == null) {
				list = testManager.getTestInstancesForStudentByTestType(studentId, testType.getTestTypeId());
			} else {
				list = testManager.getTestInstancesForStudentByTestTypeAndCollection(studentId,
						testType.getTestTypeId(), testCollection.getTestCollectionId());
			}
		} else {
			list = new ArrayList<TestInstance>();
		}
		return list;
	}

	public Boolean isTaskInTestInstanceSolved() {
		return testManager.isTaskInTestInstanceSolvedByStudent(taskInTestInstance.getTaskInTestInstanceId(), studentId);
	}

	public String getClassBtnSolved() {
		Boolean solved = isTaskInTestInstanceSolved();
		if (solved != null && solved) {
			return "btn-success";
		} else {
			return "btn-warning";
		}
	}

	public String getBtnBooleanClass(Boolean value) {
		if (value != null && value) {
			return "btn-success";
		} else {
			return "btn-warning";
		}

	}

	public String getClassOpenToAllStudents() {
		return getBtnBooleanClass(testInstance.getOpenToAllStudents());
	}

	public String getClassOpenForReviewByStudents() {
		return getBtnBooleanClass(testInstance.getOpenForReviewByStudents());
	}

	public List<TaskInTestInstance> getTaskInTestInstances() {
		return testManager.getTaskInTestInstancesByTestInstance(testInstance.getTestInstanceId());
	}

	public Long getNumPersonsSuccessful() {
		return testManager.getNumPersonsSuccessfulForTaskInTestInstance(taskInTestInstance.getTaskInTestInstanceId());
	}

	public long getNumPersonsTriedToSolve() {
		return testManager.getNumPersonsTriedToSolveTaskInTestInstance(taskInTestInstance.getTaskInTestInstanceId());
	}

	public String getTranslateTestInstanceTitle() {
		String translated = translationService.getTranslation(TestInstance.class.getSimpleName(), "title",
				testInstance.getTestInstanceId(), persistentLocale.get().getLanguage().toLowerCase());
		return (translated != null ? translated : testInstance.getTitle());
	}

	public String getTranslatedTestInstanceDescription() {
		String translated = translationService.getTranslation(TestInstance.class.getSimpleName(), "description",
				testInstance.getTestInstanceId(), persistentLocale.get().getLanguage().toLowerCase());
		return (translated != null ? translated : testInstance.getDescription());
	}

	public String getTranslateTestTypeTitle() {
		String translated = translationService.getTranslation(TestType.class.getSimpleName(), "title",
				testType.getTestTypeId(), persistentLocale.get().getLanguage().toLowerCase());
		return (translated != null ? translated : testType.getTitle());
	}

	public String getTranslatedTaskInTestInstanceTaskTitle() {
		String translated = translationService.getTranslation(Task.class.getSimpleName(), "title",
				taskInTestInstance.getTask().getTaskId(), persistentLocale.get().getLanguage().toLowerCase());
		return (translated != null ? translated : taskInTestInstance.getTask().getTitle());
	}

	public String getTranslateTaskDescription() {
		String translated = translationService.getTranslation("Task", "description",
				taskInTestInstance.getTask().getTaskId(), persistentLocale.get().getLanguage().toLowerCase());
		return (translated != null ? translated : taskInTestInstance.getTask().getDescription());
	}

	public String getClassTestIsNow() {
		if (testInstance.getScheduledFor() != null && testInstance.getScheduledUntil() != null) {
			if (testInstance.getScheduledFor().before(getCurrentTime())
					&& testInstance.getScheduledUntil().after(getCurrentTime())) {
				if ((new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)))
						.after(testInstance.getScheduledUntil())) {
					return "bg-danger";
				} else {
					return "bg-success";
				}
			} else {
				if ((new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5)))
						.before(testInstance.getScheduledUntil())) {
					return "bg-secondary";
				} else {
					return "";
				}
			}
		} else {
			return "";
		}
	}

	public Object onEvaluate(TestInstance ti) {
		submissionEvaluations.onValueChangedFromSelectTestInstance(ti);
		return submissionEvaluations;
	}

	@CommitAfter
	public void onToggleOpenToAll(TestInstance ti) {
		if (userInfo != null && userInfo.isInstructor()) {
			ti.setOpenToAllStudents(!ti.getOpenToAllStudents());
		}
	}

	@CommitAfter
	public void onToggleOpenForReviewByStudents(TestInstance ti) {
		if (userInfo != null && userInfo.isInstructor()) {
			ti.setOpenForReviewByStudents(!ti.getOpenForReviewByStudents());
		}
	}

}
