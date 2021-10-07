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

import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;

import dblearnstar.model.entities.Translation;

public class TranslationServiceImpl implements TranslationService {

	@Inject
	private Session session;

	private Session getEntityManager() {
		return session.getSession();
	}

	@Override
	public String getTranslation(String className, String attributeCode, long originalObjectId, String locale) {
		try {
			return ((Translation) getEntityManager().createQuery("""
					from Translation
					where
						className=:className and
						attributeCode=:attributeCode and
						locale=:locale and
						originalObjectId=:originalObjectId
					""").setParameter("className", className).setParameter("attributeCode", attributeCode)
					.setParameter("locale", locale).setParameter("originalObjectId", originalObjectId)
					.getSingleResult()).getTranslatedText();
		} catch (Exception e) {
			return null;
		}
	}

}
