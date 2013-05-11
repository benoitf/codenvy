/*
 *    Copyright (C) 2013 eXo Platform SAS.
 *
 *    This is free software; you can redistribute it and/or modify it
 *    under the terms of the GNU Lesser General Public License as
 *    published by the Free Software Foundation; either version 2.1 of
 *    the License, or (at your option) any later version.
 *
 *    This software is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this software; if not, write to the Free
 *    Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *    02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.analytics.scripts;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.codenvy.analytics.BaseTest;
import com.codenvy.analytics.metrics.MetricParameter;
import com.codenvy.analytics.metrics.value.LongValueData;
import com.codenvy.analytics.metrics.value.MapStringLongValueData;
import com.codenvy.analytics.scripts.util.Event;
import com.codenvy.analytics.scripts.util.LogGenerator;

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
public class TestScriptJrebelUsage extends BaseTest {
    @Test
    public void testJRebelEligible() throws Exception {
        List<Event> events = new ArrayList<Event>();
        events.add(
                Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project1", "type", false).withDate("2010-10-01").build());
        events.add(
                Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project2", "type", false).withDate("2010-10-01").build());
        events.add(
                Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project3", "type", false).withDate("2010-10-01").build());
        events.add(
                Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project4", "type", false).withDate("2010-10-01").build());

        File log = LogGenerator.generateLog(events);

        Map<String, String> params = new HashMap<String, String>();
        params.put(MetricParameter.FROM_DATE.getName(), "20101001");
        params.put(MetricParameter.TO_DATE.getName(), "20101001");

        LongValueData value = (LongValueData)executeAndReturnResult(ScriptType.EVENT_COUNT_JREBEL_USAGE, log, params);
        Assert.assertEquals(value.getAsLong(), 4L);
    }

    @Test
    public void testJRebelEligibleDistinction() throws Exception {
        List<Event> events = new ArrayList<Event>();
        events.add(
                Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project1", "type", false).withDate("2010-10-01").build());
        events.add(
                Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project1", "type", false).withDate("2010-10-01").build());
        events.add(
                Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project3", "type", false).withDate("2010-10-01").build());
        events.add(Event.Builder.createJRebelUsageEvent("user2", "ws1", "session", "project1", "type", false).withDate("2010-10-01")
                        .build());

        File log = LogGenerator.generateLog(events);

        Map<String, String> params = new HashMap<String, String>();
        params.put(MetricParameter.FROM_DATE.getName(), "20101001");
        params.put(MetricParameter.TO_DATE.getName(), "20101001");

        LongValueData value = (LongValueData)executeAndReturnResult(ScriptType.EVENT_COUNT_JREBEL_USAGE, log, params);
        Assert.assertEquals(value.getAsLong(), 3L);
    }

    @Test
    public void testDetailedJRebelUsageDistinction() throws Exception {
        List<Event> events = new ArrayList<Event>();
        events.add(
                Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project1", "type", false).withDate("2010-10-01").build());
        events.add(Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project1", "type", true).withDate("2010-10-01").build());
        events.add(Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project1", "type", true).withDate("2010-10-01").build());
        events.add(Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project4", "type", true).withDate("2010-10-01").build());

        File log = LogGenerator.generateLog(events);

        Map<String, String> params = new HashMap<String, String>();
        params.put(MetricParameter.FROM_DATE.getName(), "20101001");
        params.put(MetricParameter.TO_DATE.getName(), "20101001");

        MapStringLongValueData value = (MapStringLongValueData)executeAndReturnResult(ScriptType.DETAILS_JREBEL_USAGE, log, params);
        Map<String, Long> all = value.getAll();

        Assert.assertEquals(all.get("true"), Long.valueOf(2));
        Assert.assertEquals(all.get("false"), Long.valueOf(1));
    }

    @Test
    public void testDetailedJRebelUsage() throws Exception {
        List<Event> events = new ArrayList<Event>();
        events.add(
                Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project1", "type", false).withDate("2010-10-01").build());
        events.add(Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project2", "type", true).withDate("2010-10-01").build());
        events.add(Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project3", "type", true).withDate("2010-10-01").build());
        events.add(Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project4", "type", true).withDate("2010-10-01").build());

        File log = LogGenerator.generateLog(events);

        Map<String, String> params = new HashMap<String, String>();
        params.put(MetricParameter.FROM_DATE.getName(), "20101001");
        params.put(MetricParameter.TO_DATE.getName(), "20101001");

        MapStringLongValueData value = (MapStringLongValueData)executeAndReturnResult(ScriptType.DETAILS_JREBEL_USAGE, log, params);

        Map<String, Long> all = value.getAll();

        Assert.assertEquals(all.get("true"), Long.valueOf(3));
        Assert.assertEquals(all.get("false"), Long.valueOf(1));
    }
}
