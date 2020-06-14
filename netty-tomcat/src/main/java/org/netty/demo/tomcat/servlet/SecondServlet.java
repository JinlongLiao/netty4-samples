package org.netty.demo.tomcat.servlet;


import org.netty.demo.tomcat.http.GPRequest;
import org.netty.demo.tomcat.http.GPResponse;
import org.netty.demo.tomcat.http.GPServlet;

public class SecondServlet extends GPServlet {

	@Override
    public void doGet(GPRequest request, GPResponse response) throws Exception {
		this.doPost(request, response);
	}

	@Override
	public void doPost(GPRequest request, GPResponse response) throws Exception {
		response.write("This is Second Serlvet");
	}

}
