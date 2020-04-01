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

import com.va.securestore.DataStore;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectionsList extends ArrayList<ConnectionInfo>
{
	private static final long serialVersionUID = 1L;

	private static final String KEY_ID_LIST = "connection_ids";
	private static final String KEY_CONNECTIONS = "connections";
	private static final String KEY_NAME = "name";
	private static final String KEY_HOSTNAME = "hostname";
	private static final String KEY_PORT = "port";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";

	public static String getConnectionInfoKeyPrefix(ConnectionInfo info)
	{
		return KEY_CONNECTIONS + "." + info.getId() + ".";
	}

	public static String getPasswordKey(ConnectionInfo info)
	{
		return getConnectionInfoKeyPrefix(info) + KEY_PASSWORD;
	}

	public void load(DataStore dataStore)
	{
		List<String> ids = extractIds(dataStore);

		ids.forEach((id) ->
		{
			add(loadConnectionInfo(dataStore, id));
		});
	}

	public void save(DataStore dataStore)
	{
		StringBuilder idsString = new StringBuilder();

		forEach((c) -> {
			idsString.append(c.getId().toString()).append(",");

			saveConnectionInfo(dataStore, c);
		});

		dataStore.putData(KEY_ID_LIST, idsString.toString());
	}

	private List<String> extractIds(DataStore dataStore)
	{
		List<String> output = new ArrayList<>();

		String idsString = dataStore.getString(KEY_ID_LIST);
		if (idsString == null)
			return output;

		String[] ids = idsString.split("\\s*,\\s*");

		for (String id : ids)
		{
			if (!"".equals(id))
				output.add(id);
		}

		return output;
	}

	private ConnectionInfo loadConnectionInfo(DataStore dataStore, String id)
	{
		String prefix = KEY_CONNECTIONS + "." + id + ".";

		ConnectionInfo info = new ConnectionInfo(UUID.fromString(id));

		info.setName(dataStore.getString(prefix + KEY_NAME));
		info.setHostname(dataStore.getString(prefix + KEY_HOSTNAME));
		info.setPort(dataStore.getInt(prefix + KEY_PORT, 3306));
		info.setUsername(dataStore.getString(prefix + KEY_USERNAME));

		return info;
	}

	private void saveConnectionInfo(DataStore dataStore, ConnectionInfo info)
	{
		String prefix = getConnectionInfoKeyPrefix(info);

		dataStore.putData(prefix + KEY_NAME, info.getName());
		dataStore.putData(prefix + KEY_HOSTNAME, info.getHostname());
		dataStore.putData(prefix + KEY_PORT, info.getPort());
		dataStore.putData(prefix + KEY_USERNAME, info.getUsername());
	}
}
