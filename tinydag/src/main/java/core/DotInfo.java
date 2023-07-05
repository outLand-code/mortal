package core;

import java.util.ArrayList;
import java.util.List;


public class DotInfo {

    private String code;
    private List<DotLog> logs;

    public DotInfo(String code) {
        this.code = code;
        this.logs = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<DotLog> getLogs() {
        return logs;
    }

    public void setLogs(List<DotLog> logs) {
        this.logs = logs;
    }
}
