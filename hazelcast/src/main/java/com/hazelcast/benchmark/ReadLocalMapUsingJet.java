package com.hazelcast.benchmark;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.LogTimer;
import com.hazelcast.map.IMap;
import com.hazelcast.sql.SqlResult;
import com.hazelcast.sql.SqlRow;
import com.hazelcast.sql.SqlStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadLocalMapUsingJet {
    protected static HazelcastInstance member;
    protected static IMap map;
    public static void ReadLocalMapBenchmark() throws InterruptedException {

        Config config = new Config();
        config.getJetConfig().setEnabled(true);
        member = Hazelcast.newHazelcastInstance(config);
        map = member.getMap("mymap");
        map.put("key", "value");

        String sql = "SELECT Concat_WS('-', 'a', 'b') FROM mymap";

        int warmupCount = 500;
        int benchmarkCount = 100000;
        Thread.sleep(5000);
        for (int i = 0; i < warmupCount; i++) {
            execute(member, sql);
        }

        LogTimer.active = true;
        com.hazelcast.jet.LogTimer.active = true;

        for (int i = 0; i < benchmarkCount; i++) {
            LogTimer.start("singlequery");
            List<SqlRow> results =  execute(member, sql);
            LogTimer.stop("singlequery");
        }

        LogTimer.ExportHistograms();
        com.hazelcast.jet.LogTimer.ExportHistograms();

        member.shutdown();
    }



    public static List<SqlRow> execute(HazelcastInstance member, String sql, Object... params) {
        SqlStatement query = new SqlStatement(sql);

        if (params != null) {
            query.setParameters(Arrays.asList(params));
        }

        return executeStatement(member, query);
    }

    public static List<SqlRow> executeStatement(HazelcastInstance member, SqlStatement query) {
        List<SqlRow> rows = new ArrayList<>();

        try (SqlResult result = member.getSql().execute(query)) {
            for (SqlRow row : result) {
                rows.add(row);
            }
        }

        return rows;
    }

    public static void main(String[] args) throws InterruptedException {
        ReadLocalMapBenchmark();
    }
}
