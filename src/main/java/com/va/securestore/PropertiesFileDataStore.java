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
package com.va.securestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesFileDataStore implements DataStore
{
	private static final Logger LOG = LoggerFactory.getLogger(PropertiesFileDataStore.class);

	private final File file;
	private final Properties props;

	public PropertiesFileDataStore(File file)
	{
		this.file = file;
		props = new Properties();
	}

	@Override
	public boolean open()
	{
		props.clear();

		if (!file.exists())
			return true;

		try (FileInputStream in = new FileInputStream(file))
		{
			props.loadFromXML(in);
		}
		catch (IOException e)
		{
			LOG.error(e.toString(), e);
			return false;
		}

		return true;
	}

	@Override
	synchronized public boolean persist()
	{
		try (FileOutputStream out = new FileOutputStream(file))
		{
			props.storeToXML(out, "Last change: " +
				DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US).format(new Date()));
		}
		catch (IOException e)
		{
			LOG.error(e.toString(), e);
			return false;
		}

		return true;
	}

	@Override
	public void close()
	{
	}

	@Override
	public List<String> keys()
	{
		final Set<Object> set = props.keySet();
		final List<String> out = new ArrayList<>();
		set.forEach((o) -> {
			out.add(o.toString());
		});
		return out;
	}

	@Override
	public List<String> keys(final String prefix)
	{
		final String p = prefix + ".";
		final Set<Object> set = props.keySet();
		final List<String> out = new ArrayList<>();
		set.forEach((o) -> {
			String key = o.toString();
			if (key.startsWith(p))
				out.add(key);
		});
		return out;
	}

	@Override
	public void putData(String key, String string)
	{
		props.setProperty(key, string);
		persist();
	}

	@Override
	public void putData(String key, int num)
	{
		props.setProperty(key, Integer.toHexString(num));
		persist();
	}

	@Override
	public void putData(String key, byte[] data)
	{
		props.setProperty(key, Base64.getEncoder().encodeToString(data));
		persist();
	}

	@Override
	public String getString(String key)
	{
		return getString(key, null);
	}

	@Override
	public String getString(String key, String def)
	{
		return props.getProperty(key, def);
	}


	@Override
	public int getInt(String key)
	{
		return getInt(key, 0);
	}

	@Override
	public int getInt(String key, int def)
	{
		String o = props.getProperty(key);

		if (o == null)
			return def;

		try
		{
			return Integer.parseInt(o, 16);
		}
		catch (NumberFormatException e)
		{
			LOG.warn("Key " + key + " contains no valid number: " + e.toString());
			return def;
		}
	}


	@Override
	public byte[] getBytes(String key)
	{
		return getBytes(key, null);
	}

	@Override
	public byte[] getBytes(String key, byte[] def)
	{
		String o = props.getProperty(key);

		if (o == null)
			return def;

		try
		{
			return Base64.getDecoder().decode(o);
		}
		catch (IllegalArgumentException e)
		{
			LOG.warn("Key " + key + " contains no valid base64 string: " + e.toString());
			return def;
		}
	}


}
