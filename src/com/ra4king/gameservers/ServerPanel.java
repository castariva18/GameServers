package com.ra4king.gameservers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class ServerPanel {
	public static void main(String args[]) {
		new ServerPanel();
	}
	
	private ArrayList<Server> servers;
	private JTabbedPane tabs;
	private int connectionNumber = 5000;
	
	public ServerPanel() {
		final JFrame frame = new JFrame("Server Panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		tabs = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		JMenuItem add = new JMenuItem("Add Server");
		JMenuItem remove = new JMenuItem("Remove Server");
		JMenuItem connNumber = new JMenuItem("Set Connections Until Clear");
		fileMenu.add(add);
		fileMenu.add(remove);
		fileMenu.add(connNumber);
		
		JMenu serverMenu = new JMenu("Server");
		JMenuItem start = new JMenuItem("Start");
		JMenuItem stop = new JMenuItem("Stop");
		JMenuItem clear = new JMenuItem("Clear");
		serverMenu.add(start);
		serverMenu.add(stop);
		serverMenu.add(clear);
		
		menuBar.add(fileMenu);
		menuBar.add(serverMenu);
		
		frame.setJMenuBar(menuBar);
		
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Server server;
				try{
					server = new Server(JOptionPane.showInputDialog("Class name:"));
				}
				catch(Exception exc) {
					return;
				}
				
				servers.add(server);
			}
		});
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(JOptionPane.showConfirmDialog(frame, "Are you sure you want to remove this server?","Remove",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION)
					return;
				
				servers.get(tabs.getSelectedIndex()).stop();
				servers.remove(servers.get(tabs.getSelectedIndex()));
				tabs.removeTabAt(tabs.getSelectedIndex());
				tabs.revalidate();
			}
		});
		connNumber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					String input = JOptionPane.showInputDialog(frame,"Enter number of connections:");
					if(input == null)
						return;
					
					connectionNumber = Integer.parseInt(input);
				}
				catch(Exception exc) {
					JOptionPane.showMessageDialog(frame, "Invalid number.");
					actionPerformed(ae);
				}
			}
		});
		
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				servers.get(tabs.getSelectedIndex()).start();
			}
		});
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				servers.get(tabs.getSelectedIndex()).stop();
			}
		});
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				servers.get(tabs.getSelectedIndex()).clear();
			}
		});
		
		servers = new ArrayList<Server>();
		loadFromServerFile();
		
		frame.add(tabs);
		
		frame.setSize(600,600);
		
		Point p = readLastSavedLocation();
		if(p == null)
			frame.setLocationRelativeTo(null);
		else
			frame.setLocation(p);
		
		frame.setVisible(true);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				saveCurrentLocation(frame.getLocation());
			}
		});
	}
	
	private Point readLastSavedLocation() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream("serverdata/guidata.txt"),"UTF-8"));
			
			Scanner scanner = new Scanner(reader);
			Point point = new Point(scanner.nextInt(),scanner.nextInt());
			scanner.close();
			return point;
		}
		catch(Exception exc) {
			exc.printStackTrace();
			
			return null;
		}
		finally {
			try {
				reader.close();
			}
			catch(Exception exc) {}
		}
	}
	
	private void saveCurrentLocation(Point p) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("serverdata/guidata.txt"),"UTF-8"),true);
			
			writer.println(p.x);
			writer.println(p.y);
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		finally {
			try {
				writer.close();
			}
			catch(Exception exc) {}
		}
	}
	
	private void loadFromServerFile() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream("serverdata/serverlist.txt"),"UTF-8"));
			
			String s;
			while((s = reader.readLine()) != null)
					servers.add(new Server(s));
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		finally {
			try{
				reader.close();
			}
			catch(Exception exc) {}
		}
	}
	
	private String dataFolder = "serverdata";
	
	private class Server {
		private JTextPane log;
		private JScrollBar scrollBar;
		private Box statsPane;
		
		private PrintWriter writer;
		
		public Process server;
		public String className, folder, name;
		
		private HashMap<String,Integer> ips = new HashMap<String,Integer>();
		private long lastTime = System.currentTimeMillis();
		
		private boolean cleared;
		
		public Server(String className) {
			JPanel tab = new JPanel(new BorderLayout());
			log = new JTextPane();
			log.setEditable(false);
			log.setBackground(Color.black);
			
			final Style normal = log.addStyle("normal", null);
			StyleConstants.setForeground(normal, Color.white);
			StyleConstants.setFontFamily(normal, Font.MONOSPACED);
			StyleConstants.setFontSize(normal, 14);
			
			final Style error = log.addStyle("error", null);
			StyleConstants.setForeground(error, Color.red);
			StyleConstants.setBold(error, true);
			StyleConstants.setFontFamily(error, Font.MONOSPACED);
			StyleConstants.setFontSize(error, 14);
			
			JScrollPane scrollPane = new JScrollPane(log,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollBar = scrollPane.getVerticalScrollBar();
			
			tab.add(scrollPane);
			
			statsPane = Box.createHorizontalBox();
			
			statsPane.add(new JLabel("Today:"));
			
			statsPane.add(Box.createHorizontalStrut(20));
			
			JLabel label = new JLabel();
			label.setHorizontalAlignment(JLabel.CENTER);
			statsPane.add(label);
			
			statsPane.add(Box.createHorizontalStrut(20));
			
			label = new JLabel();
			label.setHorizontalAlignment(JLabel.CENTER);
			statsPane.add(label);
			
			statsPane.add(Box.createHorizontalStrut(20));
			
			label = new JLabel();
			label.setHorizontalAlignment(JLabel.CENTER);
			statsPane.add(label);
			
			tab.add(statsPane,BorderLayout.SOUTH);
			
			this.className = className;
			folder = className.substring(0,className.indexOf(".",className.indexOf(".")+1)).replace(".","/");
			name = className.substring(className.lastIndexOf(".")+1);
			
			tabs.addTab(name, tab);
			
			start();
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					server.destroy();
				}
			});
		}
		
		public void start() {
			if(server != null) {
				try {
					server.exitValue();
				}
				catch(Exception exc) {
					return;
				}
			}
			
			String codeBase = System.getProperty("user.dir") + "/";
			
			File f = new File(codeBase + dataFolder + "/" + folder + "/");
			if(!f.exists())
				f.mkdir();
			
			log.setText("");
			
			analyzeTemp();
			
			updateStats();
			
			try {
				server = Runtime.getRuntime().exec("java -cp GameServers.jar com.ra4king.gameservers." + className + " \"" + codeBase + dataFolder + "/" + folder + "/\"", null);
				
				writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(codeBase + dataFolder + "/" + folder + "/log.txt",true),"UTF-8"),true);
				
				new Thread() {
					public void run() {
						BufferedReader reader = null;
						try{
							reader = new BufferedReader(new InputStreamReader(server.getInputStream(),"UTF-8"));
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
						
						while(true) {
							final String line;
							try{
								line = reader.readLine();
								
								if(line == null) {
									Server.this.stop();
									return;
								}
							}
							catch(Exception exc) {
								exc.printStackTrace();
								continue;
							}
							
							if(line.startsWith("NEW CONNECTION:")) {
								recordIP(line.substring(line.indexOf(":")+1).trim());
								updateStats();
								continue;
							}
							
							writer.println(line);
							
							try{
								SwingUtilities.invokeAndWait(new Runnable() {
									public void run() {
										try {
											if(totalConnections()%connectionNumber == 0) {
												if(!cleared) {
													clear();
													cleared = true;
												}
											}
											else if(cleared)
												cleared = false;
											
											log.getDocument().insertString(log.getDocument().getLength(),line+"\n",log.getStyle("normal"));
											
											if(!scrollBar.getValueIsAdjusting())
												scrollBar.setValue(scrollBar.getMaximum());
										}
										catch(Exception exc) {
											exc.printStackTrace();
										}
									}
								});
							}
							catch(Exception exc) {
								exc.printStackTrace();
							}
						}
					}
				}.start();
				
				new Thread() {
					public void run() {
						BufferedInputStream error = new BufferedInputStream(server.getErrorStream());
						
						boolean running = true;
						while(running) {
							try {
								server.exitValue();
								
								running = false;
								
								server.destroy();
							}
							catch(Exception exc) {
								try {
									while(error.available() == 0)
										Thread.sleep(100);
								}
								catch(Exception exc2) {
									exc2.printStackTrace();
								}
								
								StringBuilder builder = new StringBuilder();
								
								try {
									while(error.available() > 0)
										builder.append((char)error.read());
								}
								catch(Exception exc2) {
									exc2.printStackTrace();
								}
								
								if(builder.equals(""))
									continue;
								
								writer.println(builder);
								
								final String line = builder.toString();
								
								try{
									SwingUtilities.invokeAndWait(new Runnable() {
										public void run() {
											try {
												log.getDocument().insertString(log.getDocument().getLength(),line+"\n",log.getStyle("error"));
												if(!scrollBar.getValueIsAdjusting())
													scrollBar.setValue(scrollBar.getMaximum());
											}
											catch(Exception exc) {
												exc.printStackTrace();
											}
										}
									});
								}
								catch(Exception exc2) {
									exc2.printStackTrace();
								}
							}
						}
					}
				}.start();
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
		}
		
		public void stop() {
			if(server == null)
				return;
			
			try {
				server.exitValue();
				return;
			}
			catch(Exception exc) {}
			
			try {
				server.destroy();
			}
			catch(Exception exc) {}
			
			try {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							log.getDocument().insertString(log.getDocument().getLength(),server.waitFor()+"\n",log.getStyle("error"));
							if(!scrollBar.getValueIsAdjusting())
								scrollBar.setValue(scrollBar.getMaximum());
						}
						catch(Exception exc) {
							exc.printStackTrace();
							JOptionPane.showMessageDialog(null, exc.getMessage());
						}
					}
				});
				
				writer.close();
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
		}
		
		public void clear() {
			log.setText(null);
		}
		
		private void updateStats() {
			((JLabel)statsPane.getComponent(2)).setText("Total Connections: " + totalConnections());
			((JLabel)statsPane.getComponent(4)).setText("Unique IPs: " + ips.size());
			((JLabel)statsPane.getComponent(6)).setText("Average Connections: " + (totalConnections()/(double)ips.size()));
		}
		
		private int totalConnections() {
			int totalConnections = 0;
			for(int i : ips.values())
				totalConnections += i;
			return totalConnections;
		}
		
		private void recordIP(String ip) {
			ip = ip.trim();
			
			final Calendar time = Calendar.getInstance();
			time.setTimeInMillis(lastTime);
			
			if(time.get(Calendar.DAY_OF_YEAR) != Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							ArrayList<String> lines = new ArrayList<String>();
							lines.add("RECORDING TODAY'S DATA");
							
							PrintWriter writer = null;
							try {
								writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + "/" + dataFolder + "/"+folder+"/visitors.txt",true),"UTF-8"),true);
								
								lines.add(time.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.getDefault()) + ", " +
										  time.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault()) + " " +
										  time.get(Calendar.DAY_OF_MONTH) + ", " + time.get(Calendar.YEAR));
								
								int sessions = 0;
								for(int i : ips.values())
									sessions += i;
								
								lines.add("Number of connections: " + sessions);
								lines.add("Number of unique IPs: " + ips.size());
								
								int most = 0;
								String mostIP = "none";
								for(String s : ips.keySet()) {
									int i = ips.get(s);
									if(i > most) {
										most = i;
										mostIP = s;
									}
								}
								lines.add("Most connections: " + most + ", from visitor: " + mostIP);
								
								int least = most;
								String leastIP = mostIP;
								for(String s : ips.keySet()) {
									int i = ips.get(s);
									if(i < least) {
										least = i;
										leastIP = s;
									}
								}
								lines.add("Least connections: " + least + ", from visitor: " + leastIP);
								
								lines.add("Average connections per visitor " + ((double)sessions/(ips.size() == 0 ? 1 : ips.size())));
								
								clear();
								
								for(String line : lines) {
									writer.println(line);
									
									try {
										log.getDocument().insertString(log.getDocument().getLength(),line + "\n",log.getStyle("error"));
									}
									catch(Exception exc) {
										exc.printStackTrace();
									}
								}
								
								writer.println();
								log.getDocument().insertString(log.getDocument().getLength(),"\n",log.getStyle("error"));
							}
							catch(Exception exc) {
								exc.printStackTrace();
								JOptionPane.showMessageDialog(null,"Error writing to visitors.txt!");
							}
							finally {
								try{
									writer.close();
								}
								catch(Exception exc) {}
								
								lastTime = System.currentTimeMillis();
								ips.clear();
							}
							
							writer = null;
							try {
								writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + "/" + dataFolder + "/"+folder+"/temp.txt"),"UTF-8"),true);
								writer.println(lastTime);
							}
							catch(Exception exc) {
								exc.printStackTrace();
							}
							finally {
								try {
									writer.close();
								}
								catch(Exception exc) {}
							}
						}
					});
				}
				catch(Exception exc) {
					exc.printStackTrace();
				}
			}
			
			if(ips.containsKey(ip))
				ips.put(ip,ips.get(ip)+1);
			else
				ips.put(ip, 1);
			
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + "/" + dataFolder + "/"+folder+"/temp.txt",true),"UTF-8"),true);
				writer.println(ip);
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
			finally {
				try {
					writer.close();
				}
				catch(Exception exc) {}
			}
		}
		
		private void analyzeTemp() {
			ArrayList<String> lines = new ArrayList<String>();
			
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.dir") + "/" + dataFolder + "/" + folder + "/temp.txt"),"UTF-8"));
				
				String s;
				while((s = reader.readLine()) != null)
					lines.add(s);
			}
			catch(Exception exc) {
				return;
			}
			finally {
				try{
					reader.close();
				}
				catch(Exception exc) {}
			}
			
			lastTime = Long.parseLong(lines.remove(0));
			
			for(String s : lines)
				if(!s.equals("")) {
					if(ips.containsKey(s))
						ips.put(s,ips.get(s)+1);
					else
						ips.put(s, 1);
				}
		}
	}
}
