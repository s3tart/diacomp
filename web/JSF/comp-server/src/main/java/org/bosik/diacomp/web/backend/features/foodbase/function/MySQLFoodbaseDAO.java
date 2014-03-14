package org.bosik.diacomp.web.backend.features.foodbase.function;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import org.bosik.diacomp.core.entities.business.foodbase.FoodItem;
import org.bosik.diacomp.core.entities.tech.Versioned;
import org.bosik.diacomp.core.persistence.serializers.Parser;
import org.bosik.diacomp.core.persistence.serializers.ParserFoodItem;
import org.bosik.diacomp.core.persistence.serializers.Serializer;
import org.bosik.diacomp.core.persistence.serializers.utils.SerializerAdapter;
import org.bosik.diacomp.core.utils.Utils;
import org.bosik.diacomp.web.backend.common.mysql.MySQLAccess;

public class MySQLFoodbaseDAO implements FoodbaseDAO
{
	private static final MySQLAccess			db			= new MySQLAccess();
	private static final Parser<FoodItem>		parser		= new ParserFoodItem();
	private static final Serializer<FoodItem>	serializer	= new SerializerAdapter<FoodItem>(parser);

	private static List<Versioned<FoodItem>> parseFoodItems(ResultSet resultSet) throws SQLException
	{
		List<Versioned<FoodItem>> result = new LinkedList<Versioned<FoodItem>>();

		while (resultSet.next())
		{
			String id = resultSet.getString(MySQLAccess.COLUMN_FOODBASE_GUID);
			Date timeStamp = Utils.parseTimeUTC(resultSet.getString(MySQLAccess.COLUMN_FOODBASE_TIMESTAMP));
			int version = resultSet.getInt(MySQLAccess.COLUMN_FOODBASE_VERSION);
			boolean deleted = (resultSet.getInt(MySQLAccess.COLUMN_FOODBASE_DELETED) == 1);
			String content = resultSet.getString(MySQLAccess.COLUMN_FOODBASE_CONTENT);

			Versioned<FoodItem> item = new Versioned<FoodItem>();
			item.setId(id);
			item.setTimeStamp(timeStamp);
			item.setVersion(version);
			item.setDeleted(deleted);
			item.setData(serializer.read(content));
			//item.setData(content);

			result.add(item);
		}

		return result;
	}

	@Override
	public List<Versioned<FoodItem>> findAll(int userId, boolean showRemoved)
	{
		try
		{
			String clause = String.format("(%s = %d)", MySQLAccess.COLUMN_FOODBASE_USER, userId);
			if (!showRemoved)
			{
				clause += String.format(" AND (%s = '%s')", MySQLAccess.COLUMN_FOODBASE_DELETED,
						Utils.formatBooleanInt(false));
			}

			String order = MySQLAccess.COLUMN_FOODBASE_NAMECACHE;

			ResultSet set = db.select(MySQLAccess.TABLE_FOODBASE, clause, order);
			List<Versioned<FoodItem>> result = parseFoodItems(set);
			set.close();
			return result;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Versioned<FoodItem>> findModified(int userId, Date time)
	{
		try
		{
			String clause = String.format("(%s = %d) AND (%s >= '%s')", MySQLAccess.COLUMN_FOODBASE_USER, userId,
					MySQLAccess.COLUMN_FOODBASE_TIMESTAMP, Utils.formatTimeUTC(time));
			String order = MySQLAccess.COLUMN_FOODBASE_NAMECACHE;

			ResultSet set = db.select(MySQLAccess.TABLE_FOODBASE, clause, order);
			List<Versioned<FoodItem>> result = parseFoodItems(set);
			set.close();
			return result;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Versioned<FoodItem> findByGuid(int userId, String guid)
	{
		try
		{
			String clause = String.format("(%s = %d) AND (%s = '%s')", MySQLAccess.COLUMN_FOODBASE_USER, userId,
					MySQLAccess.COLUMN_FOODBASE_GUID, guid);

			ResultSet set = db.select(MySQLAccess.TABLE_FOODBASE, clause, null);
			List<Versioned<FoodItem>> result = parseFoodItems(set);
			set.close();
			return result.isEmpty() ? null : result.get(0);
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void post(int userId, List<Versioned<FoodItem>> items)
	{
		try
		{
			for (Versioned<FoodItem> item : items)
			{
				final String content = serializer.write(item.getData());
				final String nameCache = item.getData().getName();
				final String timeStamp = Utils.formatTimeUTC(item.getTimeStamp());
				final String version = String.valueOf(item.getVersion());
				final String deleted = Utils.formatBooleanInt(item.isDeleted());

				if (findByGuid(userId, item.getId()) != null)
				{
					// presented, update

					SortedMap<String, String> set = new TreeMap<String, String>();
					set.put(MySQLAccess.COLUMN_FOODBASE_TIMESTAMP, timeStamp);
					set.put(MySQLAccess.COLUMN_FOODBASE_VERSION, version);
					set.put(MySQLAccess.COLUMN_FOODBASE_DELETED, deleted);
					set.put(MySQLAccess.COLUMN_FOODBASE_CONTENT, content);
					set.put(MySQLAccess.COLUMN_FOODBASE_NAMECACHE, nameCache);

					SortedMap<String, String> where = new TreeMap<String, String>();
					where.put(MySQLAccess.COLUMN_FOODBASE_GUID, item.getId());
					where.put(MySQLAccess.COLUMN_FOODBASE_USER, String.valueOf(userId));

					db.update(MySQLAccess.TABLE_FOODBASE, set, where);
				}
				else
				{
					// not presented, insert

					LinkedHashMap<String, String> set = new LinkedHashMap<String, String>();
					set.put(MySQLAccess.COLUMN_FOODBASE_GUID, item.getId());
					set.put(MySQLAccess.COLUMN_FOODBASE_USER, String.valueOf(userId));
					set.put(MySQLAccess.COLUMN_FOODBASE_TIMESTAMP, timeStamp);
					set.put(MySQLAccess.COLUMN_FOODBASE_VERSION, version);
					set.put(MySQLAccess.COLUMN_FOODBASE_DELETED, deleted);
					set.put(MySQLAccess.COLUMN_FOODBASE_CONTENT, content);
					set.put(MySQLAccess.COLUMN_FOODBASE_NAMECACHE, nameCache);

					db.insert(MySQLAccess.TABLE_FOODBASE, set);
				}
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
}