package core;



import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.util.JSONObject1O;

import java.math.BigDecimal;
import java.util.*;

public class RPNParser {


    private final static Map<String,SymbolAction> dictionary =new HashMap<String, SymbolAction>(){{
        put("*",new SymbolAction("*",10,  (i1, i2) -> new BigDecimal(i1.toString()).multiply(new BigDecimal(i2.toString()))));
        put("/",new SymbolAction("/", 10, (i1, i2) -> new BigDecimal(i1.toString()).divide(new BigDecimal(i2.toString()),2, BigDecimal.ROUND_HALF_UP)));
        put("%",new SymbolAction("%", 10, (i1, i2) -> new BigDecimal(i1.toString()).remainder(new BigDecimal(i2.toString()))));
        put("+",new SymbolAction("+", 8, (i1, i2) -> new BigDecimal(i1.toString()).add(new BigDecimal(i2.toString()))));
        put("-",new SymbolAction("-", 8, (i1, i2) -> new BigDecimal(i1.toString()).subtract(new BigDecimal(i2.toString()))));
        put(">",new SymbolAction(">", 5, (i1, i2) -> new BigDecimal(i1.toString()).compareTo(new BigDecimal(i2.toString()))>0));
        put("<",new SymbolAction("<", 5, (i1, i2) -> new BigDecimal(i1.toString()).compareTo(new BigDecimal(i2.toString()))<0));
        put(">=",new SymbolAction(">=", 5, (i1, i2) -> new BigDecimal(i1.toString()).compareTo(new BigDecimal(i2.toString()))>=0));
        put("<=",new SymbolAction("<=", 5, (i1, i2) -> new BigDecimal(i1.toString()).compareTo(new BigDecimal(i2.toString()))<=0));
        put("||",new SymbolAction("||", 4, (i1, i2) -> {
            if (i1 instanceof Boolean && i2 instanceof Boolean)
                return (Boolean)i1 || (Boolean)i2;
            throw new RuntimeException("symbol || ,the Class type not found");
        }));
        put("&&",new SymbolAction("&&", 4, (i1, i2) -> {
            if (i1 instanceof Boolean && i2 instanceof Boolean)
                return (Boolean)i1 && (Boolean)i2;
            throw new RuntimeException("symbol && ,the Class type not found");
        }));

        put("==",new SymbolAction("==", 5, (i1, i2) -> {
            boolean digit=false;
            if (i1 instanceof String){
                try {
                    i1=transNumber((String) i1);
                    digit=true;
                }catch (Exception ignored){
                }
            }
            if (!digit && i1 instanceof String && i2 instanceof String ){
                String s1 =(String)i1,s2=(String)i2;
                if (s1.charAt(0)=='"'&&s1.charAt(s1.length()-1)=='"')
                    s1=s1.substring(1,s1.length()-1);
                if (s2.charAt(0)=='"'&&s2.charAt(s2.length()-1)=='"')
                    s2=s2.substring(1,s2.length()-1);
                return s1.equals(s2);
            }
            else if (i1 instanceof Boolean)
                return i1.equals(Boolean.parseBoolean(i2.toString()));
            else if (i1 instanceof BigDecimal)
                return i1.equals(new BigDecimal(i2.toString()));
            else if (i1 instanceof Integer|| i1 instanceof Long || i1 instanceof Float || i1 instanceof Double
                    || i2 instanceof Integer|| i2 instanceof Long || i2 instanceof Float || i2 instanceof Double  )
                return new BigDecimal(i1.toString()).equals(new BigDecimal(i2.toString()));

            throw new RuntimeException("symbol == ,the Class type not found");
        }));
        put("!=",new SymbolAction("!=", 5, (i1, i2) -> {
            boolean digit=false;
            if (i1 instanceof String){
                try {
                    i1=transNumber((String) i1);
                    digit=true;
                }catch (Exception ignored){
                }
            }
            if (!digit && i1 instanceof String && i2 instanceof String ){
                String s1 =(String)i1,s2=(String)i2;
                if (s1.charAt(0)=='"'&&s1.charAt(s1.length()-1)=='"')
                    s1=s1.substring(1,s1.length()-1);
                if (s2.charAt(0)=='"'&&s2.charAt(s2.length()-1)=='"')
                    s2=s2.substring(1,s2.length()-1);
                return !s1.equals(s2);
            }
            else if (i1 instanceof Boolean)
                return !i1.equals(i2);
            else if (i1 instanceof BigDecimal)
                return !i1.equals(new BigDecimal(i2.toString()));
            else if (i1 instanceof Integer|| i1 instanceof Long || i1 instanceof Float || i1 instanceof Double
                    || i2 instanceof Integer|| i2 instanceof Long || i2 instanceof Float || i2 instanceof Double  )
                return ! new BigDecimal(i1.toString()).equals(new BigDecimal(i2.toString()));

            throw new RuntimeException("symbol != ,the Class type not found");
        }));
    }};
    private final static Set<String> boolSet=new HashSet<String>(){{
        add(">");
        add("<");
        add("==");
        add("!=");
        add(">=");
        add("<=");
        add("||");
        add("&&");
    }};

    private final static Set<Character> meant= new HashSet<Character>(){{
        add('$');
        add('.');
        add('"');
    }};

    public static List<String> transToPostfix(String infix){
        List<SegmentDto> segment = segment(infix);
        return transToPostfix(segment);
    }

    public static List<String> transToPostfix(List<SegmentDto> segment) {
        List<String> rs =new ArrayList<>();
        Stack<String> stack=new Stack<>();
        for (SegmentDto s : segment) {
            String cur = s.str;
            if (s.p==1)
                rs.add(cur);
            else{
                if (s.p==3){
                    for (String pop=stack.pop();!pop.equals("(")&&!stack.empty();pop=stack.pop())
                        rs.add(pop);
                    continue;
                }
                if(!stack.empty()&&s.p!=2){
                    String peek = stack.peek();
                    if (peek.equals("(")){
                        stack.push(cur);
                        continue;
                    }
                    int cl = dictionary.get(cur).level;
                    if(dictionary.get(peek).level>cl){
                        while (!stack.empty()&&!(peek=stack.peek()).equals("(")&&dictionary.get(peek).level>=cl)
                            rs.add(stack.pop());
                    }
                }
                stack.push(cur);
            }
        }
        while (!stack.empty()){
            rs.add(stack.pop());
        }
        return rs;
    }


    static List<SegmentDto> segment(String infix){

        List<SegmentDto> list=new ArrayList<>();
        Deque<Character> sp=new LinkedList<>();
        Stack<Integer> t=new Stack<>();
        boolean quotation=false;
        for (int i = 0; i < infix.length(); i++) {
            char c =infix.charAt(i);

            if (!quotation&&c<=' ')
                continue;
            if (c=='"')
                quotation=!quotation;
            int value = isValue(c,quotation);

            if(!sp.isEmpty()){
                int last =t.peek();
                if (last != value){
                    String s="";
                    while (!sp.isEmpty()){
                        s+=sp.pop();
                        t.pop();
                    }
                    list.add(new SegmentDto(s,last));
                }
            }
            sp.add(c);
            t.push(value);
        }

        if (quotation)
            throw new TinyDagException("wrong expression,please check it:"+infix);

        if (!sp.isEmpty()){
            String s="";
            int  last =t.peek();
            while (!sp.isEmpty()){
                s+=sp.pop();
                t.pop();
            }
            list.add(new SegmentDto(s,last));
        }
        return list;
    }
    public static List<String> segmentToString(List<SegmentDto> seg){
        List<String> list =new ArrayList<>();
        for (SegmentDto dto : seg)
            list.add(dto.str);
        return list;
    }

    static int  isValue(char c, boolean quotation){
        if (quotation || c == '"')
            return 1;
        else if (c =='(')
            return 2;
        else if (c==')')
            return 3;
        return ( (c>='A'&&c<='Z') || (c>='a'&&c<='z')|| (c>='0'&&c<='9')
                || meant.contains(c)||(19968 <= c && c <40869))?1:0;
    }



    public static Object decipherPostfixWithJsonObject(List<String> ruleExpression, JSONObject json){
        Stack<Object> stack =new Stack<>();
        // cache is not necessary, that was enough
//        Map<String ,Object> cache=new HashMap<>();
        for (String s : ruleExpression) {
            Object o=s;
            if (s.startsWith("$.")){
//                o = cache.get(s);
//                if (o==null){
                o=ForGetJson(s.substring(2),json);
//                    cache.put(s,o);
//                }
            }
            if (o==null)
                throw new NullPointerException("parameter "+s+" is null,please check it:"+json.toString());

            SymbolAction cal=null;
            if (o instanceof String){
                cal = dictionary.get(s);
            }
            if (cal!=null)
            {
                Object i2 = stack.pop(),
                        i1=stack.pop() ;
                stack.push(cal.toWay.calculate(i1,i2));
            }
            else{
                stack.push(o);
            }
        }
        if (stack.empty()||stack.size()>1)
            throw new TinyDagException("may be wrong postfix expression,please check it:"+ruleExpression.toString());
        return stack.pop();
    }


    public static Object decipherPostfix(List<Object> postfix) {

        Stack<Object> stack = new Stack<>() ;
        for (Object s : postfix) {
            SymbolAction cal=null;
            if (s instanceof String){
                cal = dictionary.get(s);
            }
            if (cal!=null)
            {
                Object i2 = stack.pop(),
                        i1=stack.pop() ;
                stack.push(cal.toWay.calculate(i1,i2));
            }
            else{
                stack.push(s);
            }
        }
        if (stack.empty()||stack.size()>1)
            throw new TinyDagException("may be wrong postfix expression,please check it:"+postfix.toString());

        return stack.pop();
    }

    public static Object ForGetJson(String s, JSONObject json){
        String[] split = s.split("\\.");
        int i=0;
        Object rs =null;
        do {
            if (rs !=null && !(rs instanceof JSONObject))
                rs = JSON.toJSON(rs);
            if ( rs instanceof JSONObject)
                json=(JSONObject) rs;
            rs=json.get(split[i]);
            i++;
        }while(i<split.length);

        return rs;
    }




    public static boolean isNumeric(CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return false;
        } else {
            int sz = cs.length();
            for(int i = 0; i < sz; ++i) {
                if (!Character.isDigit(cs.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    static Number transNumber(String s){
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c>='0'&&c<='9')|| c == '.' || c == 'e' || c == 'E') {
                return new BigDecimal(s);
            }
        }
        throw new NumberFormatException();
    }

    public static boolean isBool(String e){
        return boolSet.contains(e);
    }


    interface ISymbolAction{
        Object calculate(Object i1,Object i2);
    }

    static class SymbolAction{
        int level;
        String symbol;
        ISymbolAction toWay;

        public SymbolAction(String symbol,int level,  ISymbolAction toWay) {
            this.level = level;
            this.symbol = symbol;
            this.toWay = toWay;
        }
    }



    static class SegmentDto {
        String str;
        int p;

        public SegmentDto(String str, int p) {
            this.str = str;
            this.p = p;
        }
    }

    public static void main(String[] args) {



        testBoolCalculate();
    }

    static void testQuotation(){
        List<SegmentDto> s = segment("\"tets\"==\"test/() #$#$#$****test\"");
        System.out.println(segmentToString(s));

    }

    static void testChinese(){
        List<String> s3 = transToPostfix("(\"test\"==\"test1\")||(\"测试\"==\"测试\")");
        System.out.println(s3.toString());
//      ["test", "test", ==, "test", "", !=, ||]
        Object r3=  decipherPostfix(new ArrayList<>(s3));
        System.out.println(r3);
    }

    static void testBoolCalculate(){
        //test2
//        List<String> s2 = transToPostfix("(a==b)||(c==d)");
//        System.out.println(s2.toString());
//        //        //[a, b, ==, c, d, ==, ||]
//        String rs2= decipherPostfix(s2);
//        System.out.println(rs2);


        //test3
        List<String> s3 = transToPostfix("(\"test\"==\"test1\")||(\"test\"==\"\")");
        System.out.println(s3.toString());
//      ["test", "test", ==, "test", "", !=, ||]
        Object r3= decipherPostfix(new ArrayList<>(s3));
        System.out.println(r3);
    }


    static void testCalculate(){
        // test1
        List<String> s1 = transToPostfix("9+(3-1)*3+10/2");
        System.out.println(s1.toString());
        //        [9, 3, 1, -, 3, *, +, 10, 2, /, +]
        Object result1= decipherPostfix(new ArrayList<>(s1));
        System.out.println(result1);


        //test4
        List<String> s4 = transToPostfix("(1+3)*(4+6*3)");
        System.out.println(s4.toString());
        ////[1, 3, +, 4, 6, 3, *, +, *]
        Object result4= decipherPostfix(new ArrayList<>(s4));
        System.out.println(result4);

        //test5
        List<String> s5 = transToPostfix("(1+3)*(6*3+4)");
        System.out.println(s5.toString());
//[1, 3, +, 6, 3, *, 4, +, *]
        Object result5= decipherPostfix(new ArrayList<>(s5));
        System.out.println(result5);

        //test6
        List<String> s6 = transToPostfix("(1+3)*(3.4*2)");
        System.out.println(s6.toString());
        Object result6= decipherPostfix(new ArrayList<>(s6));
        System.out.println(result6);
    }

    static void testCalculate1(){
        List<String> s1 = transToPostfix("3*18!=3*6*1");
        System.out.println(s1.toString());
        Object result6= decipherPostfix(new ArrayList<>(s1));
        System.out.println(result6);
    }
}
