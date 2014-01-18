package org.bosik.diacomp.persistence.dao.web;

import java.util.Date;
import java.util.List;
import org.bosik.diacomp.bo.diary.DiaryRecord;
import org.bosik.diacomp.persistence.common.Versioned;
import org.bosik.diacomp.persistence.dao.DiaryDAO;
import org.bosik.diacomp.persistence.dao.web.utils.client.WebClient;
import org.bosik.diacomp.persistence.exceptions.CommonDAOException;
import org.bosik.diacomp.persistence.serializers.Parser;
import org.bosik.diacomp.persistence.serializers.ParserDiaryRecord;
import org.bosik.diacomp.persistence.serializers.Serializer;
import org.bosik.diacomp.persistence.serializers.utils.ParserVersioned;
import org.bosik.diacomp.persistence.serializers.utils.SerializerAdapter;

public class WebDiaryDAO implements DiaryDAO
{
	// private static String TAG = WebDiaryDAO.class.getSimpleName();
	private WebClient							webClient;
	private Parser<DiaryRecord>					parser		= new ParserDiaryRecord();
	private Parser<Versioned<DiaryRecord>>		parserV		= new ParserVersioned<DiaryRecord>(parser);
	private Serializer<Versioned<DiaryRecord>>	serializerV	= new SerializerAdapter<Versioned<DiaryRecord>>(parserV);

	/* ============================ CONSTRUCTOR ============================ */

	public WebDiaryDAO(WebClient webClient)
	{
		if (webClient == null)
		{
			throw new NullPointerException("WebClient can't be null");
		}

		this.webClient = webClient;
	}

	/* ============================ API ============================ */

	@Override
	public List<Versioned<DiaryRecord>> getRecords(List<String> guids) throws CommonDAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Versioned<DiaryRecord>> getRecords(Date time) throws CommonDAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Versioned<DiaryRecord>> getRecords(Date fromDate, Date toDate) throws CommonDAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void postRecords(List<Versioned<DiaryRecord>> records) throws CommonDAOException
	{
		// TODO Auto-generated method stub
	}

	/* ======================= ROUTINES ========================= */

}
