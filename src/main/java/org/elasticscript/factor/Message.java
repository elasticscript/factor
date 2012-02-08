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

import java.io.Serializable;
import java.net.URI;
import java.security.SecureRandom;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Message<T> implements Serializable {
    private static final long serialVersionUID = 1267847486595729710L;
    private final T payload;
    private final Long id;
    private final URI to;
    private final URI from;

    public Message(T payload, URI to, URI from) {
        this.payload = payload;
        this.id = newUID();
        this.to = to;
        this.from = from;
    }

    public Long getId() {
        return id;
    }

    public T getPayload() {
        return payload;
    }

    public URI getFrom() {
        return from;
    }

    public URI getTo() {
        return to;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Message<T> other = (Message<T>) obj;
        if (this.payload != other.payload && 
                (this.payload == null || !this.payload.equals(other.payload))) {
            return false;
        }
        if ((this.id == null) ? 
                (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.to != other.to && 
                (this.to == null || !this.to.equals(other.to))) {
            return false;
        }
        if (this.from != other.from && 
                (this.from == null || !this.from.equals(other.from))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.payload != null ? this.payload.hashCode() : 0);
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.to != null ? this.to.hashCode() : 0);
        hash = 97 * hash + (this.from != null ? this.from.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Message{" + "payload=" + payload + ", id=" + id + 
               ", to=" + to + ", from=" + from + '}';
    }
    
    private Long newUID() {
        try {
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            prng.setSeed(Long.toString(System.nanoTime()).getBytes());
            return prng.nextLong();
        } 
        catch (Exception ex) {
        }
        
        return -1L;
    }
}
