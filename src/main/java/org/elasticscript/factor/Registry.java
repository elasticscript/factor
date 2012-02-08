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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.elasticscript.factor.Actions.Start;
import org.elasticscript.factor.Actions.Stop;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Registry {
    private final FJPool fjpool;
    private final Map<URI, Actor> registry;
    
    public Registry() {
        this.fjpool = new FJPool(Registry.class.getSimpleName());
        this.registry = new ConcurrentHashMap<URI, Actor>();
    }
    
    public void shutdown() {
        Set<URI> uris = registry.keySet();
        
        for (URI uri : uris) {
            fjpool.submit(new Stop(this, uri));
        }
    }
    
    public URI register(final Actor actor) {
        URI address = actor.getAddress();
        
        if (registry.containsKey(address))
            throw new RuntimeException(address + " already registered.");
        
        fjpool.submit(new Start(this, actor));
        
        return address;
    }   
    
    public Actor find(URI address) {
        return registry.get(address);
    }
    
    void put(URI uri, Actor a) {
        registry.put(uri, a);
    }
    
    Actor remove(URI uri) {
        return registry.remove(uri);
    }
}
