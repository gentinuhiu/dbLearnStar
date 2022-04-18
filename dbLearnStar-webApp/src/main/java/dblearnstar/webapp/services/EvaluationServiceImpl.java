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
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import dblearnstar.webapp.model.ApplicationConstants;
import dblearnstar.webapp.util.AppConfig;

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

	/**
	 * @return < resultsEval, resultsErrors, evaluation result >
	 */
	@Override
	public Triplet<List<String>, List<String>, Boolean> evalResultsIn(String userName, String queryToRun,
			TaskInTestInstance taskInTestInstance, TestInstanceParameters tip, String schema) {
		Triplet<List<String>, List<String>, Boolean> rslts = new Triplet<List<String>, List<String>, Boolean>(
				new ArrayList<String>(), new ArrayList<String>(), Boolean.valueOf(false));

		if (queryToRun != null) {
			String qtlc = queryToRun.toLowerCase();
			if (qtlc.contains("delete") || qtlc.contains("update") || qtlc.contains("insert") || qtlc.contains("create")
					|| qtlc.contains("drop") || qtlc.contains("alter") || qtlc.contains("information_schema")) {
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

					String queryStringManip = queryToRun.replace("now()", schema + ".now()");
					queryStringManip = queryStringManip.replace("current_date", schema + ".now()");

					String evalQueryString = "select *, 'EDEN' from ( ( " + queryStringManip
							+ " ) except ( select * from " + evalViewName
							+ " ) ) eden union select *, 'DVA' from ( ( select * from " + evalViewName + " ) except ( "
							+ queryStringManip + " ) ) dva ";

					logger.debug("user {} issued evalQueryString: {}", userName, evalQueryString);

					PreparedStatement pstmt = connection.prepareStatement(evalQueryString);
					pstmt.setQueryTimeout(120);
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
					pstmt.close();
					SQLWarning w = connection.getWarnings();
					if (w != null) {
						rslts.getSecondItem().add(w.getMessage());
					}
					connection.rollback();
				} catch (Exception e) {
					logger.error("Error occured {}", e);
					if (e.getMessage().contains("ERROR: each EXCEPT query must have the same number of columns")) {
						rslts.getFirstItem().add(messages.get("sql-outputSchemaFormatError"));
					} else if (e.getMessage().contains("ERROR: EXCEPT types")) {
						rslts.getFirstItem().add(messages.get("sql-outputSchemaFormatError"));
					} else if (e.getMessage().toLowerCase().contains(evalViewName.toLowerCase())) {
						logger.error("The view: {} with the correct solution for taskInTestInstanceId: {} is missing!",
								evalViewName, taskInTestInstance.getTaskInTestInstanceId());
						rslts.getSecondItem().add(messages.get("sql-notPossibleToEvaluate"));
					} else if (e.getMessage().toLowerCase().contains("DateStyle parameter was changed")) {
						logger.error("" + e);
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
					and sssL.submittedOn in (
					    select max(sssR.submittedOn) from StudentSubmitSolution sssR
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
		TaskInTestInstance tti = s.getTaskInTestInstance();
		/*
		 * TODO: Only supports TestInstances with a single TestInstanceParameter line
		 */
		TestInstanceParameters tip = tti.getTestInstance().getTestInstanceParameters().get(0);

		Triplet<List<String>, List<String>, Boolean> rsltsSimple = evalResultsIn(issuedByUserName, s.getSubmission(),
				tti, tip, tip.getSchemaSimple());
		Triplet<List<String>, List<String>, Boolean> rsltsComplex = evalResultsIn(issuedByUserName, s.getSubmission(),
				tti, tip, tip.getSchemaComplex());
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

	/**
	 * @return <evaluationData, resultsHeadersSimple, resultsErrors>
	 */
	@Override
	public Triplet<List<Object[]>, List<String>, List<String>> getResultsForPrintingPurposes(String userName,
			String queryToRun, TestInstanceParameters tip, String schema, String type) {
		List<Object[]> resultsSimple = new ArrayList<Object[]>();
		List<String> resultsHeadersSimple = new ArrayList<String>();
		List<String> resultsErrors = new ArrayList<String>();

		if (queryToRun != null) {
			String qtlc = queryToRun.toLowerCase();
			if (qtlc.contains("delete") || qtlc.contains("update") || qtlc.contains("insert") || qtlc.contains("create")
					|| qtlc.contains("drop") || qtlc.contains("alter") || qtlc.contains("information_schema")) {
				resultsErrors.add(messages.get("sql-db-modifications"));
				logger.error("Database modification or catalog or directory query issued by {}", userName);
			} else {
				resultsSimple = new ArrayList<Object[]>();
				resultsHeadersSimple = new ArrayList<String>();

				Connection connection = null;
				int statusCounter = 0;
				try {
					Properties props = new Properties();
					props.setProperty("user", tip.getDbUser());
					props.setProperty("password", tip.getDbPass());
					String url = "jdbc:postgresql://" + tip.getHostname() + ":" + tip.getPort() + "/" + tip.getDbName();

					connection = DriverManager.getConnection(url, props);
					statusCounter = 1;

					connection.setClientInfo("ApplicationName", "dbLearn*Evaluator");
					connection.setReadOnly(true);
					connection.setAutoCommit(false);
					connection.setSavepoint();
					connection.setSchema(schema);
					statusCounter = 2;

					ResultSet rs = connection.prepareStatement(queryToRun).executeQuery();
					boolean isNextRow = rs.next();
					int numColumns = rs.getMetaData().getColumnCount();
					for (int i = 1; i <= numColumns; i++) {
						resultsHeadersSimple.add(rs.getMetaData().getColumnName(i));
					}
					statusCounter = 3;

					int count = 1;
					while (isNextRow && count < 500) {
						Object[] o = new Object[numColumns];
						for (int i = 1; i <= numColumns; i++) {
							o[i - 1] = rs.getObject(i);
						}
						resultsSimple.add(o);
						isNextRow = rs.next();
						count++;
					}
					statusCounter = 4;
					rs.close();
					SQLWarning w = connection.getWarnings();
					if (w != null) {
						logger.debug("warning");
						resultsErrors.add(w.getMessage());
					}
					if (count >= 500) {
						logger.debug("count");
						resultsErrors.add(messages.get("sql-moreThan500Rows"));
					}
					connection.rollback();
				} catch (Exception e) {
					if (statusCounter == 0) {
						logger.error("Error when connecting to evaluation database for testinstance: {}",
								tip.getTestInstance().getTestInstanceId());
						logger.debug("Exception: {}", e);
						resultsErrors.add(messages.get("evalDBNA-label"));
					} else {
						logger.error(
								"Connected to evaluation database for test instance {}, but failed in running query: {} ",
								tip.getTestInstance().getTestInstanceId(), queryToRun);
						logger.debug("Exception: {}", e);
						resultsErrors.add(e.getMessage());
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
			resultsSimple = new ArrayList<Object[]>();
			resultsHeadersSimple = new ArrayList<String>();
		}

		Triplet<List<Object[]>, List<String>, List<String>> results = new Triplet<List<Object[]>, List<String>, List<String>>(
				resultsSimple, resultsHeadersSimple, resultsErrors);
		return results;
	}

	/**
	 * @return <evaluationData, resultsHeadersSimple, resultsErrors>
	 */
	@Override
	public Triplet<List<Object[]>, List<String>, List<String>> getEvalResultsForViewing(String userName,
			String queryToRun, TaskInTestInstance taskInTestInstance, TestInstanceParameters tip, String schema) {
		List<Object[]> resultsSimple = new ArrayList<Object[]>();
		List<String> resultsHeadersSimple = new ArrayList<String>();
		List<String> resultsErrors = new ArrayList<String>();

		if (queryToRun != null) {
			String qtlc = queryToRun.toLowerCase();
			if (qtlc.contains("delete") || qtlc.contains("update") || qtlc.contains("insert") || qtlc.contains("create")
					|| qtlc.contains("drop") || qtlc.contains("alter") || qtlc.contains("information_schema")) {
				resultsErrors.add(messages.get("sql-db-modifications"));
				logger.error("Database modification or catalog or directory query issued by {}", userName);
			} else {
				resultsSimple = new ArrayList<Object[]>();
				resultsHeadersSimple = new ArrayList<String>();

				Connection connection = null;
				String evalViewName = tip.getEvaluationViewPrefix() + taskInTestInstance.getTask().getTitle();

				int statusCounter = 0;

				try {
					Properties props = new Properties();
					props.setProperty("user", tip.getDbUser());
					props.setProperty("password", tip.getDbPass());
					String url = "jdbc:postgresql://" + tip.getHostname() + ":" + tip.getPort() + "/" + tip.getDbName();

					connection = DriverManager.getConnection(url, props);
					statusCounter = 1;

					connection.setClientInfo("ApplicationName", "dbLearnStarEvaluator");
					connection.setReadOnly(true);
					connection.setAutoCommit(false);
					connection.setSavepoint();
					connection.setSchema(schema);
					statusCounter = 2;

					String queryStringManip = queryToRun.replace("now()", schema + ".now()");
					queryStringManip = queryStringManip.replace("current_date", schema + ".now()");

//					String evalQueryString = "((" + queryStringManip + ") except (select * from " + evalViewName
//							+ ")) union ((select * from " + evalViewName + ") except (" + queryStringManip + "))";

					String evalQueryString = "select '<span class=\"inSubmission\">In Submission</span>' as WHERE, *   from ( ( "
							+ queryStringManip + " ) except ( select * from " + evalViewName
							+ " ) ) eden union select '<span class=\"inCorrectSolution\">In Correct Solution</span>' as WHERE, *  from ( ( select * from "
							+ evalViewName + " ) except ( " + queryStringManip + " ) ) dva order by 1,2";

					logger.debug("user {} issued evalQueryString: {}", userName, evalQueryString);

					PreparedStatement pstmt = connection.prepareStatement(evalQueryString);
					pstmt.setQueryTimeout(120);
					ResultSet rs = pstmt.executeQuery();
					boolean isNextRow = rs.next();
					int numColumns = rs.getMetaData().getColumnCount();
					for (int i = 1; i <= numColumns; i++) {
						resultsHeadersSimple.add(rs.getMetaData().getColumnName(i));
					}
					statusCounter = 3;

					int count = 1;
					while (isNextRow && count < 500) {
						Object[] o = new Object[numColumns];
						for (int i = 1; i <= numColumns; i++) {
							o[i - 1] = rs.getObject(i);
						}
						resultsSimple.add(o);
						isNextRow = rs.next();
						count++;
					}
					statusCounter = 4;
					rs.close();
					SQLWarning w = connection.getWarnings();
					if (w != null) {
						logger.debug("warning");
						resultsErrors.add(w.getMessage());
					}
					if (count >= 500) {
						logger.debug("count");
						resultsErrors.add(messages.get("sql-moreThan500Rows"));
					}
					connection.rollback();
				} catch (Exception e) {
					if (statusCounter == 0) {
						logger.error("Error when connecting to evaluation database for testinstance: {}",
								tip.getTestInstance().getTestInstanceId());
						logger.debug("Exception: {}", e);
						resultsErrors.add(messages.get("evalDBNA-label"));
					} else {
						if (e.getMessage().contains("ERROR: each EXCEPT query must have the same number of columns")) {
							resultsErrors.add(messages.get("sql-outputSchemaFormatError"));
						} else if (e.getMessage().contains("ERROR: EXCEPT types")) {
							resultsErrors.add(messages.get("sql-outputSchemaFormatError"));
						} else if (e.getMessage().toLowerCase().contains(evalViewName.toLowerCase())) {
							logger.error(
									"The view: {} with the correct solution for taskInTestInstanceId: {} is missing!",
									evalViewName, taskInTestInstance.getTaskInTestInstanceId());
							resultsErrors.add(messages.get("sql-notPossibleToEvaluate"));
						} else if (e.getMessage().toLowerCase().contains("DateStyle parameter was changed")) {
							logger.error("" + e);
						} else {
							resultsErrors.add(e.getMessage());
						}
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
			resultsSimple = new ArrayList<Object[]>();
			resultsHeadersSimple = new ArrayList<String>();
		}

		return new Triplet<List<Object[]>, List<String>, List<String>>(resultsSimple, resultsHeadersSimple,
				resultsErrors);
	}

	@Override
	public List<String[]> execQuery(StudentSubmitSolution submission, Connection connection, String gradingSchema,
			String queryToRun) throws SQLException {
		List<String[]> resultData = new ArrayList<String[]>();

		PreparedStatement stat = connection.prepareStatement(queryToRun);
		if (gradingSchema != null) {
			stat.setString(1, gradingSchema);
		}
		ResultSet rs = stat.executeQuery();
		boolean isNextRow = rs.next();
		int numColumns = rs.getMetaData().getColumnCount();

		logger.debug("Executing Query {} {} {} ", submission.getStudentStartedTest().getStudent().getPerson().getUserName(), queryToRun,
				numColumns);

		while (isNextRow) {
			String[] o = new String[numColumns + 2];
			o[0] = Long.toString(submission.getStudentSubmitSolutionId());
			o[1] = null; // grade
			for (int i = 1; i <= numColumns; i++) {
				Object ofromq = rs.getObject(i);
				if (ofromq == null) {
					o[i + 1] = null;
				} else {
					o[i + 1] = ofromq.toString();
				}
			}
			resultData.add(o);
			isNextRow = rs.next();
		}
		rs.close();

		return resultData;
	}

	@Override
	public Triplet<List<String[]>, List<String>, List<String>> getDDLEvaluationDataFromStudentDatabases(
			List<StudentSubmitSolution> submissions) {
		List<String[]> ddlEvaluationData = new ArrayList<String[]>();
		List<String> resultsHeadersSimple = new ArrayList<String>();
		List<String> resultsErrors = new ArrayList<String>();

		String[] queryToRun = {
				"""
						select
							'TABLE' type,
						 	current_database() as DB,
						 	table_name,
							(select column_name  from information_schema.columns isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and ordinal_position=1) col1,
							(select column_name  from information_schema.columns isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and ordinal_position=2) col2,
							(select column_name  from information_schema.columns isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and ordinal_position=3) col3,
							(select column_name  from information_schema.columns isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and ordinal_position=4) col4,
							(select column_name  from information_schema.columns isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and ordinal_position=5) col5,
							(select column_name  from information_schema.columns isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and ordinal_position=6) col6,
							(select column_name  from information_schema.columns isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and ordinal_position=7) col7,
							(select column_name  from information_schema.columns isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and ordinal_position=8) col8,
							(select column_name  from information_schema.columns isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and ordinal_position=9) col9
						from information_schema.tables ist
						where table_schema=?
						order by table_catalog, table_name
						""",
				"""
						select 'PK',
							current_database() as DB,
							table_name,
							(select column_name  from information_schema.key_column_usage isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and isc.constraint_name=ist.constraint_name and isc.ordinal_position=1) col1,
							(select column_name  from information_schema.key_column_usage isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and isc.constraint_name=ist.constraint_name and isc.ordinal_position=2) col2,
							(select column_name  from information_schema.key_column_usage isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and isc.constraint_name=ist.constraint_name and isc.ordinal_position=3) col3,
							(select column_name  from information_schema.key_column_usage isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and isc.constraint_name=ist.constraint_name and isc.ordinal_position=4) col4,
							(select column_name  from information_schema.key_column_usage isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and isc.constraint_name=ist.constraint_name and isc.ordinal_position=5) col5,
							(select column_name  from information_schema.key_column_usage isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and isc.constraint_name=ist.constraint_name and isc.ordinal_position=6) col6,
							(select column_name  from information_schema.key_column_usage isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and isc.constraint_name=ist.constraint_name and isc.ordinal_position=7) col7,
							(select column_name  from information_schema.key_column_usage isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and isc.constraint_name=ist.constraint_name and isc.ordinal_position=8) col8,
							(select column_name  from information_schema.key_column_usage isc where isc.table_catalog=ist.table_catalog and isc.table_schema=ist.table_schema and
								isc.table_name=ist.table_name and isc.constraint_name=ist.constraint_name and isc.ordinal_position=9) col9
							from information_schema.table_constraints ist
						where table_schema =? and ist.constraint_type='PRIMARY KEY'
							order by table_catalog, table_schema, table_name
						""",
				"""
						SELECT 'FK',
						    current_database() as DB,
							conrelid::regclass  AS table_from,
							confrelid::regclass as table_to,
							regexp_replace(pg_get_constraintdef(oid), '^FOREIGN KEY (.*) REFERENCES .*$', '\\1') foreign_key_columns,
							regexp_replace(pg_get_constraintdef(oid), '^.*REFERENCES (.*).*$', '\\1') primary_key_columns
						FROM   pg_catalog.pg_constraint pc
						WHERE  pc.contype IN ('f')
						AND    pc.connamespace=?::regnamespace
						ORDER  BY 1,2,3
						""",
				"""
						select 'NN',
							current_database() as DB,
							table_name,
							column_name
						from information_schema.columns where is_nullable='NO' and table_schema=?
						order by table_catalog, table_name;
						""",
				"""
						SELECT 'CHECK',
						       current_database(),
						       source_table::regclass,
						       source_attr1.attname AS source_col1,
						       source_attr2.attname AS source_col2,
						       source_attr3.attname AS source_col3,
						       source_attr4.attname AS source_col4,
						       source_attr5.attname AS source_col5,
						       consrc
						FROM
						  (SELECT consrc, connamespace, conname,
						          source_table,
						          target_table,
						          source_constraints[1] AS source_cons1,
						          source_constraints[2] AS source_cons2,
						          source_constraints[3] AS source_cons3,
						          source_constraints[4] AS source_cons4,
						          source_constraints[5] AS source_cons5
						   FROM
						     (SELECT pg_get_constraintdef(oid) consrc, conname, connamespace, conrelid as source_table,
						             confrelid AS target_table, conkey AS source_constraints, confkey AS target_constraints
						      FROM pg_constraint
						      WHERE contype = 'c'
						     ) query1
						  ) query2
						  left outer join pg_attribute source_attr1 on source_attr1.attnum = source_cons1 AND source_attr1.attrelid = source_table
						  left outer join pg_attribute source_attr2 on source_attr2.attnum = source_cons2 AND source_attr2.attrelid = source_table
						  left outer join pg_attribute source_attr3 on source_attr3.attnum = source_cons3 AND source_attr3.attrelid = source_table
						  left outer join pg_attribute source_attr4 on source_attr4.attnum = source_cons4 AND source_attr4.attrelid = source_table
						  left outer join pg_attribute source_attr5 on source_attr5.attnum = source_cons5 AND source_attr5.attrelid = source_table
						where (SELECT nspname FROM pg_namespace WHERE oid=connamespace)=?
						""",
				"""
						SELECT 'UK',
						       current_database() as DB,
						       source_table::regclass,
						       source_attr1.attname AS source_col1,
						       source_attr2.attname AS source_col2,
						       source_attr3.attname AS source_col3,
						       source_attr4.attname AS source_col4,
						       source_attr5.attname AS source_col5,
						       consrc
						FROM
						  (SELECT consrc, connamespace, conname,
						          source_table,
						          target_table,
						          source_constraints[1] AS source_cons1,
						          source_constraints[2] AS source_cons2,
						          source_constraints[3] AS source_cons3,
						          source_constraints[4] AS source_cons4,
						          source_constraints[5] AS source_cons5
						   FROM
						     (SELECT pg_get_constraintdef(oid) consrc, conname, connamespace, conrelid as source_table, confrelid AS target_table,
						     	conkey AS source_constraints, confkey AS target_constraints
						      FROM pg_constraint
						      WHERE contype = 'u'
						     ) query1
						  ) query2
						  left outer join pg_attribute source_attr1 on source_attr1.attnum = source_cons1 AND source_attr1.attrelid = source_table
						  left outer join pg_attribute source_attr2 on source_attr2.attnum = source_cons2 AND source_attr2.attrelid = source_table
						  left outer join pg_attribute source_attr3 on source_attr3.attnum = source_cons3 AND source_attr3.attrelid = source_table
						  left outer join pg_attribute source_attr4 on source_attr4.attnum = source_cons4 AND source_attr4.attrelid = source_table
						  left outer join pg_attribute source_attr5 on source_attr5.attnum = source_cons5 AND source_attr5.attrelid = source_table
						where (SELECT nspname FROM pg_namespace WHERE oid=connamespace)=?
												""",
				"""
						select 'DATA',
							current_database() as DB,
							c.table_name,
							0 as broj
						from information_schema.tables c
						where
							c.table_schema=? and
							c.table_name not in ('')
						order by table_catalog, table_schema, table_name
														""" };

		if (submissions.size() < 100) {
			Connection connectionEpm = null;
			try {
				connectionEpm = DriverManager.getConnection(AppConfig.getString(ApplicationConstants.EPRMS_JDBC_URL),
						AppConfig.getString(ApplicationConstants.EPRMS_JDBC_USERNAME),
						AppConfig.getString(ApplicationConstants.EPRMS_JDBC_PASSWORD));
				connectionEpm.setSchema(AppConfig.getString(ApplicationConstants.EPRMS_JDBC_SCHEMA));
				connectionEpm.setAutoCommit(false);
				connectionEpm.setReadOnly(true);
				PreparedStatement statEpm = connectionEpm.prepareStatement("select * from database where name like ?");

				for (StudentSubmitSolution submission : submissions) {
					String student = submission.getStudentStartedTest().getStudent().getPerson().getUserName();

					Connection connection = null;
					String DbPass = "", DbName = "", DbUser = "";
					String gradingSchema = AppConfig.getString(ApplicationConstants.STUDENTDBS_JDBC_SCHEMA);
					String mainUrl = AppConfig.getString(ApplicationConstants.STUDENTDBS_JDBC_URL);

					for (int n = 0; n < 7; n++) {
						int statusCounter = 0;

						try {
							SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstants.DATEFORMAT_CONDENSED);
							String formattedTestDate = sdf
									.format(submission.getStudentStartedTest().getTestInstance().getScheduledFor());

							String likestring = "db%ispit%" + student + "%" + formattedTestDate + "%";
							statEpm.setString(1, likestring);
							ResultSet rsEpm = statEpm.executeQuery();
							if (rsEpm != null && rsEpm.next()) {
								DbPass = rsEpm.getString("password");
								DbName = rsEpm.getString("name");
								DbUser = rsEpm.getString("owner");
							} else {
								logger.error("Did not find student data for {}", likestring);
							}

							Properties props = new Properties();
							props.setProperty("user", DbUser);
							props.setProperty("password", DbPass);
							String url = mainUrl + "/" + DbName;

							connection = DriverManager.getConnection(url, props);
							statusCounter = 1;

							connection.setClientInfo("ApplicationName", "dbLearn*RelationalEvaluator");
							connection.setReadOnly(true);
							connection.setAutoCommit(false);
							connection.setSavepoint();
							connection.setSchema(gradingSchema);
							statusCounter = 2;

							List<String[]> tempResults = execQuery(submission, connection, gradingSchema,
									queryToRun[n]);
							if (n == 6) {
								for (String[] l : tempResults) {
									String query = "SELECT count(*) FROM " + gradingSchema + "." + l[4];
									List<String[]> countResults = execQuery(submission, connection, null, query);
									if (countResults != null) {

									}
									l[5] = countResults.get(0)[2];
									logger.debug("{} {} {} {} ", query, countResults.get(0)[0], countResults.get(0)[1],
											countResults.get(0)[2]);
								}
							}
							ddlEvaluationData.addAll(tempResults);
							statusCounter = 4; // 3 should be before the preparedstatement, but it is not set

							SQLWarning w = connection.getWarnings();
							if (w != null) {
								logger.debug("warning");
								resultsErrors.add(w.getMessage());
							}
							connection.rollback();
						} catch (Exception e) {
							if (statusCounter == 0) {
								logger.error("{}", e);
								resultsErrors.add(e.getMessage());
							} else {
								logger.error(
										"Connected to evaluation database {}, but failed in running query {} due to {}",
										DbName, queryToRun[n], e.getMessage());
								logger.debug("Exception: {}", e);
								resultsErrors.add(e.getMessage());
							}
						} finally {
							if (connection != null) {
								try {
									connection.close();
								} catch (Exception e) {
									logger.error("Connection can't be closed {} {}", DbName, e.getMessage());
								}
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("Cant connect to epm due to {}", e.getMessage());
			} finally {
				if (connectionEpm != null) {
					try {
						connectionEpm.close();
					} catch (Exception e) {
						logger.error("EPM connection can't be closed {} ", e.getMessage());
					}
				}
			}
		}
		return new Triplet<List<String[]>, List<String>, List<String>>(ddlEvaluationData, resultsHeadersSimple,
				resultsErrors);
	}

}
