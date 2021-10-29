package com.itahm.erp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import com.itahm.http.HTTPServer;
import com.itahm.http.Request;
import com.itahm.http.Response;
import com.itahm.json.JSONException;
import com.itahm.json.JSONObject;

import com.itahm.service.Serviceable;

public class ITAhM extends HTTPServer {
	
	private final Path root;
	private final Serviceable erp;
	private Boolean isClosed = false;
	
	
	public ITAhM() throws Exception {
		this("0.0.0.0", 2014);
	}
	
	public ITAhM(String ip, int tcp) throws Exception {
		this(ip, tcp, Path.of(ITAhM.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent());
	}

	public ITAhM(String ip, int tcp, Path path) throws Exception {
		super(ip, tcp);
		
		System.out.format("ITAhM HTTP Server started with TCP %d.\n", tcp);
		
		this.root = path.resolve("data");
		
		Path root = path.resolve("data");
		
		if (!Files.isDirectory(root)) {
			Files.createDirectories(root);
		}
		
		erp = new ERP(root);
		
		erp.start();
	}
	
	@Override
	public void doGet(Request request, Response response) {
		String uri = request.getRequestURI();
		
		if ("/".equals(uri)) {
			uri = "/index.html";
		}
		
		Path path = this.root.resolve(uri.substring(1));
		
		if (!Pattern.compile("^/data/.*").matcher(uri).matches() && Files.isRegularFile(path)) {
			try {
				response.write(path);
			} catch (IOException e) {
				response.setStatus(Response.Status.SERVERERROR);
			}
		}
		else {
			response.setStatus(Response.Status.NOTFOUND);
		}
	}
	
	@Override
	public void doPost(Request request, Response response) {
		try {
			JSONObject data = new JSONObject(new String(request.read(), StandardCharsets.UTF_8.name()));
			
			if (!data.has("command")) {
				throw new JSONException("Command is not found");
			}
			
			switch (data.getString("command").toUpperCase()) {
			case "START":				
				this.erp.start();
				
				return;
			case "STOP":
				this.erp.stop();
				
				return;
				
			default:
				this.erp.service(request, response, data);
				
				return;
			}
		} catch (JSONException | UnsupportedEncodingException e) {
			response.write(new JSONObject()
				.put("error", e.getMessage())
				.toString());
		}
		
		response.setStatus(Response.Status.BADREQUEST);
	}
	
	@Override
	public void doPut(Request request, Response response) {		
		try {
			this.erp.service(request, response, null);
			
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		response.setStatus(Response.Status.BADREQUEST);
	}
	
	public void close() {
		synchronized (this.isClosed) {
			if (this.isClosed) {
				return;
			}
			
			this.isClosed = true;
		}
		
		this.erp.stop();
		
		try {
			super.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		Path root = null;
		String ip = "0.0.0.0";
		int tcp = 2014;
		
		for (int i=0, _i=args.length; i<_i; i++) {
			if (args[i].indexOf("-") != 0) {
				continue;
			}
			
			switch(args[i].substring(1).toUpperCase()) {
			case "ROOT":
				root = Path.of(args[++i]);
				
				break;
			case "TCP":
				try {
					tcp = Integer.parseInt(args[++i]);
				} catch (NumberFormatException nfe) {}
				
				break;
			}
		}
		
		ITAhM itahm = root == null? new ITAhM(ip, tcp): new ITAhM(ip, tcp, root);
		
		Runtime.getRuntime().addShutdownHook(
			new Thread() {
				
				@Override
				public void run() {
					if (itahm != null) {
						itahm.close();
					}
				}
			}
		);
	}
}
