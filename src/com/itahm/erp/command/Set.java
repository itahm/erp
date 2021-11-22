package com.itahm.erp.command;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.itahm.http.Response;
import com.itahm.json.JSONException;
import com.itahm.json.JSONObject;
import com.itahm.erp.Commander;

public class Set implements Executor {
	private final Map<String, Executor> map = new HashMap<>();
	
	public Set(Commander agent) {
		map.put("CAR", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.setCar(request.getLong("id"), request.getJSONObject("car"));
			}
			
		});
		
		map.put("COMPANY", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.setCompany(request.getString("id"), request.getJSONObject("company"));
			}
			
		});
		
		map.put("INVOICE", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				if (Command.isValidInvoice(request.getJSONObject("invoice"))) {
					agent.setInvoice(request.getLong("id"), request.getJSONObject("invoice"));
				} else {
					throw new JSONException("Invalid invoice form.");
				}
			}
		});

		map.put("ITEM", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.setItem(request.getLong("id"), request.getJSONObject("item"));
			}
			
		});

		map.put("MANAGER", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.setManager(request.getLong("id"), request.getJSONObject("manager"));
			}
			
		});

		map.put("OPERATION", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.setOperation(request.getLong("id"), request.getJSONObject("operation"));
			}
			
		});
		
		map.put("PASSWORD", new Executor() {
			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				if (request.has("id")) {
					if (session.getInt("level") > 0) {
						response.setStatus(Response.Status.FORBIDDEN);	
					} else {
						agent.setPassword(request.getLong("id"), request.getString("password"));
					}
				} else {
					agent.setPassword(session.getLong("id"), request.getString("password"));
				}		
			}
		});
		
		map.put("PROJECT", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.setProject(request.getLong("id"), request.getJSONObject("project"));
			}
			
		});
		
		map.put("REPAIR", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.setRepair(request.getJSONObject("repair"));
			}
			
		});
		
		map.put("USER", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				if (session.getInt("level") > 0) {
					response.setStatus(Response.Status.FORBIDDEN);
				} else {
					agent.setUser(request.getLong("id"), request.getJSONObject("user"));
				}
			}
			
		});
	}
	
	@Override
	public void execute(Response response, JSONObject request, JSONObject session) throws SQLException {
		String target = request.getString("target");
		Executor executor = this.map.get(target.toUpperCase());
		
		if (executor == null) {
			throw new JSONException("Target is not found.");
		} else {
			executor.execute(response, request, session);
		}
	}
}
