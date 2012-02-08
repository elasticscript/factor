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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.elasticscript.factor.Actor;
import org.elasticscript.factor.CallableActor;
import org.elasticscript.factor.Registry;
import org.elasticscript.factor.Message;
import org.elasticscript.factor.MessageCallable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class ActorTest {
    private static final Logger log = LoggerFactory.getLogger(ActorTest.class);
    private Registry r = new Registry();
    private URI address;
    
    public ActorTest() throws URISyntaxException {
        address = new URI("vm://actortest");
    }

    @Before
    public void setUp() {
        System.out.println("setUp");
        Actor a = new CallableActor(
            new MessageCallable() {
                @Override
                public Object call(Message msg) throws Exception {
                    Thread.sleep(5);
                    System.out.println("--- Msg: " + msg);
                    return null;
                }
            }, r, address);
        
        r.register(a);
    }
    
    @After
    public void tearDown() {
        r.shutdown();
    }
    
    @Test
    public void test() throws InterruptedException {
        System.out.println("----------------------- ActorTest ---------------------");
        List<String> list = new ArrayList<String>();
        list.add("Hello!");
        list.add("World!");
        
        Actor a = r.find(address);
                
        // wait for startup
        while(a == null || !a.isStarted()) {
            Thread.sleep(100);
            a = r.find(address);
        }
        
        a.tell(new Message(Collections.unmodifiableList(list), address, null));
            
        Thread.sleep(500);
    }
}
