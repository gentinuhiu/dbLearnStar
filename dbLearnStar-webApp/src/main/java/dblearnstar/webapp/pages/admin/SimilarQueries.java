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
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.beaneditor.RelativePosition;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.beanmodel.services.PropertyConduitSource;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.util.AbstractSelectModel;
import org.slf4j.Logger;

import dblearnstar.model.entities.Student;
import dblearnstar.model.entities.StudentSubmitSolution;
import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.model.entities.TestInstance;
import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.model.StudentSelectModel;
import dblearnstar.webapp.services.EvaluationService;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.PersonManager;
import dblearnstar.webapp.services.TestManager;
import dblearnstar.webapp.services.UsefulMethods;

@AdministratorPage
@Import(stylesheet = { "SubmissionLogViewer.css" })
public class SimilarQueries {

	@Inject
	private Logger logger;
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private Messages messages;
	@Inject
	private PropertyConduitSource pcs;
	@Inject
	private SelectModelFactory selectModelFactory;
	@Inject
	private Request request;

	@Inject
	private EvaluationService evaluationService;
	@Inject
	private GenericService genericService;
	@Inject
	private TestManager testManager;
	@Inject
	private PersonManager personManager;

	@Property
	@SessionState
	private UserInfo userInfo;

	@InjectComponent
	private Zone zSubmissions;
	@InjectComponent
	private Zone zTask;
	@InjectComponent
	private Zone zStudent;

	@Property
	private Object[] redica;
	@Property
	private StudentSubmitSolution submission;

	@Persist
	@Property
	private Student filterStudent;
	@Persist
	@Property
	private TestInstance filterTestInstance;
	@Persist
	@Property
	private TaskInTestInstance filterTaskInTestInstance;
	@Persist
	@Property
	private Boolean onlyEval;
	@Persist
	@Property
	private Boolean onlyLast;
	@Persist
	@Property
	private Boolean onlyCorrect;
	@Persist
	@Property
	private Boolean hideClientInfo;

	public String getRedicaSol1() {
		return (String) redica[0];
	}

	public String getRedicaSol2() {
		return (String) redica[1];
	}

	public String getRedicaName1() {
		return (String) redica[2];
	}

	public String getRedicaName2() {
		return (String) redica[3];
	}

	public String getRedicaIp1() {
		return (String) redica[4];
	}

	public String getRedicaIp2() {
		return (String) redica[5];
	}

	public Long getRedicaDif() {
		return Math.round((Double) redica[6]);
	}

	public void onActivate() {
		logger.info("activated from {} by {} {}", request.getRemoteHost(), userInfo.getUserName(),
				request.getHeader("User-Agent"));
		if (filterStudent != null) {
			filterStudent = genericService.getByPK(Student.class, filterStudent.getStudentId());
		}
		if (filterTestInstance != null) {
			filterTestInstance = genericService.getByPK(TestInstance.class, filterTestInstance.getTestInstanceId());
		}
		if (filterTaskInTestInstance != null) {
			filterTaskInTestInstance = genericService.getByPK(TaskInTestInstance.class,
					filterTaskInTestInstance.getTaskInTestInstanceId());
		}
	}

	public List<StudentSubmitSolution> getAllSubmissions() {
		if (filterTestInstance != null) {
			if (onlyLast != null && onlyLast) {
				return evaluationService.getOnlyLastSubmissionsByStudentAndTaskInTestInstance(filterStudent,
						filterTestInstance, filterTaskInTestInstance, onlyEval, onlyCorrect);
			} else {
				return evaluationService.getSubmissionsByStudentAndTaskInTestInstance(filterStudent, filterTestInstance,
						filterTaskInTestInstance, onlyEval, onlyCorrect);
			}
		} else {
			return null;
		}
	}

	public List<Object[]> getSimilarQueries() {
		return evaluationService.getSimilarQueries();
	}

	public BeanModel<StudentSubmitSolution> getModelSSS() {
		BeanModel<StudentSubmitSolution> modelSSS = beanModelSource.createDisplayModel(StudentSubmitSolution.class,
				messages);
		modelSSS.add(RelativePosition.BEFORE, "submission", "submittedBy",
				pcs.create(StudentSubmitSolution.class, "studentStartedTest.student.person.lastName"));
		modelSSS.add(RelativePosition.BEFORE, "submission", "task",
				pcs.create(StudentSubmitSolution.class, "taskInTestInstance.task.title"));
		modelSSS.add(RelativePosition.BEFORE, "submission", "test",
				pcs.create(StudentSubmitSolution.class, "taskInTestInstance.testInstance.title"));
		if (filterStudent != null) {
			modelSSS.exclude("submittedBy");
		}
		if (filterTestInstance != null) {
			modelSSS.exclude("test");
		}
		if (onlyCorrect != null && onlyCorrect) {
			modelSSS.exclude("evaluationSimple", "evaluationComplex", "evaluationExam");
		}
		if (hideClientInfo != null && hideClientInfo) {
			modelSSS.exclude("ipAddress", "clientInfo");
		}
		modelSSS.exclude("studentSubmitSolutionId");
		return modelSSS;
	}

	public List<Student> getAllStudents() {
		if (filterTestInstance != null) {
			return testManager.getStudentsWhoStartedTestInstance(filterTestInstance);
		} else {
			return UsefulMethods.castList(Student.class, genericService.getAll(Student.class));
		}
	}

	public SelectModel getSelectTestInstanceModel() {
		return selectModelFactory.create(testManager.getAllTestInstances(), "title");
	}

	public SelectModel getSelectStudentsModel() {
		return new StudentSelectModel(getAllStudents());
	}

	public SelectModel getSelectTaskInTestInstanceModel() {

		class TaskInTestInstanceSelectModel extends AbstractSelectModel {
			private List<TaskInTestInstance> taskInTestInstances;

			public TaskInTestInstanceSelectModel(List<TaskInTestInstance> taskInTestInstances) {
				if (taskInTestInstances == null) {
					this.taskInTestInstances = new ArrayList<TaskInTestInstance>();
				} else {
					this.taskInTestInstances = taskInTestInstances;
				}
			}

			@Override
			public List<OptionGroupModel> getOptionGroups() {
				return null;
			}

			@Override
			public List<OptionModel> getOptions() {
				List<OptionModel> options = new ArrayList<OptionModel>();
				for (TaskInTestInstance taskInTestInstance : taskInTestInstances) {
					options.add(new OptionModelImpl(taskInTestInstance.getTask().getTitle(), taskInTestInstance));
				}
				return options;
			}
		}

		return new TaskInTestInstanceSelectModel(filterTestInstance.getTaskInTestInstances());
	}

	public void onValueChangedFromSelectStudent(Student newStudent) {
		logger.info("selectstudent");
		filterStudent = newStudent;
		ajaxResponseRenderer.addRender(zSubmissions);
	}

	public void onValueChangedFromSelectTestInstance(TestInstance ti) {
		filterTestInstance = ti;
		filterTaskInTestInstance = null;
		filterStudent = null;
		ajaxResponseRenderer.addRender(zStudent).addRender(zTask).addRender(zSubmissions);
	}

	public void onValueChangedFromSelectTaskInTestInstance(TaskInTestInstance tti) {
		filterTaskInTestInstance = tti;
		ajaxResponseRenderer.addRender(zSubmissions);
	}

	public void onActionFromShowUserActivities(StudentSubmitSolution selectedSubmission) {
		filterStudent = selectedSubmission.getStudentStartedTest().getStudent();
		ajaxResponseRenderer.addRender(zSubmissions);
	}

	public String getSubmittedByNameWithId() {
		return personManager.getPersonFullNameWithId(submission.getStudentStartedTest().getStudent().getPerson());
	}

	@CommitAfter
	public void onActionFromReevaluateSubmission(StudentSubmitSolution s) {
		evaluationService.processSolution(userInfo.getUserName(), s);
	}

}
