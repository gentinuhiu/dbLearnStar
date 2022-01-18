package dblearnstar.webapp.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;

import dblearnstar.model.entities.TaskInTestInstance;

public class TaskInTestInstanceSelectModel extends AbstractSelectModel {
	private List<TaskInTestInstance> taskInTestInstances;

	public TaskInTestInstanceSelectModel(List<TaskInTestInstance> taskInTestInstances) {
		if (taskInTestInstances == null) {
			this.taskInTestInstances = new ArrayList<TaskInTestInstance>();
		} else {
			this.taskInTestInstances = taskInTestInstances;
		}
	}

	@Override
	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	@Override
	public List<OptionModel> getOptions() {
		List<OptionModel> options = new ArrayList<OptionModel>();
		for (TaskInTestInstance taskInTestInstance : taskInTestInstances) {
			options.add(new OptionModelImpl(taskInTestInstance.getTask().getTitle(), taskInTestInstance));
		}
		return options;
	}
}