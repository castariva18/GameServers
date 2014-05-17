package com.ra4king.gameservers.jdoodlejump.multiplayer.game.util;

public class FastMath {
	public static final double PI = Math.PI;
	public static final double E = Math.E;
	
	private static final double RAD = PI/180.0;
	private static final double DEG = 180.0/PI;
	private static final double radToIndex, degToIndex;
	private static final int SIN_MASK = ~(-1 << 12);
	private static final double[] sin, cos;
	
	private static final int ATAN2_COUNT = (~(-1 << (7 << 1))) + 1;
	private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
	private static final double[] atan2 = new double[ATAN2_COUNT];
	
	static {
		final int SIN_COUNT = SIN_MASK + 1;
		
		final double radFull    = PI * 2.0;
		radToIndex = SIN_COUNT/radFull;
		degToIndex = SIN_COUNT/360.0;
		
		sin = new double[SIN_COUNT];
		cos = new double[SIN_COUNT];
		
		for(int i = 0; i < SIN_COUNT; i++) {
			sin[i] = Math.sin((i + 0.5)/SIN_COUNT * radFull);
			cos[i] = Math.cos((i + 0.5)/SIN_COUNT * radFull);
		}
		
		for(int i = 0; i < 360; i += 90) {
			sin[(int)(i * degToIndex) & SIN_MASK] = Math.sin(i * Math.PI/180.0);
			cos[(int)(i * degToIndex) & SIN_MASK] = Math.cos(i * Math.PI/180.0);
		}
		
		for(int i = 0; i < ATAN2_DIM; i++) {
			for (int j = 0; j < ATAN2_DIM; j++) {
				double x0 = (double)i/ATAN2_DIM;
				double y0 = (double)j/ATAN2_DIM;
				
				atan2[j * ATAN2_DIM + i] = Math.atan2(y0, x0);
			}
		}
	}
	
	public static final double toDegrees(double rad) {
		return rad*DEG;
	}
	
	public static final double toRadians(double deg) {
		return deg*RAD;
	}
	
	public static final double sin(double rad) {
		return sin[(int)(rad * radToIndex) & SIN_MASK];
	}
	
	public static final double cos(double rad) {
		return cos[(int)(rad * radToIndex) & SIN_MASK];
	}
	
	public static final double sinDeg(double deg) {
		return sin[(int)(deg * degToIndex) & SIN_MASK];
	}
	
	public static final double cosDeg(double deg) {
		return cos[(int) (deg * degToIndex) & SIN_MASK];
	}
	
	public static final double atan2Deg(double y, double x) {
		return atan2(y, x) * DEG;
	}
	
	public static final double atan2(double y, double x) {
		double add, mul;
		
		if (x < 0.0) {
			if (y < 0.0) {
				x = -x;
				y = -y;
				
				mul = 1.0;
			}
			else {
				x = -x;
				mul = -1.0;
			}
			
			add = -PI;
		}
		else {
			if (y < 0.0) {
				y = -y;
				mul = -1.0;
			}
			else
				mul = 1.0;
			
			add = 0.0;
		}
		
		double invDiv = (ATAN2_DIM-1) / ((x < y) ? y : x);
		
		int xi = (int) (x * invDiv);
		int yi = (int) (y * invDiv);
		
		return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
	}
}