package org.bosik.diacomp.core.persistence.services.fakes;

import org.bosik.diacomp.core.persistence.services.TestFoodBaseService;
import org.bosik.diacomp.core.services.FoodBaseService;
import org.bosik.diacomp.core.testutils.fakes.services.FakeFoodBaseService;
import org.junit.Ignore;

@Ignore
public class TestFakeFoodBaseService extends TestFoodBaseService
{
	@Override
	protected FoodBaseService getService()
	{
		return new FakeFoodBaseService();
	}
}