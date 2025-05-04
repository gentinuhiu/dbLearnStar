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

package dblearnstar.model.entities;

import java.util.*;
import javax.persistence.*;

/*
*/
@Entity
@Table(schema = "dblearnstar", name = "translations")
public class Translation implements java.io.Serializable {
	private long translationId;
	private String className;
	private long originalObjectId;
	private String locale;
	private String attributeCode;
	private String translatedText;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "translation_id", unique = true, nullable = false)
	public long getTranslationId() {
		return this.translationId;
	}

	public void setTranslationId(long translationId) {
		this.translationId = translationId;
	}

	@Column(name = "class_name", nullable = false)
	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Column(name = "original_object_id", nullable = false)
	public long getOriginalObjectId() {
		return this.originalObjectId;
	}

	public void setOriginalObjectId(long originalObjectId) {
		this.originalObjectId = originalObjectId;
	}

	@Column(name = "locale", nullable = false)
	public String getLocale() {
		return this.locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Column(name = "attribute_code", nullable = false)
	public String getAttributeCode() {
		return this.attributeCode;
	}

	public void setAttributeCode(String attributeCode) {
		this.attributeCode = attributeCode;
	}

	@Column(name = "translated_text", nullable = false)
	public String getTranslatedText() {
		return this.translatedText;
	}

	public void setTranslatedText(String translatedText) {
		this.translatedText = translatedText;
	}

}
