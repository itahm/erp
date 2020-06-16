package com.itahm.erp;

import java.io.Closeable;
import java.sql.SQLException;

import com.itahm.json.JSONObject;

public interface Commander extends Closeable {
	public boolean addCompany(JSONObject company);
	public boolean addCar(JSONObject car);
	public boolean addInvoice(JSONObject invoice);
	public boolean addItem(JSONObject item);
	public boolean addManager(JSONObject manager);
	public boolean addOperation(JSONObject operation);
	public boolean addProject(JSONObject project);
	public boolean addReport(JSONObject report, long owner);
	public boolean addSpend(JSONObject spend, long id);
	public boolean addUser(JSONObject user);
	public void backup() throws Exception;
	public byte [] download(long id, String doc);
	public JSONObject getCar();
	public JSONObject getCar(long id);
	public JSONObject getCompany();
	public JSONObject getCompany(String id);
	public JSONObject getFile();
	public JSONObject getFile(long id, String doc);
	public JSONObject getInvoice();
	public JSONObject getInvoice(long project);
	public JSONObject getItem();
	public JSONObject getItem(long id);
	public JSONObject getManager();
	public JSONObject getManager(long id);
	public JSONObject getManager(String company);
	public JSONObject getOperation();
	public JSONObject getOperation(long id);
	public JSONObject getProject();
	public JSONObject getProject(long id);
	public JSONObject getReport(long id);
	public JSONObject getMySpend(long id);
	public JSONObject getSpend(long id);
	public JSONObject getUser();
	public JSONObject getUser(long id);
	public boolean removeCar(long id);
	public boolean removeOperation(long id);
	public boolean removeCompany(String id);
	public boolean removeInvoice(long id);
	public boolean removeItem(long id);
	public boolean removeFile(long id, String doc);
	public boolean removeManager(long id);
	public boolean removeProject(long id);
	public boolean removeReport(long id, String doc);
	public boolean removeSpend(long id);
	public boolean removeUser(long id);
	public boolean setCar(long id, JSONObject car);
	public boolean setCompany(String id, JSONObject company);
	public boolean setFile(long id, String doc, String name, byte [] binary);
	public boolean setInvoice(long id, JSONObject invoice);
	public boolean setItem(long id, JSONObject item);
	public boolean setManager(long id, JSONObject manager);
	public boolean setOperation(long id, JSONObject operation);
	public boolean setPassword(long id, String password);
	public boolean setProject(long id, JSONObject project);
	public boolean setReport(long id, String doc, Long boss);
	public boolean setReport(long id, String doc, Long boss, String password) throws SQLException;
	public boolean setSpend(long id, JSONObject spend);
	public boolean setUser(long id, JSONObject user);
	public JSONObject signIn(JSONObject user);
}
