package org.bosik.diacomp.persistence.dao.web;

import java.util.List;
import org.bosik.diacomp.bo.foodbase.FoodItem;
import org.bosik.diacomp.persistence.common.Versioned;
import org.bosik.diacomp.persistence.dao.FoodBaseDAO;
import org.bosik.diacomp.persistence.dao.web.utils.client.WebClient;
import org.bosik.diacomp.persistence.exceptions.DuplicateException;
import org.bosik.diacomp.persistence.exceptions.ItemNotFoundException;
import org.bosik.diacomp.persistence.serializers.Serializer;

public class WebFoodBaseDAO implements FoodBaseDAO
{
	// private static final String TAG = WebFoodBaseDAO.class.getSimpleName();

	private WebClient						webClient;
	private Serializer<Versioned<FoodItem>>	serializer;

	public WebFoodBaseDAO(WebClient webClient, Serializer<Versioned<FoodItem>> serializer)
	{
		this.webClient = webClient;
		this.serializer = serializer;
	}

	@Override
	public String add(Versioned<FoodItem> item) throws DuplicateException
	{
		throw new UnsupportedOperationException();
		// item.updateTimeStamp();
		//
		// MemoryBase<Versioned<FoodItem>> base = load();
		// base.add(item);
		// save(base);
		// return item.getId();
	}

	@Override
	public void delete(String id) throws ItemNotFoundException
	{
		throw new UnsupportedOperationException();
		// MemoryBase<FoodItem> base = load();
		// base.remove(id);
		// save(base);
	}

	@Override
	public List<Versioned<FoodItem>> findAll()
	{
		throw new UnsupportedOperationException();
		// MemoryBase<FoodItem> base = load();
		// return base.findAll();
	}

	@Override
	public List<Versioned<FoodItem>> findAny(String filter)
	{
		throw new UnsupportedOperationException();
		// MemoryBase<FoodItem> base = load();
		// return base.findAny(filter);
	}

	@Override
	public Versioned<FoodItem> findById(String id)
	{
		throw new UnsupportedOperationException();
		// MemoryBase<FoodItem> base = load();
		// return base.findById(id);
	}

	@Override
	public Versioned<FoodItem> findOne(String exactName)
	{
		throw new UnsupportedOperationException();
		// MemoryBase<FoodItem> base = load();
		// return base.findOne(exactName);
	}

	@Override
	public void update(Versioned<FoodItem> item) throws ItemNotFoundException
	{
		throw new UnsupportedOperationException();
		// MemoryBase<FoodItem> base = load();
		// base.update(item);
		// save(base);
	}

	@Override
	public List<Versioned<FoodItem>> findSysAll()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Versioned<FoodItem> findSysById(String id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	// ----------------------------------- Web I/O -----------------------------------

	// private MemoryBase<FoodItem> load()
	// {
	// String source = webClient.getFoodBase();
	// return serializer.read(source);
	// }

	// private void save(MemoryBase<FoodItem> base)
	// {
	// String source = serializer.write(base);
	// webClient.postFoodBase(base.getVersion(), source);
	// }
}
