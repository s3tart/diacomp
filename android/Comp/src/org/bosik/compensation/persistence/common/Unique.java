package org.bosik.compensation.persistence.common;

import java.util.UUID;

/**
 * 1. Имеет поле ID (потребуется в методах get() / set() контейнеров).<br/>
 * 2. Реализует {@link Cloneable}.
 * 
 * @author Bosik
 * 
 */
public class Unique implements Cloneable
{
	private String	id;
	private String	name;

	public Unique()
	{
		id = UUID.randomUUID().toString();
	}

	// ================================ GET / SET ================================

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	// ================================ CLONE ================================

	@Override
	public Unique clone() throws CloneNotSupportedException
	{
		Unique result = (Unique) super.clone();

		result.id = getId();

		return result;
	}

	// ================================ OTHER ================================

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Unique other = (Unique) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else
			if (!id.equals(other.id))
				return false;
		return true;
	}
}