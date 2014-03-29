package org.bosik.diacomp.core.entities.business.dishbase;

import junit.framework.TestCase;
import org.bosik.diacomp.core.entities.business.FoodMassed;
import org.bosik.diacomp.core.utils.Utils;

public class TestDishItem extends TestCase
{
	private DishItem	dish;

	@Override
	protected void setUp()
	{
		dish = new DishItem();
	}

	public void test_setName_normal_ok()
	{
		dish.setName("My test name");
		assertEquals("My test name", dish.getName());
	}

	public void test_setMassdouble_notNull_ok()
	{
		dish.setMass(123.5);
		assertEquals(123.5, dish.getMass(), Utils.EPS);
	}

	public void test_setMassDouble_notNull_ok()
	{
		dish.setMass((Double)123.5);
		assertEquals(123.5, dish.getMass(), Utils.EPS);
	}

	public void test_setMass_null_ok()
	{
		dish.setMass(null);
		assertEquals(null, dish.getMass());
	}

	public void test_setTag_normal_ok()
	{
		dish.setTag(42);
		assertEquals(42, dish.getTag());
	}

	public void test_getRels_withoutMass_ok()
	{
		dish.add(new FoodMassed("Item", 10.0, 20.0, 30.0, 40.0, 100));
		assertEquals(10.0 / 100, dish.getRelProts(), Utils.EPS);
		assertEquals(20.0 / 100, dish.getRelFats(), Utils.EPS);
		assertEquals(30.0 / 100, dish.getRelCarbs(), Utils.EPS);
		assertEquals(40.0 / 100, dish.getRelValue(), Utils.EPS);
	}

	public void test_getRels_withMass_ok()
	{
		dish.add(new FoodMassed("Item", 10.0, 20.0, 30.0, 40.0, 100));
		dish.setMass(200);
		assertEquals(5.0 / 100, dish.getRelProts(), Utils.EPS);
		assertEquals(10.0 / 100, dish.getRelFats(), Utils.EPS);
		assertEquals(15.0 / 100, dish.getRelCarbs(), Utils.EPS);
		assertEquals(20.0 / 100, dish.getRelValue(), Utils.EPS);
	}

	public void test_add_null_exceptionThrown()
	{
		try
		{
			dish.add(null);
			fail("No exception was thrown");
		}
		catch (NullPointerException e)
		{
			// just as planned
		}
	}

	public void test_remove_normal_ok()
	{
		dish.add(new FoodMassed("Item", 10.0, 20.0, 30.0, 40.0, 100));
		dish.remove(0);
		assertEquals(0, dish.count());
	}
}
