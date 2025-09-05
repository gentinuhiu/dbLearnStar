package dblearnstar.webapp.services;

import java.util.List;
import java.util.Map;

import dblearnstar.model.entities.TestInstanceParameters;
import dblearnstar.model.model.Triplet;

public interface TransactionService {
	Triplet<List<Object[]>, List<String>, List<String>> runInSandboxClone(
	        String sql,
	        String correctQuery,
	        Map<String, String> testCases,
	        TestInstanceParameters tip,
	        String schema,
	        String userName,
	        String templateDb);
}
