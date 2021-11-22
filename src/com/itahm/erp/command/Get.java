package com.itahm.erp.command;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.itahm.http.Response;
import com.itahm.json.JSONException;
import com.itahm.json.JSONObject;
import com.itahm.erp.Commander;

public class Get implements Executor {
	private final Map<String, Helper> map = new HashMap<>();

	public Get(Commander agent) {
		map.put("CAR", new Helper() {

			@Override
			public byte [] help(Response response, JSONObject request, JSONObject session) throws SQLException {
				return toByteArray(
					request.has("id")?
					agent.getCar(request.getLong("id")):
					agent.getCar()
				);
			}
			
		});
		
		map.put("COMPANY", new Helper() {

			@Override
			public byte [] help(Response response, JSONObject request, JSONObject session) throws SQLException {
				return toByteArray(
					request.has("id")?
					agent.getCompany(request.getString("id")):
					agent.getCompany()
				);
			}
			
		});
		
		map.put("FILE", new Helper() {

			@Override
			public byte [] help(Response response, JSONObject request, JSONObject session) throws SQLException {
				if (request.has("id")) {
					return agent.download(request.getLong("id"));
					
				} else if (request.has("tid") && request.has("type")) {
					switch(request.getString("type").toUpperCase()) {
					case "PROJECT":
						return toByteArray(agent.getFile(request.getLong("tid"), request.getString("type")));
					case "COMPANY":
						return toByteArray(agent.getFile(request.getString("tid"), request.getString("type")));
					}
				} else {
					return toByteArray(agent.getFile());
				}
				
				return null;
			}
		});

		map.put("INVOICE", new Helper() {

			@Override
			public byte [] help(Response response, JSONObject request, JSONObject session) throws SQLException {
				return toByteArray(
					request.has("project")?
						agent.getInvoice(request.getLong("project")):
					request.has("type") && request.has("status")?
						agent.getInvoice(request.getInt("type"), request.getInt("status"), request.getInt("date")):
					agent.getInvoice()
				);
			}
			
		});

		
		map.put("ITEM", new Helper() {

			@Override
			public byte [] help(Response response, JSONObject request, JSONObject session) throws SQLException {
				return toByteArray(
					request.has("id")?
						agent.getItem(request.getLong("id")):
					agent.getItem()
				);
			}
			
		});
		
		map.put("MANAGER", new Helper() {

			@Override
			public byte [] help(Response response, JSONObject request, JSONObject session) throws SQLException {
				return toByteArray(
					request.has("id")?
						agent.getManager(request.getLong("id")):
					request.has("company")?
						agent.getManager(request.getString("company")):
					agent.getManager()
				);
			}
			
		});
		
		map.put("OPERATION", new Helper() {

			@Override
			public byte [] help(Response response, JSONObject request, JSONObject session) throws SQLException {
				return toByteArray(
					request.has("id")?
						agent.getOperation(request.getLong("id")):
					agent.getOperation()
				);
			}
			
		});
		
		map.put("PROJECT", new Helper() {

			@Override
			public byte [] help(Response response, JSONObject request, JSONObject session) throws SQLException {
				return toByteArray(
					request.has("id")?
						agent.getProject(request.getLong("id")):
					agent.getProject()
				);
			}
			
		});

		map.put("REPAIR", new Helper() {

			@Override
			public byte [] help(Response response, JSONObject request, JSONObject session) throws SQLException {
				return toByteArray(
					request.has("id")?
						agent.getRepair(request.getLong("id")):
					agent.getRepair()
				);
			}
			
		});
		
		map.put("USER", new Helper() {

			@Override
			public byte [] help(Response response, JSONObject request, JSONObject session) throws SQLException {
				return toByteArray(
					!request.has("id")?
						agent.getUser():
					(session.getInt("level") == 0 && session.getLong("id") == request.getLong("id"))?
						agent.getUser(request.getLong("id")):
					null
				);
			}
			
		});
	}
	
	@Override
	public void execute(Response response, JSONObject request, JSONObject session)
		throws SQLException {
		Executor executor = this.map.get(request.getString("target").toUpperCase());
		
		if (executor == null) {
			throw new JSONException("Target is not found.");
		} else {
			executor.execute(response, request, session);
		}
	}
	
	abstract class Helper implements Executor {
		abstract public byte [] help(Response response, JSONObject request, JSONObject session)
			throws SQLException;
		
		public byte [] toByteArray (JSONObject result) {
			try {
				return result == null? null: result.toString().getBytes(StandardCharsets.UTF_8.name());
			} catch (UnsupportedEncodingException e) {
				return new byte [0];
			}
		}
		
		@Override
		public void execute(Response response, JSONObject request, JSONObject session)
			throws SQLException {
			
			byte [] result = help(response, request, session);
			
			if (result == null) {
				response.setStatus(Response.Status.NOCONTENT);
			} else {
				response.write(result);
			}
		}
	}
}
