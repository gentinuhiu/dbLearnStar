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
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;

public class GenericDaoImpl implements GenericDao {

	@Inject
	private Logger logger;

	@Inject
	private Session session;

	private Session getEntityManager() {
		return session.getSession();
	}

	@Override
	public Object getByPK(Class<?> classToLoad, long id) {
		return session.get(classToLoad, id);
	}

	@Override
	public void deleteByPK(Class<?> classToLoad, long id) {
		session.delete(getByPK(classToLoad, id));
	}

	@Override
	public void delete(Object object) {
		session.delete(object);
	}

	@Override
	public List<Object> getQueryResult(String guery) {
		try {
			javax.persistence.Query q = getEntityManager().createQuery(guery);
			List<Object> l = new ArrayList<Object>();

			for (Iterator<?> it = q.getResultList().iterator(); it.hasNext();) {
				Object[] row = (Object[]) it.next();
				for (int i = 0; i < row.length; i++) {
					l.add(row[i]);
				}
				l.add(" | ");
			}

			return l;

		} catch (DataException e) {
			// Critical errors : database unreachable, etc.
			logger.error(
					"Exception - DataAccessException occurs : " + e.getMessage() + " on complete getQueryResult().");
			return null;
		}
	}

	@Override
	public void saveOrUpdate(Object object) {
		session.saveOrUpdate(object);
	}

	@Override
	public Object save(Object object) {
		// Object a = session.merge(object);
		return session.save(object);
	}

	@Override
	public List<?> getAll(Class<?> classToLoad) {
		return UsefulMethods.castList(classToLoad,
				getEntityManager().createQuery("from " + classToLoad.getName()).getResultList());
	}

	@Override
	public Object getByCode(Class<?> classToLoad, String code) {
		List<?> l = getEntityManager().createQuery("from " + classToLoad.getName() + "where code=:code")
				.setParameter("code", code).getResultList();
		if (l.size() > 0) {
			return l.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<?> getByTitleSubstring(Class<?> classToSearch, String searchSubString) {
		if (searchSubString != null) {
			return (List<?>) getEntityManager()
					.createQuery("from " + classToSearch.getName() + "where title ilike :searchSubString")
					.setParameter("searchSubString", searchSubString).getResultList();
		} else {
			return null;
		}
	}
}
