package org.bosik.diacomp.features.diary;

import java.util.Date;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.bosik.diacomp.bo.diary.DiaryRecord;
import org.bosik.diacomp.features.common.WebService;
import org.bosik.diacomp.persistence.common.Versioned;
import org.bosik.diacomp.persistence.serializers.Serializer;
import org.bosik.diacomp.persistence.serializers.ready.SerializerDiaryRecord;
import org.bosik.diacomp.services.DiaryService;
import org.bosik.diacomp.services.exceptions.CommonServiceException;
import org.bosik.diacomp.utils.StdResponse;
import org.bosik.diacomp.utils.Utils;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class DiaryWebService extends WebService implements DiaryService
{
	private static Serializer<Versioned<DiaryRecord>>	serializer	= new SerializerDiaryRecord();

	@Override
	public List<Versioned<DiaryRecord>> getRecords(List<String> guids) throws CommonServiceException
	{
		try
		{
			WebResource resource = getResource("api/diary/guid");
			resource = resource.queryParam("guids", Utils.formatJSONArray(guids));
			String str = resource.accept(MediaType.APPLICATION_JSON).get(String.class);

			StdResponse resp = new StdResponse(str);
			checkResponse(resp);

			return serializer.readAll(resp.getResponse());
		}
		catch (UniformInterfaceException e)
		{
			throw new CommonServiceException(e);
		}
	}

	@Override
	public List<Versioned<DiaryRecord>> getRecords(Date time, boolean includeRemoved) throws CommonServiceException
	{
		try
		{
			WebResource resource = getResource("api/diary/new");
			resource = resource.queryParam("mod_after", Utils.formatTimeUTC(time));
			String str = resource.accept(MediaType.APPLICATION_JSON).get(String.class);

			StdResponse resp = new StdResponse(str);
			checkResponse(resp);

			return serializer.readAll(resp.getResponse());
		}
		catch (UniformInterfaceException e)
		{
			throw new CommonServiceException(e);
		}
	}

	@Override
	public List<Versioned<DiaryRecord>> getRecords(Date fromTime, Date toTime, boolean includeRemoved)
			throws CommonServiceException
	{
		try
		{
			WebResource resource = getResource("api/diary/period");
			resource = resource.queryParam("start_time", Utils.formatTimeUTC(fromTime));
			resource = resource.queryParam("end_time", Utils.formatTimeUTC(toTime));
			resource = resource.queryParam("show_rem", Utils.formatBooleanStr(includeRemoved));
			String str = resource.accept(MediaType.APPLICATION_JSON).get(String.class);

			StdResponse resp = new StdResponse(str);
			checkResponse(resp);

			return serializer.readAll(resp.getResponse());
		}
		catch (UniformInterfaceException e)
		{
			throw new CommonServiceException(e);
		}
	}

	@Override
	public void postRecords(List<Versioned<DiaryRecord>> records) throws CommonServiceException
	{
		WebResource resource = getResource("api/diary/");
		try
		{
			String items = serializer.writeAll(records);
			String str = resource.accept(MediaType.APPLICATION_JSON).put(String.class, items);

			StdResponse resp = new StdResponse(str);
			checkResponse(resp);
		}
		catch (UniformInterfaceException e)
		{
			System.err.println(e.getResponse().getEntity(String.class));
			throw new CommonServiceException("URL: " + resource.getURI(), e);
		}
	}
}
