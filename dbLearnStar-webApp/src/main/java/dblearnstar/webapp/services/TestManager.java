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

package dblearnstar.webapp.services;

import java.util.List;

import dblearnstar.model.entities.Person;
import dblearnstar.model.entities.SolutionAssessment;
import dblearnstar.model.entities.Student;
import dblearnstar.model.entities.StudentStartedTest;
import dblearnstar.model.entities.StudentSubmitSolution;
import dblearnstar.model.entities.Task;
import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.model.entities.TestInstance;

public interface TestManager {

	public List<TestInstance> getAllTestInstances();

	public List<TestInstance> getAllTestInstancesByTestType(long testTypeId);

	public List<TestInstance> getAllCurrentlyAvailableTestInstancesByTestType(long testTypeId);

	public List<TestInstance> getTestInstancesForStudent(long studentId);

	public List<TestInstance> getTestInstancesForStudentByTestType(long studentId, long testTypeId);

	public Boolean isTaskInTestInstanceSolvedByStudent(long taskInTestInstanceId, long studentId);

	public Float getGradeForTaskInTestInstanceByStudent(long taskInTestInstanceId, long studentId);

	public Long getNumPersonsSuccessfulForTaskInTestInstance(long taskInTestInstanceId);

	public Long getNumPersonsTriedToSolveTaskInTestInstance(long taskInTestInstanceId);

	public List<Object[]> getStudentsSolving();

	public List<StudentSubmitSolution> getHistoryOfSolutions(long taskInTestInstanceId, Boolean filterNotForEvalution,
			long studentId);

	public StudentStartedTest studentStartTest(long studentId, long testInstanceId);

	public List<StudentSubmitSolution> getSolutionsByStudent(long studentId);

	public List<StudentSubmitSolution> getSolutionsOfTaskInTestInstanceByOtherStudents(long taskInTestInstanceId,
			long studentId);

	public List<TestInstance> getTestInstancesByTestType(long testTypeId);

	public List<Task> getTasksByModel(long modelId);

	public List<StudentSubmitSolution> getCorrectSolutionsByStudentAndTaskInTestInstance(long studentId,
			long taskInTestInstanceId);

	public List<StudentSubmitSolution> getIncorrectSolutionsByStudentAndTaskInTestInstance(long studentId,
			long taskInTestInstanceId);

	public List<TaskInTestInstance> getTasksInTestInstance(long testInstanceId);

	public void recordActivityInTask(Person person, TaskInTestInstance taskInTestInstance, String type, String payload);

	public List<Student> getStudentsWhoStartedTestInstance(TestInstance testInstance);

	public List<StudentSubmitSolution> getEvaluatedSolutionsForTaskInTestInstance(long studentId,
			long taskInTestInstanceId);

	public List<SolutionAssessment> getAllEvaluationsOfSolutionsForTaskInTestInstance(long studentId,
			long taskInTestInstanceId);

	public Float getTotalPoints(long studentId, long testInstanceId);

	public boolean accessToTaskInTestInstanceAllowed(Student student, TaskInTestInstance tti);

	public String getCodeType(StudentSubmitSolution submittedSolution);
}
