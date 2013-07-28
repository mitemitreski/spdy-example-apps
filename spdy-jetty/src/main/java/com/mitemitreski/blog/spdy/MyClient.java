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

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.spdy.SPDYClient;
import org.eclipse.jetty.spdy.api.DataInfo;
import org.eclipse.jetty.spdy.api.SPDY;
import org.eclipse.jetty.spdy.api.Session;
import org.eclipse.jetty.spdy.api.Stream;
import org.eclipse.jetty.spdy.api.StreamFrameListener;
import org.eclipse.jetty.spdy.api.StringDataInfo;
import org.eclipse.jetty.spdy.api.SynInfo;

public class MyClient {

  public static void main(String[] args) throws Exception {

    StreamFrameListener streamListener = new StreamFrameListener.Adapter() {

      public void onData(Stream stream, DataInfo dataInfo) {
        String content = dataInfo.asString("UTF-8", true);
        System.out.println("I got from the server: " + content);
      }
    };

    SPDYClient.Factory clientFactory = new SPDYClient.Factory();
    clientFactory.start();
    SPDYClient client = clientFactory.newSPDYClient(SPDY.V2);

    Session session = client.connect(new InetSocketAddress("localhost", 8181), null).get(3, TimeUnit.SECONDS);

    final Stream stream = session.syn(new SynInfo(false), streamListener).get(3, TimeUnit.SECONDS);

    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    Runnable periodicTask = new Runnable() {

      private int counter = 0;

      public void run() {
        stream.data(new StringDataInfo("Data from the client " + counter + " sent on " + new Date(), false));
        counter++;
      }
    };
    executor.scheduleAtFixedRate(periodicTask, 0, 1, TimeUnit.SECONDS);
  }
}
