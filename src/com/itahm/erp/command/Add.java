package com.itahm.erp.command;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.itahm.http.Response;
import com.itahm.json.JSONException;
import com.itahm.json.JSONObject;
import com.itahm.erp.Commander;

public class Add implements Executor {
	private final Map<String, Executor> map = new HashMap<>();
	
	public Add(Commander agent) {
		map.put("CAR", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.addCar(request.getJSONObject("car"));
			}
			
		});
		
		map.put("COMPANY", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.addCompany(request.getJSONObject("company"));
			}
			
		});
		
		map.put("INVOICE", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				if (!Command.isValidInvoice(request.getJSONObject("invoice"))) {
					throw new JSONException("Invalid invoice form.");
				}
				else {
					response.write(agent.addInvoice(request.getJSONObject("invoice")).toString());
				}
			}
			
		});

		map.put("ITEM", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.addItem(request.getJSONObject("item"));
			}
			
		});

		map.put("MANAGER", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.addManager(request.getJSONObject("manager"));
			}
			
		});

		map.put("OPERATION", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				agent.addOperation(
					request
						.getJSONObject("operation")
						.put("user", session.getLong("id"))
				);
			}
			
		});
		
		map.put("PROJECT", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				response.write(
					agent
						.addProject(request.getJSONObject("project")
						.put("user", session.getLong("id")))
						.toString()
				);
			}
			
		});
		
		map.put("REPAIR", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				response.write(
					agent
						.addRepair(request.getJSONObject("repair")
						.put("user", session.getLong("id")))
						.toString()
				);
			}
			
		});
		
		map.put("USER", new Executor() {

			@Override
			public void execute(Response response, JSONObject request, JSONObject session)
				throws SQLException {
				response.write(
					agent
						.addUser(request.getJSONObject("user"))
						.toString()
				);
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
