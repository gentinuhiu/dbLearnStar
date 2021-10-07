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

public class GenericServiceImpl implements GenericService {

	@Inject
	private GenericDao genericDao;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getByPK(Class<T> classToLoad, long id) {
		return (T) genericDao.getByPK(classToLoad, id);
	}

	@Override
	public void deleteByPK(Class<?> classToDelete, long id) {
		genericDao.deleteByPK(classToDelete, id);
	}

	@Override
	public void delete(Object object) {
		genericDao.delete(object);
	}

	@Override
	public void saveOrUpdate(Object object) {
		genericDao.saveOrUpdate(object);
	}

	@Override
	public Object save(Object object) {
		return genericDao.save(object);
	}

	@Override
	public List<?> getQueryResult(String guery) {
		return genericDao.getQueryResult(guery);
	}

	@Override
	public List<?> getAll(Class<?> classToLoad) {
		return genericDao.getAll(classToLoad);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getByCode(Class<T> classToLoad, String code) {
		return (T) genericDao.getByCode(classToLoad, code);
	}

	@Override
	public List<?> getByTitleSubstring(Class<?> classToSearch, String searchSubString) {
		return genericDao.getByTitleSubstring(classToSearch, searchSubString);
	}

}
