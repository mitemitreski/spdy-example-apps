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


import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.spdy.SPDYServerConnector;
import org.eclipse.jetty.spdy.api.DataInfo;
import org.eclipse.jetty.spdy.api.ReplyInfo;
import org.eclipse.jetty.spdy.api.Stream;
import org.eclipse.jetty.spdy.api.StreamFrameListener;
import org.eclipse.jetty.spdy.api.StringDataInfo;
import org.eclipse.jetty.spdy.api.SynInfo;
import org.eclipse.jetty.spdy.api.server.ServerSessionFrameListener;

public class MyListener {

  public static void main(String[] args) throws Exception {

    ServerSessionFrameListener frameListener = new ServerSessionFrameListener.Adapter() {

      @Override
      public StreamFrameListener onSyn(final Stream stream, SynInfo synInfo) {

        stream.reply(new ReplyInfo(false));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable periodicTask = new Runnable() {

          private int counter = 0;

          public void run() {
            // send a request and don't close the stream
            stream.data(new StringDataInfo("Server data number " + counter + " sent on "
                + new Date(), false));
            counter++;
          }
        };
        executor.scheduleAtFixedRate(periodicTask, 0, 1, TimeUnit.SECONDS);

        return new StreamFrameListener.Adapter() {

          public void onData(Stream stream, DataInfo dataInfo) {
            String clientData = dataInfo.asString("UTF-8", true);
            System.out.println("Received the following client data: " + clientData);
          }
        };
      }
    };

    Server server = new Server();
    SPDYServerConnector connector = new SPDYServerConnector(frameListener);
    connector.setPort(8181);

    server.addConnector(connector);
    server.start();
    server.join();
  }
}
