package org.bosik.compensation.bo.basic;

public class UniqueNamed extends Unique
{
	private static final long	serialVersionUID	= -7519935950545758515L;

	private String				name;

	public UniqueNamed(String name)
	{
		setName(name);
	}

	// ================================ GET / SET ================================

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		if (name == null)
		{
			throw new NullPointerException("Name can't be null");
		}
		if (name.trim().equals(""))
		{
			throw new IllegalArgumentException("Name must contain non-whitespace characters");
		}

		this.name = name;
	}

	// ================================ CLONE ================================

	@Override
	public UniqueNamed clone() throws CloneNotSupportedException
	{
		UniqueNamed result = (UniqueNamed) super.clone();
		result.name = name;
		return result;
	}
}