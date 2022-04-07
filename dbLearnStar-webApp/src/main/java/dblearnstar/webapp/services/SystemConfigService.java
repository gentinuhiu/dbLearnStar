package dblearnstar.webapp.services;

public interface SystemConfigService {
	public String getCode(String className, long originalObjectId, String type);

	public String getValue(String className, long originalObjectId, String type);
}
