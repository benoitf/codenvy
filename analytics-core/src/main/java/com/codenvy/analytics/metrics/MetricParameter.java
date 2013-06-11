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
package com.codenvy.analytics.metrics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
public enum MetricParameter {
    ALIAS {
        @Override
        public String getDefaultValue() {
            return null;
        }

        @Override
        public String getName() {
            return "alias";
        }
    },

    TIME_UNIT {
        @Override
        public String getDefaultValue() {
            return TimeUnit.DAY.toString();
        }

        @Override
        public String getName() {
            return "timeUnit";
        }
    },

    FROM_DATE {
        @Override
        public String getDefaultValue() {
            return "20120101";
        }

        @Override
        public String getName() {
            return "fromDate";
        }
    },

    ENTITY {
        @Override
        public String getDefaultValue() {
            return null;
        }

        @Override
        public String getName() {
            return "entity";
        }
    },

    INTERVAL {
        @Override
        public String getDefaultValue() {
            return null;
        }

        @Override
        public String getName() {
            return "interval";
        }
    },

    RESULT_DIR {
        @Override
        public String getDefaultValue() {
            return ".";
        }

        @Override
        public String getName() {
            return "resultDir";
        }
    },

    TO_DATE {
        @Override
        public String getDefaultValue() {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);

            DateFormat df = new SimpleDateFormat(MetricParameter.PARAM_DATE_FORMAT);
            return df.format(calendar.getTime());
        }

        @Override
        public String getName() {
            return "toDate";
        }
    };

    /**
     * The date format is used in scripts.
     */
    public static final String PARAM_DATE_FORMAT = "yyyyMMdd";

    /**
     * @return the default value for given parameter.
     */
    public abstract String getDefaultValue();

    /**
     * @return the parameter's name is used in script
     */
    public abstract String getName();

    /**
     * Enumeration for {@link MetricParameter#ENTITY}
     */
    public enum ENTITY_TYPE {
        USERS,
        COMPANIES,
        DOMAINS
    }
}
