/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.thrift.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around ServerSocket for Thrift.
 *
 */
public class TServerSocket extends TServerTransport {

	private static final Logger LOGGER = LoggerFactory.getLogger(TServerSocket.class.getName());

	/**
	 * Underlying ServerSocket object
	 */
	private ServerSocket serverSocket_ = null;

	/**
	 * Timeout for client sockets from accept
	 */
	private int clientTimeout_ = 0;

	public static class ServerSocketTransportArgs extends AbstractServerTransportArgs<ServerSocketTransportArgs> {
		ServerSocket serverSocket;

		public ServerSocketTransportArgs serverSocket(ServerSocket serverSocket) {
			this.serverSocket = serverSocket;
			return this;
		}
	}

	/**
	 * Creates a server socket from underlying socket object
	 */
	public TServerSocket(ServerSocket serverSocket) throws TTransportException {
		this(serverSocket, 0);
	}

	/**
	 * Creates a server socket from underlying socket object
	 */
	public TServerSocket(ServerSocket serverSocket, int clientTimeout) throws TTransportException {
		this(new ServerSocketTransportArgs().serverSocket(serverSocket).clientTimeout(clientTimeout));
	}

	/**
	 * Creates just a port listening server socket
	 */
	public TServerSocket(int port) throws TTransportException {
		this(port, 0);
	}

	/**
	 * Creates just a port listening server socket
	 */
	public TServerSocket(int port, int clientTimeout) throws TTransportException {
		this(new InetSocketAddress(port), clientTimeout);
	}

	public TServerSocket(InetSocketAddress bindAddr) throws TTransportException {
		this(bindAddr, 0);
	}

	public TServerSocket(InetSocketAddress bindAddr, int clientTimeout) throws TTransportException {
		this(new ServerSocketTransportArgs().bindAddr(bindAddr).clientTimeout(clientTimeout));
	}

	public TServerSocket(ServerSocketTransportArgs args) throws TTransportException {
		this.clientTimeout_ = args.clientTimeout;
		if (args.serverSocket != null) {
			this.serverSocket_ = args.serverSocket;
			return;
		}
		try {
			// Make server socket
			this.serverSocket_ = new ServerSocket();
			// Prevent 2MSL delay problem on server restarts
			this.serverSocket_.setReuseAddress(true);
			// Bind to listening port
			this.serverSocket_.bind(args.bindAddr, args.backlog);
		} catch (IOException ioe) {
			this.close();
			throw new TTransportException("Could not create ServerSocket on address " + args.bindAddr.toString() + ".", ioe);
		}
	}

	@Override
	public void listen() throws TTransportException {
		// Make sure not to block on accept
		if (this.serverSocket_ != null) {
			try {
				this.serverSocket_.setSoTimeout(0);
			} catch (SocketException sx) {
				TServerSocket.LOGGER.error("Could not set socket timeout.", sx);
			}
		}
	}

	@Override
	protected TSocket acceptImpl() throws TTransportException {
		if (this.serverSocket_ == null) {
			throw new TTransportException(TTransportException.NOT_OPEN, "No underlying server socket.");
		}
		try {
			Socket result = this.serverSocket_.accept();
			TSocket result2 = new TSocket(result);
			result2.setTimeout(this.clientTimeout_);
			return result2;
		} catch (IOException iox) {
			throw new TTransportException(iox);
		}
	}

	@Override
	public void close() {
		if (this.serverSocket_ != null) {
			try {
				this.serverSocket_.close();
			} catch (IOException iox) {
				TServerSocket.LOGGER.warn("Could not close server socket.", iox);
			}
			this.serverSocket_ = null;
		}
	}

	@Override
	public void interrupt() {
		// The thread-safeness of this is dubious, but Java documentation
		// suggests
		// that it is safe to do this from a different thread context
		this.close();
	}

	public ServerSocket getServerSocket() {
		return this.serverSocket_;
	}
}
