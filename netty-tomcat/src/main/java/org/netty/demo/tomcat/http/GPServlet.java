package org.netty.demo.tomcat.http;

public abstract class GPServlet {
    public static final String GET = "GET";

    public void service(GPRequest request, GPResponse response) throws Exception {

        //由service方法来决定，是调用doGet或者调用doPost
        if (GET.equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }

    }

    /**
     * @param request
     * @param response
     * @throws Exception
     */
    public abstract void doGet(GPRequest request, GPResponse response) throws Exception;

    /**
     * @param request
     * @param response
     * @throws Exception
     */
    public abstract void doPost(GPRequest request, GPResponse response) throws Exception;

}
