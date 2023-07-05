package core;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Test {

    public static void main(String[] args) {
        TinyDag.Node a =new TinyDag.Node("test_a","a");
        TinyDag.Node b =new TinyDag.Node("test_b","b");
        TinyDag.Node c =new TinyDag.Node("test_c","c");
        TinyDag.Node d =new TinyDag.Node("test_d","d");
        TinyDag.Node e =new TinyDag.Node("test_e","e");
        TinyDag.Node f =new TinyDag.Node("test_f","f");
        TinyDag.Node g =new TinyDag.Node("test_g","g");

        a.isFirst=true;
        a.outDegree=new ArrayList<TinyDag.RuleExpression>(){{
            add(new TinyDag.RuleExpression("test_b","$.deptCode==\"W1020\""));
            add(new TinyDag.RuleExpression("test_c","$.deptCode==\"W1021\""));
        }};
        a.action= data -> {
            System.out.println("a.action");
            data.put("deptCode","W1021");
            return data;
        };
        b.action= data -> {
            System.out.println("b.action");
            return data;
        };
        c.outDegree=new ArrayList<TinyDag.RuleExpression>(){{
            add(new TinyDag.RuleExpression("test_d","$.dept.name==\"testA\""));
        }};
        c.action= data -> {
            System.out.println("c.action");
            data.put("dept",new Student("testA",123));
            return data;
        };
        d.outDegree=new ArrayList<TinyDag.RuleExpression>(){{
            add(new TinyDag.RuleExpression("test_e","$.createTime> 1234"));
        }};
        d.action= data -> {
            System.out.println("d.action");
            data.put("createTime",1235);
            return data;
        };

        e.outDegree=new ArrayList<TinyDag.RuleExpression>(){{
            add(new TinyDag.RuleExpression("test_f","$.deptName!= \"\""));
        }};
        e.action= data -> {
            System.out.println("e.action");
            data.put("deptName","dept");
            return data;
        };

        f.outDegree=new ArrayList<TinyDag.RuleExpression>(){{
            add(new TinyDag.RuleExpression("test_g"," (3*18+$.var)==60"));
        }};
        f.action= data -> {
            System.out.println("f.action");
            Integer id = (Integer) data.get("id");
            if (id==1)
                data.put("test111",data.get("name"));

            data.put("var","6");
            return data;
        };
        g.action= data -> {
            System.out.println(data.toString());
            return null;
        };

        List<TinyDag.Node> list =new ArrayList<TinyDag.Node>(){{
           add(a);
           add(b);
           add(c);
           add(d);
           add(e);
           add(f);
           add(g);
        }};
        TinyDag tinyDag = new TinyDag(list,"test");

        Thread t1= new Thread(() -> {
            Student track = new Student();
            track.setName(UUID.randomUUID().toString());
            track.setId(1);
            JSONObject parse = JSONObject.parse(JSON.toJSONString(track));
            System.out.println(parse.toString());

            Track run = tinyDag.run(parse, null);
            System.out.println(JSONObject.toJSONString(run));
        });
        Thread t2= new Thread(() -> {
            Student track = new Student();
            track.setName(UUID.randomUUID().toString());
            track.setId(2);
            JSONObject parse = JSONObject.parse(JSON.toJSONString(track));
            System.out.println(parse.toString());
            Track run = tinyDag.run(parse, null);
            System.out.println(JSONObject.toJSONString(run));
        });
        t1.start();
        t2.start();

        while (true){

        }



    }
}
