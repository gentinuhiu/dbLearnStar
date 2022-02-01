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

import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.slf4j.Logger;

import dblearnstar.model.entities.Person;
import dblearnstar.model.entities.PersonRole;
import dblearnstar.model.entities.Student;
import dblearnstar.model.model.ModelConstants;

public class PersonDaoImpl implements PersonDao {
	@Inject
	private Logger logger;

	@Inject
	private Session session;

	private Session getEntityManager() {
		return session.getSession();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getAllPersons() {
		try {
			return UsefulMethods.castList(Person.class,
					getEntityManager().createQuery("from Person order by lastName").getResultList());
		} catch (Exception e) {
			logger.error("getAllPersons {}", e);
			return null;
		}
	}

	@Override
	public Person getPersonByUsername(String username) {
		try {
			return (Person) getEntityManager().createQuery("from Person where userName=:param")
					.setParameter("param", username).getSingleResult();
		} catch (Exception e) {
			logger.error("getPersonByUsername {}", e);
			return null;
		}
	}

	@Override
	public List<Student> getStudentsByPersonId(long personId) {
		try {
			return UsefulMethods.castList(Student.class,
					getEntityManager().createQuery("from Student s where s.person.personId=:personId")
							.setParameter("personId", personId).getResultList());
		} catch (Exception e) {
			logger.error("getStudentsByPersonId {}", e);
			return null;
		}
	}

	@Override
	public Student getStudentByStudentId(long studentId) {
		try {
			return (Student) getEntityManager().createQuery("from Student where studentId=:studentId")
					.setParameter("studentId", studentId).getSingleResult();
		} catch (Exception e) {
			logger.error("getStudentByStudentId {}", e);
			return null;
		}
	}

	@Override
	public List<Person> getPersonByFilter(String filter, long institutionId) {
		String f = "%" + filter.toLowerCase() + "%";
		try {
			return UsefulMethods.castList(Person.class, getEntityManager().createQuery("""
					select p from Person p where (
						p in (select person from Staff where institution.institutionId=:inst) or
						p in (select person from Student where institution.institutionId=:inst) or
						p in (select person from Instructor where institution.institutionId=:inst)
					) and
					(lower(concat(userName,firstName,lastName)) like :filter)
					""").setParameter("filter", f).setParameter("inst", institutionId).getResultList());
		} catch (Exception e) {
			logger.error("getPersonByFilter {}", e);
			return null;
		}
	}

	@Override
	public List<PersonRole> getPersonRolesForPerson(long personId) {
		try {
			return UsefulMethods.castList(PersonRole.class,
					getEntityManager().createQuery("from PersonRole pr where pr.person.personId=:personId")
							.setParameter("personId", personId).getResultList());
		} catch (Exception e) {
			logger.error("getPersonRolesForPerson {}", e);
			return null;
		}
	}

	@Override
	public String getPersonFullName(Person person) {
		return person.getLastName() + " " + person.getFirstName();
	}

	@Override
	public String getPersonFullNameWithId(Person person) {
		return person.getLastName() + " " + person.getFirstName() + " [" + person.getUserName() + "]";
	}

	@Override
	public List<Student> getAllStudents() {
		try {
			return UsefulMethods.castList(Student.class,
					getEntityManager().createQuery("from Student s order by s.person.lastName").getResultList());
		} catch (Exception e) {
			logger.error("getAllStudents {}", e);
			return null;
		}
	}

	@Override
	public boolean isInstructor(Person person) {
		try {
			List list = getEntityManager()
					.createQuery(
							"from PersonRole pr where pr.person.personId=:personId and pr.role.name=:instructorRole")
					.setParameter("personId", person.getPersonId())
					.setParameter("instructorRole", ModelConstants.InstructorRole).getResultList();
			if (list != null && list.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error("isInstructor {}", e);
			return false;
		}
	}

}
