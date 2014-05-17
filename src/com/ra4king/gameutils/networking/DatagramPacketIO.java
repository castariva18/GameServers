package com.ra4king.gameutils.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * A blocking or non-blocking UDP wrapper that uses NIO.
 * @author Roi Atalla
 */
public class DatagramPacketIO implements PacketIO {
	private DatagramChannel channel;
	private InetSocketAddress address;
	private ByteBuffer in, out;
	
	public DatagramPacketIO(String address, int port, boolean isBlocking) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking);
	}
	
	public DatagramPacketIO(String address, int port, int bufferSize) throws IOException {
		this(new InetSocketAddress(address,port),true,bufferSize);
	}
	
	public DatagramPacketIO(String address, int port, boolean isBlocking, int bufferSize) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking,bufferSize);
	}
	
	public DatagramPacketIO(InetSocketAddress address) throws IOException {
		this(address,true);
	}
	
	public DatagramPacketIO(InetSocketAddress address, boolean isBlocking) throws IOException {
		this(address,isBlocking,8192);
	}
	
	public DatagramPacketIO(InetSocketAddress address, int bufferSize) throws IOException {
		this(address,true,bufferSize);
	}
	
	public DatagramPacketIO(InetSocketAddress address, boolean isBlocking, int bufferSize) throws IOException {
		this(DatagramChannel.open(),address,isBlocking,bufferSize);
	}
	
	public DatagramPacketIO(DatagramChannel channel) throws IOException {
		this(channel,null);
	}
	
	public DatagramPacketIO(DatagramChannel channel, boolean isBlocking) throws IOException {
		this(channel,null,isBlocking);
	}
	
	public DatagramPacketIO(DatagramChannel channel, int bufferSize) throws IOException {
		this(channel,true,bufferSize);
	}
	
	public DatagramPacketIO(DatagramChannel channel, boolean isBlocking, int bufferSize) throws IOException {
		this(channel,null,isBlocking,bufferSize);
	}
	
	public DatagramPacketIO(DatagramChannel channel, InetSocketAddress address) throws IOException {
		this(channel,address,true);
	}
	
	public DatagramPacketIO(DatagramChannel channel, InetSocketAddress address, boolean isBlocking) throws IOException {
		this(channel,address,isBlocking,8192);
	}
	
	public DatagramPacketIO(DatagramChannel channel, InetSocketAddress address, int bufferSize) throws IOException {
		this(channel,address,true,bufferSize);
	}
	
	public DatagramPacketIO(DatagramChannel channel, InetSocketAddress address, boolean isBlocking, int bufferSize) throws IOException {
		if(!channel.isOpen())
			throw new IllegalStateException("channel is not open.");
		
		this.channel = channel;
		this.address = address;
		
		channel.configureBlocking(isBlocking);
		
		if(address != null)
			channel.connect(address);
		
		setBufferSize(bufferSize);
	}
	
	public Packet read() throws IOException {
		final ByteBuffer in = this.in;
		
		in.clear();
		
		SocketAddress address = channel.receive(in);
		
		if(address == null)
			return null;
		
		in.flip();
		
		return new Packet((ByteBuffer)ByteBuffer.allocate(in.remaining()).put(in).flip(),address);
	}
	
	public boolean write(Packet packet) throws IOException {
		final ByteBuffer out = this.out;
		
		out.clear();
		
		ByteBuffer data = packet.getData();
		
		data.flip();
		out.put(data);
		out.flip();
		
		SocketAddress sa = (packet.getAddress() == null ? address : packet.getAddress());
		
		if(sa == null)
			throw new IOException("No address specified.");
		
		return channel.send(out, sa) > 0;
	}
	
	public void write(Packet packet, SocketAddress address) throws IOException {
		write(packet.setAddress(address));
	}
	
	public int getBufferSize() {
		return in.capacity();
	}
	
	public void setBufferSize(int bufferSize) {
		in = ByteBuffer.allocateDirect(bufferSize);
		out = ByteBuffer.allocateDirect(bufferSize);
	}
	
	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}
	
	public InetSocketAddress getSocketAddress() {
		return address;
	}
	
	public boolean isConnected() {
		throw new UnsupportedOperationException("UDP has no form of 'connection'");
	}
	
	public void setBlocking(boolean isBlocking) throws IOException {
		channel.configureBlocking(isBlocking);
	}
	
	public boolean isBlocking() {
		return channel.isBlocking();
	}
	
	public void close() throws IOException {
		channel.close();
	}
}
