package com.itahm.erp;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.h2.jdbcx.JdbcConnectionPool;

import com.itahm.json.JSONObject;

public class H2Agent implements Commander, Closeable {
	private final static String MD5_ROOT = "63a9f0ea7bb98050796b649e85481845";
	
	private Boolean isClosed = false;
	private final JdbcConnectionPool connPool;
	
	private final Path root;
	private final Path attach;
	
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
		
		attach = path.resolve("attach");
		
		if (!Files.isDirectory(attach)) {
			Files.createDirectories(attach);
		}
		
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
	public boolean addFile(long lID, String sID, String type, String name, byte [] bin) {
		String uuid = UUID.randomUUID().toString().toUpperCase();
		
		try {
			Files.write(this.attach.resolve(uuid), bin, StandardOpenOption.CREATE_NEW);
			
			try (Connection c = this.connPool.getConnection()) {
				try (PreparedStatement pstmt = c.prepareStatement("Insert INTO t_file"+
					" (tid, sid, type, name, file)"+
					" VALUES(?, ?, ?, ?, ?);")) {
					
					pstmt.setLong(1, lID);
					pstmt.setString(2, sID);
					pstmt.setString(3, type);
					pstmt.setString(4, name);
					pstmt.setString(5, uuid);
					
					pstmt.executeUpdate();
					
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean addInvoice(JSONObject invoice) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("INSERT INTO t_invoice"+
				" (expect, confirm, complete, amount, tax, comment, project, invoice, company)"+
				" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
				if (invoice.has("expect")) {
					pstmt.setString(1, invoice.getString("expect"));
				} else {
					pstmt.setNull(1, Types.NULL);
				}
				
				if (invoice.has("confirm")) {
					pstmt.setBoolean(2, invoice.getBoolean("confirm"));
				} else {
					pstmt.setNull(2, Types.NULL);
				}
				
				if (invoice.has("complete")) {
					pstmt.setString(3, invoice.getString("complete"));
				} else {
					pstmt.setNull(3, Types.NULL);
				}
				
				if (invoice.has("invoice")) {
					pstmt.setLong(8, invoice.getLong("invoice"));
				} else {
					pstmt.setNull(8, Types.NULL);
				}
				
				if (invoice.has("company")) {
					pstmt.setString(9, invoice.getString("company"));
				} else {
					pstmt.setNull(9, Types.NULL);
				}
				
				pstmt.setInt(4, invoice.getInt("amount"));
				pstmt.setInt(5, invoice.getInt("tax"));
				pstmt.setString(6, invoice.getString("comment"));
				pstmt.setLong(7, invoice.getLong("project"));
				
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
				" (name, deposit, start, end, content, company, origin, manager)"+
				" VALUES(?, ?, ?, ?, ?, ?, ?, ?);")) {
				pstmt.setString(1, project.getString("name"));
				pstmt.setLong(2, project.getLong("deposit"));
				pstmt.setString(3, project.getString("start"));
				pstmt.setString(4, project.getString("end"));
				pstmt.setString(5, project.getString("content"));
				pstmt.setString(6, project.getString("company"));
				pstmt.setString(7, project.getString("origin"));
				pstmt.setLong(8, project.getLong("manager"));
				
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
	public byte [] download(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT file" + 
				" FROM t_file"+
				" WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return Files.readAllBytes(this.attach.resolve(rs.getString(1)));
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
				
				try (ResultSet rs = stmt.executeQuery("SELECT id, type, tid, sid, name"+
					" FROM t_file;")) {
					while (rs.next()) {
						file = new JSONObject()
							.put("id", rs.getLong(1))
							.put("type", rs.getString(2))
							.put("tid", rs.getLong(3))
							.put("sid", rs.getString(4))
							.put("name", rs.getString(5));
						
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
	public JSONObject getFile(long id, String type) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT id, name" + 
				" FROM t_file"+
				" WHERE tid=? AND type=?;")) {
				pstmt.setLong(1, id);
				pstmt.setString(2, type);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					JSONObject fileData = new JSONObject();
					
					while (rs.next()) {
						fileData.put(Long.toString(rs.getLong(1)), new JSONObject()
							.put("id", rs.getLong(1))
							.put("name", rs.getString(2)));
					}
					
					return fileData;
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public JSONObject getFile(String id, String type) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT id, name" + 
				" FROM t_file"+
				" WHERE sid=? AND type=?;")) {
				pstmt.setString(1, id);
				pstmt.setString(2, type);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					JSONObject fileData = new JSONObject();
					
					while (rs.next()) {
						fileData.put(Long.toString(rs.getLong(1)), new JSONObject()
							.put("id", rs.getLong(1))
							.put("name", rs.getString(2)));
					}
					
					return fileData;
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
					" I.id, expect, confirm, complete, amount, tax, comment, project, P.name, invoice, C.id, C.name"+
					" FROM t_invoice AS I"+
					" LEFT JOIN t_project AS P ON I.project=P.id"+
					" LEFT JOIN t_company AS C ON I.company=C.id"+
					";")) {
					JSONObject
						invoiceData = new JSONObject(),
						invoice;
					String value;
					long id;
					boolean confirm;
					
					while (rs.next()) {
						invoice = new JSONObject()
							.put("id", rs.getLong(1))
							.put("amount", rs.getInt(5))
							.put("tax", rs.getInt(6))
							.put("comment", rs.getString(7))
							.put("project", rs.getLong(8))
							.put("pName", rs.getString(9));
						
						value = rs.getString(2);
						
						if (!rs.wasNull()) {
							invoice.put("expect", value);
						}
						
						confirm = rs.getBoolean(3);
						
						if (!rs.wasNull()) {
							invoice.put("confirm", confirm);
						}
						
						value = rs.getString(4);
						
						if (!rs.wasNull()) {
							invoice.put("complete", value);
						}
						
						id = rs.getLong(10);
						
						if (!rs.wasNull()) {
							invoice.put("invoice", id);
						}
						
						value = rs.getString(11);
						
						if (!rs.wasNull()) {
							invoice.put("company", value);
						}
						
						value = rs.getString(12);
						
						if (!rs.wasNull()) {
							invoice.put("cName", value);
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
	public JSONObject getInvoice(int type, int status, int date) {
		try (Connection c = this.connPool.getConnection()) {
			String condition1, condition2, condition3, statement;
			
			switch (type) {
			case 1:
				condition1 = "invoice IS NULL";
				break;
			case 2:
				condition1 = "invoice IS NOT NULL";
				break;
			default:
				condition1 = "TRUE";
			}
			
			switch (status) {
			case 1:
				condition2 = "confirm IS NULL";
				break;
			case 2:
				condition2 = "confirm=FALSE";
				break;
			case 3:
				condition2 = "confirm=TRUE AND complete IS NULL";
				break;
			case 4:
				condition2 = "complete IS NOT NULL";
				break;
			default:
				condition2 = "TRUE";
			}
			
			if (date == 0) {
				condition3 = "TRUE";
			} else {
				Calendar cal = Calendar.getInstance();
				
				cal.add(Calendar.MONTH, date);
				
				if (date > 0){
					condition3 = String.format("expect<'%s'", new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
				} else {
					condition3 = String.format("expect>'%s'", new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
					
				}
			}
			
			statement = String.format("SELECT"+
				" I.id, expect, confirm, complete, amount, tax, comment, project, P.name, invoice, C.name"+
				" FROM t_invoice AS I"+
				" LEFT JOIN t_project AS P ON I.project=P.id"+
				" LEFT JOIN t_company AS C ON I.company=C.id"+
				" WHERE %s AND %s AND expect IS NOT NULL AND %s"+
				";",
				condition1, condition2, condition3);
			
			try (PreparedStatement pstmt = c.prepareStatement(statement)) {
				try (ResultSet rs = pstmt.executeQuery()) {
					JSONObject
						invoiceData = new JSONObject(),
						invoice;
					String value;
					long id;
					boolean confirm;
					
					while (rs.next()) {
						invoice = new JSONObject()
							.put("id", rs.getLong(1))
							.put("amount", rs.getInt(5))
							.put("tax", rs.getInt(6))
							.put("comment", rs.getString(7))
							.put("project", rs.getLong(8))
							.put("pName", rs.getString(9));
						
						value = rs.getString(2);
						
						if (!rs.wasNull()) {
							invoice.put("expect", value);
						}
						
						confirm = rs.getBoolean(3);
						
						if (!rs.wasNull()) {
							invoice.put("confirm", confirm);
						}
						
						value = rs.getString(4);
						
						if (!rs.wasNull()) {
							invoice.put("complete", value);
						}
						
						id = rs.getLong(10);
						
						if (!rs.wasNull()) {
							invoice.put("invoice", id);
						}
						
						value = rs.getString(11);
						
						if (!rs.wasNull()) {
							invoice.put("cName", value);
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
				" I.id, expect, confirm, complete, amount, tax, comment, invoice, C.id, C.name"+
				" FROM t_invoice AS I"+
				" LEFT JOIN t_company AS C ON I.company=C.id"+
				" WHERE project=?"+
				";")) {
				pstmt.setLong(1, project);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					JSONObject
						invoiceData = new JSONObject(),
						invoice;
					String value;
					long id;
					boolean confirm;
					
					while (rs.next()) {
						invoice = new JSONObject()
							.put("id", rs.getLong(1))
							.put("amount", rs.getInt(5))
							.put("tax", rs.getInt(6))
							.put("comment", rs.getString(7));
						
						value = rs.getString(2);
						
						if (!rs.wasNull()) {
							invoice.put("expect", value);
						}
						
						confirm = rs.getBoolean(3);
						
						if (!rs.wasNull()) {
							invoice.put("confirm", confirm);
						}
						
						value = rs.getString(4);
						
						if (!rs.wasNull()) {
							invoice.put("complete", value);
						}
						
						id = rs.getLong(8);
						
						if (!rs.wasNull()) {
							invoice.put("invoice", id);
						}
						
						value = rs.getString(9);
						
						if (!rs.wasNull()) {
							invoice.put("company", value);
						}
						
						value = rs.getString(10);
						
						if (!rs.wasNull()) {
							invoice.put("cName", value);
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
				try (ResultSet rs = stmt.executeQuery("SELECT M.id, M.name, mobile, email, C.name"+
					" FROM t_manager AS M"+
					" LEFT JOIN t_company AS C"+
					" ON M.company=C.id"+
					";")) {
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
					" P.id, P.name, deposit, start, end, content, C.name, C2.name, manager"+
					" FROM t_project AS P"+
					" LEFT JOIN t_company AS C"+
					" ON P.company=C.id"+
					" LEFT JOIN t_company AS C2"+
					" ON P.origin=C2.id"+
					";")) {
					JSONObject
						prjData = new JSONObject(),
						project;
					
					while (rs.next()) {
						project = new JSONObject()
							.put("id", rs.getLong(1))
							.put("name", rs.getString(2))
							.put("deposit", rs.getLong(3))
							.put("start", rs.getString(4))
							.put("end", rs.getString(5))
							.put("content", rs.getString(6))
							.put("company", rs.getString(7))
							.put("origin", rs.getString(8))
							.put("manager", rs.getLong(9));
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
				" name, deposit, start, end, content, company, origin, manager"+
				" FROM t_project"+
				" WHERE id=?"+
				";")) {
				pstmt.setLong(1, id);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return new JSONObject()
							.put("id", id)
							.put("name", rs.getString(1))
							.put("deposit", rs.getLong(2))
							.put("start", rs.getString(3))
							.put("end", rs.getString(4))
							.put("content", rs.getString(5))
							.put("company", rs.getString(6))
							.put("origin", rs.getString(7))
							.put("manager", rs.getString(8));
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
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS t_file"+
					" (id BIGINT PRIMARY KEY AUTO_INCREMENT"+
					", tid BIGINT NOT NULL"+ // project 등 id가 숫자인 것
					", sid VARCHAR NOT NULL"+ // company 등 id가 문자인 것
					", type VARCHAR NOT NULL"+
					", name VARCHAR NOT NULL"+
					", file VARCHAR NOT NULL"+
					");");
			}
			/**END**/
			
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("ALTER TABLE IF EXISTS t_file ADD COLUMN IF NOT EXISTS sid VARCHAR NOT NULL DEFAULT '';");
			}
			
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
			try (Statement stmt = c.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS t_project ("+
					"id BIGINT PRIMARY KEY AUTO_INCREMENT"+
					", name VARCHAR NOT NULL"+
					", deposit bigint NOT NULL"+
					", start date NOT NULL DEFAULT CURRENT_DATE"+
					", end date NOT NULL DEFAULT CURRENT_DATE"+
					", content VARCHAR NOT NULL DEFAULT ''"+
					", company VARCHAR NOT NULL"+
					", origin VARCHAR NOT NULL"+
					", manager BIGINT NOT NULL"+
					", CONSTRAINT FK_COMPANY_PROJECT FOREIGN KEY (company, origin) REFERENCES t_company(id, id)"+
					", CONSTRAINT FK_MANAGER_PROJECT FOREIGN KEY (manager) REFERENCES t_manager(id)"+
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
					", confirm BOOLEAN DEFAULT NULL"+
					", complete DATE DEFAULT NULL"+
					", amount INTEGER NOT NULL DEFAULT 0"+
					", tax INTEGER NOT NULL DEFAULT 0"+
					", comment INTEGER NOT NULL DEFAULT ''"+
					", project BIGINT NOT NULL"+
					", invoice BIGINT DEFAULT NULL"+
					", company VARCHAR DEFAULT NULL"+
					", CONSTRAINT FK_PROJECT_INVOICE FOREIGN KEY (project) REFERENCES t_project(id)"+
					", CONSTRAINT FK_INVOICE_INVOICE FOREIGN KEY (invoice) REFERENCES t_invoice(id)"+
					", CONSTRAINT FK_INVOICE_COMPANY FOREIGN KEY (company) REFERENCES t_company(id)"+
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
	public boolean removeFile(long id) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("SELECT"+
				" file"+
				" FROM t_file"+
				" WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						Files.delete(this.attach.resolve(rs.getString(1)));
					}
				}
			}
			
			try (PreparedStatement pstmt = c.prepareStatement("DELETE"+
				" FROM t_file"+
				" WHERE id=?;")) {
				pstmt.setLong(1, id);
				
				pstmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
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
	public boolean setInvoice(long id, JSONObject invoice) {
		try (Connection c = this.connPool.getConnection()) {
			try (PreparedStatement pstmt = c.prepareStatement("UPDATE t_invoice"+
				" SET expect=?,"+
				" confirm=?,"+
				" complete=?,"+
				" amount=?,"+
				" tax=?,"+
				" comment=?,"+
				" company=?"+
				" WHERE id=?;")) {
				if (invoice.has("expect")) {
					pstmt.setString(1, invoice.getString("expect"));
				} else {
					pstmt.setNull(1, Types.NULL);
				}
				
				if (invoice.has("confirm")) {
					pstmt.setBoolean(2, invoice.getBoolean("confirm"));
				} else {
					pstmt.setNull(2, Types.NULL);
				}
				
				if (invoice.has("complete")) {
					pstmt.setString(3, invoice.getString("complete"));
				} else {
					pstmt.setNull(3, Types.NULL);
				}
				
				if (invoice.has("company")) {
					pstmt.setString(7, invoice.getString("company"));
				} else {
					pstmt.setNull(7, Types.NULL);
				}
				
				pstmt.setInt(4, invoice.getInt("amount"));
				pstmt.setInt(5, invoice.getInt("tax"));
				pstmt.setString(6, invoice.getString("comment"));
				pstmt.setLong(8, id);
				
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
				" deposit=?,"+
				" start=?,"+
				" end=?,"+
				" content=?,"+
				" company=?,"+
				" origin=?"+
				" WHERE id=?"+
				";")) {
				
				pstmt.setString(1, project.getString("name"));
				pstmt.setLong(2, project.getLong("deposit"));
				pstmt.setString(3, project.getString("start"));
				pstmt.setString(4, project.getString("end"));
				pstmt.setString(5, project.getString("content"));
				pstmt.setString(6, project.getString("company"));
				pstmt.setString(7, project.getString("origin"));
				pstmt.setLong(8, id);
				
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
