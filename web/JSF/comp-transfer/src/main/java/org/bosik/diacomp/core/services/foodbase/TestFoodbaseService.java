package org.bosik.diacomp.core.services.foodbase;

public interface TestFoodbaseService
{
	void test_addFindById_single_PersistedOk();

	void test_delete_notFound_exceptionRaised();
}
