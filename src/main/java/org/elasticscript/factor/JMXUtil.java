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

import java.lang.management.ManagementFactory;
import java.util.Hashtable;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

final public class JMXUtil {

    private JMXUtil() {
    }

    public static void registerMBean(
        final Object mbean, final ObjectName oname)
    {
        try
        {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean(mbean, oname);
        }
        catch (JMException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    public static void registerMBean(
        final Object mbean, final String name)
    { 
        try
        {
            registerMBean(mbean, onameForBean(mbean, name));
        }
        catch (JMException ex)
        {
            throw new RuntimeException(ex);
        }     
    }
    
    public static void unregisterMBean(
            final Object mbean, final String name)
    {
        try
        {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.unregisterMBean(onameForBean(mbean, name));
        }
        catch (JMException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    public static void unregisterMBean(final ObjectName oname)
    {
        try
        {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.unregisterMBean(oname);
        }
        catch (JMException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    public static ObjectName onameForBean(
            final Object mbean, final String name) 
        throws MalformedObjectNameException
    {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Hashtable<String, String> ht = new Hashtable<String, String>();
        Class klass = mbean.getClass();
        
        ht.put("class", klass.getSimpleName());
        ht.put("name", name);
        return new ObjectName(klass.getPackage().getName(), ht);
    }
}
