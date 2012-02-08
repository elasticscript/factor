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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinTask;
import jsr166y.RecursiveAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FJPool<T> {
    private static final Logger log = LoggerFactory.getLogger(FJPool.class);
    private final ForkJoinPool pool;
    private final String poolName;
    
    public FJPool(String poolName) {
        this.poolName = poolName;   
        this.pool = new ForkJoinPool(
            Runtime.getRuntime().availableProcessors(), 
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            new Thread.UncaughtExceptionHandler(){
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    System.err.println(String.format(
                    "uncaughtException thread '%s'", t));
                }
            },
            true);
    }
    
    public void submit(ForkJoinTask<T> task) {
        pool.submit(task);
    }
    
    public void submit(RecursiveAction ra) {
        pool.submit(ra);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> c) {
        return pool.invokeAll(c);
    }
    
    public void shutdown() {
        pool.shutdown();
    }

    public void awaitTermination(long waitSeconds) {
        try {
            pool.awaitTermination(waitSeconds, TimeUnit.SECONDS);
        } 
        catch (InterruptedException ex) {
            log.warn(ex.getMessage(), ex);
        }
    }
    
    public String getPoolName() {
        return poolName;
    }
    
    ForkJoinPool getPool() {
        return pool;
    }
}
