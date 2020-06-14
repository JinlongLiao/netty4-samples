package org.netty.demo.rpc.provider;

import org.netty.demo.rpc.api.IRpcHelloService;

public class RpcHelloServiceImpl implements IRpcHelloService {

    public String hello(String name) {  
        return "Hello " + name + "!";  
    }  
  
}  
