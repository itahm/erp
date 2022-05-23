package com.itahm.erp.command;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.itahm.http.Response;
import com.itahm.json.JSONException;
import com.itahm.json.JSONObject;
import com.itahm.erp.Commander;

public class Remove implements Executor {
	private final Map<String, Executor> map = new HashMap<>();
	
	public Remove(Commander agent) {
		map.put("ASSIGN", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.removeAssign(request.getLong("user"), request.getInt("year"));
			}
			
		});
		
		map.put("CAR", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.removeCar(request.getLong("id"));
			}
			
		});
		
		map.put("COMPANY", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.removeCompany(request.getString("id"));
			}
			
		});
		
		map.put("FILE", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				try {
					agent.removeFile(request.getLong("id"));
				} catch (IOException e) {
					response.setStatus(Response.Status.SERVERERROR);
				}
			}
			
		});
		
		map.put("INVOICE", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.removeInvoice(request.getLong("id"));
			}
			
		});

		map.put("ITEM", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.removeItem(request.getLong("id"));
			}
			
		});

		map.put("LEAVE", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.removeLeave(request.getLong("id"));
			}
			
		});
		
		map.put("MANAGER", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.removeManager(request.getLong("id"));
			}
			
		});

		map.put("OPERATION", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.removeOperation(request.getLong("id"));
			}
			
		});
		
		map.put("PROJECT", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.removeProject(request.getLong("id"));
			}
			
		});

		map.put("REPAIR", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.removeRepair(request.getLong("id"));
			}
			
		});
		
		map.put("USER", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				long id = request.getLong("id");
				
				if (session.getInt("level") > 0 || id == session.getLong("id")) {
					response.setStatus(Response.Status.FORBIDDEN);
				} else {
					agent.removeUser(request.getLong("id"));
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
