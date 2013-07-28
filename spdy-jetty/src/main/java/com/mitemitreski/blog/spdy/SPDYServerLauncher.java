/*
 * Copyright (C) 2013 by Netcetera AG.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of Netcetera AG, Switzerland.
 * The program(s) may be used and/or copied only with the written permission of Netcetera AG or
 * in accordance with the terms and conditions stipulated in the agreement/contract under which 
 * the program(s) have been supplied.
 */
package com.mitemitreski.blog.spdy;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.spdy.http.HTTPSPDYServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;


public class SPDYServerLauncher {

  public static void main(String[] args) throws Exception {

    Server server = new Server();

    // the ssl context to use
    SslContextFactory sslFactory = new SslContextFactory();
    //NOTE: keystore.jks should not end up in the repo on a real application since it is private
    sslFactory.setKeyStorePath("src/main/resources/keystore.jks");
    sslFactory.setKeyStorePassword("password");
    sslFactory.setProtocol("TLSv1");

    Connector connector = new HTTPSPDYServerConnector(sslFactory);
    connector.setPort(8443);

    // add connector to the server
    server.addConnector(connector);

    // add a handler to serve content
    ContextHandler handler = new ContextHandler();
    handler.setContextPath("/mysite");
    handler.setResourceBase("src/main/webapp/");
    handler.setHandler(new ResourceHandler());

    server.setHandler(handler);

    server.start();
    server.join();
  }
}
