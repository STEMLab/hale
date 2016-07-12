/*
 * Copyright (c) 2016 wetransform GmbH
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.jdbc.msaccess.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.jdbc.JDBCSchemaReader;
import eu.esdihumboldt.hale.io.jdbc.msaccess.MsAccessSchemaReader;

/**
 * To test Access database
 * 
 * @author Arun
 *
 */
public abstract class MsAccessDataReaderTestSuit {

	private static final ALogger log = ALoggerFactory.getLogger(MsAccessDataReaderTestSuit.class);

	/**
	 * Source Database name
	 */
	protected String SOURCE_DB_NAME;

	/**
	 * Source Database Extension
	 */
	protected String SOURCE_DB_EXT;

	/**
	 * Source Database path
	 */
	protected String SOURCE_DB_PATH;

	/**
	 * User name to connect to database
	 */
	protected String USER_NAME;

	/**
	 * password to connect to database
	 */
	protected String PASSWORD;

	/**
	 * Query to execute
	 */
	protected String SQL_QUERY;

	private static File TEMP_SOURCE_FILE_NAME = null;

	/**
	 * Copies the source database to a temporary file.
	 * 
	 * @throws IOException
	 *             if temp file can't be created
	 */
	public void createSourceTempFile() throws IOException {
		ByteSource source = Resources.asByteSource(MsAccessDataReaderTestSuit.class.getClassLoader().getResource(SOURCE_DB_PATH));
		ByteSink dest = Files.asByteSink(getSourceTempFilePath());
		source.copyTo(dest);
	}

	/**
	 * Generates a random path (within the system's temporary folder) for the
	 * source database.
	 * 
	 * @return the absolute path of the source temp file
	 */
	public File getSourceTempFilePath() {

		if (TEMP_SOURCE_FILE_NAME == null) {
			try {
				TEMP_SOURCE_FILE_NAME = File.createTempFile(SOURCE_DB_NAME, SOURCE_DB_EXT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return TEMP_SOURCE_FILE_NAME;
		// return getTempDir() + File.separator + getRandomNumber() + "_" +
		// SOURCE_DB_NAME;
	}

	/**
	 * Deletes the source temp file.
	 */
	public void deleteSourceTempFile() {
		deleteTempFile(getSourceTempFilePath());
	}

	private void deleteTempFile(File tempFile) {
		if (tempFile.exists()) {
			tempFile.delete();
		}
	}

	/**
	 * To get {@link Connection} object by giving database location, user name
	 * and password.
	 * 
	 * @return Connection object
	 */
	public Connection getConnection() {
		Connection con = null;

		try {
			con = DriverManager.getConnection("jdbc:ucanaccess://" + getSourceTempFilePath(), USER_NAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return con;

	}

	/**
	 * Connection to MS Access database and getting first row data.
	 * 
	 * @return String value, First row data joined by delimiter \t
	 */
	public String getFirstData() {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = getConnection();

			st = con.createStatement();
			rs = st.executeQuery(SQL_QUERY);

			StringBuilder row = new StringBuilder();
			if (rs != null) {
				rs.next();

				row.append(String.valueOf(rs.getInt(1)));
				row.append("\t");
				row.append(rs.getString(2));
				row.append("\t");
				row.append(String.valueOf(rs.getInt(3)));
				row.append("\t");
				row.append(String.valueOf(rs.getInt(4)));

			}

			return row.toString();

		} catch (Exception e) {
			log.error("Could not able to get data from MsAccessDatabase", e);
			return null;
		} finally {
			try {
				if (st != null)
					st.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				// ignore
			}
		}
	}

	/**
	 * Test - reads a sample MsAccess Database schema. UCanAccess lib should not
	 * throw any error.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	public void schemaReaderTest() throws Exception {
		MsAccessSchemaReader schemaReader = new MsAccessSchemaReader();
		schemaReader.setSource(new FileIOSupplier(getSourceTempFilePath()));
		schemaReader.setParameter(JDBCSchemaReader.PARAM_USER, Value.of(USER_NAME));
		schemaReader.setParameter(JDBCSchemaReader.PARAM_PASSWORD, Value.of(PASSWORD));
		schemaReader.setIsSchemaNameQuoted(false);

		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();
		Collection<? extends TypeDefinition> k = schema.getTypes();

		for (TypeDefinition def : k)
			System.out.println(def.getDisplayName());

		assertTrue(schema != null);
	}

	/**
	 * Test - reads a sample MsAccess Database schema. UCanAccess lib should not
	 * throw any error.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	public void schemaReaderWithErrorTest() throws Exception {
		MsAccessSchemaReader schemaReader = new MsAccessSchemaReader();
		schemaReader.setSource(new FileIOSupplier(getSourceTempFilePath()));
		schemaReader.setParameter(JDBCSchemaReader.PARAM_USER, Value.of(USER_NAME));
		schemaReader.setParameter(JDBCSchemaReader.PARAM_PASSWORD, Value.of(PASSWORD));

		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();
		Collection<? extends TypeDefinition> k = schema.getTypes();

		for (TypeDefinition def : k)
			System.out.println(def.getDisplayName());

		assertTrue(schema != null);
	}

}