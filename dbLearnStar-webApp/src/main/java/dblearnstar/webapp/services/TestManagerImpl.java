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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.slf4j.Logger;

import dblearnstar.model.entities.ActivityInTask;
import dblearnstar.model.entities.Person;
import dblearnstar.model.entities.SolutionAssessment;
import dblearnstar.model.entities.Student;
import dblearnstar.model.entities.StudentStartedTest;
import dblearnstar.model.entities.StudentSubmitSolution;
import dblearnstar.model.entities.Task;
import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.model.entities.TestInstance;

public class TestManagerImpl implements TestManager {

	@Inject
	private Logger logger;

	@Inject
	private Session session;

	private Session getEntityManager() {
		return session.getSession();
	}

	@Override
	public List<TestInstance> getAllTestInstances() {
		List<TestInstance> testInstances = UsefulMethods.castList(TestInstance.class,
				getEntityManager().createQuery("from TestInstance order by title desc").getResultList());
		return testInstances;
	}

	@Override
	public List<TestInstance> getTestInstancesForStudent(long studentId) {
		return UsefulMethods.castList(TestInstance.class,
				getEntityManager()
						.createQuery("from TestInstance t where t.openToAllStudents=true order by t.title desc")
						.getResultList());
	}

	@Override
	public List<TestInstance> getTestInstancesForStudentByTestType(long studentId, long testTypeId) {
		String query = """
				from TestInstance t
				where t.testTemplate.testType.testTypeId = :testTypeId and (
					t.openToAllStudents=true or
					t in (
						select ti from GroupMember gm
						join gm.student s
						join s.person p
						join gm.group g
						join g.groupFocusOnTests gft
						join gft.testInstance ti
						where
							s.studentId=:studentId and
							now() between ti.scheduledFor and ti.scheduledUntil
					)
				)
				order by t.title desc
				""";
		return UsefulMethods.castList(TestInstance.class, getEntityManager().createQuery(query)
				.setParameter("testTypeId", testTypeId).setParameter("studentId", studentId).getResultList());
	}

	@Override
	public List<TestInstance> getAllTestInstancesByTestType(long testTypeId) {
		String query = """
				from TestInstance t
				where
					t.testTemplate.testType.testTypeId = :testTypeId
				order by t.title desc
				""";
		return UsefulMethods.castList(TestInstance.class,
				getEntityManager().createQuery(query).setParameter("testTypeId", testTypeId).getResultList());
	}

	@Override
	public List<TestInstance> getAllCurrentlyAvailableTestInstancesByTestType(long testTypeId) {
		return UsefulMethods.castList(TestInstance.class, getEntityManager().createQuery("""
				from TestInstance t
				where
					(t.scheduledFor is null or t.scheduledFor<=current_timestamp()) and
					t.testTemplate.testType.testTypeId = :testTypeId
				order by t.title desc
				""").setParameter("testTypeId", testTypeId).getResultList());
	}

	@Override
	public Boolean isTaskInTestInstanceSolvedByStudent(long taskInTestInstanceId, long studentId) {
		Iterator<StudentSubmitSolution> i = getEntityManager().createQuery("""
				from StudentSubmitSolution sss
				where
					sss.evaluationSimple=true and
					sss.evaluationComplex=true and
					sss.notForEvaluation=false and
					sss.taskInTestInstance.taskInTestInstanceId = :taskInTestInstanceId and
					sss.studentStartedTest.student.studentId = :studentId
				""").setParameter("taskInTestInstanceId", taskInTestInstanceId).setParameter("studentId", studentId)
				.getResultList().iterator();
		if (i.hasNext()) {
			return true;
		} else {
			i = getEntityManager().createQuery("""
					from StudentSubmitSolution sss
					where
						sss.taskInTestInstance.taskInTestInstanceId=:taskInTestInstanceId and
						sss.studentStartedTest.student.studentId=:studentId
					""").setParameter("taskInTestInstanceId", taskInTestInstanceId).setParameter("studentId", studentId)
					.getResultList().iterator();
			if (i.hasNext()) {
				return false;
			} else {
				return null;
			}
		}
	}

	@Override
	public Float getGradeForTaskInTestInstanceByStudent(long taskInTestInstanceId, long studentId) {
		try {
			return (Float) getEntityManager().createQuery("""
					select max(sa.grade) from SolutionAssessment sa join sa.studentSubmitSolution sss
					where
						sss.taskInTestInstance.taskInTestInstanceId = :taskInTestInstanceId and
						sss.studentStartedTest.student.studentId = :studentId
					""").setParameter("taskInTestInstanceId", taskInTestInstanceId).setParameter("studentId", studentId)
					.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Long getNumPersonsSuccessfulForTaskInTestInstance(long taskInTestInstanceId) {
		try {
			return (Long) getEntityManager().createQuery("""
					select count(distinct sss.studentStartedTest.student.studentId)
					from StudentSubmitSolution sss
					where
						sss.evaluationSimple=true and sss.evaluationComplex=true and
						sss.taskInTestInstance.taskInTestInstanceId=:taskInTestInstanceId
					""").setParameter("taskInTestInstanceId", taskInTestInstanceId).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Long getNumPersonsTriedToSolveTaskInTestInstance(long taskInTestInstanceId) {
		try {
			return (Long) getEntityManager().createQuery("""
					select count(distinct sss.studentStartedTest.student.studentId)
					from StudentSubmitSolution sss
					where sss.taskInTestInstance.taskInTestInstanceId=:taskInTestInstanceId
					""").setParameter("taskInTestInstanceId", taskInTestInstanceId).getSingleResult();
		} catch (Exception e) {
			return Long.valueOf(0);
		}
	}

	@Override
	public List<Object[]> getStudentsSolving() {
		try {
			Query query = getEntityManager().createQuery("""
					select p, count(distinct sss.taskInTestInstance.taskInTestInstanceId)
					from StudentSubmitSolution sss
					join sss.studentStartedTest sst
					join sst.student s join s.person p
					where
						sss.evaluationExam=true and sss.submittedOn>:datum
					group by p
					order by count(distinct sss.taskInTestInstance.taskInTestInstanceId) desc
					""");
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -3);
			Date datum = cal.getTime();
			query.setParameter("datum", datum);
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<StudentSubmitSolution> getHistoryOfSolutions(long taskInTestInstanceId, Boolean filterNotForEvalution,
			long studentId) {
		return UsefulMethods.castList(StudentSubmitSolution.class, getEntityManager().createQuery("""
				from StudentSubmitSolution sss
				where
					sss.taskInTestInstance.taskInTestInstanceId=:taskInTestInstanceId and
					sss.notForEvaluation=:filterNotForEvalution and
					sss.studentStartedTest.student.studentId=:studentId
				order by sss.submittedOn desc
				""").setParameter("taskInTestInstanceId", taskInTestInstanceId).setParameter("studentId", studentId)
				.setParameter("filterNotForEvalution", filterNotForEvalution).getResultList());
	}

	@Override
	public StudentStartedTest studentStartTest(long studentId, long testInstanceId) {
		Student student = (Student) session.get(Student.class, studentId);
		TestInstance testInstance = (TestInstance) session.get(TestInstance.class, testInstanceId);
		javax.persistence.Query q = getEntityManager().createQuery("""
				from StudentStartedTest st
				where
					st.testInstance.testInstanceId=:testInstanceId and
					st.student.studentId=:studentId
				""");
		q.setParameter("testInstanceId", testInstanceId);
		q.setParameter("studentId", student.getStudentId());
		List<StudentStartedTest> studentStartedTests = UsefulMethods.castList(StudentStartedTest.class,
				q.getResultList());
		StudentStartedTest studentStartedTest;
		if (studentStartedTests.isEmpty()) {
			studentStartedTest = new StudentStartedTest();
			studentStartedTest.setTestInstance(testInstance);
			studentStartedTest.setStudent(student);
			session.save(studentStartedTest);
		} else {
			studentStartedTest = studentStartedTests.get(0);
		}
		return studentStartedTest;
	}

	@Override
	public List<StudentSubmitSolution> getSolutionsByStudent(long studentId) {
		javax.persistence.Query q = getEntityManager().createQuery(
				"from StudentSubmitSolution sss where sss.studentStartedTest.student.studentId = :studentId");
		q.setParameter("studentId", studentId);
		return UsefulMethods.castList(StudentSubmitSolution.class, q.getResultList());
	}

	@Override
	public List<StudentSubmitSolution> getSolutionsOfTaskInTestInstanceByOtherStudents(long taskInTestInstanceId,
			long studentId) {
		javax.persistence.Query q = getEntityManager().createQuery("""
				from StudentSubmitSolution sss
				where sss.evaluationSimple=true and
					sss.evaluationComplex=true and
					sss.studentStartedTest.student.studentId!=:studentId  and
					sss.taskInTestInstance.taskInTestInstanceId=:taskInTestInstanceId
				order by sss.submittedOn desc
				""");
		q.setParameter("studentId", studentId);
		q.setParameter("taskInTestInstanceId", taskInTestInstanceId);
		return UsefulMethods.castList(StudentSubmitSolution.class, q.getResultList());
	}

	@Override
	public List<TestInstance> getTestInstancesByTestType(long testTypeId) {
		javax.persistence.Query query = getEntityManager().createQuery(
				"from TestInstance t where t.testTemplate.testType.testTypeId = :testTypeId order by t.title desc")
				.setParameter("testTypeId", testTypeId);
		return UsefulMethods.castList(TestInstance.class, query.getResultList());
	}

	@Override
	public List<Task> getTasksByModel(long modelId) {
		List<Task> tasks = UsefulMethods.castList(Task.class,
				getEntityManager().createQuery("from Task where model.modelId=:modelId")
						.setParameter("modelId", modelId).getResultList());
		logger.info("model {} \n tasks {}", modelId, tasks.size());
		return tasks;
	}

	@Override
	public List<StudentSubmitSolution> getCorrectSolutionsByStudentAndTaskInTestInstance(long studentId,
			long taskInTestInstanceId) {
		try {
			Query q = getEntityManager().createQuery("""
					select sss
					from StudentSubmitSolution sss
					left outer join sss.evaluations eval
					where
						((eval.solutionAssessmentId is not null) or
						(sss.evaluationSimple=true and sss.evaluationComplex=true)) and
						sss.studentStartedTest.student.studentId=:studentId and
						sss.taskInTestInstance.taskInTestInstanceId=:taskInTestInstanceId
					order by sss.submittedOn desc
					""");
			q.setParameter("studentId", studentId);
			q.setParameter("taskInTestInstanceId", taskInTestInstanceId);
			return UsefulMethods.castList(StudentSubmitSolution.class, q.getResultList());
		} catch (Exception e) {
			logger.error("getCorrectSolutionsByStudentAndTaskInTestInstance failed {}", e);
			return null;
		}
	}

	@Override
	public List<StudentSubmitSolution> getIncorrectSolutionsByStudentAndTaskInTestInstance(long studentId,
			long taskInTestInstanceId) {
		try {
			Query q = getEntityManager().createQuery("""
					from StudentSubmitSolution sss
					where
						not(sss.evaluationSimple=true and sss.evaluationComplex=true) and
						sss.studentStartedTest.student.studentId=:studentId and
						sss.taskInTestInstance.taskInTestInstanceId=:taskInTestInstanceId
					order by sss.submittedOn desc
					""");
			q.setParameter("studentId", studentId);
			q.setParameter("taskInTestInstanceId", taskInTestInstanceId);
			return UsefulMethods.castList(StudentSubmitSolution.class, q.getResultList());
		} catch (Exception e) {
			logger.error("getIncorrectSolutionsByStudentAndTaskInTestInstance failed {}", e);
			return null;
		}
	}

	@Override
	public List<TaskInTestInstance> getTasksInTestInstance(long testInstanceId) {
		return UsefulMethods.castList(TaskInTestInstance.class, getEntityManager().createQuery("""
				from TaskInTestInstance tti
				where tti.testInstance.testInstanceId = :testInstanceId
				order by tti.task.title
				""").setParameter("testInstanceId", testInstanceId).getResultList());
	}

	@Override
	public void recordActivityInTask(Person person, TaskInTestInstance taskInTestInstance, String type,
			String payload) {
		ActivityInTask activityInTask = new ActivityInTask();
		activityInTask.setPerson(person);
		activityInTask.setTaskInTestInstance(taskInTestInstance);
		activityInTask.setType(type);
		activityInTask.setWhenOccured(new Date());
		activityInTask.setPayload(payload);
		session.save(activityInTask);
	}

	@Override
	public List<Student> getStudentsWhoStartedTestInstance(TestInstance testInstance) {
		try {
			return UsefulMethods.castList(Student.class, getEntityManager().createQuery("""
					select s
					from StudentStartedTest sst join sst.student s
					where sst.testInstance.testInstanceId = :testInstanceId
					order by s.person.lastName, s.person.firstName
					""").setParameter("testInstanceId", testInstance.getTestInstanceId()).getResultList());
		} catch (Exception e) {
			logger.error("Error {}", e.getMessage());
			return new ArrayList<Student>();
		}
	}

	@Override
	public List<StudentSubmitSolution> getEvaluatedSolutionsForTaskInTestInstance(long studentId,
			long taskInTestInstanceId) {
		try {
			Query q = getEntityManager().createQuery("""
					select sss
					from StudentSubmitSolution sss
					left outer join sss.evaluations eval
					where
						sss.studentStartedTest.student.studentId = :studentId and
					    sss.taskInTestInstance.taskInTestInstanceId = :taskInTestInstanceId
					order by eval.evaluatedOn desc, sss.submittedOn desc
					""");
			q.setParameter("studentId", studentId);
			q.setParameter("taskInTestInstanceId", taskInTestInstanceId);
			List<StudentSubmitSolution> output = UsefulMethods.castList(StudentSubmitSolution.class, q.getResultList());
			return output;
		} catch (Exception e) {
			logger.error("getEvaluatedSolutionsForTaskInTestInstance failed {}", e);
			return null;
		}
	}

	@Override
	public List<SolutionAssessment> getAllEvaluationsOfSolutionsForTaskInTestInstance(long studentId,
			long taskInTestInstanceId) {
		try {
			Query q = getEntityManager().createQuery("""
					select sa
					from SolutionAssessment sa
					where
						sa.studentSubmitSolution.studentStartedTest.student.studentId = :studentId and
					    sa.studentSubmitSolution.taskInTestInstance.taskInTestInstanceId = :taskInTestInstanceId
					order by sa.evaluatedOn desc, sa.studentSubmitSolution.submittedOn desc
					""");
			q.setParameter("studentId", studentId);
			q.setParameter("taskInTestInstanceId", taskInTestInstanceId);
			List<SolutionAssessment> output = UsefulMethods.castList(SolutionAssessment.class, q.getResultList());
			return output;
		} catch (Exception e) {
			logger.error("getAllEvaluationsOfSolutionsForTaskInTestInstance failed {}", e);
			return null;
		}
	}

	@Override
	public Float getTotalPoints(long studentId, long testInstanceId) {
		try {
			Query q = getEntityManager().createQuery("""
					select max(sa.grade)
					from SolutionAssessment sa
					join sa.studentSubmitSolution sss
					where
						sss.taskInTestInstance.testInstance.testInstanceId = :testInstanceId and
						sss.studentStartedTest.student.studentId = :studentId
					group by sss.taskInTestInstance.taskInTestInstanceId
					""");
			q.setParameter("studentId", studentId);
			q.setParameter("testInstanceId", testInstanceId);
			float total = (float) 0;
			for (Float sa : UsefulMethods.castList(Float.class, q.getResultList())) {
				if (sa != null) {
					total += sa;
				}
			}
			return total;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean accessToTaskInTestInstanceAllowed(Student student, TaskInTestInstance tti) {
		return getTestInstancesForStudentByTestType(student.getStudentId(),
				tti.getTestInstance().getTestTemplate().getTestType().getTestTypeId()).stream()
						.anyMatch(ti -> ti.getTestInstanceId() == tti.getTestInstance().getTestInstanceId());
	}

}
