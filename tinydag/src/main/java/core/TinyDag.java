package core;

import com.alibaba.fastjson2.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

public class TinyDag {

    final private String code;
    final Map<String,Node> stone=new HashMap<>();
    private static final ThreadLocal<Track> track=new ThreadLocal<>();
    private static final ThreadLocal<Object> flotsam=new ThreadLocal<>();

    private String graph;
    private int loop=1;

    static String Cont_In="in";
    static String Cont_Out="out";
    static String Cont_Rule="rule";

    Node first;



    public TinyDag(List<Node> nodes,String code) {
        check(nodes);
        if (this.first==null)
            this.first=nodes.get(0);
        this.code=code;
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
        List<Node> values = new ArrayList<>(this.stone.values());
        for (Node col : values) {
            int[] a = new int[values.size()];
            List<String> outList = col.outDegree.stream().map(RuleExpression::getCode).collect(Collectors.toList());
            for (int i = 0; i < values.size(); i++) {
                Node node = values.get(i);
                if (outList.contains(node.code))
                    a[i]=1;

            }
            System.out.println(col.code+":"+ Arrays.toString(a));
        }

    }

    public Track run(Object boat,JSONObject data, FineTune f){
        if (f==null)
            f=new FineTune();
        Track t=new Track(this.loop,f.trackId,f.idKey);
        try {
            if (data==null)return t;
            String dot =f.dotCode;
            boolean isDotSkip=f.isDotSkip;

            Node cur =this.first;
            if (dot!=null && this.stone.containsKey(dot))
                cur=this.stone.get(dot);
            track.set(t);
            flotsam.set(boat);
            while (cur!=null&& cur.action!=null){
                t.setCurDot(cur.code);
                if (t.checkLoop(cur.code))
                    break;
                t.write(cur.code,Cont_In,data.toString());
                if (!isDotSkip)
                    data = cur.action.doAction(data);
                String code = pick(data, cur.code,cur.outDegree);
                cur= this.stone.get(code);
                isDotSkip=false;
            }
        }catch (Exception e){
            t.setSuccess(false);
            if (e.getMessage()==null || e.getMessage().equals(""))
                t.setMessage("NullPointerException");
            else
                t.setMessage(e.getMessage());
            e.printStackTrace();
            t.setErrorDetail(getStackTrace(e));
        }
        finally {
            track.remove();
            flotsam.remove();
        }
        return t;
    }

    String pick(JSONObject rs, String code, List<RuleExpression> rules){
        Track track = TinyDag.track.get();
        if (rs !=null)
            track.write(code,Cont_Out,rs.toString());
        for (RuleExpression rule : rules) {
            if (boolPostfix(rule,rs)){
                track.write(code,Cont_Rule,rule.expression);
                return rule.code;
            }
        }
        return null;
    }

    boolean boolPostfix(RuleExpression rule   , JSONObject json){
        if (rule.async)
            return false;
        List<String> postfix=rule.rpnExpression;
        if(!RPNParser.isBool(postfix.get(postfix.size()-1)))
            throw new TinyDagException("this postfix is not bool expression,please check it:"+postfix.toString());
        Object o=null;
        try {
            o = RPNParser.decipherPostfixWithJsonObject(postfix, json);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        if (!(o instanceof Boolean))
            throw new TinyDagException("The result of decryption is not a Boolean value");
        return (boolean) o;
    }

    public static Object GetFlotsam(){
        return flotsam.get();
    }

    /**
     * it is dangerous,so just return track id
     */
    public static String GetTrackId(){
        return track.get().getId();
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
            if (outDegree==null)
                outDegree=new ArrayList<>();
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
        String idKey;
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

        public String getIdKey() {
            return idKey;
        }

        public void setIdKey(String idKey) {
            this.idKey = idKey;
        }
    }

    public static class RuleExpression{
        String code;
        String expression;
        List<String> segment;
        List<String> rpnExpression;
        boolean async=false;

        public RuleExpression(String code, String expression) {
            this.code = code;
            this.expression = expression;
            List<RPNParser.SegmentDto> segment = RPNParser.segment(expression);
            this.segment = RPNParser.segmentToString(segment);
            this.rpnExpression= RPNParser.transToPostfix(segment);
        }

        public RuleExpression(String code, String expression ,boolean async) {
            this.code = code;
            this.expression = expression;
            List<RPNParser.SegmentDto> segment = RPNParser.segment(expression);
            this.segment = RPNParser.segmentToString(segment);
            this.rpnExpression= RPNParser.transToPostfix(segment);
            this.async=async;
        }

        public String getCode() {
            return code;
        }

        public boolean isAsync() {
            return async;
        }

        public void setAsync(boolean async) {
            this.async = async;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public List<String> getSegment() {
            return segment;
        }

        public void setSegment(List<String> segment) {
            this.segment = segment;
        }

        public List<String> getRpnExpression() {
            return rpnExpression;
        }

        public void setRpnExpression(List<String> rpnExpression) {
            this.rpnExpression = rpnExpression;
        }
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public String getCode() {
        return code;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    String getStackTrace(Exception e){
        StringWriter stringWriter=new StringWriter();
        PrintWriter printWriter =new PrintWriter(stringWriter,true);
        e.printStackTrace(printWriter);
        String fullInfo = stringWriter.toString();
        return  fullInfo.substring(0, fullInfo.length()> 500?499:fullInfo.length());

    }


    public static void main(String[] args) {
//        RuleExpression ruleExpression=new RuleExpression("SolveTransport","($.directoryThree==\"优先运输\")||($.directoryThree==\"时效外催运输\")",false);
//        System.out.println(JSONObject.toJSONString(ruleExpression));
        testGetJsonValue();
    }


    static void testGetJsonValue(){
        String jsonStr="\n" +
                "{\n" +
                "  \"dt\":\"优先运输1\",\n" +
                "    \"dept\":{\n" +
                "        \"deptCode\":\"123\",\n" +
                "        \"employee\":{\n" +
                "            \"name\":\"test\",\n" +
                "            \"directoryThree\":\"优先运输\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        JSONObject parse = JSONObject.parseObject(jsonStr);
        parse.put("st",new Student("test11",1));
        long l = System.currentTimeMillis();
        RuleExpression ruleExpression=new RuleExpression("SolveTransport","$.st.name==\"优先运输\"||" +
                "$.st.name==\"时效外催运输1\"||$.st.name==\"优先运输1\"||" +
                "$.st.name==\"时效外催运输2\"||$.st.name==\"优先运输2\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"||" +
                "$.st.name==\"时效外催运输3\"||$.st.name==\"时效外催运输3\"",false);

        for (int i=0;i<1000000;i++){
//        System.out.println(ruleExpression.getRpnExpression().toString());
            Object s = RPNParser.decipherPostfixWithJsonObject(ruleExpression.getRpnExpression(), parse);
        }
        System.out.println(System.currentTimeMillis()-l);
    }
}
