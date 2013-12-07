package org.bosik.compensation.persistence.dao.fakes;

import org.bosik.compensation.fakes.dao.FakeDiaryDAO;
import org.bosik.compensation.persistence.dao.DiaryDAO;
import org.bosik.compensation.persistence.dao.DiaryDAOTest;

public class FakeDiaryDAOTest extends DiaryDAOTest
{
	private static DiaryDAO	dao	= new FakeDiaryDAO();

	@Override
	protected DiaryDAO getDAO()
	{
		return dao;
	}
}
