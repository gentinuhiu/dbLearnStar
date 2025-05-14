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

import dblearnstar.model.entities.Model;
import dblearnstar.model.entities.SolutionAssessment;
import dblearnstar.model.entities.Student;
import dblearnstar.model.entities.StudentStartedTest;
import dblearnstar.model.entities.StudentSubmitSolution;
import dblearnstar.model.entities.Task;
import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.model.entities.TaskType;
import dblearnstar.model.entities.TestCollection;
import dblearnstar.model.entities.TestInstance;
import dblearnstar.model.entities.TestType;

public interface TestManager {

	public boolean accessToTaskInTestInstanceAllowed(Student student, TaskInTestInstance tti);

	public List<TestInstance> getAllCurrentlyAvailableTestInstancesByTestType(long testTypeId);

	public List<SolutionAssessment> getAllEvaluationsOfSolutionsForTaskInTestInstance(long studentId,
			long taskInTestInstanceId);

	public List<Model> getAllModels();

	public List<TaskType> getAllTaskTypes();

	public List<TaskType> getAllTaskTypesDefinedOverModel(Model chosenModel);

	public List<TestInstance> getAllTestInstances();

	public List<TestInstance> getAllTestInstancesByTestType(long testTypeId);

	public List<TestInstance> getAllTestInstancesByTestTypeAndCollection(long testTypeId, long testCollectionId);

	public List<TestType> getAllTestTypes();

	public String getCodeType(StudentSubmitSolution submittedSolution);

	public List<StudentSubmitSolution> getCorrectSolutionsByStudentAndTaskInTestInstance(long studentId,
			long taskInTestInstanceId);

	public List<StudentSubmitSolution> getEvaluatedSolutionsForTaskInTestInstance(long studentId,
			long taskInTestInstanceId);

	public Float getGradeForTaskInTestInstanceByStudent(long taskInTestInstanceId, long studentId);

	public List<StudentSubmitSolution> getHistoryOfSolutions(long taskInTestInstanceId, Boolean filterNotForEvalution,
			long studentId);

	public List<StudentSubmitSolution> getIncorrectSolutionsByStudentAndTaskInTestInstance(long studentId,
			long taskInTestInstanceId);

	public Long getNumPersonsSuccessfulForTaskInTestInstance(long taskInTestInstanceId);

	public Long getNumPersonsTriedToSolveTaskInTestInstance(long taskInTestInstanceId);

	public List<StudentSubmitSolution> getSolutionsByStudent(long studentId);

	public List<StudentSubmitSolution> getSolutionsOfTaskInTestInstanceByOtherStudents(long taskInTestInstanceId,
			long studentId);

	public List<Object[]> getStudentsSolving();

	public List<Student> getStudentsWhoStartedTestInstance(TestInstance testInstance);

	public List<TaskInTestInstance> getTaskInTestInstancesByModel(long modelId);

	public List<TaskInTestInstance> getTaskInTestInstancesByTaskType(TaskType taskType);

	public List<TaskInTestInstance> getTaskInTestInstancesByTestInstance(long testInstanceId);

	public List<Task> getTasksByModel(long modelId);

	public List<TestCollection> getTestCollectionsWithTestInstances();

	public List<Object[]> getTestInstanceResultsByStudentSortedByTaskName(Student student, TestInstance testInstance);

	
	public List<TestInstance> getTestInstancesByTestType(long testTypeId);

	public List<TestInstance> getTestInstancesForStudent(long studentId);

	public List<TestInstance> getTestInstancesForStudentByTestType(long studentId, long testTypeId);

	public List<TestInstance> getTestInstancesForStudentByTestTypeAndCollection(long studentId, long testTypeId,
			long testCollectionId);

	public Float getTotalPoints(long studentId, long testInstanceId);

	public Boolean isTaskInTestInstanceSolvedByStudent(long taskInTestInstanceId, long studentId);

	public StudentStartedTest studentStartTest(long studentId, long testInstanceId);

}
