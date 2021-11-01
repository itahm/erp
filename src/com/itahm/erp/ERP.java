package com.itahm.erp;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLException;

import com.itahm.http.Request;
import com.itahm.http.Response;
import com.itahm.http.Session;
import com.itahm.json.JSONException;
import com.itahm.json.JSONObject;

import com.itahm.service.Serviceable;

public class ERP implements Serviceable {

	private final static int SESS_TIMEOUT = 3600;
	
	private Commander agent;
	private final Path root;
	private Boolean isClosed = true;
	
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
					case "ADD":
						add(data, response, account);
						
						break;
					case "BACKUP":
						if (account.getInt("level") > 0) {
							response.setStatus(Response.Status.FORBIDDEN);
						} else {
							this.agent.backup();
						}
						
						break;
					case "ECHO": break;
					case "GET":
						get(data, response, account);				
						
						break;			
					case "REMOVE":
						remove(data, response, account);
						
						break;
					case "SET":
						set(data, response, account);
						
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
	
	private void add(JSONObject request, Response response, JSONObject account) throws JSONException, SQLException {
		JSONObject result;
		
		switch(request.getString("target").toUpperCase()) {
		case "CAR":
			if (!agent.addCar(request.getJSONObject("car"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "COMPANY":
			if (!agent.addCompany(request.getJSONObject("company"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "INVOICE":
			if (!isValidInvoice(request.getJSONObject("invoice"))) {
				response.setStatus(Response.Status.BADREQUEST);
			}
			else {
				agent.addInvoice(request.getJSONObject("invoice"));
			}
			
			break;
		case "ITEM":
			if (!agent.addItem(request.getJSONObject("item"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
		case "MANAGER":
			if (!agent.addManager(request.getJSONObject("manager"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "OPERATION":
			if (!agent.addOperation(request.getJSONObject("operation")
				.put("user", account.getLong("id")))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "PROJECT":
			result = agent.addProject(request.getJSONObject("project"));
			
			response.write(result.toString());
			
			break;
		case "REPORT":
			if (!agent.addReport(request.getJSONObject("report"), account.getLong("id"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "USER":
			result = agent.addUser(request.getJSONObject("user"));
			
			response.write(result.toString());
			
			break;
		default:
			throw new JSONException("Target is not found.");
		}
	}
	
	private void get(JSONObject request, Response response, JSONObject account) {
		JSONObject result;
		
		switch(request.getString("target").toUpperCase()) {
		case "CAR":
			if (request.has("id")) {
				result = this.agent.getCar(request.getLong("id"));
				
				if (result == null) {
					response.setStatus(Response.Status.NOCONTENT);
				} else {
					response.write(result.toString());
				}
			} else {
				result = this.agent.getCar();
				
				if (result == null) {
					response.setStatus(Response.Status.SERVERERROR);
				} else {
					response.write(result.toString());
				}
			}
			
			break;
		case "COMPANY":
			if (request.has("id")) {
				result = this.agent.getCompany(request.getString("id"));
				
				if (result == null) {
					response.setStatus(Response.Status.NOCONTENT);
				} else {
					response.write(result.toString());
				}
			} else {
				result = this.agent.getCompany();
				
				if (result == null) {
					response.setStatus(Response.Status.SERVERERROR);
				} else {
					response.write(result.toString());
				}
			}
			
			break;
		case "FILE":
			if (request.has("id")) {
				byte [] binary = this.agent.download(request.getLong("id"));
				
				if (binary != null) {
					response.write(binary);
				} else {
					response.setStatus(Response.Status.NOCONTENT);
				}
			} else if (request.has("tid") && request.has("type")) {
				JSONObject fileData = null;
				
				switch(request.getString("type").toUpperCase()) {
				case "PROJECT":
					fileData = this.agent.getFile(request.getLong("tid"), request.getString("type"));
					
					break;
				case "COMPANY":
					fileData = this.agent.getFile(request.getString("tid"), request.getString("type"));
					
					break;
				}
				
				if (fileData == null) {
					response.setStatus(Response.Status.SERVERERROR);
				} else {
					response.write(fileData.toString());
				}
			} else {
				JSONObject fileData = this.agent.getFile();
				
				if (fileData != null) {
					response.write(fileData.toString());
				} else {
					response.setStatus(Response.Status.SERVERERROR);
				}
			}
			
			break;
		case "INVOICE":
			if (request.has("project")) {
				result = this.agent.getInvoice(request.getLong("project"));
			} else if (request.has("type") && request.has("status")) {
				result = this.agent.getInvoice(request.getInt("type"), request.getInt("status"), request.getInt("date"));
			} else {
				result = this.agent.getInvoice();
			}
			
			if (result == null) {
				response.setStatus(Response.Status.SERVERERROR);
			} else {
				response.write(result.toString());
			}
			
			break;
		case "ITEM":
			if (request.has("id")) {
				JSONObject item = this.agent.getItem(request.getLong("id"));
				
				if (item == null) {
					response.setStatus(Response.Status.NOCONTENT);
				} else {
					response.write(item.toString());
				}
			} else {
				JSONObject itemData = this.agent.getItem();
				
				if (itemData == null) {
					response.setStatus(Response.Status.SERVERERROR);
				} else {
					response.write(itemData.toString());
				}
			}
			
			break;
		case "MANAGER":			
			if (request.has("id")) {
				result = this.agent.getManager(request.getLong("id"));
				
				if (result == null) {
					response.setStatus(Response.Status.NOCONTENT);
				} else {
					response.write(result.toString());
				}
			} else if (request.has("company")) {
				result = this.agent.getManager(request.getString("company"));
				
				if (result == null) {
					response.setStatus(Response.Status.NOCONTENT);
				} else {
					response.write(result.toString());
				}
			} else {
				result = this.agent.getManager();
				
				if (result == null) {
					response.setStatus(Response.Status.SERVERERROR);
				} else {
					response.write(result.toString());
				}
			}
			
			break;
		case "OPERATION":
			if (request.has("id")) {
				result = this.agent.getOperation(request.getLong("id"));
				
				if (result == null) {
					response.setStatus(Response.Status.NOCONTENT);
				} else {
					response.write(result.toString());
				}
			} else {
				result = this.agent.getOperation();
				
				if (result == null) {
					response.setStatus(Response.Status.SERVERERROR);
				} else {
					response.write(result.toString());
				}
			}
			
			break;
		case "PROJECT":
			if (request.has("id")) {
				result = this.agent.getProject(request.getLong("id"));
				
				if (result == null) {
					response.setStatus(Response.Status.NOCONTENT);
				} else {
					response.write(result.toString());
				}
			} else {
				result = this.agent.getProject();
				
				if (result == null) {
					response.setStatus(Response.Status.SERVERERROR);
				} else {
					response.write(result.toString());
				}
			}
			
			break;
		case "REPORT":
			JSONObject reportData = this.agent.getReport(account.getLong("id"));
			
			if (reportData != null) {
				response.write(reportData.toString());
			} else {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "USER":
			if (request.has("id")) {
				if (account.getInt("level") > 0 && account.getLong("id") != request.getLong("id")) {
					response.setStatus(Response.Status.FORBIDDEN);
				} else {
					JSONObject user = this.agent.getUser(request.getLong("id"));
					
					if (user == null) {
						response.setStatus(Response.Status.NOCONTENT);
					} else {
						response.write(user.toString());
					}
				}
			} else {
				JSONObject userData = this.agent.getUser();
				
				if (userData == null) {
					response.setStatus(Response.Status.SERVERERROR);
				} else {
					response.write(userData.toString());
				}
			}
			
			break;
		default:
			throw new JSONException("Target is not found.");
		}
	}
	
	private void remove(JSONObject request, Response response, JSONObject account) {
		switch(request.getString("target").toUpperCase()) {
		case "CAR":
			if (!agent.removeCar(request.getLong("id"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "COMPANY":
			if (!agent.removeCompany(request.getString("id"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "FILE":
			if (!agent.removeFile(request.getLong("id"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "INVOICE":
			if (!agent.removeInvoice(request.getLong("id"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "ITEM":
			if (!agent.removeItem(request.getLong("id"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "MANAGER":
			if (!agent.removeManager(request.getLong("id"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "OPERATION":
			if (!agent.removeOperation(request.getLong("id"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "PROJECT":
			if (!agent.removeProject(request.getLong("id"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "REPORT":
			if (!agent.removeReport(request.getLong("id"), request.getString("doc"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "USER":
			if (!account.getString("username").equals("root")) {
				response.setStatus(Response.Status.FORBIDDEN);
			} else if (!agent.removeUser(request.getLong("id"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		default:
			throw new JSONException("Target is not found.");
		}
	}
	
	private void set(JSONObject request, Response response, JSONObject account) throws JSONException, SQLException {
		switch(request.getString("target").toUpperCase()) {
		case "CAR":
			if (!this.agent.setCar(request.getLong("id"), request.getJSONObject("car"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "COMPANY":
			if (!this.agent.setCompany(request.getString("id"), request.getJSONObject("company"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "INVOICE":
			if (!isValidInvoice(request.getJSONObject("invoice"))) {
				response.setStatus(Response.Status.BADREQUEST);
			}
			else if (!this.agent.setInvoice(request.getLong("id"), request.getJSONObject("invoice"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "ITEM":
			if (!this.agent.setItem(request.getLong("id"), request.getJSONObject("item"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "MANAGER":
			if (!this.agent.setManager(request.getLong("id"), request.getJSONObject("manager"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "OPERATION":
			if (!this.agent.setOperation(request.getLong("id"), request.getJSONObject("operation"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "PASSWORD":
			if (request.has("id")) {
				if (account.getInt("level") > 0) {
					response.setStatus(Response.Status.FORBIDDEN);	
				} else if (!this.agent.setPassword(request.getLong("id"), request.getString("password"))){
					response.setStatus(Response.Status.SERVERERROR);
				}
			} else {
				if (!this.agent.setPassword(account.getLong("id"), request.getString("password"))) {
					response.setStatus(Response.Status.SERVERERROR);
				}
			}
			
			break;
		case "PROJECT":
			if (!this.agent.setProject(request.getLong("id"), request.getJSONObject("project"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		case "REPORT":
			if (request.has("boss")) {
				if (!this.agent.setReport(request.getLong("id"), request.getString("doc"), request.getLong("boss"))) {
					response.setStatus(Response.Status.SERVERERROR);
				}
			} else if (request.has("password")) {
				if (!this.agent.setReport(request.getLong("id"), request.getString("doc"), account.getLong("id"), request.getString("password"))) {
					response.setStatus(Response.Status.FORBIDDEN);
				}
			}
			
			break;
		case "USER":
			if (account.getInt("level") > 0) {
				response.setStatus(Response.Status.FORBIDDEN);
			} else if (!this.agent.setUser(request.getLong("id"), request.getJSONObject("user"))) {
				response.setStatus(Response.Status.SERVERERROR);
			}
			
			break;
		default:	
			throw new JSONException("Target is not found.");
		}
	}
	
	@Override
	public boolean isRunning() {
		synchronized(this.isClosed) {
			return !this.isClosed;
		}
	}
	
	private boolean isValidInvoice(JSONObject invoice) {
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
