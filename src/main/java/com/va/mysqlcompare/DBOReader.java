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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBOReader
{
	private final Connection connection;

	public DBOReader(Connection connection)
	{
		this.connection = connection;
	}

	NamedObjectList<TableInfo> readTables(String databaseName) throws SQLException
	{
		NamedObjectList<TableInfo> tables = new NamedObjectList<>();
		try (Statement stmt = connection.createStatement())
		{
			ResultSet result = stmt.executeQuery("SHOW TABLES FROM `" + databaseName + "`");
			while (result.next())
			{
				try (Statement stmt2 = connection.createStatement())
				{
					ResultSet result2 = stmt2.executeQuery(
						"SHOW CREATE TABLE `" + databaseName + "`.`" + result.getString(1) + "`");
					result2.next();

					String query = result2.getString(2);

					tables.add(parseTableInfo(result2.getString(1), result2.getString(2)));
				}
			}
		}
		return tables;
	}

	private TableInfo parseTableInfo(String name, String info)
	{
		// ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci

		info = Pattern.compile("AUTO_INCREMENT=\\d+", Pattern.CASE_INSENSITIVE).matcher(info).replaceAll("");

		int pos = info.lastIndexOf(')');

		String engine = "InnoDB";
		Matcher em = Pattern.compile("ENGINE=([^\\s]+)", Pattern.CASE_INSENSITIVE).matcher(info);
		if (em.find(pos))
		{
			engine = em.group(1);
		}

		String charset = "utf8mb4";
		Matcher chm = Pattern.compile("CHARSET=([^\\s]+)", Pattern.CASE_INSENSITIVE).matcher(info);
		if (chm.find(pos))
		{
			charset = chm.group(1);
		}

		String collation = charset + "_general_ci";
		Matcher com = Pattern.compile("COLLATE=([^\\s]+)", Pattern.CASE_INSENSITIVE).matcher(info);
		if (com.find(pos))
		{
			collation = com.group(1);
		}

		return new TableInfo(name, info, engine, charset, collation);
	}
}
