package org.bosik.diacomp.android.persistence.services.local;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.bosik.diacomp.android.persistence.services.local.utils.DiaryContentProvider;
import org.bosik.diacomp.core.bo.foodbase.FoodItem;
import org.bosik.diacomp.core.persistence.common.Versioned;
import org.bosik.diacomp.core.persistence.serializers.Parser;
import org.bosik.diacomp.core.persistence.serializers.ParserFoodItem;
import org.bosik.diacomp.core.persistence.serializers.Serializer;
import org.bosik.diacomp.core.persistence.serializers.utils.SerializerAdapter;
import org.bosik.diacomp.core.services.FoodBaseService;
import org.bosik.diacomp.core.services.exceptions.AlreadyDeletedException;
import org.bosik.diacomp.core.services.exceptions.CommonServiceException;
import org.bosik.diacomp.core.services.exceptions.ItemNotFoundException;
import org.bosik.diacomp.core.services.exceptions.PersistenceException;
import org.bosik.diacomp.core.utils.Utils;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

public class NewLocalFoodBaseService implements FoodBaseService
{
	private ContentResolver			resolver;
	private Serializer<FoodItem>	serializer;

	// ====================================================================================

	public NewLocalFoodBaseService(ContentResolver resolver)
	{
		if (null == resolver)
		{
			throw new NullPointerException("Content resolver can't be null");
		}
		this.resolver = resolver;

		Parser<FoodItem> s = new ParserFoodItem();
		serializer = new SerializerAdapter<FoodItem>(s);
	}

	private List<Versioned<FoodItem>> find(String id, boolean includeDeleted)
	{
		try
		{
			// constructing parameters
			String[] mProj = { DiaryContentProvider.COLUMN_FOODBASE_GUID,
					DiaryContentProvider.COLUMN_FOODBASE_TIMESTAMP, DiaryContentProvider.COLUMN_FOODBASE_VERSION,
					DiaryContentProvider.COLUMN_FOODBASE_DELETED, DiaryContentProvider.COLUMN_FOODBASE_DATA };

			String mSelectionClause = "";
			List<String> args = new LinkedList<String>();

			if (id != null)
			{
				mSelectionClause += mSelectionClause.isEmpty() ? "" : " AND ";
				mSelectionClause += DiaryContentProvider.COLUMN_FOODBASE_GUID + " = ?";
				args.add(id);
			}

			if (!includeDeleted)
			{
				mSelectionClause += mSelectionClause.isEmpty() ? "" : " AND ";
				mSelectionClause += DiaryContentProvider.COLUMN_FOODBASE_DELETED + " = 0";
			}

			String[] mSelectionArgs = args.toArray(new String[] {});
			String mSortOrder = null;

			// execute query
			Cursor cursor = resolver.query(DiaryContentProvider.CONTENT_FOODBASE_URI, mProj, mSelectionClause,
					mSelectionArgs, mSortOrder);

			List<Versioned<FoodItem>> result = new LinkedList<Versioned<FoodItem>>();

			// analyze response
			if (cursor != null)
			{
				int indexId = cursor.getColumnIndex(DiaryContentProvider.COLUMN_FOODBASE_GUID);
				int indexTimeStamp = cursor.getColumnIndex(DiaryContentProvider.COLUMN_FOODBASE_TIMESTAMP);
				int indexVersion = cursor.getColumnIndex(DiaryContentProvider.COLUMN_FOODBASE_VERSION);
				int indexData = cursor.getColumnIndex(DiaryContentProvider.COLUMN_FOODBASE_DATA);
				int indexDeleted = cursor.getColumnIndex(DiaryContentProvider.COLUMN_FOODBASE_DELETED);

				while (cursor.moveToNext())
				{
					String valueId = cursor.getString(indexId);
					Date valueTimeStamp = Utils.parseTimeUTC(cursor.getString(indexTimeStamp));
					int valueVersion = cursor.getInt(indexVersion);
					boolean valueDeleted = cursor.getInt(indexDeleted) == 1;
					String valueData = cursor.getString(indexData);

					FoodItem item = serializer.read(valueData);
					Versioned<FoodItem> versioned = new Versioned<FoodItem>(item);
					versioned.setId(valueId);
					versioned.setTimeStamp(valueTimeStamp);
					versioned.setVersion(valueVersion);
					versioned.setDeleted(valueDeleted);

					result.add(versioned);
				}
			}
			else
			{
				throw new NullPointerException("Cursor is null");
			}

			return result;
		}
		catch (Exception e)
		{
			throw new CommonServiceException(e);
		}
	}

	@Override
	public String add(Versioned<FoodItem> item) throws PersistenceException
	{
		try
		{
			ContentValues newValues = new ContentValues();
			newValues.put(DiaryContentProvider.COLUMN_FOODBASE_GUID, item.getId());
			newValues.put(DiaryContentProvider.COLUMN_FOODBASE_TIMESTAMP, Utils.formatTimeUTC(item.getTimeStamp()));
			newValues.put(DiaryContentProvider.COLUMN_FOODBASE_VERSION, item.getVersion());
			newValues.put(DiaryContentProvider.COLUMN_FOODBASE_DELETED, false);
			newValues.put(DiaryContentProvider.COLUMN_FOODBASE_DATA, serializer.write(item.getData()));

			resolver.insert(DiaryContentProvider.CONTENT_FOODBASE_URI, newValues);

			return item.getId();
		}
		catch (Exception e)
		{
			throw new PersistenceException(e);
		}
	}

	@Override
	public void delete(String id) throws ItemNotFoundException, AlreadyDeletedException
	{
		try
		{
			Versioned<FoodItem> founded = findById(id);

			if (founded == null)
			{
				throw new ItemNotFoundException(id);
			}

			if (founded.isDeleted())
			{
				throw new AlreadyDeletedException(id);
			}

			ContentValues newValues = new ContentValues();
			newValues.put(DiaryContentProvider.COLUMN_FOODBASE_DELETED, 1);
			String[] args = new String[] { id };
			resolver.update(DiaryContentProvider.CONTENT_FOODBASE_URI, newValues, "GUID = ?", args);
		}
		catch (Exception e)
		{
			throw new PersistenceException(e);
		}
	}

	@Override
	public List<Versioned<FoodItem>> findAll()
	{
		return find(null, false);
	}

	@Override
	public List<Versioned<FoodItem>> findAny(String filter)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Versioned<FoodItem> findOne(String exactName)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Versioned<FoodItem> findById(String id)
	{
		List<Versioned<FoodItem>> list = find(id, false);
		if (list.isEmpty())
		{
			return null;
		}
		else
		{
			return list.get(0);
		}
	}

	@Override
	public List<Versioned<FoodItem>> findSysAll()
	{
		return find(null, true);
	}

	@Override
	public Versioned<FoodItem> findSysById(String id)
	{
		List<Versioned<FoodItem>> list = find(id, true);
		if (list.isEmpty())
		{
			return null;
		}
		else
		{
			return list.get(0);
		}
	}

	@Override
	public void update(Versioned<FoodItem> item) throws ItemNotFoundException, PersistenceException
	{
		try
		{
			Versioned<FoodItem> founded = findById(item.getId());

			if ((founded == null) || founded.isDeleted())
			{
				throw new ItemNotFoundException(item.getId());
			}

			ContentValues newValues = new ContentValues();
			newValues.put(DiaryContentProvider.COLUMN_FOODBASE_TIMESTAMP, Utils.formatTimeUTC(item.getTimeStamp()));
			newValues.put(DiaryContentProvider.COLUMN_FOODBASE_VERSION, item.getVersion());
			newValues.put(DiaryContentProvider.COLUMN_FOODBASE_DATA, serializer.write(item.getData()));

			String[] args = new String[] { item.getId() };
			resolver.update(DiaryContentProvider.CONTENT_FOODBASE_URI, newValues, "GUID = ?", args);
		}
		catch (Exception e)
		{
			throw new PersistenceException(e);
		}
	}
}