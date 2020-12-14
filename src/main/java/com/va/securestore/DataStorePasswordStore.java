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

import com.va.common.UserInteraction;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStorePasswordStore implements PasswordStore
{
	private static final Logger LOG = LoggerFactory.getLogger(DataStorePasswordStore.class);

	private static final String CIPHER_ALGORITHM_BASE = "AES";
	private static final String CIPHER_ALGORITHM = CIPHER_ALGORITHM_BASE + "/CBC/PKCS5Padding";
	private static final String KEY_MASTER_PASSWORD_SALT = "passwords.master_salt";
	private static final String KEY_MASTER_PASSWORD_HASH = "passwords.master_hash";

	private final DataStore dataStore;
	private boolean open;
	private Cipher cipher;
	private SecretKey cryptoKey;

	public DataStorePasswordStore(DataStore dataStore)
	{
		this.dataStore = dataStore;
		this.open = false;
	}

	@Override
	public boolean isOpen()
	{
		return open;
	}

	@Override
	public boolean open(UserInteraction ui)
	{
		try
		{
			cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			throw new RuntimeException(e);
		}

		byte[] salt = dataStore.getBytes(KEY_MASTER_PASSWORD_SALT);
		if (salt == null)
		{
			salt = PasswordUtils.generateRandomBytes(16);
			dataStore.putData(KEY_MASTER_PASSWORD_SALT, salt);
		}

		while (true)
		{
			char[] password = ui.getPassword("Master password");
			if (password == null)
				break;

			cryptoKey = new SecretKeySpec(PasswordUtils.hashPassword(password, salt), CIPHER_ALGORITHM_BASE);

			open = testOpen(PasswordUtils.hashPassword(toChars(cryptoKey.getEncoded()), salt));
			if (open)
				break;

			ui.showErrorMessage("Could not open password store: Wrong master password.", null);
		}

		 return open;
	}

	@Override
	public void close()
	{
		open = false;
	}

	@Override
	public char[] loadPassword(String key)
	{
		if (!open)
			return null;

		byte[] rawData = dataStore.getBytes(key, null);

		if (rawData == null)
			return null;

		PasswordValue pwVal = new PasswordValue();

		pwVal.loadFromBytes(rawData);

		IvParameterSpec ivSpec = new IvParameterSpec(pwVal.iv);

		char[] output;

		try
		{
			cipher.init(Cipher.DECRYPT_MODE, cryptoKey, ivSpec);

			output = toChars(cipher.doFinal(pwVal.encoded));
		}
		catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
			| BadPaddingException e)
		{
			LOG.error(e.toString(), e);
			return null;
		}

		return output;
	}

	@Override
	public void storePassword(String key, char[] password)
	{
		if (!open)
			return;

		PasswordValue pwVal = new PasswordValue();

		pwVal.iv = PasswordUtils.generateRandomBytes(cipher.getBlockSize());
		IvParameterSpec ivSpec = new IvParameterSpec(pwVal.iv);

		try
		{
			cipher.init(Cipher.ENCRYPT_MODE, cryptoKey, ivSpec);

			pwVal.encoded = cipher.doFinal(toBytes(password));
		}
		catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
			| BadPaddingException e)
		{
			LOG.error(e.toString(), e);
			return;
		}

		dataStore.putData(key, pwVal.combine());
	}

	private boolean testOpen(byte[] doubleHash)
	{
		byte[] pwHash = dataStore.getBytes(KEY_MASTER_PASSWORD_HASH);
		if (pwHash == null)
		{
			dataStore.putData(KEY_MASTER_PASSWORD_HASH, doubleHash);
			return true;
		}

		return Arrays.equals(doubleHash, pwHash);
	}

	private byte[] toBytes(char[] chars)
	{
		CharBuffer cb = CharBuffer.wrap(chars);
		ByteBuffer bb = Charset.forName("UTF-8").encode(cb);
		byte[] bytes = Arrays.copyOfRange(bb.array(), bb.position(), bb.limit());
		Arrays.fill(bb.array(), (byte)0); // clear sensitive data
		return bytes;
	}

	private char[] toChars(byte[] bytes)
	{
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		CharBuffer cb = Charset.forName("UTF-8").decode(bb);
		char[] chars = Arrays.copyOfRange(cb.array(), cb.position(), cb.limit());
		Arrays.fill(cb.array(), (char)0);
		return chars;
	}

	private class PasswordValue
	{
		public byte[] iv;
		public byte[] encoded;

		public byte[] combine()
		{
			byte[] allByteArray = new byte[iv.length + encoded.length];

			ByteBuffer buff = ByteBuffer.wrap(allByteArray);
			buff.put(iv);
			buff.put(encoded);

			return buff.array();
		}

		public void loadFromBytes(byte[] bytes)
		{
			int ivLen = cipher.getBlockSize();

			iv = new byte[ivLen];
			System.arraycopy(bytes, 0, iv, 0, ivLen);

			encoded = new byte[bytes.length - ivLen];
			System.arraycopy(bytes, ivLen, encoded, 0, bytes.length - ivLen);
		}
	}
}
