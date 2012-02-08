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
import jsr166y.RecursiveAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Actions {
    private static final Logger log = LoggerFactory.getLogger(Actions.class);
    
    static class Start extends RecursiveAction {
        private final Registry r;
        private final Actor actor;
        
        public Start(Registry r, Actor actor) {
            this.r = r;
            this.actor = actor;
        }
        
        @Override
        public void compute() {
            try {
                actor.startup();
                r.put(actor.getAddress(), actor);
                
                if (actor instanceof FJMBean) {
                    JMXUtil.registerMBean(actor, actor.getName());
                }
            }
            catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }
    
    static class Stop extends RecursiveAction {
        private final Registry r;
        private final URI uri;
        
        public Stop(Registry r, URI uri) {
            this.r = r;
            this.uri = uri;
        }
        
        @Override
        public void compute() {
            try {
                Actor a = r.remove(uri);
                a.shutdown();
                
                if (a instanceof FJMBean) {
                    JMXUtil.unregisterMBean(a, a.getName());
                }
            }
            catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }
}
