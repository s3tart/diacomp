package org.bosik.compensation.persistence.repository.foodbase;

import org.bosik.compensation.persistence.entity.foodbase.Food;
import org.bosik.compensation.persistence.repository.common.LocalBase;
import android.content.Context;

public class LocalFoodBase extends LocalBase<Food>
{
	public LocalFoodBase(Context context, String fileName)
	{
		// один параметр скрываем, подставляя определённый сериализатор
		super(context, fileName, new FoodBaseXMLSerializer());
	}
}