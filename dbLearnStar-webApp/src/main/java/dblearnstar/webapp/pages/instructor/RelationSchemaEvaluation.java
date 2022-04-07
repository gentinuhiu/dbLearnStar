package dblearnstar.webapp.pages.instructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import dblearnstar.model.entities.SolutionAssessment;
import dblearnstar.model.entities.StudentSubmitSolution;
import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.model.model.ModelConstants;
import dblearnstar.model.model.Triplet;
import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.annotations.InstructorPage;
import dblearnstar.webapp.services.EvaluationService;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.SystemConfigService;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

@AdministratorPage
@InstructorPage
@Import(module = { "zoneUpdateEffect" })
public class RelationSchemaEvaluation {

	@Inject
	private Logger logger;
	@Inject
	private Messages messages;

	@SessionState
	private UserInfo userInfo;

	@Inject
	private GenericService genericService;
	@Inject
	private EvaluationService evaluationService;
	@Inject
	private SystemConfigService systemConfigService;

	public String[] getModel() {
		return ModelConstants.AllDBRelationalObjectTypes;
	}

	@Property
	private TaskInTestInstance taskInTestInstanceToGrade;

	@Property
	private StudentSubmitSolution submission;
	@Persist
	@Property
	private String filterType;
	@Persist
	@Property
	private String filterTableName;

	@Persist
	@Property
	private String filterDB;

	@Property
	private String oneHeader;
	@Property
	private String[] oneRow;
	@Property
	private String oneColumn;

	@Persist
	@Property
	List<String[]> evaluationData;
	@Persist
	@Property
	List<String> resultsHeadersSimple;
	@Persist
	@Property
	List<String> resultsErrors;

	void onActivate(TaskInTestInstance taskInTestInstance) {
		taskInTestInstanceToGrade = genericService.getByPK(TaskInTestInstance.class,
				taskInTestInstance.getTaskInTestInstanceId());
		if (evaluationData == null || evaluationData.size() == 0) {
			onLoadSavedData();
			ComparatorEvaluationData ced = new ComparatorEvaluationData();
			Collections.sort(evaluationData, ced);
		}
	}

	List<StudentSubmitSolution> getSubmissions() {
		return evaluationService.getOnlyLastSubmissionsByStudentAndTaskInTestInstance(null,
				taskInTestInstanceToGrade.getTestInstance(), taskInTestInstanceToGrade, true, false);
	}

	public TaskInTestInstance onPassivate() {
		return taskInTestInstanceToGrade;
	}

	public List<String[]> getFilteredEvaluationData() {
		try {
			List<String[]> list = evaluationData;
			if (filterType != null) {
				list = list.stream().filter(r -> ((String) r[2]).contains(filterType)).collect(Collectors.toList());
			}
			if (filterDB != null && filterDB.length() > 0) {
				list = list.stream().filter(r -> ((String) r[3]).toLowerCase().contains(filterDB.toLowerCase()))
						.collect(Collectors.toList());
			}
			if (filterTableName != null && filterTableName.length() > 0) {
				list = list.stream().filter(r -> ((String) r[4]).toLowerCase().contains(filterTableName.toLowerCase()))
						.collect(Collectors.toList());
			}
			return list;
		} catch (Exception e) {
			logger.error("filtering error {}", e.getMessage());
			return new ArrayList<String[]>();
		}
	}

	@Inject
	private SelectModelFactory selectModelFactory;

	@InjectComponent
	private Zone zEvaluationTable;

	@Inject
	private Request request;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	public void onSuccessFromFormFilter() {
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(zEvaluationTable);
		}
	}

	private void setEvaluationPointGrade(Object[] context, String grade) {
		String[] object = evaluationData.stream().filter(e -> Arrays.equals(e, context)).findFirst().orElse(null);
		if (object == null) {
			logger.error("{} not found", context);
		} else {
			object[1] = grade;
		}
	}

	public void onMarkPos(Object[] context) {
		setEvaluationPointGrade(context, "1");
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(zEvaluationTable);
		}
	}

	public void onMarkNeg(Object[] context) {
		setEvaluationPointGrade(context, "-1");
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(zEvaluationTable);
		}
	}

	public void onMarkZero(Object[] context) {
		setEvaluationPointGrade(context, "0");
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(zEvaluationTable);
		}
	}

	public void onMarkReset(Object[] context) {
		setEvaluationPointGrade(context, null);
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(zEvaluationTable);
		}
	}

	public void onSetUngradedPos() {
		for (String[] ss : getFilteredEvaluationData()) {
			if (ss[1] == null || ss[1].equals("")) {
				setEvaluationPointGrade(ss, "1");
			}
		}
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(zEvaluationTable);
		}
	}

	public void onSetUngradedNeg() {
		for (String[] ss : getFilteredEvaluationData()) {
			if (ss[1] == null || ss[1].equals("")) {
				setEvaluationPointGrade(ss, "-1");
			}
		}
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(zEvaluationTable);
		}
	}

	public void onSetUngradedZero() {
		for (String[] ss : getFilteredEvaluationData()) {
			if (ss[1] == null || ss[1].equals("")) {
				setEvaluationPointGrade(ss, "0");
			}
		}
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(zEvaluationTable);
		}
	}

	public void onResetGraded() {
		for (String[] ss : getFilteredEvaluationData()) {
			setEvaluationPointGrade(ss, null);
		}
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(zEvaluationTable);
		}
	}

	public String getGenContext() {
		return oneRow[0] + "!" + oneRow[1] + "!" + oneRow[2] + "!" + oneRow[3];
	}

	public String[] getParsedRow() {
		return Arrays.copyOfRange(oneRow, 1, oneRow.length);
	}

	public String getCorrectnessClass() {
		if (oneRow != null && oneRow[1] != null) {
			Integer grade = Integer.parseInt(oneRow[1]);
			if (grade > 0) {
				return "grade-pos";
			} else if (grade == 0) {
				return "grade-zero";
			} else if (grade < 0) {
				return "grade-neg";
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	private Long evalPoints(long submissionId, String evalPart) {
		Long sum = 0L;
		for (String[] tableUserDataPoint : evaluationData.stream()
				.filter(e -> e[2].equals(evalPart) && e[0].equals(Long.toString(submissionId)))
				.collect(Collectors.toList())) {
			if (tableUserDataPoint[1] != null) {
				sum += Long.parseLong(tableUserDataPoint[1]);
			}
		}
		return sum;
	}

	private long calcNumElements(long submissionId, String evalPart) {
		return evaluationData.stream().filter(e -> e[2].equals(evalPart) && e[0].equals(Long.toString(submissionId)))
				.collect(Collectors.toList()).size();
	}

	public void onLoadSavedData() {
		evaluationData = new ArrayList<String[]>();
		for (StudentSubmitSolution submission : getSubmissions()) {
			if (submission.getEvaluations() != null && submission.getEvaluations().size() > 0) {
				if (submission.getEvaluations().size() == 1) {
					SolutionAssessment sa = submission.getEvaluations().get(0);
					if (sa.getFeedbackSource() != null) {
						ObjectMapper mapper = new ObjectMapper();
						mapper.enable(SerializationFeature.INDENT_OUTPUT);
						try {
							List<String[]> partialData = mapper.readValue(sa.getFeedbackSource(),
									new TypeReference<List<String[]>>() {
									});
							logger.info("From JSON {} {}", submission.getStudentSubmitSolutionId(), partialData.size());
							evaluationData.addAll(partialData);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					throw new RuntimeException("Too many evaluations");
				}
			}
		}
	}

	@CommitAfter
	public void onSaveDataAndGenerateReports() {
		for (StudentSubmitSolution submission : getSubmissions()) {
			String assessmentComment = "<h2>Evaluation report of the relational schema</h2>\n";
			double total = 0;
			String formula = systemConfigService.getCode("Task", taskInTestInstanceToGrade.getTask().getTaskId(),
					"FORMULA");
			HashMap<String, Long> sums = new HashMap<String, Long>();

			for (String s : ModelConstants.AllDBRelationalObjectTypes) {
				Long points = evalPoints(submission.getStudentSubmitSolutionId(), s);
				sums.put(s, points);
				Long numElements = calcNumElements(submission.getStudentSubmitSolutionId(), s);
				assessmentComment += "<p>Evaluting part <b>" + s.toString() + "</b>: we have found "
						+ numElements.toString() + " elements in your DB relational schema.<br/>\n";
				assessmentComment += "After checking for correctness you were awarded " + points.toString()
						+ " points.<p>";
			}

			if (formula != null) {
				Expression e = new ExpressionBuilder(formula)
						.variables(ModelConstants.DBRelationalObjectTypesTABLE,
								ModelConstants.DBRelationalObjectTypesPK, ModelConstants.DBRelationalObjectTypesFK,
								ModelConstants.DBRelationalObjectTypesNN, ModelConstants.DBRelationalObjectTypesCHECK,
								ModelConstants.DBRelationalObjectTypesUK, ModelConstants.DBRelationalObjectTypesDATA)
						.build()
						.setVariable(ModelConstants.DBRelationalObjectTypesTABLE,
								sums.get(ModelConstants.DBRelationalObjectTypesTABLE))
						.setVariable(ModelConstants.DBRelationalObjectTypesPK,
								sums.get(ModelConstants.DBRelationalObjectTypesPK))
						.setVariable(ModelConstants.DBRelationalObjectTypesFK,
								sums.get(ModelConstants.DBRelationalObjectTypesFK))
						.setVariable(ModelConstants.DBRelationalObjectTypesNN,
								sums.get(ModelConstants.DBRelationalObjectTypesNN))
						.setVariable(ModelConstants.DBRelationalObjectTypesCHECK,
								sums.get(ModelConstants.DBRelationalObjectTypesCHECK))
						.setVariable(ModelConstants.DBRelationalObjectTypesUK,
								sums.get(ModelConstants.DBRelationalObjectTypesUK))
						.setVariable(ModelConstants.DBRelationalObjectTypesDATA,
								sums.get(ModelConstants.DBRelationalObjectTypesDATA));
				total = e.evaluate();
			} else {
				total = -1;
			}

			assessmentComment += "<p>Total score: " + total + "</p>";
			SolutionAssessment sa = null;
			if (submission.getEvaluations() == null || submission.getEvaluations().size() == 0) {
				sa = new SolutionAssessment();
				sa.setStudentSubmitSolution(submission);
			} else {
				sa = submission.getEvaluations().get(0);
			}
			sa.setGrade((float) total);
			sa.setEvaluatedOn(new Date());
			sa.setFeedback(assessmentComment);
			sa.setPassed(true);

			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			try {
				List<String[]> partialData = evaluationData.stream()
						.filter(e -> e[0].equals(Long.toString(submission.getStudentSubmitSolutionId())))
						.collect(Collectors.toList());
				logger.info("To JSON {} {}", submission.getStudentSubmitSolutionId(), partialData.size());
				String json = mapper.writeValueAsString(partialData);
				sa.setFeedbackSource(json);
			} catch (Exception e) {
				logger.error("JSON convertion {}", e.getMessage());
			}
			genericService.saveOrUpdate(sa);
		}
	}

	void onRefreshData() {
		Triplet<List<String[]>, List<String>, List<String>> results = evaluationService
				.getDDLEvaluationDataFromStudentDatabases(getSubmissions());
		evaluationData = results.getFirstItem();
		resultsHeadersSimple = results.getSecondItem();
		resultsErrors = results.getThirdItem();
	}

	public class ComparatorEvaluationData implements Comparator<String[]> {

		public String getCoding(String[] array) {
			String res = "";
			for (int i = 4; i < array.length; i++) {
				if (array[i] != null) {
					res += array[i] + "|";
				} else {
					res += "|";
				}
			}
			return res;
		}

		@Override
		public int compare(String[] s1, String[] s2) {
			String coded1 = getCoding(s1);
			String coded2 = getCoding(s2);
			if (coded1.length() < coded2.length()) {
				return -1;
			} else {
				if (coded1.length() > coded2.length()) {
					return 1;
				} else {
					return coded1.compareTo(coded2);
				}
			}
		}

	}

}
