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

package dblearnstar.webapp.pages.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.slf4j.Logger;

import dblearnstar.model.entities.Model;
import dblearnstar.model.entities.Task;
import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.model.entities.TaskIsOfType;
import dblearnstar.model.entities.TaskType;
import dblearnstar.model.entities.TestInstance;
import dblearnstar.model.entities.TestInstanceParameters;
import dblearnstar.model.entities.TestTemplate;
import dblearnstar.model.entities.TestType;
import dblearnstar.model.model.ComparatorTaskInTestInstance;
import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.pages.QueryTest;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.PersonManager;
import dblearnstar.webapp.services.TestManager;
import dblearnstar.webapp.services.UsefulMethods;

@AdministratorPage
@Import(stylesheet = { "TestAdmin.css" }, module = { "zoneUpdateEffect", "bootstrap/modal", "bootstrap/collapse" })
public class TestAdminPage {

	@Inject
	private Logger logger;
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	@Inject
	private SelectModelFactory selectModelFactory;
	@Inject
	private Request request;

	@Property
	@SessionState
	private UserInfo userInfo;

	@Inject
	private PersonManager pm;
	@Inject
	private GenericService genericService;
	@Inject
	private TestManager testManager;

	@InjectComponent
	private Zone testInstanceEditZone;
	@InjectComponent
	private Zone taskInTestInstanceEditZone;
	@InjectComponent
	private Zone taskZone;
	@InjectComponent
	private Zone testTypeZone;
	@InjectComponent
	private Zone testInstanceZone;
	@InjectComponent
	private Zone testTemplateZone;

	@InjectPage
	private QueryTest queryTest;

	@Persist
	@Property
	private TestTemplate editedTestTemplate;
	@Persist
	@Property
	private TaskType chosentaskType;
	@Persist
	@Property
	private TestType testType;
	@Persist
	@Property
	private TestInstance editedTestInstance;
	@Persist
	@Property
	private TestInstanceParameters editedTestInstanceParameters;
	@Persist
	@Property
	private Boolean creatingANewTask;
	@Persist
	@Property
	private TaskInTestInstance editedTaskInTestInstance;
	@Persist
	@Property
	private Boolean isNewTaskInTestInstance;
	@Persist
	@Property
	private Task editedTask;
	@Persist
	@Property
	private TestInstance whereToPutNewTask;
	@Persist
	@Property
	private TestInstance selectedTestInstance;
	@Property
	private TestInstance listedTestInstance;
	@Property
	private TaskInTestInstance taskInTestInstance;
	@Property
	private TaskIsOfType taskIsOfType;

	public void onActivate() {
		logger.info("activated from {} by {} {}", request.getRemoteHost(), userInfo.getUserName(),
				request.getHeader("User-Agent"));
		if (selectedTestInstance != null) {
			selectedTestInstance = genericService.getByPK(TestInstance.class, selectedTestInstance.getTestInstanceId());
		}
		if (editedTestInstance != null) {
			editedTestInstance = genericService.getByPK(TestInstance.class, editedTestInstance.getTestInstanceId());
		}
	}

	public Date getCurrentTime() {
		return new Date();
	}

	public Object getModelTestTypes() {
		return selectModelFactory.create(testManager.getAllTestTypes(), "title");
	}

	public Object onValueChangedFromSelectTestType(TestType selectedtestType) {
		testType = selectedtestType;
		return testTypeZone.getBody();
	}

	public List<TestInstance> getTestInstancesByTestType() {
		return testManager.getTestInstancesByTestType(testType.getTestTypeId());
	}

	public List<TaskInTestInstance> getTaskInTestInstances() {
		List<TaskInTestInstance> list = selectedTestInstance.getTaskInTestInstances();
		ComparatorTaskInTestInstance ctti = new ComparatorTaskInTestInstance();
		Collections.sort(list, ctti);
		return list;
	}

	void onActionFromEditTestInstance(TestInstance ti) {
		editedTestInstance = ti;
		if (ti.getTestInstanceParameters().isEmpty()) {
			editedTestInstanceParameters = new TestInstanceParameters();
			editedTestInstanceParameters.setTestInstance(editedTestInstance);
		} else {
			editedTestInstanceParameters = ti.getTestInstanceParameters().get(0);
		}
		ajaxResponseRenderer.addRender(testInstanceEditZone);
	}

	void onActionFromNewTestInstance(TestType tt) {
		editedTestInstance = new TestInstance();
		if (editedTestTemplate != null) {
			editedTestInstance.setTestTemplate(editedTestTemplate);
		}
		editedTestInstanceParameters = null;
		ajaxResponseRenderer.addRender(testInstanceEditZone);
	}

	@CommitAfter
	void onSuccessFromFrmTestInstance() {
		genericService.saveOrUpdate(editedTestInstance);
		if (editedTestInstanceParameters != null) {
			genericService.saveOrUpdate(editedTestInstanceParameters);
			editedTestInstance = null;
			editedTestInstanceParameters = null;
			editedTestTemplate = null;
		} else {
			editedTestInstanceParameters = new TestInstanceParameters();
			editedTestInstanceParameters.setTestInstance(editedTestInstance);
		}
	}

	@CommitAfter
	void onSuccessFromFrmTestInstanceParameters() {
		genericService.saveOrUpdate(editedTestInstance);
		genericService.saveOrUpdate(editedTestInstanceParameters);
		editedTestInstance = null;
		editedTestInstanceParameters = null;
		editedTestTemplate = null;
	}

	void onActionFromCancelFrmTestInstance() {
		editedTestInstance = null;
	}

	public Object getTestTemplates() {
		return selectModelFactory
				.create(UsefulMethods.castList(TestTemplate.class, genericService.getAll(TestTemplate.class)), "title");
	}

	@CommitAfter
	void onActionFromDeleteTestInstance(TestInstance testInstance) {
		testInstance.getTestInstanceParameters().forEach(tip -> genericService.delete(tip));
		testInstance.getTaskInTestInstances().forEach(tti -> genericService.delete(tti));
		genericService.delete(testInstance);
	}

	@CommitAfter
	void onActionFromDuplicateTestInstance(TestInstance testInstance) {
		String lblDuplicate = " Duplicate";
		TestInstance newTI = new TestInstance();
		newTI.setTitle(testInstance.getTitle() + lblDuplicate);
		newTI.setDescription(testInstance.getDescription());
		newTI.setOpenForReviewByStudents(false);
		newTI.setOpenToAllStudents(false);
		newTI.setScheduledFor(testInstance.getScheduledFor());
		newTI.setScheduledUntil(testInstance.getScheduledUntil());
		newTI.setTestTemplate(testInstance.getTestTemplate());
		genericService.saveOrUpdate(newTI);
		for (TestInstanceParameters tip : testInstance.getTestInstanceParameters()) {
			TestInstanceParameters newTIP = new TestInstanceParameters();
			newTIP.setTestInstance(newTI);
			newTIP.setDbDriver(tip.getDbDriver());
			newTIP.setDbName(tip.getDbName());
			newTIP.setDbPass(tip.getDbPass());
			newTIP.setDbUser(tip.getDbUser());
			newTIP.setEvaluationViewPrefix(tip.getEvaluationViewPrefix());
			newTIP.setHostname(tip.getHostname());
			newTIP.setPort(tip.getPort());
			newTIP.setSchemaSimple(tip.getSchemaSimple());
			newTIP.setSchemaComplex(tip.getSchemaComplex());
			newTIP.setSchemaExam(tip.getSchemaExam());
			genericService.saveOrUpdate(newTIP);
		}
		for (TaskInTestInstance tti : testInstance.getTaskInTestInstances()) {
			TaskInTestInstance newTTI = new TaskInTestInstance();
			Task newTask = new Task();
			newTask.setTitle(tti.getTask().getTitle());
			newTask.setDescription(tti.getTask().getDescription());
			newTask.setModel(tti.getTask().getModel());
			newTask.setShortDescription(tti.getTask().getShortDescription());
			genericService.saveOrUpdate(newTask);
			for (TaskIsOfType tiot : tti.getTask().getTaskIsOfTypes()) {
				TaskIsOfType newTiot = new TaskIsOfType();
				newTiot.setTask(newTask);
				newTiot.setTaskType(tiot.getTaskType());
				genericService.saveOrUpdate(newTiot);
			}
			newTTI.setTestInstance(newTI);
			newTTI.setTask(newTask);
			newTTI.setPoints(tti.getPoints());
			genericService.saveOrUpdate(newTTI);
		}
	}

	void onActionFromEditTaskInTestInstance(TaskInTestInstance taskInTestInstance) {
		editedTaskInTestInstance = taskInTestInstance;
		isNewTaskInTestInstance = false;
		ajaxResponseRenderer.addRender(taskInTestInstanceEditZone);
	}

	void onActionFromCancelFrmTaskInTestInstance() {
		editedTaskInTestInstance = null;
	}

	public SelectModel getTasksModel() {
		TestInstance ti = genericService.getByPK(TestInstance.class,
				editedTaskInTestInstance.getTestInstance().getTestInstanceId());
		List<Task> tasks = testManager.getTasksByModel(ti.getTestTemplate().getModel().getModelId());
		List<Task> tasks2 = new ArrayList<Task>();
		if (isNewTaskInTestInstance) {
			for (Task t : tasks) {
				if (!(ti.getTaskInTestInstances().stream()
						.anyMatch(tti -> tti.getTask().getTaskId() == t.getTaskId()))) {
					tasks2.add(t);
				}
			}
		} else {
			tasks2 = tasks;
		}
		if (tasks2.size() > 0) {
			return selectModelFactory.create(tasks2, "title");
		} else {
			return null;
		}
	}

	void onActionFromEditTask(Task t) {
		editedTask = t;
		if (t.getTaskIsOfTypes() != null && t.getTaskIsOfTypes().size() > 0) {
			chosentaskType = t.getTaskIsOfTypes().get(0).getTaskType();
		}
		ajaxResponseRenderer.addRender(taskZone);
	}

	public SelectModel getAllModels() {
		return selectModelFactory.create(genericService.getAll(Model.class), "title");
	}

	void onActionFromCancelFrmTask() {
		editedTask = null;
		ajaxResponseRenderer.addRender(testInstanceZone);
	}

	@CommitAfter
	void onSuccessFromFrmTask() {
		genericService.saveOrUpdate(editedTask);

		if (creatingANewTask != null && creatingANewTask) {
			TaskIsOfType taskIsOfType = new TaskIsOfType();
			taskIsOfType.setTask(editedTask);
			taskIsOfType.setTaskType(chosentaskType);
			genericService.saveOrUpdate(taskIsOfType);

			TaskInTestInstance titi = new TaskInTestInstance();
			titi.setTask(editedTask);
			titi.setTestInstance(whereToPutNewTask);
			genericService.saveOrUpdate(titi);

			chosentaskType = null;
			whereToPutNewTask = null;
			creatingANewTask = null;
		} else {
			TaskIsOfType type = editedTask.getTaskIsOfTypes().get(0);
			type.setTaskType(chosentaskType);
			genericService.saveOrUpdate(type);
		}

		editedTask = null;
		//ajaxResponseRenderer.addRender(testInstanceZone);
	}

	void onActionFromNewTask(TestInstance testInstance) {
		creatingANewTask = true;
		editedTask = new Task();
		whereToPutNewTask = testInstance;
		editedTask.setModel(whereToPutNewTask.getTestTemplate().getModel());
	}

	void onActionFromNewTestTemplate(TestType testType) {
		editedTestTemplate = new TestTemplate();
		editedTestTemplate.setTestType(testType);
		ajaxResponseRenderer.addRender(testTemplateZone);
	}

	@CommitAfter
	void onSuccessFromFrmTestTemplate() {
		genericService.saveOrUpdate(editedTestTemplate);
		editedTestTemplate = null;
		ajaxResponseRenderer.addRender(testTemplateZone);
	}

	void onCancelFrmTestTemplate() {
		editedTestTemplate = null;
		ajaxResponseRenderer.addRender(testTemplateZone);
	}

	public SelectModel getAllTaskTypes() {
		return selectModelFactory.create(testManager.getAllTaskTypes(), "title");
	}

	@CommitAfter
	void onSuccessFromFrmTaskInTestInstance() {
		genericService.saveOrUpdate(editedTaskInTestInstance);
		editedTaskInTestInstance = null;
	}

	@CommitAfter
	void onActionFromDeleteTaskFromTestInstance(TaskInTestInstance tti) {
		genericService.delete(tti);
	}

	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private Messages messages;
	@Property
	@Persist
	private BeanModel<TestTemplate> modelTestTemplate;

	void setupRender() {
		modelTestTemplate = beanModelSource.createEditModel(TestTemplate.class, messages);
		modelTestTemplate.add("model");
		modelTestTemplate.exclude("testTemplateId");
	}

	void onShowTestInstance(TestInstance ti) {
		selectedTestInstance = ti;
	}

	void onHideTestInstance() {
		selectedTestInstance = null;
	}
}
