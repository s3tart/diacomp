package org.bosik.compensation.persistence.entity.diary.records;

import java.util.ArrayList;
import java.util.List;
import org.bosik.compensation.persistence.entity.common.FoodMassed;

public class MealRecord extends DiaryRecord
{
	private final List<FoodMassed> items = new ArrayList<FoodMassed>();
	private boolean shortMeal;

	public MealRecord(int time, boolean shortMeal)
	{
		setTime(time);
		setShortMeal(shortMeal);
	}

	public MealRecord()
	{
	}

	// ================================ GET / SET ================================

	public boolean getShortMeal()
	{
		return shortMeal;
	}

	public void setShortMeal(boolean value)
	{
		if (value != shortMeal)
		{
			this.shortMeal = value;
			notifyModified();
		}
	}

	// работа с характеристиками

	public double getProts()
	{
		double res = 0;
		for (int i = 0; i < items.size(); i++)
			res += items.get(i).getProts();
		return res;
	}

	public double getFats()
	{
		double res = 0;
		for (int i = 0; i < items.size(); i++)
			res += items.get(i).getFats();
		return res;
	}

	public double getCarbs()
	{
		double res = 0;
		for (int i = 0; i < items.size(); i++)
			res += items.get(i).getCarbs();
		return res;
	}

	public double getValue()
	{
		double res = 0;
		for (int i = 0; i < items.size(); i++)
			res += items.get(i).getValue();
		return res;
	}

	public double getMass()
	{
		double res = 0;
		for (int i = 0; i < items.size(); i++)
			res += items.get(i).getMass();
		return res;
	}

	// ============================== РАБОТА СО СПИСКОМ ==============================

	// работа со списком

	public int add(FoodMassed item)
	{
		if (item == null)
			throw new NullPointerException("Food item can't be null");
		items.add(item);
		return items.size() - 1;
	}

	public void clear()
	{
		items.clear();
	}

	public FoodMassed get(int index)
	{
		return items.get(index);
	}

	public int size()
	{
		return items.size();
	}

}