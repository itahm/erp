package com.itahm.erp.command;

import java.sql.SQLException;

import com.itahm.http.Response;
import com.itahm.json.JSONObject;

public interface Executor {
	public void execute(Response response, JSONObject request, JSONObject session) throws SQLException;
}
