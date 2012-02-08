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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FJActor<T> extends Actor<T> {
    private static final Logger log = LoggerFactory.getLogger(FJActor.class);
    private final FJPool fjpool;
    private final List<MessageCallable<T>> cbl;
    private final MessageReducer<T> reducer;
    
    public FJActor(
            List<MessageCallable<T>> cbl, 
            MessageReducer<T> reducer, 
            Registry channel, URI address) {
        super(channel, address);
        this.cbl = cbl;
        this.reducer = reducer;
        this.fjpool = new FJPool(address.toASCIIString().replaceFirst("://", "."));
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
        try {
            Collection<Callable<Message<T>>> coll = 
                    new ArrayList<Callable<Message<T>>>();
            
            for (MessageCallable<T> cb : cbl) {
                coll.add(new MessageWrappingCallable(cb, msg));
            }
            
            List<Message<T>> msgs = new ArrayList<Message<T>>();
            List<Future<Message<T>>> futures = fjpool.invokeAll(coll);
            
            // will block
            for (Future<Message<T>> f : futures) {
                msgs.add(f.get());
            }
            
            reducer.reduce(msgs);
        } 
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
