package com.itahm.erp.command;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.itahm.http.Request;
import com.itahm.http.Response;
import com.itahm.http.Session;
import com.itahm.json.JSONException;
import com.itahm.json.JSONObject;
import com.itahm.erp.Commander;

public class Command {
	private final Map<String, Executor> map = new HashMap<>();
	
	
	public Command(Commander commander) {
		
		map.put("ADD", new Executor() {
			private final Executor add = new Add(commander);
			
			@Override
			public void execute(Response response, JSONObject request, JSONObject session) throws SQLException {
				add.execute(response, request, session);
			}
			
		});
		
		map.put("GET", new Executor() {
			private final Executor get = new Get(commander);
			
			@Override
			public void execute(Response response, JSONObject request, JSONObject session) throws SQLException {
				get.execute(response, request, session);
			}
			
		});
		
		map.put("REMOVE", new Executor() {
			private final Executor remove = new Remove(commander);
			
			@Override
			public void execute(Response response, JSONObject request, JSONObject session) throws SQLException {
				remove.execute(response, request, session);
			}
			
		});
		
		map.put("SET", new Executor() {
			private final Executor set = new Set(commander);
			
			@Override
			public void execute(Response response, JSONObject request, JSONObject session) throws SQLException {
				set.execute(response, request, session);
			}
			
		});
	}
	
	public final boolean execute(Request request, Response response, JSONObject data) {
		Session session = request.getSession(false);
		
		if (session == null) {
			throw new JSONException("No session.");
		}
		
		JSONObject account = (JSONObject)session.getAttribute("account");
		Executor executor = this.map.get(data.getString("command").toUpperCase());
		
		if (executor == null) {
			return false;
		} else {
			try {
				executor.execute(response, data, account);
			} catch (JSONException e) {
				response.setStatus(Response.Status.BADREQUEST);
				
				response.write(new JSONObject().
					put("error", e.getMessage()).toString());
			} catch (Exception e) {
				response.setStatus(Response.Status.SERVERERROR);
				
				response.write(new JSONObject().
					put("error", e.getMessage()).toString());
				
				e.printStackTrace();
			}
		}
		
		return true;
	}

	public static boolean isValidInvoice(JSONObject invoice) {
		if (!invoice.has("expect")) {
			return false;
		}
		
		if (invoice.has("complete")) {
			if (!invoice.has("confirm") || !invoice.getBoolean("confirm")) {
				return false;
			}
		}
		
		return true;
	}
	
}
