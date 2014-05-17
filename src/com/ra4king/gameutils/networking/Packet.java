package com.ra4king.gameutils.networking;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * A Packet is a container of any type of data.
 * @author Roi Atalla
 */
public class Packet {
	private SocketAddress address;
	private ByteBuffer data;
	
	/**
	 * Initializes this object. The InetSocketAddress is set to null.
	 * @param messages A variable number of data to be written to this Packet.
	 */
	public Packet() {
		this(null);
	}
	
	/**
	 * Initializes this object.
	 * @param address The address to connect to or which is received from.
	 * @param messages A variable number of data to be written to this Packet.
	 */
	public Packet(SocketAddress address) {
		this(ByteBuffer.allocate(64),address);
	}
	
	Packet(ByteBuffer buffer, SocketAddress address) {
		this.data = buffer;
		this.address = address;
	}
	
	ByteBuffer getData() {
		return data;
	}
	
	/**
	 * Reads the next byte.
	 * @return The next byte.
	 */
	public byte readByte() {
		return data.get();
	}
	
	/**
	 * Reads the next short.
	 * @return The next short.
	 */
	public short readShort() {
		return data.getShort();
	}
	
	/**
	 * Reads the next char.
	 * @return The next char.
	 */
	public char readChar() {
		return data.getChar();
	}
	
	/**
	 * Reads the next integer.
	 * @return The next integer.
	 */
	public int readInt() {
		return data.getInt();
	}
	
	/**
	 * Reads the next long.
	 * @return The next long.
	 */
	public long readLong() {
		return data.getLong();
	}
	
	/**
	 * Reads the next float.
	 * @return The next float.
	 */
	public float readFloat() {
		return data.getFloat();
	}
	
	/**
	 * Reads the next double.
	 * @return The next double.
	 */
	public double readDouble() {
		return data.getDouble();
	}
	
	/**
	 * Reads the next boolean.
	 * @return The next boolean.
	 */
	public boolean readBoolean() {
		return readByte() == (byte)1;
	}
	
	/**
	 * Reads the next String.
	 * @return The next String.
	 */
	public String readString() {
		byte[] b = new byte[readInt()];
		
		data.get(b);
		
		try{
			return new String(b,"UTF-8");
		}
		catch(Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Writes a byte.
	 * @param b The byte.
	 * @return Returns this.
	 */
	public Packet writeByte(byte ... b) {
		ensureSize(b.length);
		
		data.put(b);
		
		return this;
	}
	
	/**
	 * Writes a short.
	 * @param s The short.
	 * @return Returns this.
	 */
	public Packet writeShort(short ... ss) {
		ensureSize(ss.length * 2);
		
		for(short s : ss)
			data.putShort(s);
		
		return this;
	}
	
	/**
	 * Writes a char.
	 * @param c The char.
	 * @return Returns this.
	 */
	public Packet writeChar(char ... cs) {
		ensureSize(cs.length * 2);
		
		for(char c : cs)
			data.putChar(c);
		
		return this;
	}
	
	/**
	 * Writes an integer.
	 * @param i The integer.
	 * @return Returns this.
	 */
	public Packet writeInt(int ... is) {
		ensureSize(is.length * 4);
		
		for(int i : is)
			data.putInt(i);
		
		return this;
	}
	
	/**
	 * Writes a long.
	 * @param l The long.
	 * @return Returns this.
	 */
	public Packet writeLong(long ... ls) {
		ensureSize(ls.length * 8);
		
		for(long l : ls)
			data.putLong(l);
		
		return this;
	}
	
	/**
	 * Writes a float.
	 * @param f The float.
	 * @return Returns this.
	 */
	public Packet writeFloat(float ... fs) {
		ensureSize(fs.length * 4);
		
		for(float f : fs)
			data.putFloat(f);
		
		return this;
	}
	
	/**
	 * Writes a double.
	 * @param d The double.
	 * @return Returns this.
	 */
	public Packet writeDouble(double ... ds) {
		ensureSize(ds.length * 8);
		
		for(double d : ds)
			data.putDouble(d);
		
		return this;
	}
	
	/**
	 * Writes a boolean.
	 * @param b The boolean.
	 * @return Returns this.
	 */
	public Packet writeBoolean(boolean ... bs) {
		ensureSize(bs.length);
		
		for(boolean b : bs)
			data.put(b ? (byte)1 : (byte)0);
		
		return this;
	}
	
	/**
	 * Writes a String.
	 * @param s The String.
	 * @return Returns this.
	 */
	public Packet writeString(String ... ss) {
		for(String s : ss) {
			try{
				byte[] b = s.getBytes("UTF-8");
				
				ensureSize(b.length + 4);
				
				data.putInt(b.length);
				
				data.put(b);
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
		}
		
		return this;
	}
	
	/**
	 * Writes a Packet.
	 * @param p The Packet.
	 * @return Returns this.
	 */
	public Packet writePacket(Packet p) {
		data.put(p.data);
		return this;
	}
	
	/**
	 * Absolute get method
	 * @param index The index of the object
	 * @return The object at the specified index.
	 */
	public byte get(int index) {
		return data.get(index);
	}
	
	/**
	 * Absolute set method
	 * @param index The index of the object
	 * @param o The object to set 
	 * @return Returns this.
	 */
	public Packet set(int index, byte b) {
		data.put(index, b);
		return this;
	}
	
	public int getPosition() {
		return data.position();
	}
	
	public void setPosition(int pos) {
		data.position(pos);
	}
	
	/**
	 * Resets the position but does not clear the buffer.
	 * @return Returns this.
	 */
	public Packet reset() {
		data.rewind();
		return this;
	}
	
	/**
	 * Clears the entire buffer and resets the Packet.
	 * @return Returns this.
	 */
	public Packet clear() {
		data.clear();
		return this;
	}
	
	public int getRemaining() {
		return data.remaining();
	}
	
	public boolean hasMore() {
		return getRemaining() > 0;
	}
	
	/**
	 * Returns the address to connect to or which is received from.
	 * @return The address to connect to or which is received from.
	 */
	public SocketAddress getAddress() {
		return address;
	}
	
	/**
	 * Sets the address to connect to or which is received from.
	 * @param address The new address to connect to or the one to received from.
	 * @return Returns this.
	 */
	public Packet setAddress(SocketAddress address) {
		if(address == null)
			throw new IllegalArgumentException("address cannot be null");
		
		this.address = address;
		
		return this;
	}
	
	private void ensureSize(int size) {
		if(data.capacity() >= data.position() + size)
			return;
		ByteBuffer temp = ByteBuffer.allocate((data.position() + size + 1) * 3 / 2);
		data.flip();
		temp.put(data);
		data = temp;
	}
}