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
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;

import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.model.entities.TaskIsOfType;
import dblearnstar.model.entities.TestCollection;
import dblearnstar.model.entities.TestInstance;
import dblearnstar.model.entities.TestType;
import dblearnstar.model.model.ComparatorTestCollection;
import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.annotations.StudentPage;
import dblearnstar.webapp.model.StudentSelectModel;
import dblearnstar.webapp.model.TestCollectionSelectModel;
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

	@SessionState
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
		if (testCollection == null)
			testCollection = getTestCollections().stream().findFirst().orElse(null);
	}

	public Date getCurrentTime() {
		return new Date();
	}

	public List<TestType> getTestTypes() {
		return UsefulMethods.castList(TestType.class, genericService.getAll(TestType.class));
	}

	public List<TestCollection> getTestCollections() {
		List<TestCollection> list = ((List<TestCollection>) genericService.getAll(TestCollection.class)).stream()
				.filter(p -> (p.getTestInstances() != null && p.getTestInstances().size() > 0)
						|| (p.getSubCollections() != null && p.getSubCollections().size() > 0))
				.collect(Collectors.toList());
		ComparatorTestCollection c = new ComparatorTestCollection();
		Collections.sort(list, c);
		return list;
	}

	public SelectModel getTestCollectionModel2() {
		return selectModelFactory.create(getTestCollections(), "title");
	}

	public SelectModel getTestCollectionModel() {
		List<TestCollection> list = getTestCollections();
		if (list == null) {
			return null;
		} else {
			ComparatorTestCollection c = new ComparatorTestCollection();
			Collections.sort(list, c);
			// return selectModelFactory.create(list, "title");
			return new TestCollectionSelectModel(list);
		}
	}

	public void onValueChanged(TestCollection newTestCollection) {
		testCollection = newTestCollection;
		ajaxResponseRenderer.addRender(collectionZone);
	}

	public List<TestInstance> getTestInstances() {
		if (userInfo.isAdministrator()) {
			if (testCollection == null) {
				return testManager.getAllTestInstancesByTestType(testType.getTestTypeId());
			} else {
				return testCollection.getTestInstances().stream()
						.filter(p -> p.getTestTemplate().getTestType().getTestTypeId() == testType.getTestTypeId())
						.collect(Collectors.toList());
			}
		} else if (userInfo.isStudent()) {
			if (testCollection == null) {
				return testManager.getTestInstancesForStudentByTestType(studentId, testType.getTestTypeId());
			} else {
				return testCollection.getTestInstances().stream()
						.filter(p -> p.getTestTemplate().getTestType().getTestTypeId() == testType.getTestTypeId())
						.collect(Collectors.toList());
			}
		} else {
			return null;
		}
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

	public List<TaskInTestInstance> getTaskInTestInstances() {
		return testManager.getTasksInTestInstance(testInstance.getTestInstanceId());
	}

	public Long getNumPersonsSuccessful() {
		return testManager.getNumPersonsSuccessfulForTaskInTestInstance(taskInTestInstance.getTaskInTestInstanceId());
	}

	public long getNumPersonsTriedToSolve() {
		return testManager.getNumPersonsTriedToSolveTaskInTestInstance(taskInTestInstance.getTaskInTestInstanceId());
	}

	public String getTranslateTestInstanceTitle() {
		String translated = translationService.getTranslation("TestInstance", "title", testInstance.getTestInstanceId(),
				persistentLocale.get().getLanguage().toLowerCase());
		return (translated != null ? translated : testInstance.getTitle());
	}

	public String getTranslateTestTypeTitle() {
		String translated = translationService.getTranslation("TestType", "title", testType.getTestTypeId(),
				persistentLocale.get().getLanguage().toLowerCase());
		return (translated != null ? translated : testType.getTitle());
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
}
