package org.bosik.compensation.persistence.dao.local.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bosik.compensation.persistence.common.MemoryBase;
import org.bosik.compensation.persistence.common.UniqueNamed;
import org.bosik.compensation.persistence.dao.BaseDAO;
import org.bosik.compensation.persistence.serializers.Serializer;
import org.bosik.compensation.utils.FileWorker;
import android.content.Context;
import android.util.Log;

/**
 * Local file base
 * 
 * @author Bosik
 * 
 * @param <T>
 *            Type of base's item
 */
public class FileBaseDAO<T extends UniqueNamed> implements BaseDAO<T>
{
	private static final String			TAG	= FileBaseDAO.class.getSimpleName();

	private MemoryBase<T>				base;
	private String						fileName;
	private Serializer<MemoryBase<T>>	serializer;
	private FileWorker					fileWorker;

	public FileBaseDAO(Context context, String fileName, Serializer<MemoryBase<T>> serializer) throws IOException
	{
		this.fileName = fileName;
		this.serializer = serializer;
		fileWorker = new FileWorker(context);

		load();
	}

	@Override
	public String add(T item) throws BaseDAO.DuplicateException
	{
		if (base.get(item.getId()) == null)
		{
			base.add(item);
			save();
			return item.getId();
		}
		else
		{
			throw new DuplicateException(item.getId());
		}
	}

	@Override
	public void delete(String id) throws BaseDAO.ItemNotFoundException
	{
		if (base.remove(id))
		{
			save();
		}
		else
		{
			throw new ItemNotFoundException(id);
		}
	}

	@Override
	public List<T> findAll()
	{
		return base.getAll();
	}

	@Override
	public List<T> findAny(String filter)
	{
		List<T> result = new ArrayList<T>();
		filter = filter.toUpperCase(Locale.US);

		for (int i = 0; i < base.count(); i++)
		{
			T item = base.get(i);
			if (item.getName().toUpperCase(Locale.US).contains(filter))
			{
				result.add(item);
			}
		}

		return result;
	}

	@Override
	public T findOne(String exactName)
	{
		for (int i = 0; i < base.count(); i++)
		{
			T item = base.get(i);
			if (item.getName().equals(exactName))
			{
				return item;
			}
		}
		return null;
	}

	@Override
	public void replaceAll(List<T> newList, int newVersion)
	{
		base.clear();
		for (T item : newList)
		{
			base.add(item);
		}
		base.setVersion(newVersion);
		save();
	}

	@Override
	public void update(T item) throws BaseDAO.ItemNotFoundException
	{
		try
		{
			base.update(item);
			save();
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new ItemNotFoundException(item.getId());
		}
	}

	@Override
	public int getVersion()
	{
		return base.getVersion();
	}

	// ----------------------------------- ОСТАЛЬНОЕ -----------------------------------

	private void load() throws IOException
	{
		if (fileWorker.fileExists(fileName))
		{
			String source = fileWorker.readFromFile(fileName);
			base = serializer.read(source);
			Log.v(TAG, String.format("Memory base \"%s\" loaded, total items: %d", fileName, base.count()));
		}
		else
		{
			base = new MemoryBase<T>();
			Log.w(TAG, String.format("Failed to load memory base \"%s\": file not found", fileName));
		}
	}

	public void save()
	{
		try
		{
			fileWorker.writeToFile(fileName, serializer.write(base));
			Log.v(TAG, String.format("Memory base \"%s\" saved", fileName));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
