/*******************************************************************************

 * Copyright (C) 2021 Vangel V. Ajanovski
 *     
 * This file is part of the dbLearn* system (hereinafter: dbLearn*).
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

import jakarta.validation.constraints.NotNull;

/*
*/
@Entity
@Table (schema="dblearnstar", name="system_parameter")
public class SystemParameter implements java.io.Serializable {
	private long systemParameterId;
	private String className;
	private long originalObjectId;
	private String type;
	private String code;
	private String value;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull
	@Column(name = "system_parameter_id", unique = true, nullable = false)
	public long getSystemParameterId() {
		return this.systemParameterId;
	}

	public void setSystemParameterId(long systemParameterId) {
		this.systemParameterId=systemParameterId;
	}

	@NotNull
	@Column(name = "class_name", nullable = false)
	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className=className;
	}

	@NotNull
	@Column(name = "original_object_id", nullable = false)
	public long getOriginalObjectId() {
		return this.originalObjectId;
	}

	public void setOriginalObjectId(long originalObjectId) {
		this.originalObjectId=originalObjectId;
	}

	@NotNull
	@Column(name = "type", nullable = false)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type=type;
	}

	@NotNull
	@Column(name = "code", nullable = false)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code=code;
	}

	@NotNull
	@Column(name = "value", nullable = false)
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value=value;
	}

}
