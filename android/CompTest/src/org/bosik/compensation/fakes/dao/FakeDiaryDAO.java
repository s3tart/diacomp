package org.bosik.compensation.fakes.dao;

import java.util.Date;
import java.util.List;
import org.bosik.compensation.bo.diary.DiaryPage;
import org.bosik.compensation.persistence.dao.DiaryDAO;

public class FakeDiaryDAO implements DiaryDAO
{

	public List<PageVersion> getModList(Date time)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<DiaryPage> getPages(List<Date> dates)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean postPages(List<DiaryPage> pages)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public DiaryPage getPage(Date date)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean postPage(DiaryPage page)
	{
		// TODO Auto-generated method stub
		return false;
	}

}