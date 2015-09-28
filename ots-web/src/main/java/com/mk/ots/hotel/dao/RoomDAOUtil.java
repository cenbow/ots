package com.mk.ots.hotel.dao;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mk.orm.plugin.bean.Db;
import com.mk.orm.plugin.bean.IAtom;

public class RoomDAOUtil {

	private static RoomDAOUtil instance = new RoomDAOUtil();

	private ExecutorService service = null;

	private RoomDAOUtil() {
		this.init();
	}

	public static RoomDAOUtil getInstance() {
		return instance;
	}

	public void addRoomSubmitGroup(RoomSubmitSqlGroup group) {
		this.getService().submit(new SubmitTask(group));
	}

	private void init() {
		this.service = Executors.newFixedThreadPool(10);
	}

	public ExecutorService getService() {
		return service;
	}

	private class SubmitTask implements Runnable {

		private RoomSubmitSqlGroup group = null;

		public SubmitTask(RoomSubmitSqlGroup group) {
			this.group = group;
		}

		@Override
		public void run() {
			final RoomSubmitSqlGroup group = this.getGroup();
			boolean succeed = Db.tx(new IAtom() {
				public boolean run() throws SQLException {
				    /*
                     * String sql = "insert into user(name, cash) values(?, ?)";
                     * int[] result = Db.batch(sql, new Object[][]{{"James", 888}, {"zhanjin", 888}},1000);
                     * 客单状态（RE预定中,IN在住,RX预订取消,OK退房,IX入住取消,PM挂账）
                     */
                    int[] result = Db.batch(group.getInSql(), group.getInParams(), 1000);
					return true;
				}
			});
		}

		private RoomSubmitSqlGroup getGroup() {
			return group;
		}

	}

}
