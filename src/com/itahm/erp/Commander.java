package com.itahm.erp;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

import com.itahm.json.JSONObject;

public interface Commander extends Closeable {
	public boolean addCar(JSONObject car);
	public boolean addCompany(JSONObject company);
	public boolean addFile(long lID, String sID, String type, String name, byte [] bin);
	public JSONObject addInvoice(JSONObject invoice) throws SQLException;
	public boolean addItem(JSONObject item);
	public boolean addManager(JSONObject manager);
	public boolean addOperation(JSONObject operation);
	public JSONObject addProject(JSONObject project) throws SQLException;
	public JSONObject addRepair(JSONObject repair) throws SQLException;
	public JSONObject addUser(JSONObject user) throws SQLException;
	public void backup() throws Exception;
	public byte [] download(long id) throws SQLException;
	public JSONObject getCar() throws SQLException;
	public JSONObject getCar(long id) throws SQLException;
	public JSONObject getCompany() throws SQLException;
	public JSONObject getCompany(String id) throws SQLException;
	public JSONObject getFile() throws SQLException;
	public JSONObject getFile(long id, String type) throws SQLException;
	public JSONObject getFile(String id, String type) throws SQLException;
	public JSONObject getInvoice() throws SQLException;
	public JSONObject getInvoice(int type, int status, int date) throws SQLException;
	public JSONObject getInvoice(long project) throws SQLException;
	public JSONObject getItem() throws SQLException;
	public JSONObject getItem(long id) throws SQLException;
	public JSONObject getManager() throws SQLException;
	public JSONObject getManager(long id) throws SQLException;
	public JSONObject getManager(String company) throws SQLException;
	public JSONObject getOperation() throws SQLException;
	public JSONObject getOperation(long id) throws SQLException;
	public JSONObject getProject() throws SQLException;
	public JSONObject getProject(long id) throws SQLException;
	public JSONObject getRepair() throws SQLException;
	public JSONObject getRepair(long id) throws SQLException;
	public JSONObject getUser() throws SQLException;
	public JSONObject getUser(long id) throws SQLException;
	public void removeCar(long id) throws SQLException;
	public void removeCompany(String id) throws SQLException;
	public void removeFile(long id) throws IOException, SQLException;
	public void removeInvoice(long id) throws SQLException;
	public void removeItem(long id) throws SQLException;
	public void removeManager(long id) throws SQLException;
	public void removeOperation(long id) throws SQLException;
	public void removeProject(long id) throws SQLException;
	public void removeRepair(long id) throws SQLException;
	public boolean removeUser(long id) throws SQLException;
	public void setCar(long id, JSONObject car) throws SQLException;
	public void setCompany(String id, JSONObject company) throws SQLException;
	public void setInvoice(long id, JSONObject invoice) throws SQLException;
	public void setItem(long id, JSONObject item) throws SQLException;
	public void setManager(long id, JSONObject manager) throws SQLException;
	public void setOperation(long id, JSONObject operation) throws SQLException;
	public void setPassword(long id, String password) throws SQLException;
	public void setProject(long id, JSONObject project) throws SQLException;
	public void setRepair(JSONObject repair) throws SQLException;
	public void setUser(long id, JSONObject user) throws SQLException;
	public JSONObject signIn(JSONObject user) throws SQLException;
	
}
