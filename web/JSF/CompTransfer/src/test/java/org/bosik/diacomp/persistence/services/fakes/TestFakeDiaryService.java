package org.bosik.diacomp.persistence.services.fakes;

import org.bosik.diacomp.fakes.services.FakeDiaryService;
import org.bosik.diacomp.persistence.services.TestDiaryService;
import org.bosik.diacomp.services.DiaryService;
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