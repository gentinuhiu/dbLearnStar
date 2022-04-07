package dblearnstar.webapp.pages.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import dblearnstar.model.entities.Translation;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.annotations.InstructorPage;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.TranslationService;
import dblearnstar.webapp.services.UsefulMethods;

@AdministratorPage
@InstructorPage
@Import(stylesheet = { "TranslationPage.css" })
public class TranslationPage {

	@Inject
	private TranslationService translationService;
	@Inject
	private GenericService genericService;

	@Persist
	@Property
	private String filter;

	@Persist
	@Property
	private Translation editedTranslation;

	@Property
	private Translation translationRow;

	public List<Translation> getListTranslations() {
		List<Translation> l = UsefulMethods.castList(Translation.class, genericService.getAll(Translation.class));
		if (filter != null && filter.length() > 0) {
			for (String filterWord : filter.split(" ")) {
				l = l.stream().filter(
						t -> (t.getOriginalObjectId() + t.getClassName() + t.getAttributeCode() + t.getTranslatedText())
								.toLowerCase().contains(filterWord.toLowerCase()))
						.collect(Collectors.toList());
			}
		}
		return l;
	}

	void onActionFromEditTranslation(Translation t) {
		editedTranslation = t;
	}

	void onActionFromNewTranslation() {
		editedTranslation = new Translation();
	}

	void onActionFromCancelTranslation() {
		editedTranslation = null;
	}

	@CommitAfter
	void onActionFromDeleteTranslation(Translation t) {
		genericService.delete(t);
	}

	@CommitAfter
	void onSuccessFromFrmTranslation() {
		genericService.save(editedTranslation);
		editedTranslation = null;
	}
}
