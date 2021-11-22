package com.itahm.erp;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import com.itahm.http.Request;
import com.itahm.http.Response;
import com.itahm.http.Session;
import com.itahm.json.JSONException;
import com.itahm.json.JSONObject;
import com.itahm.erp.command.Command;
import com.itahm.service.Serviceable;

public class ERP implements Serviceable {

	private final static int SESS_TIMEOUT = 3600;
	
	private Commander agent;
	private final Path root;
	private Boolean isClosed = true;
	private Command command;
	public ERP(Path path) throws Exception {
		root = path;
	}

	@Override
	public void start() {
		synchronized(this.isClosed) {
			if (!this.isClosed) {
				return;
			}
			
			try {
				this.agent = new H2Agent(this.root);
				
				this.command = new Command(this.agent);
				
				this.isClosed = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void stop() {
		synchronized(this.isClosed) {
			if (this.isClosed) {
				return;
			}
			
			try {
				this.agent.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			this.isClosed = true;
		}
	}

	@Override
	public boolean service(Request request, Response response, JSONObject data) {
		synchronized(this.isClosed) {
			if (this.isClosed) {
				return false;
			}
		}
		
		try {
			Session session = request.getSession(false);
			
			if (session == null) {
				if (data != null) {
					String command = data.getString("command").toUpperCase();
					
					if (command.equals("SIGNIN")) {
						JSONObject account = this.agent.signIn(data);
						
						if (account != null) {
							session = request.getSession();
							
							session.setAttribute("account", account);
							
							session.setMaxInactiveInterval(SESS_TIMEOUT);
							
							response.write(account.toString());
							
							return true;
						}
					}
				}
				
				response.setStatus(Response.Status.UNAUTHORIZED);
			} else {
				if (data != null) {
					JSONObject account = (JSONObject)session.getAttribute("account");
					
					switch (data.getString("command").toUpperCase()) {
					case "SIGNIN":
						response.write(account.toString());
						
						break;
					case "SIGNOUT":
						session.invalidate();
						
						break;
					case "BACKUP":
						if (account.getInt("level") > 0) {
							response.setStatus(Response.Status.FORBIDDEN);
						} else {
							this.agent.backup();
						}
						
						break;
					case "ECHO": break;
					case "SET":
					case "GET":
					case "ADD":
					case "REMOVE":
						this.command.execute(request, response, data);
						
						break;
					default:
						response.setStatus(Response.Status.BADREQUEST);
						
						response.write(new JSONObject().
							put("error", String.format("Command %s not found.", data.getString("command").toUpperCase())).toString());
					}
				} else {
					String
						type = request.getHeader("File-Target"),
						id = request.getHeader("File-Id");
					
					if (type != null && id != null) {
						byte [] bin = request.read();
						
						if (bin.length > 0) {
							try {
								switch(type.toUpperCase()) {
								case "PROJECT":
									if (!this.agent.addFile(Long.parseLong(id), "", type, URLDecoder.decode(request.getRequestURI().split("/")[1], StandardCharsets.UTF_8.name()), bin)) {
										response.setStatus(Response.Status.SERVERERROR);
									}
									
									break;
								case "COMPANY":
									if (!this.agent.addFile(0, id, type, URLDecoder.decode(request.getRequestURI().split("/")[1], StandardCharsets.UTF_8.name()), bin)) {
										response.setStatus(Response.Status.SERVERERROR);
									}
									
									break;
								default:
									response.setStatus(Response.Status.BADREQUEST);
									
									response.write(new JSONObject().
											put("error","Target not found").toString());
								}
								
							} catch (NumberFormatException nfe) {
								response.setStatus(Response.Status.BADREQUEST);
								
								response.write(new JSONObject().
										put("error","Number required.").toString());
							} catch (Exception e) {
								response.setStatus(Response.Status.BADREQUEST);
								
								response.write(new JSONObject().
										put("error",e.getMessage()).toString());
							}
						} else {
							response.setStatus(Response.Status.NOCONTENT);
							
							response.write(new JSONObject().
									put("error","Empty file.").toString());
						}
					} else {
						response.setStatus(Response.Status.BADREQUEST);
						
						response.write(new JSONObject().
								put("error","Insufficient header.").toString());
					}
				}
			}
		} catch (JSONException jsone) {
			response.setStatus(Response.Status.BADREQUEST);
			
			response.write(new JSONObject().
				put("error", jsone.getMessage()).toString());
		} catch (Exception e) {
			response.setStatus(Response.Status.SERVERERROR);
			
			response.write(new JSONObject().
				put("error", e.getMessage()).toString());
		}
	
		return true;
	}
	
	@Override
	public boolean isRunning() {
		synchronized(this.isClosed) {
			return !this.isClosed;
		}
	}
	
}
