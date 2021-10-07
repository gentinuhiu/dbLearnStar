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
@Table(schema = "sql_learning", name = "test_instance_parameters")
public class TestInstanceParameters implements java.io.Serializable {
	private long testInstanceParametersId;
	private String hostname;
	private String port;
	private String dbDriver;
	private String dbName;
	private String dbUser;
	private String dbPass;
	private String evaluationViewPrefix;
	private String schemaSimple;
	private String schemaComplex;
	private String schemaExam;
	private TestInstance testInstance;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "test_instance_parameters_id", unique = true, nullable = false)
	public long getTestInstanceParametersId() {
		return this.testInstanceParametersId;
	}

	public void setTestInstanceParametersId(long testInstanceParametersId) {
		this.testInstanceParametersId = testInstanceParametersId;
	}

	@Column(name = "hostname")
	public String getHostname() {
		return this.hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@Column(name = "port")
	public String getPort() {
		return this.port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Column(name = "db_driver")
	public String getDbDriver() {
		return this.dbDriver;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	@Column(name = "db_name")
	public String getDbName() {
		return this.dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	@Column(name = "db_user")
	public String getDbUser() {
		return this.dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	@Column(name = "db_pass")
	public String getDbPass() {
		return this.dbPass;
	}

	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}

	@Column(name = "evaluation_view_prefix")
	public String getEvaluationViewPrefix() {
		return this.evaluationViewPrefix;
	}

	public void setEvaluationViewPrefix(String evaluationViewPrefix) {
		this.evaluationViewPrefix = evaluationViewPrefix;
	}

	@Column(name = "schema_simple")
	public String getSchemaSimple() {
		return this.schemaSimple;
	}

	public void setSchemaSimple(String schemaSimple) {
		this.schemaSimple = schemaSimple;
	}

	@Column(name = "schema_complex")
	public String getSchemaComplex() {
		return this.schemaComplex;
	}

	public void setSchemaComplex(String schemaComplex) {
		this.schemaComplex = schemaComplex;
	}

	@Column(name = "schema_exam")
	public String getSchemaExam() {
		return this.schemaExam;
	}

	public void setSchemaExam(String schemaExam) {
		this.schemaExam = schemaExam;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_instance_id", nullable = false, foreignKey = @ForeignKey(name = "fk_test_instance_parameters_test_instance"))
	public TestInstance getTestInstance() {
		return this.testInstance;
	}

	public void setTestInstance(TestInstance testInstance) {
		this.testInstance = testInstance;
	}

}
