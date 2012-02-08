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
import jsr166y.ForkJoinPool.ManagedBlocker;
import jsr166y.ForkJoinPool;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

abstract public class Actor<T> implements ActorMBean {
    private static final Logger log = LoggerFactory.getLogger(Actor.class);
    private final Mailbox mailbox;
    private final Registry registry;
    private final URI address;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private long waitSeconds = 1;
    
    abstract protected void start();
    abstract protected void stop();
    abstract protected void react(Message<T> msg);
    
    static class BlockingCallable<T> implements ManagedBlocker {
        private T r;
        private Throwable t;
        private Callable<T> callable;

        public BlockingCallable(Callable<T> callable) {
            this.callable = callable;
        }

        @Override
        public boolean block() throws InterruptedException {
            try {
                r = callable.call();
            } 
            catch (Exception ex) {
                t = ex.getCause();
            } 
            finally {
                callable = null;
            }
            
            return true;
        }

        public Throwable getThrowable() {
            return t;
        }

        public T getResult() {
            return r;
        }

        @Override
        public boolean isReleasable() {
            return (callable == null);
        }
    }
    
    public Actor(Registry registry, URI address, DispatcherFactory f) {
        this.registry = registry;
        this.address = address;
        this.mailbox = new Mailbox(
            "mailbox-" + address.toASCIIString().replaceFirst("://", "."), f);
    }
    
    public void tell(Message<T> msg) {
        // ignore messages if stopped
        if (!started.get())
            return;
        
        Actor a = registry.find(msg.getTo());
        
        if (a == null) {
            throw new RuntimeException(
            String.format("Actor with address: %s not found!", msg.getTo()));
        }
        
        a.mailbox.submit(a, msg);
    }
    
    @Override
    public String getAddressString() {
        return address.toASCIIString();
    }
    
    @Override
    public String getName() {
        return mailbox.getName();
    }
    
    @Override
    public boolean isAsync() {
        return mailbox.isAsync();
    }

    @Override
    public long getParallelism() {
        return mailbox.getParallelism();
    }

    @Override
    public long getPoolSize() {
        return mailbox.getPoolSize();
    }

    @Override
    public long getRunningThreadCount() {
        return mailbox.getRunningThreadCount();
    }

    @Override
    public long getQueuedSubmissionCount() {
        return mailbox.getQueuedSubmissionCount();
    }

    @Override
    public long getQueuedTaskCount() {
        return mailbox.getQueuedTaskCount();
    }

    @Override
    public long getStealCount() {
        return mailbox.getStealCount();
    }

    public void setWaitSeconds(long waitSeconds) {
        this.waitSeconds = waitSeconds;
    }
    
    final public boolean isStarted() {
        return started.get();
    }
    
    final URI getAddress() {
        return address;
    }

    final Registry getRegistry() {
        return registry;
    }
    
    final void dispatch(Message<T> msg) {
        try {
            // if stopped, ignore messages
            if (!started.get())
                return;
            
            synchronized (this) {
                react(msg);
            }
        }
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    final void startup() {
        try {
            synchronized (this) {
                start();
                started.set(true);
            }
        } 
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
    
    final void shutdown() {
        try {
            synchronized (this) {
                started.set(false);
                mailbox.getPool().shutdown();
                mailbox.getPool().awaitTermination(waitSeconds);
                stop();      
            }
        } 
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
    
    final protected <T> T receive(final Callable<T> cb) 
            throws InterruptedException {
        BlockingCallable<T> wrapper = new BlockingCallable<T>(cb);
        ForkJoinPool.managedBlock(wrapper);
        
        if (wrapper.getThrowable() != null)
            throw new RuntimeException(wrapper.getThrowable());
        
        return wrapper.getResult();
    }
}
