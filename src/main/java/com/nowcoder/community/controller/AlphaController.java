package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {//"alpha为给类起的访问名"

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(
                PrintWriter write = response.getWriter();
                ) {
            write.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Get请求

    //  /student?current=1&limit=20
    @RequestMapping(path = "/student", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some Student";
    }

    //  /student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }


    // Post
    @RequestMapping(path="/student", method=RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name+": "+age);
        return "success";
    }


    // 响应HTML数据
    @RequestMapping(path="/teacher", method=RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "张三");
        mav.addObject("age", "30");
        mav.setViewName("/demo/view"); // view.html
        return mav;
    }

    @RequestMapping(path="/school", method=RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name", "北京大学");
        model.addAttribute("age", "120");
        return "/demo/view";
    }
 

    // 响应JSON数据（异步请求：当前网页不刷新，但访问了服务器并返回了数据）
    @RequestMapping(path="/emp", method=RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String, Object> emp = new HashMap<String, Object>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 1000.00);
        return emp;
    }//单个员工

    @RequestMapping(path="/emps", method=RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp1 = new HashMap<String, Object>();
        emp1.put("name", "张三");
        emp1.put("age", 23);
        emp1.put("salary", 1000.00);
        list.add(emp1);

        Map<String, Object> emp2 = new HashMap<String, Object>();
        emp2.put("name", "李四");
        emp2.put("age", 20);
        emp2.put("salary", 2000.00);
        list.add(emp2);

        Map<String, Object> emp3 = new HashMap<String, Object>();
        emp3.put("name", "王五");
        emp3.put("age", 29);
        emp3.put("salary", 4000.00);
        list.add(emp3);

        return list;
    }//多个员工

}
