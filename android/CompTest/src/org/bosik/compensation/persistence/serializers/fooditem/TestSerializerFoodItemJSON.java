package org.bosik.compensation.persistence.serializers.fooditem;

import org.bosik.compensation.bo.foodbase.FoodItem;
import org.bosik.compensation.persistence.serializers.SerializerJSONAdapter;
import org.bosik.compensation.persistence.serializers.Serializer;
import org.bosik.compensation.persistence.serializers.foodbase.JSONParserFoodItem;

public class TestSerializerFoodItemJSON extends FoodItemSerializerTest
{
	@Override
	protected Serializer<FoodItem> getSerializer()
	{
		return new SerializerJSONAdapter<FoodItem>(new JSONParserFoodItem());
	}
}
