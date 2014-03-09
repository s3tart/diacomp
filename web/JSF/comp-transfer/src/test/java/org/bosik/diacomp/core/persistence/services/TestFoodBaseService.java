package org.bosik.diacomp.core.persistence.services;

import junit.framework.TestCase;
import org.bosik.diacomp.core.entities.business.foodbase.FoodItem;
import org.bosik.diacomp.core.entities.tech.Versioned;
import org.bosik.diacomp.core.services.FoodBaseService;
import org.bosik.diacomp.core.utils.test.fakes.mocks.Mock;
import org.bosik.diacomp.core.utils.test.fakes.mocks.MockFoodItem;
import org.bosik.diacomp.core.utils.test.fakes.mocks.MockVersionedConverter;

public abstract class TestFoodBaseService extends TestCase
{
	private FoodBaseService							foodBaseService;
	private static Mock<Versioned<FoodItem>>	mockGenerator	= new MockVersionedConverter<FoodItem>(
																		new MockFoodItem());

	protected abstract FoodBaseService getService();

	@Override
	protected void setUp()
	{
		foodBaseService = getService();
	}

	public void testPersistanceSingle()
	{
		Versioned<FoodItem> org = mockGenerator.getSamples().get(0);
		// int version;

		if (foodBaseService.findById(org.getId()) != null)
		{
			// version = foodBaseService.getVersion();
			foodBaseService.delete(org.getId());
			// assertEquals(version + 1, foodBaseService.getVersion());
			assertNull(foodBaseService.findById(org.getId()));
		}

		// version = foodBaseService.getVersion();
		foodBaseService.add(org);
		// assertEquals(version + 1, foodBaseService.getVersion());

		// ------------------------------
		setUp();
		// ------------------------------

		Versioned<FoodItem> restored = foodBaseService.findById(org.getId());
		assertNotNull(restored);
		assertNotSame(org, restored);
		mockGenerator.compare(org, restored);

		// version = foodBaseService.getVersion();
		foodBaseService.delete(org.getId());
		// assertEquals(version + 1, foodBaseService.getVersion());
		assertNull(foodBaseService.findById(org.getId()));
	}

	// TODO: create testPersistenceMultiple()
}