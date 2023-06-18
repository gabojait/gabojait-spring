package com.gabojait.gabojaitspring.log;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class P6spyLogging implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId,
                                String now,
                                long elapsed,
                                String category,
                                String prepared,
                                String sql,
                                String url) {

        return formatSql(sql, elapsed);
    }

    private String formatSql(String sql, long elapsed) {

        String sqlLog = "[DB] " + elapsed + "ms";

        if (sql == null || sql.trim().equals(""))
            return sqlLog;
        else {
            StringBuilder sqlBuilder = new StringBuilder();

            sqlBuilder.append(" | ");

            String[] splitSql = sql.split("(\\/\\*|\\*\\/)");
            for (int i = 1; i < splitSql.length; i++) {
                sqlBuilder.append(splitSql[i].trim());
                if (splitSql.length > i + 1)
                    sqlBuilder.append(" | ");
            }
            return sqlLog + sqlBuilder;
        }
    }
}
