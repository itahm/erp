package com.itahm.erp;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.h2.jdbcx.JdbcConnectionPool;

import com.itahm.json.JSONObject;

public class H2Agent implements Commander, Closeable {
	private final static String MD5_ROOT = "63a9f0ea7bb98050796b649e85481845";
	
	private Boolean isClosed = false;
	private final JdbcConnectionPool connPool;
	
	private final Path root;
	
	{
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}
	
	public H2Agent (Path path) throws Exception {
		System.out.println("Commander ***CeRP v1.0***");
		
		System.out.format("Directory: %s\n", path.toString());
		
		root = path;
		
		connPool = JdbcConnectionPool.create(String.format("jdbc:h2:%s", path.resolve("erp").toString()), "sa", "");
		
		initTable();
		initData();
				
		System.out.println("Agent start.");
	}
	
	@Override
	public boolean addCar(JSONObject car) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("INSERT INTO t_car"+
				" (name, number)"+
				" VALUES(?, ?);")) {
				pstmt.setString(1, car.getString("name"));
				pstmt.setString(2, car.getString("number"));
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;	
	}
	
	@Override
	public boolean addCompany(JSONObject company) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("INSERT INTO t_company (name, id, ceo, address)"+
				" VALUES(?, ?, ?, ?);")) {
				pstmt.setString(1, company.getString("name"));
				pstmt.setString(2, company.getString("id"));
				pstmt.setString(3, company.getString("ceo"));
				pstmt.setString(4, company.getString("address"));
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;	
	}
	
	@Override
	public boolean addInvoice(JSONObject invoice) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("INSERT INTO t_invoice (type, project)"+
				" VALUES(?, ?);")) {
				pstmt.setInt(1, invoice.getInt("type"));
				pstmt.setLong(2, invoice.getLong("project"));
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;	
	}
	
	@Override
	public boolean addItem(JSONObject item) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("INSERT INTO item (maker, name, spec)"+
				" VALUES(?, ?, ?);")) {
				pstmt.setString(1, item.getString("maker"));
				pstmt.setString(2, item.getString("name"));
				pstmt.setString(3, item.getString("spec"));
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;	
	}
	
	@Override
	public boolean addManager(JSONObject manager) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("INSERT INTO t_manager (name, mobile, email, company)"+
				" VALUES(?, ?, ?, ?);")) {
				pstmt.setString(1, manager.getString("name"));
				pstmt.setString(2, manager.getString("mobile"));
				pstmt.setString(3, manager.getString("email"));
				pstmt.setString(4, manager.getString("company"));
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;	
	}
	
	@Override
	public boolean addOperation(JSONObject operation) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("INSERT INTO t_operation"+
				" (user, car, date, before, after, extra, total, parking, stock, comment)"+
				" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
				pstmt.setLong(1, operation.getLong("user"));
				pstmt.setLong(2, operation.getLong("car"));
				pstmt.setString(3, operation.getString("date"));
				pstmt.setInt(4, operation.getInt("before"));
				pstmt.setInt(5, operation.getInt("after"));
				pstmt.setInt(6, operation.getInt("extra"));
				pstmt.setInt(7, operation.getInt("total"));
				pstmt.setString(8, operation.getString("parking"));
				pstmt.setString(9, operation.getString("stock"));
				pstmt.setString(10, operation.getString("comment"));
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;	
	}
	
	@Override
	public boolean addProject(JSONObject project) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("INSERT INTO t_project"+
				" (name, contract, deposit, start, end, payment, content, company, manager)"+
				" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
				pstmt.setString(1, project.getString("name"));
				pstmt.setString(2, project.getString("contract"));
				pstmt.setLong(3, project.getLong("deposit"));
				pstmt.setString(4, project.getString("start"));
				pstmt.setString(5, project.getString("end"));
				pstmt.setString(6, project.getString("payment"));
				pstmt.setString(7, project.getString("content"));
				pstmt.setString(8, project.getString("company"));
				pstmt.setLong(9, project.getLong("manager"));
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;	
	}
	
	@Override
	public boolean addReport(JSONObject report, long owner) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("INSERT INTO report (doc_id, doc_name, boss, owner)"+
				" VALUES(?, ?, ?, ?);")) {
				pstmt.setLong(1, report.getLong("id"));
				pstmt.setString(2, report.getString("doc"));
				pstmt.setLong(3, report.getLong("boss"));
				pstmt.setLong(4, owner);
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;	
	}
	
	@Override
	public boolean addSpend(JSONObject spend, long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("INSERT INTO spend (user_id, date, type, target, amount)"+
				" VALUES(?, ?, ?, ?, ?);")) {
				pstmt.setLong(1, id);
				pstmt.setLong(2, spend.getLong("date"));
				pstmt.setString(3, spend.getString("type"));
				pstmt.setString(4, spend.getString("target"));
				pstmt.setLong(5, spend.getLong("amount"));
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;	
	}
	
	@Override
	public boolean addUser(JSONObject user) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("INSERT INTO t_user (username, password, name, role, part, mobile, phone, level)"+
				" VALUES(?, ?, ?, ?, ?, ?, ?, ?);")) {
				pstmt.setString(1, user.getString("username"));
				pstmt.setString(2, user.getString("password"));
				pstmt.setString(3, user.getString("name"));
				pstmt.setString(4, user.getString("role"));
				pstmt.setString(5, user.getString("part"));
				pstmt.setString(6, user.getString("mobile"));
				pstmt.setString(7, user.getString("phone"));
				pstmt.setInt(8, user.getInt("level"));
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public void backup() throws Exception {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate(String.format("BACKUP TO '%s';", this.root.resolve("backup.zip")));
			}
		}
	}
	
	@Override
	public void close() {
		synchronized(this.isClosed) {
			if (this.isClosed) {
				return;
			}
		}
		
		this.connPool.dispose();
		
		System.out.println("Agent stop.");
	}
	
	@Override
	public byte [] download(long id, String doc) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT file" + 
				" FROM file"+
				" WHERE doc_id=? AND doc_name=?;")) {
				pstmt.setLong(1, id);
				pstmt.setString(2, doc);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return rs.getBinaryStream(1).readAllBytes();
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getCar() {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {				
				try (ResultSet rs = stmt.executeQuery("SELECT id, name, number"+
					" FROM t_car;")) {
					JSONObject
						carData = new JSONObject(),
						car;
					
					while (rs.next()) {
						car = new JSONObject()
							.put("id", rs.getLong(1))
							.put("name", rs.getString(2))
							.put("number", rs.getString(3));
						
						carData.put(rs.getString(1), car);
					}
					
					return carData;
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getCar(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT id, name, number"+
				" FROM t_car"+
				" WHERE id=?"+
				";")) {
				pstmt.setLong(1, id);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return new JSONObject()
							.put("id", id)
							.put("name", rs.getString(2))
							.put("number", rs.getString(3));
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	
	@Override
	public JSONObject getCompany() {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {
				JSONObject
					companyData = new JSONObject(),
					company;
				
				try (ResultSet rs = stmt.executeQuery("SELECT id, name, address, ceo"+
					" FROM t_company;")) {
					while (rs.next()) {
						company = new JSONObject()
							.put("id", rs.getString(1))
							.put("name", rs.getString(2))
							.put("address", rs.getString(3))
							.put("ceo", rs.getString(4));
						
						companyData.put(rs.getString(1), company);
					}
				}
				
				return companyData;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getCompany(String id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT name, address, ceo" + 
				" FROM t_company"+
				" WHERE id=?;")) {
				pstmt.setString(1, id);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return new JSONObject()
							.put("id", id)
							.put("name", rs.getString(1))
							.put("address", rs.getString(2))
							.put("ceo", rs.getString(3));
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}		
		return null;
	}
	
	@Override
	public JSONObject getFile() {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {
				JSONObject
					fileData = new JSONObject(),
					file;
				
				try (ResultSet rs = stmt.executeQuery("SELECT doc_id, doc_name, name"+
					" FROM file;")) {
					while (rs.next()) {
						file = new JSONObject()
							.put("id", rs.getLong(1))
							.put("doc", rs.getString(2))
							.put("name", rs.getString(3));
						
						fileData.put(Long.toString(rs.getLong(1)), file);
					}
				}
				
				return fileData;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getFile(long id, String doc) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT name" + 
				" FROM file WHERE doc_id=? AND doc_name=?;")) {
				pstmt.setLong(1, id);
				pstmt.setString(2, doc);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return new JSONObject()
							.put("id", id)
							.put("doc", doc)
							.put("name", rs.getString(1));
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getInvoice() {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {				
				try (ResultSet rs = stmt.executeQuery("SELECT"+
					" id, expect, issue, complete, amount, type, project"+
					" FROM t_invoice"+
					";")) {
					JSONObject
						invoiceData = new JSONObject(),
						invoice;
					String date;
					
					while (rs.next()) {
						invoice = new JSONObject()
							.put("id", rs.getLong(1))
							.put("amount", rs.getInt(5))
							.put("type", rs.getInt(6))
							.put("project", rs.getLong(7));
						
						date = rs.getString(2);
						
						if (!rs.wasNull()) {
							invoice.put("expect", date);
						}
						
						date = rs.getString(3);
						
						if (!rs.wasNull()) {
							invoice.put("issue", date);
						}
						
						date = rs.getString(4);
						
						if (!rs.wasNull()) {
							invoice.put("complete", date);
						}
						
						invoiceData.put(Long.toString(rs.getLong(1)), invoice);
					}
					
					return invoiceData;
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getInvoice(long project) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT"+
				" id, expect, issue, complete, amount, type"+
				" FROM t_invoice"+
				" WHERE project=?"+
				";")) {
				pstmt.setLong(1, project);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					JSONObject
						invoiceData = new JSONObject(),
						invoice;
					String date;
					
					while (rs.next()) {
						invoice = new JSONObject()
							.put("id", rs.getLong(1))
							.put("amount", rs.getInt(5))
							.put("type", rs.getInt(6));
						
						date = rs.getString(2);
						
						if (!rs.wasNull()) {
							invoice.put("expect", date);
						}
						
						date = rs.getString(3);
						
						if (!rs.wasNull()) {
							invoice.put("issue", date);
						}
						
						date = rs.getString(4);
						
						if (!rs.wasNull()) {
							invoice.put("complete", date);
						}
						
						invoiceData.put(Long.toString(rs.getLong(1)), invoice);
					}
					
					return invoiceData;
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getItem() {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {
				JSONObject
					itemData = new JSONObject(),
					item;
				
				try (ResultSet rs = stmt.executeQuery("SELECT id, maker, name, spec"+
					" FROM item;")) {
					while (rs.next()) {
						item = new JSONObject()
							.put("id", rs.getLong(1))
							.put("maker", rs.getString(2))
							.put("name", rs.getString(3))
							.put("spec", rs.getString(4));
						
						itemData.put(Long.toString(rs.getLong(1)), item);
					}
				}
				
				return itemData;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getItem(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT maker, name, spec " + 
				" FROM item"+
				" WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return new JSONObject()
							.put("id", id)
							.put("maker", rs.getString(1))
							.put("name", rs.getString(2))
							.put("spec", rs.getString(3));
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}		
		return null;
	}
	
	@Override
	public JSONObject getManager() {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {				
				try (ResultSet rs = stmt.executeQuery("SELECT id, name, mobile, email, company"+
					" FROM t_manager;")) {
					JSONObject
						mgrData = new JSONObject(),
						manager;
					
					while (rs.next()) {
						manager = new JSONObject()
							.put("id", rs.getLong(1))
							.put("name", rs.getString(2))
							.put("mobile", rs.getString(3))
							.put("email", rs.getString(4))
							.put("company", rs.getString(5));
						
						mgrData.put(Long.toString(rs.getLong(1)), manager);
					}
					
					return mgrData;
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getManager(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT id, name, mobile, email, company"+
				" FROM t_manager WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return new JSONObject()
							.put("id", rs.getLong(1))
							.put("name", rs.getString(2))
							.put("mobile", rs.getString(3))
							.put("email", rs.getString(4))
							.put("company", rs.getString(5));
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getManager(String company) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT id, name, mobile, email, company"+
				" FROM t_manager WHERE company=?;")) {
				pstmt.setString(1, company);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					JSONObject
						mgrData = new JSONObject(),
						manager;
					
					while (rs.next()) {
						manager = new JSONObject()
							.put("id", rs.getLong(1))
							.put("name", rs.getString(2))
							.put("mobile", rs.getString(3))
							.put("email", rs.getString(4))
							.put("company", rs.getString(5));
						
						mgrData.put(Long.toString(rs.getLong(1)), manager);
					}
					
					return mgrData;
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getOperation() {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {
				try (ResultSet rs = stmt.executeQuery("SELECT"+
					" O.id, U.name, C.name, date, before, after, extra, total, parking, stock, comment"+
					" FROM t_operation AS O"+
					" LEFT JOIN t_car AS C"+
					" ON O.car=C.id"+
					" LEFT JOIN t_user AS U"+
					" ON O.user=U.id"+
					";")) {
					JSONObject
						opData = new JSONObject(),
						operation;
					
					while (rs.next()) {
						operation = new JSONObject()
							.put("id", rs.getLong(1))
							.put("user", rs.getString(2))
							.put("car", rs.getString(3))
							.put("date", rs.getString(4))
							.put("before", rs.getInt(5))
							.put("after", rs.getInt(6))
							.put("extra", rs.getInt(7))
							.put("total", rs.getInt(8))
							.put("parking", rs.getString(9))
							.put("stock", rs.getString(10))
							.put("comment", rs.getString(11));
						
						opData.put(Long.toString(rs.getLong(1)), operation);
					}
					
					return opData;
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;	
	}
	
	@Override
	public JSONObject getOperation(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT"+
				" U.name, car, date, before, after, extra, total, parking, stock, comment"+
				" FROM t_operation AS O"+
				" LEFT JOIN t_car AS C"+
				" ON O.car=C.id"+
				" LEFT JOIN t_user AS U"+
				" ON O.user=U.id"+
				" WHERE O.id=?"+
				";")) {
				pstmt.setLong(1, id);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return new JSONObject()
							.put("id", id)
							.put("user", rs.getString(1))
							.put("car", rs.getLong(2))
							.put("date", rs.getString(3))
							.put("before", rs.getInt(4))
							.put("after", rs.getInt(5))
							.put("extra", rs.getInt(6))
							.put("total", rs.getInt(7))
							.put("parking", rs.getString(8))
							.put("stock", rs.getString(9))
							.put("comment", rs.getString(10));
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;	
	}
	
	@Override
	public JSONObject getProject() {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {
				try (ResultSet rs = stmt.executeQuery("SELECT"+
					" P.id, P.name, contract, deposit, start, end, payment, content, C.name, manager"+
					" FROM t_project AS P"+
					" LEFT JOIN t_company AS C"+
					" ON P.company=C.id"+
					";")) {
					JSONObject
						prjData = new JSONObject(),
						project;
					
					while (rs.next()) {
						project = new JSONObject()
							.put("id", rs.getLong(1))
							.put("name", rs.getString(2))
							.put("contract", rs.getString(3))
							.put("deposit", rs.getLong(4))
							.put("start", rs.getString(5))
							.put("end", rs.getString(6))
							.put("payment", rs.getString(7))
							.put("content", rs.getString(8))
							.put("company", rs.getString(9))
							.put("manager", rs.getLong(10));
						
						prjData.put(Long.toString(rs.getLong(1)), project);
					}
					
					return prjData;
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;	
	}
	
	@Override
	public JSONObject getProject(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT"+
				" name, contract, deposit, start, end, payment, content, company, manager"+
				" FROM t_project"+
				" WHERE id=?"+
				";")) {
				pstmt.setLong(1, id);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return new JSONObject()
							.put("id", id)
							.put("name", rs.getString(1))
							.put("contract", rs.getString(2))
							.put("deposit", rs.getLong(3))
							.put("start", rs.getString(4))
							.put("end", rs.getString(5))
							.put("payment", rs.getString(6))
							.put("content", rs.getString(7))
							.put("company", rs.getString(8))
							.put("manager", rs.getString(9));
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;	
	}
	
	@Override
	public JSONObject getReport(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT r.id, doc_id, doc_name, name"+
				" FROM report AS r"+
				" LEFT JOIN t_user AS u"+
				" ON owner=u.id"+
				" WHERE boss=? AND confirm=FALSE"+
				";")) {
				pstmt.setLong(1, id);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					JSONObject
						reportData = new JSONObject(),
						report;
					
					while (rs.next()) {
						report = new JSONObject()
							.put("id", rs.getLong(1))
							.put("docID", rs.getLong(2))
							.put("docName", rs.getString(3))
							.put("userName", rs.getString(4))
							.put("boss", id);
						
						reportData.put(Long.toString(rs.getLong(1)), report);
					}
					
					return reportData;
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;	
	}
	
	@Override
	public JSONObject getMySpend(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {
				try (PreparedStatement pstmt = c.prepareStatement("SELECT s.id, date, type, target, amount, name"+
					" FROM spend AS s"+
					" LEFT JOIN file AS f"+
					" ON s.id=f.doc_id AND f.doc_name='spend'"+
					" LEFT JOIN report AS r"+
					" ON s.id=r.doc_id AND r.doc_name='spend'"+
					" WHERE user_id=? AND boss IS NULL;")) {
					pstmt.setLong(1, id);
					
					try (ResultSet rs = pstmt.executeQuery()) {
						JSONObject
							spendData = new JSONObject(),
							spend;
						String name;
						
						while (rs.next()) {
							spend = new JSONObject()
								.put("id", rs.getLong(1))
								.put("date", rs.getLong(2))
								.put("type", rs.getString(3))
								.put("target", rs.getString(4))
								.put("amount", rs.getLong(5));
							
							name = rs.getString(6);
							
							if (!rs.wasNull()) {
								spend.put("file", name);
							}
							
							spendData.put(Long.toString(rs.getLong(1)), spend);
						}
						
						return spendData;
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getSpend(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {
				try (PreparedStatement pstmt = c.prepareStatement("SELECT date, type, target, amount, name"+
					" FROM spend AS s"+
					" LEFT JOIN file AS f"+
					" ON s.id=doc_id AND doc_name='spend'"+
					" WHERE id=?;")) {
					pstmt.setLong(1, id);
					
					try (ResultSet rs = pstmt.executeQuery()) {
						JSONObject spend;
						String name;
						
						if (rs.next()) {
							spend = new JSONObject()
								.put("id", id)
								.put("date", rs.getLong(1))
								.put("type", rs.getString(2))
								.put("target", rs.getString(3))
								.put("amount", rs.getLong(4));
							
							name = rs.getString(5);
							
							if (!rs.wasNull()) {
								spend.put("file", name);
							}
							
							return spend;
						}
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getUser() {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {
				JSONObject
					userData = new JSONObject(),
					user;
				
				try (ResultSet rs = stmt.executeQuery("SELECT id, username, name, role, part, mobile, phone, level"+
					" FROM t_user"+
					" WHERE username != 'root';")) {
					while (rs.next()) {
						user = new JSONObject()
							.put("id", rs.getLong(1))
							.put("username", rs.getString(2))
							.put("name", rs.getString(3))
							.put("role", rs.getString(4))
							.put("part", rs.getString(5))
							.put("mobile", rs.getString(6))
							.put("phone", rs.getString(7))
							.put("level", rs.getInt(8));
						
						userData.put(Long.toString(rs.getLong(1)), user);
					}
				}
				
				return userData;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getUser(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {
				try (PreparedStatement pstmt = c.prepareStatement("SELECT id, username, name, role, part, mobile, phone, level"+
					" FROM t_user"+
					" WHERE id=?;")) {
					pstmt.setLong(1, id);
					
					try (ResultSet rs = pstmt.executeQuery()) {
						if (rs.next()) {
							return new JSONObject()
								.put("id", rs.getLong(1))
								.put("username", rs.getString(2))
								.put("name", rs.getString(3))
								.put("role", rs.getString(4))
								.put("part", rs.getString(5))
								.put("mobile", rs.getString(6))
								.put("phone", rs.getString(7))
								.put("level", rs.getInt(8));
						}
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	private void initTable() throws SQLException {
		long start = System.currentTimeMillis();
		
		try (Connection c = connPool.getConnection()) {
			c.setAutoCommit(false);
			
			/**
			 * CAR
			 **/
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS t_car"+
					" (id BIGINT PRIMARY KEY AUTO_INCREMENT"+
					", name VARCHAR NOT NULL"+
					", number VARCHAR NOT NULL"+
					");");
			}
			/**END**/
			
			/**
			 * COMPANY
			 **/
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS t_company"+
					" (id varchar PRIMARY KEY"+
					", name VARCHAR NOT NULL"+
					", ceo VARCHAR NOT NULL"+
					", address VARCHAR NOT NULL"+
					");");
			}
			/**END**/
			
			/**
			 * FILE
			 **/
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS file ("+
					"doc_id BIGINT NOT NULL"+
					", doc_name VARCHAR NOT NULL"+
					", name VARCHAR NOT NULL"+
					", file BLOB NOT NULL"+
					", UNIQUE(doc_id, doc_name)"+
					");");
			}
			/**END**/
			
			/**
			 * INVOICE
			 **/
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS t_invoice"+
					" (id BIGINT PRIMARY KEY AUTO_INCREMENT"+
					", expect DATE DEFAULT NULL"+
					", issue DATE DEFAULT NULL"+
					", complete DATE DEFAULT NULL"+
					", amount INTEGER NOT NULL DEFAULT 0"+
					", type INTEGER NOT NULL"+
					", project BIGINT NOT NULL"+
					", CONSTRAINT FK_PROJECT_INVOICE FOREIGN KEY (project) REFERENCES t_project(id)"+
					");");
			}
			/**END**/
			
			/**
			 * ITEM
			 **/
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS item"+
					" (id BIGINT PRIMARY KEY AUTO_INCREMENT"+
					", maker VARCHAR NOT NULL"+
					", name VARCHAR NOT NULL"+
					", spec VARCHAR NOT NULL"+
					");");
			}
			/**END**/
			
			/**
			 * MANAGER
			 **/
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS t_manager ("+
					"id BIGINT PRIMARY KEY AUTO_INCREMENT"+
					", name VARCHAR NOT NULL"+
					", mobile VARCHAR NOT NULL DEFAULT ''"+
					", email VARCHAR NOT NULL DEFAULT ''"+
					", company VARCHAR NOT NULL"+
					", UNIQUE (name, company)"+
					", CONSTRAINT FK_COMPANY_MANAGER FOREIGN KEY (company) REFERENCES t_company(id)"+
					");");
			}
			
			/**END**/
			
			/**
			 * PROJECT
			 **/
			/*try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("drop TABLE IF exists t_project");
			}*/
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS t_project ("+
					"id BIGINT PRIMARY KEY AUTO_INCREMENT"+
					", name VARCHAR NOT NULL"+
					", contract date NOT NULL DEFAULT CURRENT_DATE"+
					", deposit bigint NOT NULL"+
					", start date NOT NULL DEFAULT CURRENT_DATE"+
					", end date NOT NULL DEFAULT CURRENT_DATE"+
					", payment VARCHAR NOT NULL"+
					", content VARCHAR NOT NULL DEFAULT ''"+
					", company VARCHAR NOT NULL"+
					", manager BIGINT NOT NULL"+
					", CONSTRAINT FK_COMPANY_PROJECT FOREIGN KEY (company) REFERENCES t_company(id)"+
					", CONSTRAINT FK_MANAGER_PROJECT FOREIGN KEY (manager) REFERENCES t_manager(id)"+
					");");
			}
			/**END**/
			
			/**
			 * REPORT
			 **/
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS report"+
					" (id BIGINT PRIMARY KEY AUTO_INCREMENT"+
					", doc_id BIGINT NOT NULL"+
					", doc_name VARCHAR NOT NULL"+
					", boss BIGINT NOT NULL"+
					", owner BIGINT NOT NULL"+
					", confirm BOOLEAN NOT NULL DEFAULT FALSE"+
					", FOREIGN KEY (boss) REFERENCES t_user(id)"+
					", UNIQUE(doc_id, doc_name)"+
					");");
			}
			/**END**/
			
			/**
			 * USER
			 **/
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS t_user ("+
					"id BIGINT PRIMARY KEY AUTO_INCREMENT"+
					", username VARCHAR NOT NULL"+
					", name VARCHAR NOT NULL DEFAULT ''"+
					", password VARCHAR NOT NULL"+
					", role VARCHAR NOT NULL DEFAULT ''"+
					", part VARCHAR NOT NULL DEFAULT ''"+
					", mobile VARCHAR NOT NULL DEFAULT ''"+
					", phone VARCHAR NOT NULL DEFAULT ''"+
					", level INTEGER NOT NULL  DEFAULT 1"+
					", UNIQUE(username)"+
					");");
			}			
			/**END**/
			
			/**
			 * OPERATION
			 **/
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS t_operation ("+
					"id BIGINT PRIMARY KEY AUTO_INCREMENT"+
					", user BIGINT NOT NULL"+
					", car BIGINT NOT NULL"+
					", date DATE NOT NULL"+
					", before INTEGER NOT NULL"+
					", after INTEGER NOT NULL"+
					", extra INTEGER NOT NULL"+
					", total INTEGER NOT NULL"+
					", parking VARCHAR NOT NULL"+
					", stock VARCHAR NOT NULL"+
					", comment VARCHAR NOT NULL"+
					", CONSTRAINT FK_USER_OPERATION FOREIGN KEY (user) REFERENCES t_user(id)"+
					", CONSTRAINT FK_CAR_OPERATION FOREIGN KEY (car) REFERENCES t_car(id)"+
					");");
			}
			
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("alter TABLE IF EXISTS t_operation add column parking varchar not null default ''");
			}
			
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("alter TABLE IF EXISTS t_operation add column stock varchar not null default ''");
			}
			/**END**/
			
			/**
			 * SPEND
			 **/
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS spend ("+
					"id BIGINT PRIMARY KEY AUTO_INCREMENT"+
					", user_id BIGINT NOT NULL"+
					", date BIGINT NOT NULL"+
					", type VARCHAR NOT NULL"+
					", target VARCHAR NOT NULL"+
					", amount BIGINT NOT NULL"+
					", CONSTRAINT FK_USER_SPEND FOREIGN KEY (user_id) REFERENCES t_user(id)"+
					");");
			}
			/**END**/
			
			try {
				c.commit();
			} catch (Exception e) {
				c.rollback();
				
				throw e;
			}
		}
		
		System.out.format("Database initialized in %dms.\n", System.currentTimeMillis() - start);
	}

	private void initData() throws SQLException {
		long start = System.currentTimeMillis();

		try (Connection c = connPool.getConnection()) {
			c.setAutoCommit(false);
			
			try {
				/**
				 * USER
				 */
				try (Statement stmt = c.createStatement()) {
					try (ResultSet rs = stmt.executeQuery("SELECT COUNT(username) FROM t_user;")) {
						if (!rs.next() || rs.getLong(1) == 0) {
							try (PreparedStatement pstmt = c.prepareStatement("INSERT INTO t_user"+
								" (username, name, password)"+
								" VALUES ('root', 'root', ?);")) {
								pstmt.setString(1, MD5_ROOT);
								
								pstmt.executeUpdate();
							}
						}
					}
				}
				
				c.commit();
			} catch (SQLException sqle) {
				c.rollback();
				
				throw sqle;
			}
		}
		
		System.out.format("Database parsed in %dms.\n", System.currentTimeMillis() - start);
	}
	
	@Override
	public boolean removeCar(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
				" FROM t_car"+
				" WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean removeCompany(String id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
				" FROM t_company"+
				" WHERE id=?;")) {
				pstmt.setString(1, id);
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean removeFile(long id, String doc) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
				" FROM file"+
				" WHERE doc_id=? AND doc_name=?;")) {
				pstmt.setLong(1, id);
				pstmt.setString(2, doc);
				
				pstmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean removeInvoice(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
				" FROM t_invoice"+
				" WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				pstmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean removeItem(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
				" FROM item"+
				" WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				pstmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean removeManager(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
				" FROM t_manager"+
				" WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				pstmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean removeOperation(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
				" FROM t_operation"+
				" WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				pstmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean removeProject(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
				" FROM t_project"+
				" WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				pstmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean removeReport(long id, String doc) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
				" FROM report"+
				" WHERE doc_id=? AND doc_name=?;")) {
				pstmt.setLong(1, id);
				pstmt.setString(2, doc);
				
				pstmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean removeSpend(long id) {
		try (Connection c = this.connPool.getConnection()) {
			c.setAutoCommit(false);
			
			try {
				try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
					" FROM file"+
					" WHERE doc_id=? AND doc_name='spend';")) {
					pstmt.setLong(1, id);
					
					pstmt.executeUpdate();
				}
				
				try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
					" FROM spend"+
					" WHERE id=?;")) {
					pstmt.setLong(1, id);
					
					pstmt.executeUpdate();
				}
				
				c.commit();
				
				return true;
			} catch (SQLException sqle) {
				c.rollback();
				
				throw sqle;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	@Override
	public boolean removeUser(long id) {
		try (Connection c = connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT username FROM t_user WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					if (!rs.next() || rs.getString(1).equals("root")) {
						return false;
					}
				}
			}
			
			try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
				" FROM t_user"+
				" WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				pstmt.executeUpdate();
			}	
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean setCar(long id, JSONObject car) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE t_car"+
				" SET name=?, number=?"+
				" WHERE id=?;")) {
				pstmt.setString(1, car.getString("name"));
				pstmt.setString(2, car.getString("number"));
				
				pstmt.setLong(3, id);
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean setCompany(String id, JSONObject company) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE t_company"+
				" SET name=?,"+
				" address=?,"+
				" ceo=?"+
				" WHERE id=?;")) {
				pstmt.setString(1, company.getString("name"));
				pstmt.setString(2, company.getString("address"));
				pstmt.setString(3, company.getString("ceo"));
				
				pstmt.setString(4, id);
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean setFile(long id, String doc, String name, byte [] binary) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("MERGE INTO file"+
				" KEY (doc_id, doc_name)"+
				" VALUES(?, ?, ?, ?);")) {
				pstmt.setLong(1, id);
				pstmt.setString(2, doc);
				pstmt.setString(3, name);
				pstmt.setBinaryStream(4, new ByteArrayInputStream(binary));
				
				pstmt.executeUpdate();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean setInvoice(long id, JSONObject invoice) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE t_invoice"+
				" SET expect=?,"+
				" issue=?,"+
				" complete=?,"+
				" amount=?"+
				" WHERE id=?;")) {
				if (invoice.has("expect")) {
					pstmt.setString(1, invoice.getString("expect"));
				} else {
					pstmt.setNull(1, Types.NULL);
				}
				
				if (invoice.has("issue")) {
					pstmt.setString(2, invoice.getString("issue"));
				} else {
					pstmt.setNull(2, Types.NULL);
				}
				
				if (invoice.has("complete")) {
					pstmt.setString(3, invoice.getString("complete"));
				} else {
					pstmt.setNull(3, Types.NULL);
				}
				
				pstmt.setInt(4, invoice.getInt("amount"));
				pstmt.setLong(5, id);
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean setItem(long id, JSONObject item) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE item"+
				" SET maker=?,"+
				" name=?,"+
				" spec=?"+
				" WHERE id=?;")) {
				pstmt.setString(1, item.getString("maker"));
				pstmt.setString(2, item.getString("name"));
				pstmt.setString(3, item.getString("spec"));
				
				pstmt.setLong(4, id);
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean setManager(long id, JSONObject manager) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE t_manager"+
				" SET name=?,"+
				" mobile=?,"+
				" email=?,"+
				" company=?"+
				" WHERE id=?;")) {
				pstmt.setString(1, manager.getString("name"));
				pstmt.setString(2, manager.getString("mobile"));
				pstmt.setString(3, manager.getString("email"));
				pstmt.setString(4, manager.getString("company"));
				
				pstmt.setLong(5, id);
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean setOperation(long id, JSONObject operation) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE t_operation"+
				" SET car=?,"+
				" date=?,"+
				" before=?,"+
				" after=?,"+
				" extra=?,"+
				" total=?,"+
				" parking=?,"+
				" stock=?,"+
				" comment=?"+
				" WHERE id=?"+
				";")) {
				pstmt.setLong(1, operation.getLong("car"));
				pstmt.setString(2, operation.getString("date"));
				pstmt.setInt(3, operation.getInt("before"));
				pstmt.setInt(4, operation.getInt("after"));
				pstmt.setInt(5, operation.getInt("extra"));
				pstmt.setInt(6, operation.getInt("total"));
				pstmt.setString(7, operation.getString("parking"));
				pstmt.setString(8, operation.getString("stock"));
				pstmt.setString(9, operation.getString("comment"));
				pstmt.setLong(10, id);
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;	
	}
	
	@Override
	public boolean setPassword(long id, String password) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE t_user"+
				" SET password=?"+
				" WHERE id=?;")) {
				pstmt.setString(1, password);
				
				pstmt.setLong(2, id);
				
				pstmt.executeUpdate();
			}
			
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;	
	}

	@Override
	public boolean setProject(long id, JSONObject project) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE t_project"+
				" SET name=?,"+
				" SET contract=?,"+
				" SET deposit=?,"+
				" SET start=?,"+
				" SET end=?,"+
				" SET payment=?,"+
				" SET content=?,"+
				" SET company=?,"+
				" WHERE id=?"+
				";")) {
				
				pstmt.setString(1, project.getString("name"));
				pstmt.setString(2, project.getString("contract"));
				pstmt.setLong(3, project.getLong("deposit"));
				pstmt.setString(4, project.getString("start"));
				pstmt.setString(5, project.getString("end"));
				pstmt.setString(6, project.getString("payment"));
				pstmt.setString(7, project.getString("content"));
				pstmt.setString(8, project.getString("company"));
				pstmt.setLong(9, id);
				
				pstmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean setReport(long id, String doc, Long boss) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE report"+
				" SET boss=?"+
				" WHERE doc_id=? AND doc_name=?;")) {
				pstmt.setLong(1, boss);
				pstmt.setLong(2, id);
				pstmt.setString(3, doc);
				
				pstmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean setReport(long id, String doc, Long boss, String password) throws SQLException {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE report"+
				" SET confirm=TRUE"+
				" WHERE EXISTS (SELECT * FROM t_user AS u"+
				" WHERE doc_id=? AND doc_name=? AND u.password=?);")) {
				pstmt.setLong(1, id);
				pstmt.setString(2, doc);
				pstmt.setString(3, password);
				
				if (pstmt.executeUpdate() > 0) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean setSpend(long id, JSONObject spend) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE spend"+
				" SET date=?, type=?, target=?, amount=?"+
				" WHERE id=?;")) {
				pstmt.setLong(1, spend.getLong("date"));
				pstmt.setString(2, spend.getString("type"));
				pstmt.setString(3, spend.getString("target"));
				pstmt.setLong(4, spend.getLong("amount"));
				pstmt.setLong(5, id);
				
				pstmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean setUser(long id, JSONObject user) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE t_user"+
				" SET name=?, role=?, part=?, mobile=?, phone=?, level=?"+
				" WHERE id=?;")) {
				pstmt.setString(1, user.getString("name"));
				pstmt.setString(2, user.getString("role"));
				pstmt.setString(3, user.getString("part"));
				pstmt.setString(4, user.getString("mobile"));
				pstmt.setString(5, user.getString("phone"));
				pstmt.setInt(6, user.getInt("level"));
				pstmt.setLong(7, id);
				
				pstmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}

	@Override
	public JSONObject signIn(JSONObject user) {
		try (Connection c = this.connPool.getConnection()) {
			try (Statement stmt = c.createStatement()) {
				try (PreparedStatement pstmt = c.prepareStatement("SELECT id, name, level"+
					" FROM t_user"+
					" WHERE username=? AND password=?;")) {
					String username = user.getString("username");
					
					pstmt.setString(1, username);
					pstmt.setString(2, user.getString("password"));
					
					try (ResultSet rs = pstmt.executeQuery()) {
						if (rs.next()) {
							return new JSONObject()
								.put("id", rs.getLong(1))
								.put("name", rs.getString(2))
								.put("level", rs.getInt(3))
								.put("username", username);
						}
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
}
