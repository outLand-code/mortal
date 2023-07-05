package core;



import java.util.*;

public class RPNParser {

    private final static Map<String,SymbolAction> dictionary =new HashMap<String, SymbolAction>(){{
        put("*",new SymbolAction("*",10,  (i1, i2) -> String.valueOf(Integer.parseInt(i1)*Integer.parseInt(i2))));
        put("/",new SymbolAction("/", 10, (i1, i2) -> String.valueOf(Integer.parseInt(i1)/Integer.parseInt(i2))));
        put("%",new SymbolAction("%", 10, (i1, i2) -> String.valueOf(Integer.parseInt(i1)%Integer.parseInt(i2))));
        put("+",new SymbolAction("+", 8, (i1, i2) -> String.valueOf(Integer.parseInt(i1)+Integer.parseInt(i2))));
        put("-",new SymbolAction("-", 8, (i1, i2) -> String.valueOf(Integer.parseInt(i1)-Integer.parseInt(i2))));
        put(">",new SymbolAction(">", 5, (i1, i2) -> String.valueOf(Integer.parseInt(i1)>Integer.parseInt(i2))));
        put("<",new SymbolAction("<", 5, (i1, i2) -> String.valueOf(Integer.parseInt(i1)<Integer.parseInt(i2))));
        put("==",new SymbolAction("==", 5, (i1, i2) -> String.valueOf(i1.replaceAll("\"","").equals(i2.replaceAll("\"","")))));
        put("!=",new SymbolAction("!=", 5, (i1, i2) -> String.valueOf(!i1.replaceAll("\"","").equals(i2.replaceAll("\"","")))));
        put(">=",new SymbolAction(">=", 5, (i1, i2) -> String.valueOf(Integer.parseInt(i1)>=Integer.parseInt(i2))));
        put("<=",new SymbolAction("<=", 5, (i1, i2) -> String.valueOf(Integer.parseInt(i1)<=Integer.parseInt(i2))));
        put("||",new SymbolAction("||", 5, (i1, i2) -> String.valueOf(Boolean.parseBoolean(i1)||Boolean.parseBoolean(i2))));
        put("&&",new SymbolAction("&&", 5, (i1, i2) -> String.valueOf(Boolean.parseBoolean(i1)&&Boolean.parseBoolean(i2))));
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

        for (int i = 0; i < infix.length(); i++) {
            char c =infix.charAt(i);
            if (c<=' ')
                continue;
            int value = isValue(c);
            if(!sp.isEmpty()){
                int last =t.peek();
                if (( last != value)){
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
            list.add(dto.str+":"+dto.p);
        return list;
    }

    static int  isValue(char c){
        if (c =='(')
            return 2;
        else if (c==')')
            return 3;
        return ( (c>='A'&&c<='Z') || (c>='a'&&c<='z')|| (c>='0'&&c<='9')
                || meant.contains(c)||(19968 <= c && c <40869))?1:0;
    }



    public static String decipherPostfix(List<String> postfix) {

        Stack<String> stack = new Stack<>() ;
        for (String s : postfix) {
            SymbolAction cal = dictionary.get(s);
            if (cal!=null)
            {
                String i2 = stack.pop(),
                        i1=stack.pop() ;
                stack.push(cal.toWay.calculate(i1,i2));
            }else{
                stack.push(s);
            }
        }
        if (stack.empty()||stack.size()>1)
            throw new TinyDagException("may be wrong postfix expression,please check it"+postfix.toString());

        return stack.pop();
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

    public static boolean isBool(String e){
        return boolSet.contains(e);
    }


    interface ISymbolAction{
        String calculate(String i1, String i2);
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
        List<SegmentDto> segment = segment("(a==b))||(c==d)");
        System.out.println(segmentToString(segment));
        List<String> postfix = transToPostfix(segment);
        System.out.println(postfix.toString());
//        List<String> s3 = transToPostfix("\"测试%￥@#t测试\"==\"\test\"");
//        System.out.println(s3.toString());
//      ["test", "test", ==, "test", "", !=, ||]
//        String r3= decipherPostfix(s3);
//        System.out.println(r3);

    }

    void testBoolCalculate(){
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
        String r3= decipherPostfix(s3);
        System.out.println(r3);
    }


    void testCalculate(){
        // test1
        List<String> s1 = transToPostfix("9+(3-1)*3+10/2");
        System.out.println(s1.toString());
        //        [9, 3, 1, -, 3, *, +, 10, 2, /, +]
        String result1= decipherPostfix(s1);
        System.out.println(result1);


        //test4
        List<String> s4 = transToPostfix("(1+3)*(4+6*3)");
        System.out.println(s4.toString());
        ////[1, 3, +, 4, 6, 3, *, +, *]
        String result4= decipherPostfix(s4);
        System.out.println(result4);

        //test5
        List<String> s5 = transToPostfix("(1+3)*(6*3+4)");
        System.out.println(s5.toString());
//[1, 3, +, 6, 3, *, 4, +, *]
        String result5= decipherPostfix(s5);
        System.out.println(result5);


    }
}
