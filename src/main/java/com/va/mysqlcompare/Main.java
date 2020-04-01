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

import com.va.securestore.DataStorePasswordStore;
import com.va.securestore.PropertiesFileDataStore;
import com.va.securestore.Store;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static javax.swing.UIManager.put;

public class Main
{
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	static
	{
		setupUI();
	}

	private static void setupUI()
	{
		// set laf
		String plafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
		try
		{
			UIManager.setLookAndFeel(plafClassName);
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException
			| UnsupportedLookAndFeelException ex)
		{
			LOG.error(null, ex);
		}

		// font setup
		UIManager.getDefaults().entrySet().stream().map((entry) -> entry.getKey()).forEachOrdered((key) ->
		{
			Object value = UIManager.get(key);
			if (value != null && value instanceof FontUIResource)
			{
				FontUIResource fr = (FontUIResource)value;
				FontUIResource f = new FontUIResource(fr.getFamily(), Font.PLAIN, fr.getSize() - 1);
				UIManager.put(key, f);
			}
		});

		// specials
		put("Table.alternateRowColor", new Color(0xF0F0F0));
	}

	public static Image getAppIcon()
	{
		return new ImageIcon(Main.class.getResource("/com/va/mysqlcompare/logo.png")).getImage();
	}

	/*
	private static ConnectionsList connections;

	private static void loadConnectionsInfos()
	{
		connections = new ConnectionsList();

		ConnectionInfo ci;

		ci = new ConnectionInfo();
		ci.setName("localhost");
		ci.setHostname("127.0.0.1");
		ci.setPassword("root".toCharArray());
		connections.add(ci);

		ci = new ConnectionInfo();
		ci.setName("webdev3");
		ci.setHostname("10.10.11.240");
		ci.setPassword("qwertz".toCharArray());
		connections.add(ci);

		ci = new ConnectionInfo();
		ci.setName("web3");
		ci.setHostname("192.168.100.10");
		ci.setPassword("@Ip4h)&8m-kFsj".toCharArray());
		connections.add(ci);

		ci = new ConnectionInfo();
		ci.setName("web3demo");
		ci.setHostname("192.168.100.25");
		ci.setPassword("2l9xO\"q7UB>i1/".toCharArray());
		connections.add(ci);
	}
	*/

	private static Store store = new Store();

	private static File getConfigStorePath()
	{
		File path = new File(System.getProperty("user.home") + File.separator + ".MySQLCompare");
		if (!path.exists())
		{
			if (!path.mkdirs())
				throw new RuntimeException("Cannot create user config dir");
		}
		return path;
	}

	private static void openStore()
	{
		store.data = new PropertiesFileDataStore(new File(getConfigStorePath(), "config.xml"));
		if (!store.data.open())
			throw new RuntimeException("Cannot open config file");

		store.passwords = new DataStorePasswordStore(store.data);
	}

	public static void main(String[] args) throws Exception
	{
		openStore();

		SwingUtilities.invokeLater(() ->
		{
			new MainFrame(store).setVisible(true);
		});
	}
}
