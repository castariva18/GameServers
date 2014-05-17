package com.ra4king.gameutils.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * A Non-Blocking TCP wrapper.
 * @author Roi Atalla
 */
public class SocketPacketIO implements PacketIO {
	private SocketChannel channel;
	private ByteBuffer in, out;
	private boolean isClosed = true;
	
	public SocketPacketIO(String address, int port) throws IOException {
		this(address,port,true);
	}
	
	public SocketPacketIO(String address, int port, boolean isBlocking) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking);
	}
	
	public SocketPacketIO(String address, int port, boolean isBlocking, int bufferSize) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking,bufferSize);
	}
	
	public SocketPacketIO(String address, int port, int bufferSize) throws IOException {
		this(address,port,true,bufferSize);
	}
	
	public SocketPacketIO(SocketAddress address, boolean isBlocking) throws IOException {
		this(SocketChannel.open(address),isBlocking);
	}
	
	public SocketPacketIO(SocketAddress address, boolean isBlocking, int bufferSize) throws IOException {
		this(SocketChannel.open(address),isBlocking,bufferSize);
	}
	
	public SocketPacketIO(SocketChannel channel) throws IOException {
		this(channel,true);
	}
	
	public SocketPacketIO(SocketChannel channel, boolean isBlocking) throws IOException {
		this(channel,isBlocking,8192);
	}
	
	public SocketPacketIO(SocketChannel channel, int bufferSize) throws IOException {
		this(channel,true,bufferSize);
	}
	
	public SocketPacketIO(SocketChannel channel, boolean isBlocking, int bufferSize) throws IOException {
		if(!channel.isOpen())
			throw new IllegalStateException("channel is not open.");
		
		this.channel = channel;
		
		channel.socket().setTcpNoDelay(true);
		
		setBufferSize(bufferSize);
		
		isClosed = false;
	}
	
	public Packet read() throws IOException {
		final ByteBuffer in = this.in;
		
		if(!isConnected())
			throw new IOException("Connection is closed.");
		
		if(in.position() < 4 || in.getInt(0) > in.position()-4) {
			if(isBlocking()) {
				do {
					if(channel.read(in) <= 0) {
						isClosed = true;
						throw new IOException("Connection is closed.");
					}
				} while(in.position() < 4 || in.getInt(0) > in.position()-4);
			}
			else {
				int read = channel.read(in);
				
				if(read == -1) {
					isClosed = true;
					throw new IOException("Connection is closed.");
				}
				
				if(read == 0 || in.position() < 4 || in.getInt(0) > in.position()-4)
					return null;
			}
		}
		
		in.flip();
		
		int len = in.getInt();
		
		if(in.remaining() < len) {
			in.clear();
			throw new IOException("Internal Error!! GOT LEN OF: " + len);
		}
		
		byte[] bytes = new byte[len];
		in.get(bytes);
		
		Packet packet = new Packet(ByteBuffer.wrap(bytes),getSocketAddress());
		
		if(in.remaining() > 0)
			in.compact();
		else
			in.clear();
		
		return packet;
	}
	
	public boolean write(Packet packet) throws IOException {
		final ByteBuffer out = this.out;
		
		out.clear();
		
		ByteBuffer data = packet.getData();
		
		data.flip();
		out.putInt(data.remaining());
		out.put(data);
		
		out.flip();
		
		while(out.remaining() > channel.write(out));
		
		return true;
	}
	
	public int getBufferSize() {
		return in.capacity();
	}
	
	public void setBufferSize(int bufferSize) {
		in = ByteBuffer.allocateDirect(bufferSize);
		out = ByteBuffer.allocateDirect(bufferSize);
	}
	
	public boolean isBlocking() {
		return channel.isBlocking();
	}
	
	public void setBlocking(boolean isBlocking) throws IOException {
		channel.configureBlocking(isBlocking);
	}
	
	public void setSocketAddress(SocketAddress address) {
		try {
			channel.connect(address);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public InetSocketAddress getSocketAddress() {
		return (InetSocketAddress)channel.socket().getRemoteSocketAddress();
	}
	
	public boolean isConnected() {
		return !channel.socket().isClosed() && !isClosed;
	}
	
	public void close() throws IOException {
		try{
			channel.socket().shutdownInput();
			channel.socket().shutdownOutput();
		}
		catch(Exception exc) {}
		
		channel.close();
	}
}
