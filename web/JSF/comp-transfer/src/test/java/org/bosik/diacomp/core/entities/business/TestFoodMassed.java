package org.bosik.diacomp.core.entities.business;

import junit.framework.TestCase;
import org.bosik.diacomp.core.utils.Utils;

public class TestFoodMassed extends TestCase
{
	private final FoodMassed	food	= new FoodMassed();

	public void testMass()
	{
		// normal test
		food.setMass(0.0);
		assertEquals(0.0, food.getMass(), Utils.EPS);
		food.setMass(0.01);
		assertEquals(0.01, food.getMass(), Utils.EPS);
		food.setMass(100.0);
		assertEquals(100.0, food.getMass(), Utils.EPS);
		food.setMass(999.5);
		assertEquals(999.5, food.getMass(), Utils.EPS);

		// crash-test
		try
		{
			food.setMass(-0.01);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			food.setMass(-100);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	public void testGetAbs()
	{
		// normal test
		food.setMass(10);

		food.setRelProts(1);
		assertEquals(0.1, food.getProts(), Utils.EPS);
		food.setRelFats(2);
		assertEquals(0.2, food.getFats(), Utils.EPS);
		food.setRelCarbs(3);
		assertEquals(0.3, food.getCarbs(), Utils.EPS);
		food.setRelValue(4);
		assertEquals(0.4, food.getValue(), Utils.EPS);

		food.setMass(1000);

		food.setRelProts(2);
		assertEquals(20.0, food.getProts(), Utils.EPS);
		food.setRelFats(4);
		assertEquals(40.0, food.getFats(), Utils.EPS);
		food.setRelCarbs(6);
		assertEquals(60.0, food.getCarbs(), Utils.EPS);
		food.setRelValue(8);
		assertEquals(80.0, food.getValue(), Utils.EPS);

		food.setMass(0);

		assertEquals(0.0, food.getProts(), Utils.EPS);
		assertEquals(0.0, food.getFats(), Utils.EPS);
		assertEquals(0.0, food.getCarbs(), Utils.EPS);
		assertEquals(0.0, food.getValue(), Utils.EPS);

		// crash-test
		try
		{
			food.setRelProts(-0.01);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			food.setRelProts(100.01);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			food.setRelFats(-0.01);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			food.setRelFats(100.01);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			food.setRelCarbs(-0.01);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			food.setRelCarbs(100.01);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			food.setRelValue(-0.01);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
	}
}
