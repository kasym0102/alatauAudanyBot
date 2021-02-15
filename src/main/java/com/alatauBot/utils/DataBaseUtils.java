package com.alatauBot.utils;

import com.alatauBot.utils.components.DataRec;
import com.alatauBot.utils.components.DataRecRowMapper;
import com.alatauBot.utils.components.DataTable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

public class DataBaseUtils extends JdbcTemplate {

    public              DataBaseUtils(DataSource source) { super(source); }

    public DataTable query(String sql) throws DataAccessException {
        DataTable dataTable = new DataTable();
        dataTable.addAll(this.query(sql, new DataRecRowMapper()));
        return dataTable;
    }

    public DataTable    query(String sql, Object... args) throws DataAccessException {
        DataTable dataTable = new DataTable();
        dataTable.addAll(this.query(sql, args, new DataRecRowMapper()));
        return dataTable;
    }

    public DataRec      queryDataRec(String sql) throws DataAccessException { return (DataRec) this.queryForObject(sql, new DataRecRowMapper()); }

    public DataRec      queryDataRec(String sql, Object... args) throws DataAccessException { return (DataRec) this.queryForObject(sql, args, new DataRecRowMapper()); }

    public long         updateForKeyId(String sql, Object... args) {
        KeyHolder holder            = new GeneratedKeyHolder();
        this.update((connection) -> {
            PreparedStatement ps    = connection.prepareStatement(sql, new String[]{"id"});
            int index               = 1;
            Object[] var5           = args;
            int var6                = args.length;
            for (int var7 = 0; var7 < var6; ++var7) {
                Object arg          = var5[var7];
                if (arg instanceof Date) {
                    ps.setTimestamp(index, new Timestamp(((Date) arg).getTime()));
                } else if (arg instanceof Integer) {
                    ps.setInt(index, (Integer) arg);
                } else if (arg instanceof Long) {
                    ps.setLong(index, (Long) arg);
                } else if (arg instanceof Double) {
                    ps.setDouble(index, (Double) arg);
                } else if (arg instanceof Float) {
                    ps.setFloat(index, (Float) arg);
                } else { ps.setString(index, (String) arg); }
                ++index;
            }
            return ps;
        }, holder);
        return holder.getKey().longValue();
    }
}
