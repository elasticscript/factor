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

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class VMDispatcher extends Dispatcher {
    private static final Logger log = 
            LoggerFactory.getLogger(VMDispatcher.class);
    private static final long serialVersionUID = -4591328314129190562L;
    
    private final Actor target;
    private final Message msg;

    public static class Factory implements DispatcherFactory {
        @Override
        public Dispatcher newDispatcher(Actor target, Message msg) {
            if (msg.getTo().getScheme().toLowerCase().equals("vm"))
                return new VMDispatcher(target, msg);
         
            throw new RuntimeException("Scheme not supported!");
        }
    }
    
    VMDispatcher(Actor target, Message msg) {
        this.target = target;
        this.msg = msg;
    }

    @Override
    public void compute() {
        try {
            target.dispatch(msg);
        } 
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
