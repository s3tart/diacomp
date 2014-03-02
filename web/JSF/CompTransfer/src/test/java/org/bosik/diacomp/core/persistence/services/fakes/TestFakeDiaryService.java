package org.bosik.diacomp.core.persistence.services.fakes;

import org.bosik.diacomp.core.persistence.services.TestDiaryService;
import org.bosik.diacomp.core.services.DiaryService;
import org.bosik.diacomp.core.testutils.fakes.services.FakeDiaryService;
import org.junit.Ignore;

@Ignore
public class TestFakeDiaryService extends TestDiaryService
{
	private static DiaryService	service	= new FakeDiaryService();

	@Override
	protected DiaryService getService()
	{
		return service;
	}
}
