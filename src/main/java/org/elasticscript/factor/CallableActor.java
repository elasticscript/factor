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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class CallableActor<T> extends Actor<T> {
    private static final Logger log = 
            LoggerFactory.getLogger(CallableActor.class);
    
    private final MessageCallable<T> cb;
    
    public CallableActor(
            MessageCallable<T> cb, Registry channel, URI address) {
        super(channel, address);
        this.cb = cb;
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
            receive(new MessageWrappingCallable(cb, msg));
        } 
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
