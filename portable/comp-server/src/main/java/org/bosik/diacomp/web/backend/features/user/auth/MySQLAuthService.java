package org.bosik.diacomp.web.backend.features.user.auth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bosik.diacomp.core.services.exceptions.NotAuthorizedException;
import org.bosik.diacomp.web.backend.common.MySQLAccess;
import org.bosik.diacomp.web.backend.common.MySQLAccess.DataCallback;
import org.springframework.stereotype.Service;

@Service
public class MySQLAuthService implements AuthService
{
	private static final String		TABLE_USER				= "user";
	private static final String		COLUMN_USER_ID			= "ID";
	private static final String		COLUMN_USER_LOGIN		= "Login";
	private static final String		COLUMN_USER_HASHPASS	= "HashPass";
	private static final String		COLUMN_USER_DATE_REG	= "DateReg";
	private static final String		COLUMN_USER_DATE_LOGIN	= "DateLogin";

	private static MessageDigest	md5digest;

	{
		try
		{
			md5digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public int login(String login, String pass)
	{
		try
		{
			String hash = md5(pass);

			final String[] select = { COLUMN_USER_ID };
			final String where = String.format("(%s = ?) AND (%s = ?)", COLUMN_USER_LOGIN, COLUMN_USER_HASHPASS);
			final String[] whereArgs = { login, hash };

			return MySQLAccess.select(TABLE_USER, select, where, whereArgs, null, new DataCallback<Integer>()
			{
				@Override
				public Integer onData(ResultSet set) throws SQLException
				{
					if (set.next())
					{
						int id = set.getInt(COLUMN_USER_ID);
						// TODO: update DateLogin field
						return id;
					}
					else
					{
						throw new NotAuthorizedException();
					}
				}
			});
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static String md5(String s)
	{
		try
		{
			byte[] input = md5digest.digest(s.getBytes("UTF-8"));
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < input.length; ++i)
			{
				sb.append(Integer.toHexString((input[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Integer getIdByName(String userName)
	{
		try
		{
			final String[] select = { COLUMN_USER_ID };
			final String where = String.format("(%s = ?)", COLUMN_USER_LOGIN);
			final String[] whereArgs = { userName };

			return MySQLAccess.select(TABLE_USER, select, where, whereArgs, null, new DataCallback<Integer>()
			{
				@Override
				public Integer onData(ResultSet set) throws SQLException
				{
					if (set.next())
					{
						return set.getInt(COLUMN_USER_ID);
					}
					else
					{
						return null;
					}
				}
			});
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
}
