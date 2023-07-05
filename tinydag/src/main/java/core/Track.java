package core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Track {

    private String id;
    private String message="success";
    private boolean success=true;
    private int loop;
    private int acLoop=0;
    private String curDot;
    private Map<String,DotInfo> dotMap;

    public Track( int loop,String id) {
        if (id!=null)
            this.id = id;
        else
            this.id = UUID.randomUUID().toString();
        this.loop = loop;
        this.dotMap=new LinkedHashMap<String, DotInfo>();
    }

    void write(String dot,String type,String msg){
        dot+=this.acLoop;
        if (!dotMap.containsKey(dot))
            dotMap.put(dot,new DotInfo(dot));
        dotMap.get(dot).getLogs().add(new DotLog(type,msg));
    }

    boolean checkLoop(String dot){
        dot+=this.acLoop;
        if (dotMap.containsKey(dot))
            this.acLoop++;
        return this.acLoop>this.loop;
    }

    public int getAcLoop() {
        return acLoop;
    }

    public void setAcLoop(int acLoop) {
        this.acLoop = acLoop;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public String getCurDot() {
        return curDot;
    }

    public void setCurDot(String curDot) {
        this.curDot = curDot;
    }

    public Map<String, DotInfo> getDotMap() {
        return dotMap;
    }

    public void setDotMap(Map<String, DotInfo> dotMap) {
        this.dotMap = dotMap;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


}
