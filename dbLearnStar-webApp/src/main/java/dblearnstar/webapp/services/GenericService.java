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

public interface GenericService {
	public void delete(Object object);

	public void deleteByPK(Class<?> classToDelete, long id);

	public List<?> getAll(Class<?> classToLoad);

	public <T> T getByCode(Class<T> classToLoad, String code);

	public <T> T getByPK(Class<T> classToLoad, long id);

	public List<?> getQueryResult(String guery);

	public Object save(Object object);

	public void saveOrUpdate(Object object);

	public List<?> getByTitleSubstring(Class<?> classToSearch, String searchSubString);

}
