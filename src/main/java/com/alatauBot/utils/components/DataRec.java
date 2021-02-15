package com.alatauBot.utils.components;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataRec extends HashMap<String, Object> {

    public DataRec() {}

    public Object   getValue(String key) {
        Object value = this.get(key);
        if (value == null) {
            throw new IllegalArgumentException();
        } else {
            return value;
        }
    }

    public boolean  hasValue(String key) {
        return this.containsKey(key) && this.get(key) != null;
    }

    public DataRec  set(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public long     getLong(String key) {
        Object object = this.get(key);
        if (object instanceof Integer) {
            return (long) ((Integer) object).intValue();
        } else if (object instanceof Long) {
            return ((Long) object).longValue();
        } else if (object instanceof Double) {
            return ((Double) object).longValue();
        } else if (object instanceof BigDecimal) {
            return ((BigDecimal) object).longValue();
        } else if (object instanceof String) {
            String string   = object.toString().trim();
            Matcher mDouble = Pattern.compile("^-?\\d+\\.?(\\d+)?$").matcher(string);
            Matcher mLong   = Pattern.compile("^-?\\d+$").matcher(string);
            if (mLong.matches()) {
                return Long.parseLong(string);
            } else if (mDouble.matches()) {
                return Double.valueOf(Double.parseDouble(string)).longValue();
            } else {
                throw new NumberFormatException();
            }
        } else {
            throw new NumberFormatException();
        }
    }

    public int      getInt(String key) {
        Object object = this.get(key);
        if(object instanceof Integer) {
            return ((Integer)object).intValue();
        } else if(object instanceof Long) {
            return Integer.parseInt(object.toString());
        } else if(object instanceof Double) {
            return ((Double)object).intValue();
        } else if(object instanceof BigDecimal) {
            return ((BigDecimal)object).intValue();
        } else if(object instanceof String) {
            String string   = object.toString().trim();
            Matcher mDouble = Pattern.compile("^-?\\d+\\.?(\\d+)?$").matcher(string);
            Matcher mInt    = Pattern.compile("^-?\\d+$").matcher(string);
            if(mInt.matches()) {
                return Integer.parseInt(string);
            } else if(mDouble.matches()) {
                return Double.valueOf(Double.parseDouble(string)).intValue();
            } else {
                throw new NumberFormatException();
            }
        } else {
            throw new NumberFormatException();
        }
    }

    public boolean  getBoolean(String key) {
        Object object = this.get(key);
        if (object instanceof Integer) {
            if (((Integer) object).intValue() == 0) {
                return false;
            } else if (((Integer) object).intValue() == 1) {
                return true;
            } else {
                throw new IllegalArgumentException();
            }
        } else if (object instanceof Long) {
            if (((Long) object).longValue() == 0L) {
                return false;
            } else if (((Long) object).longValue() == 1L) {
                return true;
            } else {
                throw new IllegalArgumentException();
            }
        } else if (object instanceof BigDecimal) {
            BigDecimal bg = (BigDecimal) object;
            if (bg.longValue() == 0L) {
                return false;
            } else if (bg.longValue() == 1L) {
                return true;
            } else {
                throw new IllegalArgumentException();
            }
        } else if (object instanceof Double) {
            long value = ((Double) object).longValue();
            if (value == 0L) {
                return false;
            } else if (value == 1L) {
                return true;
            } else {
                throw new IllegalArgumentException();
            }
        } else if (object instanceof String) {
            String string   = object.toString().trim();
            byte var5       = -1;
            switch (string.hashCode()) {
                case 3569038:
                    if (string.equals("true")) var5 = 1;
                    break;
                case 97196323:
                    if (string.equals("false")) var5 = 0;
            }
            switch (var5) {
                case 0:
                    return false;
                case 1:
                    return true;
                default:
                    string  = string.substring(0, 1);
                    var5    = -1;
                    switch (string.hashCode()) {
                        case 48:
                            if (string.equals("0")) var5 = 0;
                            break;
                        case 49:
                            if (string.equals("1")) var5 = 1;
                    }
                    switch (var5) {
                        case 0:
                            return false;
                        case 1:
                            return true;
                        default:
                            throw new IllegalArgumentException();
                    }
            }
        } else if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public double   getDouble(String key) {
        Object object = this.get(key);
        if (object instanceof Double) {
            return ((Double) object).doubleValue();
        } else if (object instanceof Integer) {
            return ((Double) object).doubleValue();
        } else if (object instanceof Long) {
            return ((Double) object).doubleValue();
        } else if (object instanceof BigDecimal) {
            return ((BigDecimal) object).doubleValue();
        } else if (object instanceof String) {
            String string = object.toString().trim();
            string = string.replaceAll(" ", "");
            Matcher mDouble = Pattern.compile("^-?\\d+\\.?(\\d+)?$").matcher(string);
            if (mDouble.matches()) {
                return Double.parseDouble(string);
            } else {
                throw new NumberFormatException();
            }
        } else {
            throw new NumberFormatException();
        }
    }

    public Date     getDate(String key) {
        Object object = this.get(key);
        if (object instanceof Date) {
            return (Date) object;
        } else if (object instanceof DateTime) {
            return ((DateTime) object).toDate();
        } else if (object instanceof Timestamp) {
            return new Date(((Timestamp) object).getTime());
        } else if (object instanceof String) {
            String string = object.toString().trim();
            Matcher m1 = Pattern.compile("^\\d\\d-\\d\\d-\\d\\d\\d\\d$").matcher(string);
            Matcher m2 = Pattern.compile("^\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d$").matcher(string);
            Matcher m3 = Pattern.compile("^\\d\\d\\d\\d-\\d\\d-\\d\\d$").matcher(string);
            Matcher m4 = Pattern.compile("^\\d\\d\\d\\d\\.\\d\\d\\.\\d\\d$").matcher(string);
            try {
                SimpleDateFormat format;
                if (m1.matches()) {
                    format = new SimpleDateFormat("dd-MM-yyyy");
                    return format.parse(string);
                } else if (m2.matches()) {
                    format = new SimpleDateFormat("dd.MM.yyyy");
                    return format.parse(string);
                } else if (m3.matches()) {
                    format = new SimpleDateFormat("yyyy-MM-dd");
                    return format.parse(string);
                } else if (m4.matches()) {
                    format = new SimpleDateFormat("yyyy.MM.dd");
                    return format.parse(string);
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (ParseException var9) {
                var9.printStackTrace();
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String   getString(String key) {
        Object object   = this.get(key);
        String string   = object.toString().trim();
        string          = string.replaceAll("'", "");
        return string;
    }
}
