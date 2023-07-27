package core;

import java.util.Date;
import java.util.UUID;

public class DotLog {

    private String id;
    private String type;
    private String detail;
    private Date createTime;

    public DotLog(String type, String detail) {
        this.id= UUID.randomUUID().toString();
        this.createTime=new Date();
        this.type = type;
        this.detail = detail;
    }

    public DotLog() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
