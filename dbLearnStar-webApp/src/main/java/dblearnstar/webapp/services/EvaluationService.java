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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dblearnstar.model.entities.Group;
import dblearnstar.model.entities.GroupMember;
import dblearnstar.model.entities.SolutionAssessment;
import dblearnstar.model.entities.Student;
import dblearnstar.model.entities.StudentSubmitSolution;
import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.model.entities.TestInstance;
import dblearnstar.model.entities.TestInstanceParameters;
import dblearnstar.model.model.Triplet;

public interface EvaluationService {

	public Triplet<List<String>, List<String>, Boolean> evalResultsIn(String userName, String queryString,
			TaskInTestInstance taskInTestInstance, TestInstanceParameters tip, String schema);

	public List<Object[]> getSimilarQueries();

	public List<StudentSubmitSolution> getAllSolutionsForEvaluation();

	public List<StudentSubmitSolution> getAllSolutionsForEvalutionFromTestInstance(TestInstance selectedTestInstance);

	public List<StudentSubmitSolution> getAllSolutionsForEvalutionFromTaskInTestInstance(
			TaskInTestInstance taskInTestInstance);

	public List<StudentSubmitSolution> getAllSubmissionsOrdered();

	public List<StudentSubmitSolution> getSubmissionsByStudentAndTaskInTestInstance(Student student,
			TestInstance testInstance, TaskInTestInstance taskInTestInstance, Boolean onlyForEval, Boolean onlyCorrect);

	public List<StudentSubmitSolution> getOnlyLastSubmissionsByStudentAndTaskInTestInstance(Student student,
			TestInstance testInstance, TaskInTestInstance taskInTestInstance, Boolean onlyForEval, Boolean onlyCorrect);

	public void reEvalSolution(String issuedByUserName, StudentSubmitSolution s);

	public Triplet<List<Object[]>, List<String>, List<String>> getResultsForPrintingPurposes(String userName,
			String queryToRun, String correctQuery, Map<String, String> testCases, TestInstanceParameters tip, String schema, String type);

	/**
	 * @return <evaluationData, resultsHeadersSimple, resultsErrors>
	 */
	Triplet<List<Object[]>, List<String>, List<String>> getEvalResultsForViewing(String userName, String queryToRun,
			TaskInTestInstance taskInTestInstance, TestInstanceParameters tip, String schema);

	public List<String[]> execQuery(StudentSubmitSolution submission, Connection connection, String gradingSchema,
			String string) throws SQLException;

	public Triplet<List<String[]>, List<String>, List<String>> getDDLEvaluationDataFromStudentDatabases(
			List<StudentSubmitSolution> submissions);

	public List<SolutionAssessment> getSolutionAssessmentsWithDiscussionForTestInstance(long testInstanceId);

	public void reEvalListOfSubmissions(String userName, List<StudentSubmitSolution> list);

	public List<GroupMember> groupMembersSortedByTestTotals(Group selectedGroup);

}
