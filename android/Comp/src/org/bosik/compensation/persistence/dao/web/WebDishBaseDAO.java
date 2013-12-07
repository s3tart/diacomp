package org.bosik.compensation.persistence.dao.web;

import java.util.List;
import org.bosik.compensation.bo.dishbase.DishItem;
import org.bosik.compensation.persistence.dao.DishBaseDAO;
import org.bosik.compensation.persistence.exceptions.DuplicateException;
import org.bosik.compensation.persistence.exceptions.ItemNotFoundException;

public class WebDishBaseDAO implements DishBaseDAO
{
	@Override
	public String add(DishItem item) throws DuplicateException
	{
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void delete(String id) throws ItemNotFoundException
	{
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<DishItem> findAll()
	{
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<DishItem> findAny(String filter)
	{
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public DishItem findOne(String exactName)
	{
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public DishItem findById(String id)
	{
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void replaceAll(List<DishItem> newList, int newVersion)
	{
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void update(DishItem item) throws ItemNotFoundException
	{
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public int getVersion()
	{
		throw new UnsupportedOperationException("Not implemented");
	}
}