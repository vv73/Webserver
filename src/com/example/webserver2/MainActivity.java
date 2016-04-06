package com.example.webserver2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
// Add comment
		new Thread(new Runnable() {
			@Override
			public void run() {
                Log.d("IP", getLocalIpAddresses());
				try {
					startServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();

	}

	public void startServer() throws IOException {
		ServerSocket server = new ServerSocket(8080);
		while (true) {
			System.out.println(System.getProperty("user.dir"));
			System.out.println("Waiting client");
			Socket socket = server.accept();
			System.out.println("Clent accepted");
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			byte[] array = new byte[100];
			String query = "";
			 BufferedReader br = new BufferedReader(new InputStreamReader(in));
	            while(true) {
	                String s = br.readLine();
	                if(s == null || s.trim().length() == 0) {
	                    break;
	                }
	                query += s;
	                
	            }
			System.out.println(query);
			String fileName = getFileName(query);
			System.out.println(fileName);
			byte[] file = getFile(fileName);
			if (file != null) {
				out.write(("HTTP/1.0 200 Ok\n" + "Content-type: " + getContentType(fileName) + "\nContent-length: "
						+ file.length + "\n\n").getBytes());
				out.write(file);
			} else {
				out.write(("HTTP/1.0 404 File Not Found\n\n").getBytes());
			}
			socket.close();
		}
	}

	byte[] getFile(String fileName) throws IOException {
		File file = new File(fileName);
		byte[] content = null;
		if (file.exists()) {
			content = new byte[(int) file.length()];
			FileInputStream fin = new FileInputStream(file);
			fin.read(content);

		}
		return content;

	}

	String getFileName(String query) {
		String fileName = query.split(" ")[1];
		if (fileName.charAt(fileName.length() - 1) == '/')
			fileName += "index.html";
		return Environment.getExternalStorageDirectory() + "/WWW" + fileName;
	}

	String getContentType(String fileName) {
		System.out.println(fileName);
		String[] array = fileName.split("\\.");
		String ext = array[array.length - 1];
		String contentType = "text/plain";
		if (ext.equals("htm") || ext.equals("html")) {
			contentType = "text/html";
		}
		if (ext.equals("gif")) {
			contentType = "image/gif";
		}
		if (ext.equals("jpg")) {
			contentType = "image/jpeg";
		}
		return contentType;
	}

	/** @return Возвращает локальные IP-адреса */
	static String getLocalIpAddresses() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += inetAddress.getHostAddress() + "\n";
					}

				}

			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}

		return ip;
	}

}
