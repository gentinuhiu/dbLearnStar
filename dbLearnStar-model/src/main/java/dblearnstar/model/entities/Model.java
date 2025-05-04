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
@Table(schema = "dblearnstar", name = "model")
public class Model implements java.io.Serializable {
	private long modelId;
	private String title;
	private String diagramSvg;
	private byte[] diagramPng;
	private String diagramUrl;
	private String description;
	private List<TestTemplate> testTemplates = new ArrayList<TestTemplate>();
	private List<Task> tasks = new ArrayList<Task>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "model_id", unique = true, nullable = false)
	public long getModelId() {
		return this.modelId;
	}

	public void setModelId(long modelId) {
		this.modelId = modelId;
	}

	@Column(name = "title", unique = true, nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "diagram_svg")
	public String getDiagramSvg() {
		return this.diagramSvg;
	}

	public void setDiagramSvg(String diagramSvg) {
		this.diagramSvg = diagramSvg;
	}

	@Column(name = "diagram_png")
	public byte[] getDiagramPng() {
		return this.diagramPng;
	}

	public void setDiagramPng(byte[] diagramPng) {
		this.diagramPng = diagramPng;
	}

	@Column(name = "diagram_url")
	public String getDiagramUrl() {
		return this.diagramUrl;
	}

	public void setDiagramUrl(String diagramUrl) {
		this.diagramUrl = diagramUrl;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "model")
	public List<TestTemplate> getTestTemplates() {
		return this.testTemplates;
	}

	public void setTestTemplates(List<TestTemplate> testTemplates) {
		this.testTemplates = testTemplates;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "model")
	public List<Task> getTasks() {
		return this.tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

}
