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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.slf4j.Logger;

import dblearnstar.model.entities.SolutionAssessment;
import dblearnstar.model.entities.StudentSubmitSolution;
import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.model.entities.TestInstance;
import dblearnstar.model.model.ModelConstants;
import dblearnstar.model.model.TaskTypeChecker;
import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.StudentPage;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.PersonManager;
import dblearnstar.webapp.services.TestManager;
import dblearnstar.webapp.util.AppConfig;
import dblearnstar.webapp.util.FileStreamResponse;

@StudentPage
@Import(stylesheet = { "SolutionComparatorPage.css" }, module = { "bootstrap/collapse" })
public class SolutionComparatorPage {
	@SessionState
	private UserInfo userInfo;

	@Inject
	private Logger logger;
	@Inject
	private SelectModelFactory selectModelFactory;
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	@Inject
	private PersonManager pm;
	@Inject
	private TestManager testManager;
	@Inject
	private GenericService genericService;

	@InjectComponent
	private Zone zTestInstance;

	@Persist
	@Property
	private TestInstance selectedTestInstance;

	@Property
	private StudentSubmitSolution otherStudentSubmitSolution;
	@Property
	private List<TaskInTestInstance> taskInTestInstances;
	@Property
	private TaskInTestInstance taskInTestInstance;
	@Property
	private float totalPoints;
	@Property
	Map<TaskInTestInstance, SolutionAssessment> mapTTItoSA;

	private long studentId;

	public void prepareMapOfEvaluationsAndTotal() {
		totalPoints = 0;
		mapTTItoSA = new HashMap<TaskInTestInstance, SolutionAssessment>();
		for (TaskInTestInstance tti : taskInTestInstances) {
			List<StudentSubmitSolution> lista = testManager.getEvaluatedSolutionsForTaskInTestInstance(studentId,
					tti.getTaskInTestInstanceId());
			if (lista != null && lista.size() > 0) {
				SolutionAssessment sa;
				List<StudentSubmitSolution> podlista = lista.stream().filter(p -> p.getEvaluations().size() > 0)
						.collect(Collectors.toList());
				if (podlista != null && podlista.size() > 0) {
					sa = podlista.get(0).getEvaluations().get(0);
				} else {
					if (lista.get(0).getEvaluations() != null && lista.get(0).getEvaluations().size() > 0) {
						sa = lista.get(0).getEvaluations().get(0);
					} else {
						sa = null;
					}
				}
				mapTTItoSA.put(tti, sa);
				if (sa != null && sa.getGrade() != null) {
					totalPoints += sa.getGrade();
				}
			} else {
				mapTTItoSA.put(tti, null);
			}
		}
	}

	public void onActivate() {
		studentId = pm.getStudentsByPersonId(userInfo.getPersonId()).get(0).getStudentId();
		if (selectedTestInstance != null) {
			selectedTestInstance = genericService.getByPK(TestInstance.class, selectedTestInstance.getTestInstanceId());
			taskInTestInstances = testManager.getTasksInTestInstance(selectedTestInstance.getTestInstanceId());
			prepareMapOfEvaluationsAndTotal();
		}
	}

	public StudentSubmitSolution getLastSolutionForTaskInTestInstance() {
		// if there are correct solutions, return the last correct, and if there
		// are not return the last evaluated solution
		List<StudentSubmitSolution> sss = testManager.getCorrectSolutionsByStudentAndTaskInTestInstance(studentId,
				taskInTestInstance.getTaskInTestInstanceId());
		if (sss == null || sss.isEmpty()) {
			sss = testManager.getIncorrectSolutionsByStudentAndTaskInTestInstance(studentId,
					taskInTestInstance.getTaskInTestInstanceId());
		}
		if (sss.isEmpty()) {
			return null;
		} else {
			return sss.get(0);
		}
	}

	public StudentSubmitSolution getLastEvaluatedSolutionForTaskInTestInstance() {
		SolutionAssessment sa = mapTTItoSA.get(taskInTestInstance);
		if (sa != null) {
			return sa.getStudentSubmitSolution();
		} else {
			return null;
		}
	}

	public boolean isLastSolutionCorrect() {
		StudentSubmitSolution sss = getLastEvaluatedSolutionForTaskInTestInstance();
		if (sss.getTaskInTestInstance().getTask().getTaskIsOfTypes().get(0).getTaskType().getCodetype()
				.equals(ModelConstants.TaskCodeSQL)) {
			return sss.getEvaluationSimple() && sss.getEvaluationComplex();
		} else {
			return sss.getEvaluations().stream().anyMatch(p -> p.getPassed());
		}
	}

	public String getClassLastSolutionForTaskInTestInstance() {
		StudentSubmitSolution sss = getLastEvaluatedSolutionForTaskInTestInstance();
		if (sss != null) {
			if (isLastSolutionCorrect()) {
				return "correct";
			} else {
				return "incorrect";
			}
		} else {
			return "";
		}
	}

	public List<StudentSubmitSolution> getOtherSolutions() {
		StudentSubmitSolution sss = getLastEvaluatedSolutionForTaskInTestInstance();
		if (sss.getTaskInTestInstance().getTask().getTaskIsOfTypes().get(0).getTaskType().getCodetype()
				.equals(ModelConstants.TaskCodeSQL)) {
			if (sss.getEvaluationSimple() && sss.getEvaluationComplex()) {
				return testManager.getSolutionsOfTaskInTestInstanceByOtherStudents(
						sss.getTaskInTestInstance().getTaskInTestInstanceId(), studentId);
			} else {
				return null;
			}
		} else {
			return sss.getTaskInTestInstance().getStudentSubmitSolutions().stream()
					.filter(p -> (p.getEvaluations() != null && p.getEvaluations().size() > 0
							&& p.getEvaluations().get(0).getPassed()
							&& p.getStudentSubmitSolutionId() != sss.getStudentSubmitSolutionId()))
					.collect(Collectors.toList());
		}
	}

	public List<TestInstance> getTestInstances() {
		if (userInfo.isAdministrator()) {
			return testManager.getAllTestInstances();
		} else if (userInfo.isStudent()) {
			long studentId = pm.getStudentsByPersonId(userInfo.getPersonId()).get(0).getStudentId();
			return testManager.getTestInstancesForStudent(studentId);
		} else {
			return null;
		}
	}

	public SelectModel getTestInstanceSelectModel() {
		return selectModelFactory.create(getTestInstances(), "title");
	}

	public void onValueChanged(TestInstance newTestInstance) {
		selectedTestInstance = newTestInstance;
		if (selectedTestInstance != null) {
			taskInTestInstances = testManager.getTasksInTestInstance(selectedTestInstance.getTestInstanceId());
		} else {
			taskInTestInstances = new ArrayList<>();
		}
		prepareMapOfEvaluationsAndTotal();
		ajaxResponseRenderer.addRender(zTestInstance);
	}

	private StreamResponse onActionFromDownloadFile(StudentSubmitSolution sss) throws FileNotFoundException {
		return downloadFile(sss);
	}

	private StreamResponse onActionFromDownloadFileOther(StudentSubmitSolution sss) throws FileNotFoundException {
		return downloadFile(sss);
	}

	private StreamResponse downloadFile(StudentSubmitSolution sss) throws FileNotFoundException {
		taskInTestInstance = sss.getTaskInTestInstance();

		String subm = sss.getSubmission();
		String[] listSubm = subm.split("#");
		String origFileName = "";
		String savedFileName = "";
		for (String s : listSubm) {
			if (s.startsWith("FILE:")) {
				origFileName = s;
			}
			if (s.startsWith("SAVEDFILE:")) {
				savedFileName = s;
			}
		}
		savedFileName = savedFileName.replace("SAVEDFILE:", "");
		origFileName = origFileName.replace("FILE:", "");

		File file = new File(AppConfig.getString("additionalFiles.path")
				+ AppConfig.getString("upload.path.submissions") + "/" + savedFileName);

		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		try {
			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum);
				System.out.println("read " + readNum + " bytes,");
			}
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		}

		ByteArrayInputStream bais = new ByteArrayInputStream(bos.toByteArray());
		InputStream is = bais;
		return new FileStreamResponse(is, origFileName);
	}

	public String processFileNameFromSubmission(StudentSubmitSolution sss) {
		String subm = sss.getSubmission();
		String[] listSubm = subm.split("#");
		String output = "";
		for (String s : listSubm) {
			if (s.startsWith("FILE:")) {
				output += "<p>" + s + "</p>";
			}
			if (s.startsWith("COMMENT:")) {
				output += "<p>" + s + "</p>";
			}
		}
		return output;
	}

	public String getFileFromSubmission() {
		return processFileNameFromSubmission(getLastEvaluatedSolutionForTaskInTestInstance());
	}

	public String getFileFromOtherStudentSubmission() {
		return processFileNameFromSubmission(otherStudentSubmitSolution);
	}

	public String getCodeType() {
		return taskInTestInstance.getTask().getTaskIsOfTypes().get(0).getTaskType().getCodetype();
	}

	public boolean isSQL() {
		return TaskTypeChecker.isSQL(getCodeType());
	}

	public boolean isTEXT() {
		return TaskTypeChecker.isTEXT(getCodeType());
	}

	public boolean isDDL() {
		return TaskTypeChecker.isDDL(getCodeType());
	}

	public boolean isUPLOAD() {
		return TaskTypeChecker.isUPLOAD(getCodeType());
	}

	public SolutionAssessment getSubmissionsFirstEvaluation() {
		StudentSubmitSolution ssa = getLastEvaluatedSolutionForTaskInTestInstance();
		if (ssa != null && ssa.getEvaluations() != null && ssa.getEvaluations().size() > 0) {
			return ssa.getEvaluations().get(0);
		} else {
			return null;
		}

	}

}
