/*
 * Copyright (C) 2020 Daniel Volk <mail@volkarts.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.va.mysqlcompare;

import com.va.mysqlcompare.CompareResult.Diff;
import com.va.mysqlcompare.CompareResult.FieldDiff;
import com.va.mysqlcompare.CompareResult.KeyDiff;
import com.va.mysqlcompare.CompareResult.ProcedureDiff;
import com.va.mysqlcompare.CompareResult.TableDiff;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Compare
{
	private static final Logger LOG = LoggerFactory.getLogger(Compare.class);

	private final ConnectionsManager conManager;

	public Compare(ConnectionsManager conManager)
	{
		this.conManager = conManager;
	}

	public CompareResult doCompare(String databaseA, String databaseB) throws Exception
	{
		State state = new State(conManager, databaseA, databaseB);

		DBOReader reader1 = new DBOReader(state.getConnection(State.Side.A));
		DBOReader reader2 = new DBOReader(state.getConnection(State.Side.B));

		NamedObjectList<TableInfo> tables1 = reader1.readTables(state.getDatabase(State.Side.A));
		NamedObjectList<TableInfo> tables2 = reader2.readTables(state.getDatabase(State.Side.B));

		for (TableInfo tableInfo : tables1)
		{
//            LOG.info("Scanning table {}", tableName);
			if (tables2.contains(tableInfo.getName()))
			{
				boolean tableChildrenEquals = true;

				if (!compareFields(state, tableInfo.getName()))
				{
					tableChildrenEquals = false;
				}

				if (!compareKeys(state, tableInfo.getName()))
				{
					tableChildrenEquals = false;
				}

				TableInfo tableInfo2 = tables2.get(tableInfo.getName());

				if (tableChildrenEquals)
				{
					if (!tableInfo.equals(tableInfo2))
					{
						state.compareResult.addDiff(new TableDiff(Diff.Mode.DIFFERENT, tableInfo, tableInfo2));
					}
					else
					{
						state.compareResult.addDiff(new TableDiff(Diff.Mode.EQUAL, tableInfo, tableInfo2));
					}
				}
				else
				{
					state.compareResult.addDiff(new TableDiff(Diff.Mode.CHILDREN_DIFFER, tableInfo, tableInfo2));
				}
			}
			else
			{
				state.compareResult.addDiff(new TableDiff(Diff.Mode.LEFT_ONLY, tableInfo, null));

				LOG.debug("Table {} only in A", tableInfo.getName());
			}
		}

		for (TableInfo tableInfo : tables2)
		{
//            LOG.info("Scanning table {}", tableName);
			if (!tables1.contains(tableInfo.getName()))
			{
				state.compareResult.addDiff(new TableDiff(Diff.Mode.RIGHT_ONLY, null, tableInfo));

				LOG.debug("Table {} only in B", tableInfo.getName());
			}
		}

		NamedObjectList<ProcedureInfo> procedures1 = loadProcedures(state, State.Side.A);
		NamedObjectList<ProcedureInfo> procedures2 = loadProcedures(state, State.Side.B);

		for (ProcedureInfo procedureInfo : procedures1)
		{
			if (procedures2.contains(procedureInfo.getName()))
			{
				if (!procedureInfo.equals(procedures2.get(procedureInfo.getName())))
				{
					state.compareResult.addDiff(new ProcedureDiff(Diff.Mode.DIFFERENT, procedureInfo, procedures2.get(procedureInfo.getName())));

					LOG.debug("Procedure {} (A) differs from {} (B)", procedureInfo.getName(), procedures2.get(procedureInfo.getName()).getName());
				}
				else
				{
					state.compareResult.addDiff(new ProcedureDiff(Diff.Mode.EQUAL, procedureInfo, procedures2.get(procedureInfo.getName())));
				}
			}
			else
			{
				state.compareResult.addDiff(new ProcedureDiff(Diff.Mode.LEFT_ONLY, procedureInfo, null));

				LOG.debug("Procedure {} only in A", procedureInfo.getName());
			}
		}

		for (ProcedureInfo procedureInfo : procedures2)
		{
			if (!procedures1.contains(procedureInfo.getName()))
			{
				state.compareResult.addDiff(new ProcedureDiff(Diff.Mode.RIGHT_ONLY, null, procedureInfo));

				LOG.debug("Procedure {} only in B", procedureInfo.getName());
			}
		}

		return state.compareResult;
	}

	private boolean compareFields(State state, String tableName) throws SQLException
	{
		NamedObjectList<FieldInfo> fields1 = loadFields(state, State.Side.A, tableName);
		NamedObjectList<FieldInfo> fields2 = loadFields(state, State.Side.B, tableName);

		boolean allFieldsEqual = true;

		for (FieldInfo field : fields1)
		{
			if (fields2.contains(field.getName()))
			{
				FieldInfo field2 = fields2.get(field.getName());

				if (!field.equals(field2))
				{
					allFieldsEqual = false;
					state.compareResult.addDiff(new FieldDiff(Diff.Mode.DIFFERENT, field, field2));

					LOG.debug("Field {}.{} (A) differs from {}.{} (B)", tableName, field.getName(), tableName, field2.getName());
				}
				else
				{
					state.compareResult.addDiff(new FieldDiff(Diff.Mode.EQUAL, field, field2));
				}
			}
			else
			{
				allFieldsEqual = false;
				state.compareResult.addDiff(new FieldDiff(Diff.Mode.LEFT_ONLY, field, null));

				LOG.debug("Field {}.{} only in A", tableName, field.getName());
			}
		}

		for (FieldInfo field : fields2)
		{
			if (!fields1.contains(field.getName()))
			{
				allFieldsEqual = false;
				state.compareResult.addDiff(new FieldDiff(Diff.Mode.RIGHT_ONLY, null, field));

				LOG.debug("Field {}.{} only in B", tableName, field.getName());
			}
		}

		return allFieldsEqual;
	}

	private NamedObjectList<FieldInfo> loadFields(State state, State.Side side, String tableName)
		throws SQLException
	{
		NamedObjectList<FieldInfo> fields = new NamedObjectList<>();
		try (Statement stmt = state.getConnection(side).createStatement())
		{
			ResultSet result = stmt.executeQuery("SHOW FULL FIELDS FROM `" + state.getDatabase(side)
				+ "`.`" + tableName + "`");
			String lastFieldName = null;
			while (result.next())
			{
				fields.add(new FieldInfo(tableName, result, lastFieldName));
				lastFieldName = result.getString(1);
			}
		}
		return fields;
	}

	private boolean compareKeys(State state, String tableName) throws SQLException
	{
		HashMap<String, KeyInfo> keys1 = loadKeys(state, State.Side.A, tableName);
		HashMap<String, KeyInfo> keys2 = loadKeys(state, State.Side.B, tableName);

		boolean allKeysEqual = true;

		// first process dropped keys to prevent max key size overflow
		for (Map.Entry<String, KeyInfo> entry : keys2.entrySet())
		{
			if (!keys1.containsKey(entry.getKey()))
			{
				allKeysEqual = false;
				state.compareResult.addDiff(new KeyDiff(Diff.Mode.RIGHT_ONLY, null, entry.getValue()));

				LOG.debug("Key {}.{} only in B", tableName, entry.getValue().getName());
			}
		}

		for (Map.Entry<String, KeyInfo> entry : keys1.entrySet())
		{
			if (keys2.containsKey(entry.getKey()))
			{
				if (!entry.getValue().equals(keys2.get(entry.getKey())))
				{
					allKeysEqual = false;
					state.compareResult.addDiff(new KeyDiff(Diff.Mode.DIFFERENT, entry.getValue(), keys2.get(entry.getKey())));

					LOG.debug("Key {}.{} (A) differs from {}.{} (B)", tableName, entry.getValue().getName(), tableName, keys2.get(entry.getKey()).getName());
				}
				else
				{
					state.compareResult.addDiff(new KeyDiff(Diff.Mode.EQUAL, entry.getValue(), keys2.get(entry.getKey())));
				}
			}
			else
			{
				allKeysEqual = false;
				state.compareResult.addDiff(new KeyDiff(Diff.Mode.LEFT_ONLY, entry.getValue(), null));

				LOG.debug("Key {}.{} only in A", tableName, entry.getValue().getName());
			}
		}

		return allKeysEqual;
	}

	private HashMap<String, KeyInfo> loadKeys(State state, State.Side side, String tableName)
		throws SQLException
	{
		HashMap<String, KeyInfo> keys = new HashMap<>();
		try (Statement stmt = state.getConnection(side).createStatement())
		{
			ResultSet result = stmt.executeQuery("SHOW KEYS FROM `" + state.getDatabase(side)
				+ "`.`" + tableName + "`");
			while (result.next())
			{
				keys.put(result.getString("Key_name").toLowerCase(), new KeyInfo(result));
			}
		}
		return keys;
	}

	private NamedObjectList<ProcedureInfo> loadProcedures(State state, State.Side side) throws SQLException
	{
		NamedObjectList<ProcedureInfo> tables = new NamedObjectList<>();
		try (Statement stmt = state.getConnection(side).createStatement())
		{
			ResultSet result = stmt.executeQuery(
				"SHOW FUNCTION STATUS WHERE Db = '" + state.getDatabase(side) + "'");
			while (result.next())
			{
				try (Statement stmt2 = state.getConnection(side).createStatement())
				{
					ResultSet result2 = stmt2.executeQuery("SHOW CREATE FUNCTION `"
						+ state.getDatabase(side) + "`.`" + result.getString("Name") + "`");
					result2.next();
					String query = result2.getString(3);
					tables.add(new ProcedureInfo(result.getString("Name"), result.getString("Type"), query));
				}
			}
			result = stmt.executeQuery(
				"SHOW PROCEDURE STATUS WHERE Db = '" + state.getDatabase(side) + "'");
			while (result.next())
			{
				try (Statement stmt2 = state.getConnection(side).createStatement())
				{
					ResultSet result2 = stmt2.executeQuery("SHOW CREATE PROCEDURE `"
						+ state.getDatabase(side) + "`.`" + result.getString("Name") + "`");
					result2.next();
					String query = result2.getString(3);
					tables.add(new ProcedureInfo(result.getString("Name"), result.getString("Type"), query));
				}
			}
		}
		return tables;
	}

	private static class State
	{
		public enum Side
		{
			A,
			B
		}

		public final ConnectionsManager conManager;
		public final String databaseA;
		public final String databaseB;
		public final CompareResult compareResult;

		public State(ConnectionsManager conManager, String databaseA, String databaseB)
		{
			this.conManager = conManager;
			this.databaseA = databaseA;
			this.databaseB = databaseB;
			this.compareResult = new CompareResult(databaseA, databaseB);
		}

		public Connection getConnection(Side side)
		{
			switch (side)
			{
				case A:
					return conManager.getConnectionA();
				case B:
					return conManager.getConnectionB();
				default:
					return null;
			}
		}

		public String getDatabase(Side side)
		{
			switch (side)
			{
				case A:
					return databaseA;
				case B:
					return databaseB;
				default:
					return null;
			}
		}
	}
}
