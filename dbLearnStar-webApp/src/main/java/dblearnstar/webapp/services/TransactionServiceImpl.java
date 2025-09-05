package dblearnstar.webapp.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import dblearnstar.model.entities.TestInstanceParameters;
import dblearnstar.model.model.Triplet;

public class TransactionServiceImpl implements TransactionService {
	@Inject
	private Logger logger;
	
	@Override
	public Triplet<List<Object[]>, List<String>, List<String>> runInSandboxClone(
	        String sql,
	        String correctQuery,
	        Map<String, String> testCases,
	        TestInstanceParameters tip,
	        String schema,
	        String userName,
	        String templateDb) {

	    List<Object[]> rows = new ArrayList<>();
	    List<String> headers = new ArrayList<>();
	    List<String> errors = new ArrayList<>();

	    String sbxStudent  = "sandbox_" + UUID.randomUUID().toString().replace("-", "");
	    String sbxExpected = "sandbox_" + UUID.randomUUID().toString().replace("-", "");

	    Connection admin = null, cStudent = null, cExpected = null;

	    try {
	        Properties props = new Properties();
	        props.setProperty("user", tip.getDbUser());
	        props.setProperty("password", tip.getDbPass());

	        // Admin connection (CREATE/DROP DATABASE)
	        String adminUrl = "jdbc:postgresql://" + tip.getHostname() + ":" + tip.getPort() + "/postgres";
	        admin = DriverManager.getConnection(adminUrl, props);

	        // Create two clean sandboxes from the same template
	        try (Statement st = admin.createStatement()) {
	            st.executeUpdate("CREATE DATABASE " + quoteIdent(sbxStudent)  + " TEMPLATE " + quoteIdent(templateDb));
	            st.executeUpdate("CREATE DATABASE " + quoteIdent(sbxExpected) + " TEMPLATE " + quoteIdent(templateDb));
	        }

	        // Open connections to both sandboxes
	        String urlStudent  = "jdbc:postgresql://" + tip.getHostname() + ":" + tip.getPort() + "/" + sbxStudent;
	        String urlExpected = "jdbc:postgresql://" + tip.getHostname() + ":" + tip.getPort() + "/" + sbxExpected;

	        cStudent  = DriverManager.getConnection(urlStudent,  props);
	        cExpected = DriverManager.getConnection(urlExpected, props);

	        cStudent.setAutoCommit(true);
	        cExpected.setAutoCommit(true);
	        cStudent.setReadOnly(false);
	        cExpected.setReadOnly(false);
	        setSchema(cStudent, schema);
	        setSchema(cExpected, schema);

	        // 1) Execute both batches (your existing sequential executor)
	        runBatchAndSummarizeSeq(cStudent,  sql);          // applies student changes
	        runBatchAndSummarizeSeq(cExpected, correctQuery); // applies expected changes

	        // 2) Run test cases on both DBs and compare results
	        headers.clear();
	        rows.clear();
	        headers.add("test_case");
	        headers.add("passed");

	        // If no test cases given, just return empty table
	        if (testCases != null && !testCases.isEmpty()) {
	            for (Map.Entry<String, String> tc : testCases.entrySet()) {
	                String name = tc.getKey();
	                String tcSql = tc.getValue();

	                boolean passed;
	                try {
	                    List<List<String>> mine = runSelectCanonical(cStudent, tcSql, 10_000);
	                    List<List<String>> exp  = runSelectCanonical(cExpected, tcSql, 10_000);
	                    passed = resultsEqual(mine, exp);
	                } catch (SQLException ex) {
	                    passed = false;
	                    logger.error("Test case '{}' failed to execute: {}", name, ex.getMessage());
	                    errors.add("Test case '" + name + "' error: " + ex.getMessage());
	                }

	                rows.add(new Object[] { name, String.valueOf(passed) });
	            }
	        }

	    } catch (Exception e) {
	        logger.error("Sandbox run failed for {}: {}", userName, e.getMessage());
	        errors.add("Sandbox error: " + e.getMessage());
	    } finally {
	        closeQuietly(cStudent);
	        closeQuietly(cExpected);
	        if (admin != null) {
	            try (Statement st = admin.createStatement()) {
	                try { st.executeUpdate("DROP DATABASE " + quoteIdent(sbxStudent)  + " WITH (FORCE)"); }
	                catch (SQLException ignore) { st.executeUpdate("DROP DATABASE " + quoteIdent(sbxStudent)); }
	                try { st.executeUpdate("DROP DATABASE " + quoteIdent(sbxExpected) + " WITH (FORCE)"); }
	                catch (SQLException ignore) { st.executeUpdate("DROP DATABASE " + quoteIdent(sbxExpected)); }
	            } catch (SQLException ex) {
	                logger.error("Could not drop sandboxes {} / {}: {}", sbxStudent, sbxExpected, ex.getMessage());
	            } finally {
	                closeQuietly(admin);
	            }
	        }
	    }

	    return new Triplet<>(rows, headers, errors);
	}
	/**
	 * Execute a SELECT (or any result-set returning statement) and return a canonical, order-insensitive
	 * representation: a sorted list of row-strings (each row is pipe-joined normalized values).
	 * If the query returns no ResultSet, it will return an empty list.
	 */
	private static List<List<String>> runSelectCanonical(Connection conn, String sql, int maxRows) throws SQLException {
	    List<List<String>> rows = new ArrayList<>();

	    try (Statement st = conn.createStatement()) {
	        boolean hasResult = st.execute(sql);

	        if (!hasResult) {
	            // No result set (e.g., someone put DML/DDL in a test case) -> treat as empty
	            return rows;
	        }

	        try (ResultSet rs = st.getResultSet()) {
	            final int cols = rs.getMetaData().getColumnCount();

	            while (rs.next() && rows.size() < maxRows) {
	                List<String> row = new ArrayList<>(cols);
	                for (int i = 1; i <= cols; i++) {
	                    Object v = rs.getObject(i);
	                    row.add(normalizeValue(v));
	                }
	                rows.add(row);
	            }
	        }

	        // Drain any trailing results
	        while (st.getMoreResults() || st.getUpdateCount() != -1) { /* ignore */ }
	    }

	    // Canonicalize: sort rows lexicographically
	    rows.sort((a, b) -> {
	        int n = Math.min(a.size(), b.size());
	        for (int i = 0; i < n; i++) {
	            int cmp = a.get(i).compareTo(b.get(i));
	            if (cmp != 0) return cmp;
	        }
	        return Integer.compare(a.size(), b.size());
	    });

	    return rows;
	}
	private static void setSchema(Connection c, String schema) throws SQLException {
	    if (schema != null && !schema.isBlank()) {
	        try (Statement st = c.createStatement()) {
	            st.execute("SET search_path TO " + quoteIdent(schema) + ", public");
	        }
	    }
	}
	private static String quoteIdent(String ident) {
	    return "\"" + ident.replace("\"", "\"\"") + "\"";
	}
	private static void closeQuietly(AutoCloseable c) {
	    if (c == null) return;
	    try { c.close(); } catch (Exception ignore) {}
	}
	/** Normalize values for stable comparisons (nulls, whitespace, timestamps, numerics). */
	private static String normalizeValue(Object v) {
	    if (v == null) return "<NULL>";

	    // unbox numerics to a common string form
	    if (v instanceof Number) {
	        // avoid locale issues
	        return new java.math.BigDecimal(v.toString()).stripTrailingZeros().toPlainString();
	    }

	    if (v instanceof java.sql.Timestamp) {
	        // ISO-8601 without nanos bloat
	        java.time.Instant inst = ((java.sql.Timestamp) v).toInstant();
	        return java.time.OffsetDateTime.ofInstant(inst, java.time.ZoneOffset.UTC).toString();
	    }
	    if (v instanceof java.sql.Date) {
	        return v.toString(); // yyyy-mm-dd
	    }
	    if (v instanceof java.sql.Time) {
	        return v.toString(); // HH:mm:ss[.sss]
	    }

	    String s = String.valueOf(v).trim();
	    // collapse internal whitespace to single spaces for robustness
	    s = s.replaceAll("\\s+", " ");
	    return s;
	}

	/** Deep equality of two canonical result sets (same size, same rows in same order after sorting). */
	private static boolean resultsEqual(List<List<String>> a, List<List<String>> b) {
	    if (a.size() != b.size()) return false;
	    for (int i = 0; i < a.size(); i++) {
	        List<String> ra = a.get(i);
	        List<String> rb = b.get(i);
	        if (ra.size() != rb.size()) return false;
	        for (int j = 0; j < ra.size(); j++) {
	            if (!java.util.Objects.equals(ra.get(j), rb.get(j))) return false;
	        }
	    }
	    return true;
	}
	/** Execute a sequence of statements one-by-one and summarize (skips txn-control rows). */
	private static List<StmtRes> runBatchAndSummarizeSeq(Connection conn, String rawSql) throws SQLException {
	    List<StmtRes> out = new ArrayList<>();
	    List<String> stmts = splitStatements(rawSql);
	    if (stmts.isEmpty()) return out;

	    try (Statement st = conn.createStatement()) {
	        for (String stmt : stmts) {
	            String tag = tagOf(stmt);
	            boolean isTxn = TXN_TAGS.contains(tag);

	            try {
	                boolean hasResult = st.execute(stmt);
	                if (hasResult) {
	                    // Count rows in the result set
	                    int rows = 0;
	                    try (ResultSet rs = st.getResultSet()) {
	                        while (rs.next()) rows++;
	                    }
	                    if (!isTxn) out.add(new StmtRes(tag, rows + " rows returned"));
	                } else {
	                    int upd = st.getUpdateCount();
	                    if (!isTxn) {
	                        if (upd != -1) out.add(new StmtRes(tag, upd + " rows affected"));
	                        else out.add(new StmtRes(tag, "no rows affected"));
	                    }
	                }
	                // Drain any extra results from this single statement (rare)
	                while (st.getMoreResults() || st.getUpdateCount() != -1) {
	                    // ignored on purpose
	                }
	            } catch (SQLException ex) {
	                if (!isTxn) out.add(new StmtRes(tag, "ERROR: " + ex.getMessage()));
	                // optional: break; // stop on first error
	            }
	        }
	    }
	    return out;
	}
	/** Split into simple statements by ';' (good enough for teaching/testing). */
	private static List<String> splitStatements(String sql) {
	    String s = sanitizeSql(sql);
	    List<String> parts = new ArrayList<>();
	    for (String piece : s.split(";")) {
	        String p = piece.trim();
	        if (!p.isEmpty()) parts.add(p);
	    }
	    return parts;
	}

	/** First keyword as a tag (e.g., INSERT, UPDATE, CREATE...). */
	private static String tagOf(String stmt) {
	    if (stmt == null) return "?";
	    String t = stmt.trim();
	    int sp = t.indexOf(' ');
	    String first = (sp > 0 ? t.substring(0, sp) : t);
	    return first.toUpperCase();
	}
	/** Sanitize possible annotation wrappers and comments. */
	private static String sanitizeSql(String sql) {
	    if (sql == null) return "";
	    String s = sql;

	    // Normalize possible @begin; ... end;@ wrappers to regular statements
	    s = s.replaceAll("(?i)@\\s*begin\\s*;", "BEGIN;");
	    s = s.replaceAll("(?i);\\s*end\\s*@", ";END;");
	    s = s.replace("@", "");

	    // Remove comments
	    s = s.replaceAll("(?m)--.*?$", "");   // -- line comments
	    s = s.replaceAll("/\\*.*?\\*/", "");  // /* ... */ block comments

	    // Normalize whitespace
	    s = s.replaceAll("[ \\t\\x0B\\f\\r]+", " ");
	    return s.trim();
	}
	private static final java.util.Set<String> TXN_TAGS = new java.util.HashSet<>(
		    java.util.Arrays.asList(
		        "BEGIN","COMMIT","END","ROLLBACK",
		        "START","START TRANSACTION",
		        "SAVEPOINT","RELEASE","RELEASE SAVEPOINT",
		        "SET","SET TRANSACTION"
		    )
		);

		// Minimal record for a single statement outcome
		private static final class StmtRes {
		    final String tag;
		    final String result;
		    StmtRes(String tag, String result) { this.tag = tag; this.result = result; }
		}
}
