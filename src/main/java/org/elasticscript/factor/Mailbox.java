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

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * TODO: Dispatcher must be extended to support remote
 */ 
class Mailbox implements FJMBean {
    private final FJPool fjpool;
    
    public Mailbox(String name) {
        fjpool = new FJPool(name);
    }
    
    public void shutdown() {
        fjpool.shutdown();
    }
    
    public void submit(Actor a, Message m) {
        fjpool.submit(Dispatcher.newDispatcher(a, m));
    }
    
    final FJPool getPool() {
        return fjpool;
    }
    
    @Override
    public String getName() {
        return fjpool.getPoolName();
    }
    
    @Override
    public boolean isAsync() {
        return fjpool.getPool().getAsyncMode();
    }

    @Override
    public long getParallelism() {
        return fjpool.getPool().getParallelism();
    }

    @Override
    public long getPoolSize() {
        return fjpool.getPool().getPoolSize();
    }

    @Override
    public long getRunningThreadCount() {
        return fjpool.getPool().getRunningThreadCount();
    }

    @Override
    public long getQueuedSubmissionCount() {
        return fjpool.getPool().getQueuedSubmissionCount();
    }

    @Override
    public long getQueuedTaskCount() {
        return fjpool.getPool().getQueuedTaskCount();
    }

    @Override
    public long getStealCount() {
        return fjpool.getPool().getStealCount();
    }
}
