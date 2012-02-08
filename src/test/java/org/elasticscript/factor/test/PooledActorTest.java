/*
 * Copyright (C) 2011 ELASTICSCRIPT.ORG
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *
 *          _           _   _                    _       _
 *         | |         | | (_)                  (_)     | |
 *      ___| | __ _ ___| |_ _  ___ ___  ___ _ __ _ _ __ | |_
 *     / _ \ |/ _` / __| __| |/ __/ __|/ __| '__| | '_ \| __|
 * ----  __/ | (_| \__ \ |_| | (__\__ \ (__| |  | | |_) | |_ ----
 *     \___|_|\__,_|___/\__|_|\___|___/\___|_|  |_| .__/ \__|
 *                                                | |
 *                                                |_|
 */
package org.elasticscript.factor.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.elasticscript.factor.Actor;
import org.elasticscript.factor.Registry;
import org.elasticscript.factor.Message;
import org.elasticscript.factor.MessageCallable;
import org.elasticscript.factor.MessageReducer;
import org.elasticscript.factor.PooledActor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class PooledActorTest {
    private static final Logger log = LoggerFactory.getLogger(PooledActorTest.class);
    private Registry r = new Registry();
    private URI address;
    private AtomicInteger counter = new AtomicInteger(0);
    
    public static class GetRandomQuoteCallable implements MessageCallable<String> {
        @Override
        public String call(Message msg) throws Exception {
            StringBuilder bldr = new StringBuilder("Quote #" + msg.getPayload());
            bldr.append('\n');
            URL url = new URL("http://www.iheartquotes.com/api/v1/random");
            bldr.append(IOUtil.readResource(url).toString());
            return bldr.toString();
        }
    }
    
    public PooledActorTest() {
        try {
            address = new URI("vm://test.pooled.fj.actor");
        } catch (URISyntaxException ex) {
            log.error(ex.getMessage());
        }
    }

    @Before
    public void setUp() {
        System.out.println("setUp");
        List<MessageCallable<String>> l = new ArrayList<MessageCallable<String>>();
        l.add(new GetRandomQuoteCallable());
        l.add(new GetRandomQuoteCallable());
        
        MessageReducer<String> reducer = new MessageReducer<String>() {
            @Override
            public void reduce(List<Message<String>> msgs) throws Exception {
                System.out.println("----------- reducer.reduce: " + counter.incrementAndGet());
                for (Message<String> m : msgs) {
                    System.out.println(m);
                }
            }
        };
        
        r.register(new PooledActor<String>(l, reducer, r, address, 5));
    }
    
    @After
    public void tearDown() {
        r.shutdown();
    }
    
    @Test
    public void test() throws InterruptedException {
        System.out.println("----------------------- PooledActorTest ---------------------");
        Actor a = r.find(address);
                
        // wait for startup
        while(a == null || !a.isStarted()) {
            Thread.sleep(100);
            a = r.find(address);
        }
        
        for (int i = 0; i < 15; ++i)
            a.tell(new Message(Integer.toString(i), address, null));
        
        Thread.sleep(3000);
    }
}
