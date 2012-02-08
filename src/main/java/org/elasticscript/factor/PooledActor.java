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
package org.elasticscript.factor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

final public class PooledActor<T> extends Actor<T> {
    private static final Logger log = LoggerFactory.getLogger(PooledActor.class);
    private final Map<Long, URI> actors = new HashMap<Long, URI>();
    private final int count;
    
    public PooledActor(
            List<MessageCallable<T>> cbl, 
            MessageReducer<T> reducer, 
            Registry registry, 
            URI URI,
            DispatcherFactory f,
            int count) {
        super(registry, URI, f);
        this.count = count;
        
        try {
            createPool(cbl, reducer, f);
        } 
        catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public PooledActor(
            MessageCallable<T> cb, 
            Registry registry, 
            URI URI,
            DispatcherFactory f,
            int count) {
        super(registry, URI, f);
        this.count = count;
        try {
            createPool(cb, f);
        } 
        catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void start() {
        if (log.isDebugEnabled())
            log.debug("Actor start.");
    }
    
    @Override
    protected void stop() {
        if (log.isDebugEnabled())
            log.debug("Actor stop.");
    }
    
    @Override
    protected void react(Message<T> msg) {
        long key = Math.abs(msg.getId()) % count;
        URI address = actors.get(key);
        Actor a = getRegistry().find(address);
        
        // copy the msg
        Message<T> m = new Message(msg.getPayload(), address, null);
        
        if (log.isDebugEnabled())
            log.debug("Sending message to: " + address);
        
        // forward
        a.tell(m);
    }
    
    private void createPool(
            List<MessageCallable<T>> cbl, 
            MessageReducer<T> reducer, 
            DispatcherFactory f) throws URISyntaxException {
        Registry r = getRegistry();
        Actor a;
        URI addr;
        for (long i = 0; i < count; ++i) {
            addr = new URI(getAddress().toASCIIString() + "/" + i);
            a = new FJActor(cbl, reducer, r, addr, f);
            r.register(a);
            actors.put(i, addr);
        }
    }

    private void createPool(
            MessageCallable<T> cb,
            DispatcherFactory f) throws URISyntaxException {
        Registry r = getRegistry();
        Actor a;
        URI addr;
        for (long i = 0; i < count; ++i) {
            addr = new URI(getAddress().toASCIIString() + "/" + i);
            a = new CallableActor(cb, r, addr, f);
            r.register(a);
            actors.put(i, addr);
        }
    }
}
