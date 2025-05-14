package dblearnstar.webapp.pages.admin;

import java.util.List;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import dblearnstar.model.entities.Model;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.TestManager;

@AdministratorPage
public class ManageModels {

	@Inject
	private GenericService genericService;

	@Inject
	private TestManager testManager;

	@Property
	private Model model;

	@Persist
	@Property
	private Model modelToEdit;

	public List<Model> getAllModels() {
		return testManager.getAllModels();
	}

	public void onActionFromNewModel() {
		modelToEdit = new Model();
	}

	public void onActionFromEditModel(Model m) {
		modelToEdit = m;
	}

	@CommitAfter
	public void saveChanges() {
		genericService.saveOrUpdate(modelToEdit);
	}

	private Boolean cancelForm = false;

	public void onCanceledFromFrmNewModel() {
		cancelForm = true;
	}

	public void onValidateFromFrmNewModel() {
		if (!cancelForm) {
			saveChanges();
		}
	}

	public void onSuccessFromFrmNewModel() {
		modelToEdit = null;
	}

}
