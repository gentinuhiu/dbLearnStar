package dblearnstar.webapp.services;

import org.apache.tapestry5.ioc.annotations.Inject;

import dblearnstar.model.entities.SystemParameter;

public class SystemConfigServiceImpl implements SystemConfigService {

	@Inject
	private GenericService genericService;

	@Override
	public String getValue(String className, long originalObjectId, String type) {
		SystemParameter sp = (UsefulMethods.castList(SystemParameter.class,
				genericService.getAll(SystemParameter.class))).stream()
				.filter(p -> p.getClassName().equals(className) && p.getOriginalObjectId() == originalObjectId
						&& p.getType().equals(type))
				.findFirst().orElse(null);
		if (sp != null) {
			return sp.getValue();
		} else {
			return null;
		}
	}

	@Override
	public String getCode(String className, long originalObjectId, String type) {
		SystemParameter sp = (UsefulMethods.castList(SystemParameter.class,
				genericService.getAll(SystemParameter.class))).stream()
				.filter(p -> p.getClassName().equals(className) && p.getOriginalObjectId() == originalObjectId
						&& p.getType().equals(type))
				.findFirst().orElse(null);
		if (sp != null) {
			return sp.getCode();
		} else {
			return null;
		}
	}
}
