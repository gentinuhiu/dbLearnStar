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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.slf4j.Logger;

import dblearnstar.model.entities.ActivityInTask;
import dblearnstar.model.entities.Person;
import dblearnstar.model.entities.TaskInTestInstance;

public class ActivityDaoImpl implements ActivityDao {

	@Inject
	private Session session;

	@Inject
	private Logger logger;

	private Session getEntityManager() {
		return session.getSession();
	}

	@Override
	public List<Person> activePersonsInInterval(int seconds) {
		Query q = getEntityManager().createQuery("""
				select distinct ait.person
				from ActivityInTask ait join ait.person
				where ait.whenOccured>=:filterTime
				""");
		q.setParameter("filterTime",
				Date.from(LocalDateTime.now().minusSeconds(seconds).atZone(ZoneId.systemDefault()).toInstant()));
		return UsefulMethods.castList(Person.class, q.getResultList());
	}

	@Override
	public List<ActivityInTask> getActivitiesInTaskForPerson(Person p, TaskInTestInstance tti) {
		Query q = getEntityManager().createQuery("""
				select ait
				from ActivityInTask ait 
				join ait.person p 
				join ait.taskInTestInstance tti
				where p.personId=:personId and tti.taskInTestInstanceId=:taskInTestInstanceId
				order by ait.whenOccured desc
				""");
		q.setParameter("personId", p.getPersonId());
		q.setParameter("taskInTestInstanceId", tti.getTaskInTestInstanceId());
		return UsefulMethods.castList(ActivityInTask.class, q.getResultList());
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
		getEntityManager().save(activityInTask);
	}

}
