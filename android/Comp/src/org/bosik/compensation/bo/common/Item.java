package org.bosik.compensation.bo.common;

import java.util.UUID;

/**
 * 1. Имеет поле ID (потребуется в методах get() / set() контейнеров).<br/>
 * 2. Реализует {@link Cloneable}.
 * 
 * @author Bosik
 * 
 */
public class Item implements Cloneable
{
	private String	id;

	public Item()
	{
		id = UUID.randomUUID().toString();
	}

	// ================================ GET / SET ================================

	public String getId()
	{
		return id;
	}

	// ================================ CLONE ================================

	@Override
	public Item clone() throws CloneNotSupportedException
	{
		Item result = (Item) super.clone();

		result.id = getId();

		return result;
	}

	/*
	 * public Item() {
	 * 
	 * }
	 * 
	 * public Item(Item copy) { setId(copy.getId()); }
	 */

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
		Item other = (Item) obj;
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