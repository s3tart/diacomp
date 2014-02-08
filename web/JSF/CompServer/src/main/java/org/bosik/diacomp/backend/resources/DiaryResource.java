package org.bosik.diacomp.backend.resources;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.bosik.diacomp.backend.services.AuthWebServiceImpl;
import org.bosik.diacomp.backend.services.DiaryWebServiceImpl;
import org.bosik.diacomp.persistence.common.Versioned;
import org.bosik.diacomp.persistence.serializers.Parser;
import org.bosik.diacomp.persistence.serializers.Serializer;
import org.bosik.diacomp.persistence.serializers.utils.ParserVersioned;
import org.bosik.diacomp.persistence.serializers.utils.SerializerAdapter;
import org.bosik.diacomp.services.exceptions.CommonServiceException;
import org.bosik.diacomp.utils.ResponseBuilder;
import org.json.JSONException;
import org.json.JSONObject;

@Path("diary/")
public class DiaryResource
{
	@Context
	HttpServletRequest									req;

	private DiaryWebServiceImpl							diaryService				= new DiaryWebServiceImpl();

	private AuthWebServiceImpl							authService					= new AuthWebServiceImpl();

	private static final Parser<String>					parserString				= new Parser<String>()
																					{
																						// As-is
																						// "parser"

																						@Override
																						public String read(
																								JSONObject json)
																								throws JSONException
																						{
																							if (json == null)
																							{
																								return null;
																							}
																							else
																							{
																								return json.toString();
																							}
																						}

																						@Override
																						public JSONObject write(
																								String object)
																								throws JSONException
																						{
																							if (object == null)
																							{
																								return null;
																							}
																							else
																							{
																								return new JSONObject(
																										object);
																							}
																						}
																					};
	private static final Parser<Versioned<String>>		parserVersionedString		= new ParserVersioned<String>(
																							parserString);
	private static final Serializer<Versioned<String>>	serializerVersionedString	= new SerializerAdapter<Versioned<String>>(
																							parserVersionedString);

	// @PUT
	// @Path("update")
	// @Produces(MediaType.APPLICATION_JSON)
	// public String put(@DefaultValue("") @QueryParam("value") String value)
	// {
	// HttpSession session = req.getSession(true);
	// session.setAttribute("foo", value);
	//
	// return "Value " + value + " putted";
	// }

	@GET
	@Path("new")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecords(@QueryParam("value") Date time) throws CommonServiceException
	{
		try
		{
			authService.checkAuth(req);

			int userId = authService.getCurrentUserId(req);
			List<Versioned<String>> list = diaryService.findMod(userId, time);
			String s = serializerVersionedString.writeAll(list);
			return Response.ok(list).build();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ResponseBuilder.buildFails()).build();
		}
	}
}