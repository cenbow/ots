package com.mk.orm.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.mysql.jdbc.JDBC4PreparedStatement;
import com.mysql.jdbc.StatementImpl;

public class PubStatement implements InvocationHandler {

	private JDBC4PreparedStatement statement = null;

	private StatementImpl statementImpl = null;

	public PubStatement(JDBC4PreparedStatement statement) {
		this.statement = statement;
	}

	public PubStatement(StatementImpl statementImpl) {
		this.statementImpl = statementImpl;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().startsWith("execute")) {
			if (this.getStatement() != null) {
				System.out.println(this.getStatement().asSql());
			} else {
				if (args != null) {
					System.out.println(args[0]);
				}
			}
		}
		if (this.getStatement() != null) {
			return method.invoke(this.getStatement(), args);
		}
		return method.invoke(this.getStatementImpl(), args);
	}

	private JDBC4PreparedStatement getStatement() {
		return this.statement;
	}

	private StatementImpl getStatementImpl() {
		return this.statementImpl;
	}

}
