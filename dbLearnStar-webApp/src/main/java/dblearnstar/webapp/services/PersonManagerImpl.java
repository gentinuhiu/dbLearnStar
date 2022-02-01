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

import dblearnstar.model.entities.Person;
import dblearnstar.model.entities.PersonRole;
import dblearnstar.model.entities.Student;

public class PersonManagerImpl implements PersonManager {

	@Inject
	private GenericService genericService;

	@Inject
	private PersonDao personDao;

	@Override
	public List<Person> getAllPersons() {
		return this.personDao.getAllPersons();
	}

	@Override
	public Person getPersonByUsername(String username) {
		return this.personDao.getPersonByUsername(username);
	}

	public Person getPersonById(long personId) {
		return genericService.getByPK(Person.class, personId);
	}

	@Override
	public List<Student> getStudentsByPersonId(long personId) {
		return personDao.getStudentsByPersonId(personId);
	}

	public Student getStudentByStudentId(long studentId) {
		return personDao.getStudentByStudentId(studentId);
	}

	@Override
	public List<Person> getPersonByFilter(String filter, long institutionId) {
		return personDao.getPersonByFilter(filter, institutionId);
	}

	public List<PersonRole> getPersonRolesForPerson(long personId) {
		return personDao.getPersonRolesForPerson(personId);
	}

	@Override
	public String getPersonFullName(Person person) {
		return personDao.getPersonFullName(person);
	}

	@Override
	public String getPersonFullNameWithId(Person person) {
		return personDao.getPersonFullNameWithId(person);
	}

	@Override
	public List<Student> getAllStudents() {
		// TODO Auto-generated method stub
		return personDao.getAllStudents();
	}

	@Override
	public boolean isInstructor(Person person) {
		return personDao.isInstructor(person);
	}
}
