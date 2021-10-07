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

import dblearnstar.model.entities.Person;

public class ActivityDaoImpl implements ActivityDao {

	@Inject
	private Session session;

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

}
