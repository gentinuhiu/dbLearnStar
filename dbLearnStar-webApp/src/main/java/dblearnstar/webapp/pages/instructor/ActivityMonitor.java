package dblearnstar.webapp.pages.instructor;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;

import dblearnstar.model.entities.ActivityInTask;
import dblearnstar.model.entities.Person;
import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.annotations.InstructorPage;
import dblearnstar.webapp.services.ActivityManager;

@InstructorPage
@AdministratorPage
public class ActivityMonitor {

	@SessionState
	private UserInfo userInfo;

	@Inject
	private ActivityManager activityManager;

	private Person selectedPerson;

	private TaskInTestInstance selectedTaskInTestInstance;

	@Property
	private ActivityInTask activityInTask;

	@Property
	@Persist
	private String filterType;

	public void onActivate(EventContext ec) {
		if (!ec.isEmpty()) {
			selectedPerson = ec.get(Person.class, 0);
			selectedTaskInTestInstance = ec.get(TaskInTestInstance.class, 1);
		}
	}

	public Object[] onPassivate() {
		Object[] a = { selectedPerson, selectedTaskInTestInstance };
		return a;
	}

	public List<ActivityInTask> getActivitiesInTaskForPerson() {
		List<ActivityInTask> list = activityManager.getActivitiesInTaskForPerson(selectedPerson,
				selectedTaskInTestInstance);
		if (filterType != null && filterType.length() > 0) {
			return list.stream().filter(p -> p.getType().equals(filterType)).collect(Collectors.toList());
		} else {
			return list;
		}
	}

	public String getHash() {
		return Integer.toString(activityInTask.getPayload().trim().hashCode());
	}
}
