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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.slf4j.Logger;

import dblearnstar.model.entities.Student;
import dblearnstar.model.entities.StudentSubmitSolution;
import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.model.entities.TestInstance;
import dblearnstar.model.entities.TestInstanceParameters;
import dblearnstar.model.model.Triplet;

public class EvaluationServiceImpl implements EvaluationService {

	@Inject
	private Logger logger;

	@Inject
	private Messages messages;

	@Inject
	private Session session;

	private Session getEntityManager() {
		return session.getSession();
	}

	@Override
	public Triplet<List<String>, List<String>, Boolean> evalResultsIn(String userName, String queryString,
			TaskInTestInstance taskInTestInstance, TestInstanceParameters tip, String schema) {
		Triplet<List<String>, List<String>, Boolean> rslts = new Triplet<List<String>, List<String>, Boolean>(
				new ArrayList<String>(), new ArrayList<String>(), Boolean.valueOf(false));
		// rslts.firstItem will be used for resultsEval
		// rslts.secondItem will be used for resultsErrors
		// rslts.thirdItem will be used to pass evaluation result
		if (queryString != null) {
			String qtlc = queryString.toLowerCase();
			if (qtlc.contains("delete") || qtlc.contains("update") || qtlc.contains("insert") || qtlc.contains("create")
					|| qtlc.contains("drop") || qtlc.contains("alter")) {
			} else {
				Connection connection = null;
				String evalViewName = tip.getEvaluationViewPrefix() + taskInTestInstance.getTask().getTitle();

				try {
					connection = DriverManager.getConnection(
							"jdbc:postgresql://" + tip.getHostname() + ":" + tip.getPort() + "/" + tip.getDbName(),
							tip.getDbUser(), tip.getDbPass());
					connection.setClientInfo("ApplicationName", "dbLearnStarEvaluator");
					connection.setReadOnly(true);
					connection.setAutoCommit(false);
					connection.setSavepoint();
					connection.setSchema(schema);

					String queryStringManip = queryString.replace("now()", schema + ".now()");
					queryStringManip = queryStringManip.replace("current_date", schema + ".now()");

					String evalQueryString = "((" + queryStringManip + ") except (select * from " + evalViewName
							+ ")) union ((select * from " + evalViewName + ") except (" + queryStringManip + "))";

					logger.debug("user {} issued evalQueryString: {}", userName, evalQueryString);

					PreparedStatement pstmt = connection.prepareStatement(evalQueryString);
					pstmt.setQueryTimeout(30);
					ResultSet rs = pstmt.executeQuery();
					if (rs.next()) {
						// Output is not correct
						rslts.getFirstItem().add(messages.get("sql-resultIncorrect"));
						rslts.setThirdItem(false);
					} else {
						rslts.getFirstItem().add(messages.get("sql-correct"));
						rslts.setThirdItem(true);
					}
					rs.close();
					SQLWarning w = connection.getWarnings();
					if (w != null) {
						rslts.getSecondItem().add(w.getMessage());
					}
					connection.rollback();
				} catch (Exception e) {
					logger.error("Error occured {}", e.getMessage());
					if (e.getMessage().contains("ERROR: each EXCEPT query must have the same number of columns")) {
						rslts.getFirstItem().add(messages.get("sql-outputSchemaFormatError"));
					} else if (e.getMessage().contains("ERROR: EXCEPT types")) {
						rslts.getFirstItem().add(messages.get("sql-outputSchemaFormatError"));
					} else if (e.getMessage().toLowerCase().contains(evalViewName.toLowerCase())) {
						logger.error("The view: {} with the correct solution for taskInTestInstanceId: {} is missing!",
								evalViewName, taskInTestInstance.getTaskInTestInstanceId());
						rslts.getSecondItem().add(messages.get("sql-notPossibleToEvaluate"));
					} else {
						rslts.getSecondItem().add(e.getMessage());
					}
				} finally {
					if (connection != null) {
						try {
							connection.close();
						} catch (Exception e) {
							logger.error("Connection can't be closed {} {}", userName, e.getMessage());
						}
					}
				}
			}
		} else {
			rslts.getFirstItem().add("You have not submitted a query yet.");
		}
		return rslts;
	}

	@Override
	public List<Object[]> getSimilarQueries() {
		javax.persistence.Query q = getEntityManager().createNativeQuery(
				"""
						select
							sss1.submission sub1,
							sss2.submission sub2,
							p1.last_name || ' ' || p1.first_name name1,
							p2.last_name || ' ' || p2.first_name name2,
							sss1.ip_address ip1,
							sss2.ip_address ip2,
							round(extract(epoch from sss2.submitted_on-sss1.submitted_on)) dif
						from
							sql_learning.student_submit_solution sss1
						          join sql_learning.student_started_test sst1 on sss1.student_started_test_id=sst1.student_started_test_id
						          join sql_learning.student s1 on sst1.student_id=s1.student_id
						          join sql_learning.person p1 on s1.person_id=p1.person_id,
							sql_learning.student_submit_solution sss2
						          join sql_learning.student_started_test sst2 on sss2.student_started_test_id=sst2.student_started_test_id
						          join sql_learning.student s2 on sst2.student_id=s2.student_id
						          join sql_learning.person p2 on s2.person_id=p2.person_id
						       where
							sss1.task_in_test_instance_id = sss2.task_in_test_instance_id
							and sss1.submitted_on<sss2.submitted_on
							and sss1.student_started_test_id <> sss2.student_started_test_id
							and (sss1.submitted_on + interval '30 minutes')>sss2.submitted_on
							and sss1.evaluation_complex = true
							and sss2.evaluation_complex = true
							and public.similarity(sss1.submission,sss2.submission)>0.8
						      and sss2.submitted_on > (now()-interval '3 hours')
						  order by public.similarity(sss1.submission,sss2.submission) desc
						  """);
		return q.getResultList();
	}

	@Override
	public List<StudentSubmitSolution> getAllSolutionsForEvaluation() {
		return UsefulMethods.castList(StudentSubmitSolution.class, getEntityManager()
				.createQuery("from StudentSubmitSolution sss where sss.notForEvaluation=false").getResultList());
	}

	@Override
	public List<StudentSubmitSolution> getAllSolutionsForEvalutionFromTestInstance(TestInstance testInstance) {
		return UsefulMethods.castList(StudentSubmitSolution.class, getEntityManager().createQuery("""
					from StudentSubmitSolution sss where sss.notForEvaluation=false and
				sss.studentStartedTest.testInstance.testInstanceId=:testInstanceId
				""").setParameter("testInstanceId", testInstance.getTestInstanceId()).getResultList());
	}

	@Override
	public List<StudentSubmitSolution> getAllSubmissionsOrdered() {
		return UsefulMethods.castList(StudentSubmitSolution.class, getEntityManager()
				.createQuery("from StudentSubmitSolution sss order by sss.submittedOn desc").getResultList());
	}

	@Override
	public List<StudentSubmitSolution> getSubmissionsByStudentAndTaskInTestInstance(Student student,
			TestInstance testInstance, TaskInTestInstance taskInTestInstance, Boolean onlyForEval,
			Boolean onlyCorrect) {

		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<StudentSubmitSolution> criteriaQuery = builder.createQuery(StudentSubmitSolution.class);
		Root<StudentSubmitSolution> studentSubmitSolutionEnt = criteriaQuery.from(StudentSubmitSolution.class);
		Path<TaskInTestInstance> taskInTestInstanceEnt = studentSubmitSolutionEnt.join("taskInTestInstance");
		Path<TestInstance> testInstanceEnt = studentSubmitSolutionEnt.join("taskInTestInstance").join("testInstance");
		Path<Student> studentEnt = studentSubmitSolutionEnt.join("studentStartedTest").join("student");
		CriteriaQuery<StudentSubmitSolution> query = criteriaQuery.select(studentSubmitSolutionEnt);

		Predicate predicate = builder.conjunction();

		if (student != null) {
			predicate = builder.and(predicate, builder.equal(studentEnt.get("studentId"), student.getStudentId()));
		}
		if (onlyForEval != null && onlyForEval) {
			predicate = builder.and(predicate, builder.equal(studentSubmitSolutionEnt.get("notForEvaluation"), false));
		}
		if (onlyCorrect != null && onlyCorrect) {
			predicate = builder.and(predicate, builder.equal(studentSubmitSolutionEnt.get("evaluationSimple"), true));
			predicate = builder.and(predicate, builder.equal(studentSubmitSolutionEnt.get("evaluationComplex"), true));
			predicate = builder.and(predicate, builder.equal(studentSubmitSolutionEnt.get("evaluationExam"), true));
		}
		if (taskInTestInstance != null) {
			predicate = builder.and(predicate, builder.equal(taskInTestInstanceEnt.get("taskInTestInstanceId"),
					taskInTestInstance.getTaskInTestInstanceId()));
		} else if (testInstance != null) {
			predicate = builder.and(predicate,
					builder.equal(testInstanceEnt.get("testInstanceId"), testInstance.getTestInstanceId()));
		} else {
			return null;
		}

		query.where(predicate);
		query.orderBy(builder.desc(studentSubmitSolutionEnt.get("submittedOn")));

		TypedQuery<StudentSubmitSolution> tq = getEntityManager().createQuery(criteriaQuery);

		return UsefulMethods.castList(StudentSubmitSolution.class, tq.getResultList());
	}

	@Override
	public List<StudentSubmitSolution> getOnlyLastSubmissionsByStudentAndTaskInTestInstance(Student student,
			TestInstance testInstance, TaskInTestInstance taskInTestInstance, Boolean onlyForEval,
			Boolean onlyCorrect) {
		try {
			if (testInstance == null) {
				return null;
			}
			String queryStringIntro = """
					from StudentSubmitSolution sssL where
					sssL.taskInTestInstance.testInstance.testInstanceId=:testInstanceId
					""";
			String queryStringMiddle = "";
			if (student != null) {
				queryStringMiddle += " and sssL.studentStartedTest.student.studentId=:studentId ";
			}
			if (taskInTestInstance != null) {
				queryStringMiddle += " and sssL.taskInTestInstance.taskInTestInstanceId=:taskInTestInstanceId ";
			}
			if (onlyCorrect != null && onlyCorrect) {
				queryStringMiddle += """
						and sssL.evaluationSimple=true
						and sssL.evaluationComplex=true
						and sssL.evaluationExam=true
						""";
			}
			if (onlyForEval != null && onlyForEval) {
				queryStringMiddle += " and sssL.notForEvaluation=false ";
			}
			String queryStringOutro = """
					and sssL.submittedOn>=all (
					    select sssR.submittedOn from StudentSubmitSolution sssR
					    where
					    	sssL.studentStartedTest.student.studentId=sssR.studentStartedTest.student.studentId and
					    	sssL.taskInTestInstance.taskInTestInstanceId=sssR.taskInTestInstance.taskInTestInstanceId
					) order by sssL.submittedOn desc
					""";
			javax.persistence.Query q = getEntityManager()
					.createQuery(queryStringIntro + queryStringMiddle + queryStringOutro);
			q.setParameter("testInstanceId", testInstance.getTestInstanceId());
			if (student != null) {
				q.setParameter("studentId", student.getStudentId());
			}
			if (taskInTestInstance != null) {
				q.setParameter("taskInTestInstanceId", taskInTestInstance.getTaskInTestInstanceId());
			}
			return UsefulMethods.castList(StudentSubmitSolution.class, q.getResultList());
		} catch (Exception e) {
			logger.debug("Failed {}", e);
			return null;
		}
	}

	public void processSolution(String issuedByUserName, StudentSubmitSolution s) {
		Triplet<List<String>, List<String>, Boolean> rsltsSimple = evalResultsIn(issuedByUserName, s.getSubmission(),
				s.getTaskInTestInstance(),
				s.getTaskInTestInstance().getTestInstance().getTestInstanceParameters().get(0),
				s.getTaskInTestInstance().getTestInstance().getTestInstanceParameters().get(0).getSchemaSimple());
		Triplet<List<String>, List<String>, Boolean> rsltsComplex = evalResultsIn(issuedByUserName, s.getSubmission(),
				s.getTaskInTestInstance(),
				s.getTaskInTestInstance().getTestInstance().getTestInstanceParameters().get(0),
				s.getTaskInTestInstance().getTestInstance().getTestInstanceParameters().get(0).getSchemaComplex());
		logger.info("Reevaluation studentSubmitSolutionId Simple: {} reevaluated as {}", s.getStudentSubmitSolutionId(),
				rsltsSimple.getThirdItem());
		logger.info("Reevaluation studentSubmitSolutionId Complex: {} reevaluated as {}",
				s.getStudentSubmitSolutionId(), rsltsComplex.getThirdItem());
		if (rsltsSimple.getThirdItem() == true) {
			s.setEvaluationSimple(true);
		} else {
			s.setEvaluationSimple(false);
		}
		if (rsltsComplex.getThirdItem() == true) {
			s.setEvaluationComplex(true);
		} else {
			s.setEvaluationComplex(false);
		}
		s.setEvaluationExam(s.getEvaluationSimple() && s.getEvaluationComplex());
		getEntityManager().saveOrUpdate(s);
	}

}
