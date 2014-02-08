package org.bosik.diacomp.services.exceptions;

public class DeprecatedAPIException extends AuthException
{
	private static final long	serialVersionUID	= 1L;

	public DeprecatedAPIException(String msg)
	{
		super(msg);
	}
}
