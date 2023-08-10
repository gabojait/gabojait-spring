package com.gabojait.gabojaitspring.log;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;

public class P6spyLogging implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId,
                                String now,
                                long elapsed,
                                String category,
                                String prepared,
                                String sql,
                                String url) {
        return formatSql(connectionId, category, sql, elapsed);
    }

    private String formatSql(int connectionId, String category, String sql, long elapsed) {
        String uuid = InterceptorLogging.getRequestId() == null ? "SYSTEM" : InterceptorLogging.getRequestId();
        String sqlLog = "[" + uuid  + " | DATABASE] connectionId = " + connectionId + " | time = " + elapsed + " ms";

        if (sql == null || sql.trim().equals(""))
            return sqlLog;

        if (Category.STATEMENT.getName().equals(category)) {
            String tmpSql = sql.trim().toLowerCase();

            if (tmpSql.startsWith("create")
                    || tmpSql.startsWith("drop")
                    || tmpSql.startsWith("alter")
                    || tmpSql.startsWith("truncate"))
                sqlLog = sqlLog + FormatStyle.DDL.getFormatter().format(sql);
            else
                sqlLog = sqlLog + FormatStyle.BASIC.getFormatter().format(sql);
        }

        return sqlLog;
    }
}
