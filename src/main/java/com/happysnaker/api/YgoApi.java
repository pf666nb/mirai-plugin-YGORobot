package com.happysnaker.api;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.entry.RowMapper;
import com.happysnaker.utils.IOUtil;
import com.happysnaker.utils.MapGetter;
import com.happysnaker.utils.SqliteHelper;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class YgoApi {

    public static final String NEW = "new";

    public static final String OLD = "old";


    public static final String CDB = "cards.cdb";

    public static final String api = "http://172.245.136.214:8099/ygo-print/ygo-price/-/raw/master/";


    public static String getImage(List<String> tags)  {
        String tag =  tags.stream().findFirst().orElse(OLD)+"/";
        String name =  tags.get(1);
        try {
            SqliteHelper h = new SqliteHelper(RobotConfig.configFolder+"/"+CDB);
            String sql = "select id from texts where name like '%"+name+"%' ";
            List<String> sList = h.executeQuery(sql, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int index)
                        throws SQLException {
                    return rs.getString("id");
                }
            });
            if (sList.size()>=1){
                return api+tag+sList.get(0)+".jpg";
            }else {
                return "";
            }

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
