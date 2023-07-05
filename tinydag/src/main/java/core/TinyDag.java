package core;

import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TinyDag {

    final private String name;
    private int loop=1;

    final Map<String,Node> stone=new HashMap<>();
    private final ThreadLocal<Track> track=new ThreadLocal<>();
    static String Cont_In="in";
    static String Cont_Out="out";
    static String Cont_Rule="rule";


    Node first;

    public TinyDag(List<Node> nodes,String name) {
        check(nodes);
        if (this.first==null)
            this.first=nodes.get(0);
        this.name=name;
    }

    void check(List<Node> nodes){
        if (nodes==null || nodes.size()<1)
            throw  new TinyDagException("the collection of Node is empty");
        for (Node node : nodes) {
            if (stone.containsKey(node.code))
                throw  new TinyDagException("same code exists :"+node.code);
            stone.put(node.code,node);
        }

        for (Node node : nodes) {
            if (node.outDegree.size()<1)
                continue;
            dfs(node);
        }
        for (Node node : nodes) {
            if (!node.isSkip)
                throw  new TinyDagException("these is a useless node:"+node.code);
            if (node.isFirst)
            {
                if (this.first==null)
                    this.first=node;
                else
                    throw new TinyDagException("a tiny DAG can have only one starting vertex:"+node.code);
            }
        }

    }

    void dfs(Node cur)
    {
        if (cur.isSkip)
            return;
        cur.isSkip=true;
        for (RuleExpression rule : cur.outDegree) {
            Node node = stone.get(rule.code);
            if (node!=null)
                dfs(node);
        }
    }

    void print(){

    }

    public Track run(JSONObject data, FineTune f){
        if (f==null)
            f=new FineTune();
        Track t=new Track(this.loop,f.trackId);
        try {
            if (data==null)return t;
            String dot =f.dotCode;
            boolean isDotSkip=f.isDotSkip;
            Node cur =this.first;
            if (dot!=null && this.stone.containsKey(dot))
                cur=this.stone.get(dot);
            track.set(t);
            while (cur!=null&& cur.action!=null){
                if (t.checkLoop(cur.code))
                    break;
                if (!isDotSkip)
                    t.write(cur.code,Cont_In,data.toString());
                data = cur.action.doAction(data);
                String code = pick(data, cur.code,cur.outDegree);
                cur= this.stone.get(code);
                isDotSkip=false;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            t.setSuccess(false);
            t.setMessage(e.getMessage());
            e.printStackTrace();
        }
        finally {
            track.remove();
        }
        return t;
    }

    String pick(JSONObject rs, String code, List<RuleExpression> rules){
        for (RuleExpression rule : rules) {
            if (boolPostfix(rule,rs)){
                Track track = this.track.get();
                track.write(code,Cont_Rule,rule.expression);
                track.write(code,Cont_Out,rs.toString());
                return rule.code;
            }
        }
        return null;
    }

    boolean boolPostfix(RuleExpression rule   , JSONObject json){
        List<String> postfix=rule.rpnExpression;
        if(!RPNParser.isBool(postfix.get(postfix.size()-1)))
            throw new TinyDagException("this postfix is not bool expression,please check it:"+postfix.toString());
        List<String> postList =new ArrayList<>();
        for (String s : postfix) {
            if (s.startsWith("$."))
                s=forGetJson(s.substring(2),json);
            postList.add(s);
        }
        String s = RPNParser.decipherPostfix(postList);
        return Boolean.parseBoolean(s);
    }

    static String forGetJson(String s,JSONObject json){
        String[] split = s.split("\\.");
        int i=0;
        String rs =null;
        do {
            if (rs!=null)
                json=JSONObject.parseObject(rs);
            rs=json.getString(split[i]);
            i++;
        }while(i<split.length);

        return rs;
    }

    void toTrack(JSONObject json){

    }


    public static  class Node{
        final String code;
        final String name;
        boolean isFirst;
        boolean isSkip;
        List<RuleExpression> outDegree;
        ITempAction action;


        public Node(String code, String name) {
            this.code = code;
            this.name = name;
            outDegree=new ArrayList<>();
        }

        public Node(String code, String name, List<RuleExpression> outDegree) {
            this.code = code;
            this.name = name;
            this.outDegree = outDegree;
        }

        public boolean isFirst() {
            return isFirst;
        }

        public void setFirst(boolean first) {
            isFirst = first;
        }

        public ITempAction getAction() {
            return action;
        }

        public void setAction(ITempAction action) {
            this.action = action;
        }
    }

    public static class FineTune{
        String dotCode;
        boolean isDotSkip=false;
        String trackId;

        public String getDotCode() {
            return dotCode;
        }

        public void setDotCode(String dotCode) {
            this.dotCode = dotCode;
        }

        public boolean isDotSkip() {
            return isDotSkip;
        }

        public void setDotSkip(boolean dotSkip) {
            isDotSkip = dotSkip;
        }

        public String getTrackId() {
            return trackId;
        }

        public void setTrackId(String trackId) {
            this.trackId = trackId;
        }
    }

    public static class RuleExpression{
        String code;
        String expression;
        List<String> segment;
        List<String> rpnExpression;

        public RuleExpression(String code, String expression) {
            this.code = code;
            this.expression = expression;
            List<RPNParser.SegmentDto> segment = RPNParser.segment(expression);
            this.segment = RPNParser.segmentToString(segment);
            this.rpnExpression= RPNParser.transToPostfix(segment);
        }
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public static void main(String[] args) {

    }


    void testGetJsonValue(){
        String jsonStr="\n" +
                "{\n" +
                "    \"dept\":{\n" +
                "        \"deptCode\":\"123\",\n" +
                "        \"employee\":{\n" +
                "            \"name\":\"test\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        JSONObject parse = JSONObject.parse(jsonStr);

        String str= "$.dept.employee.name";
        str = str.substring(2);
        System.out.println(str);
        String s = forGetJson(str, parse);
        System.out.println(s);
    }
}
