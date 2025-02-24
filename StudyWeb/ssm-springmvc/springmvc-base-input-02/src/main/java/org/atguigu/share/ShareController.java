package org.atguigu.share;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("share")
@ResponseBody
public class ShareController {

    @Autowired
    private ServletContext servletContext;

    public void data(HttpServletRequest request, HttpSession session) {


    }

    //springmvc提供的方法:request提供了几种{了解)
    // model modelMap map modelAndView

    public void data1(Model model) {
        model.addAttribute("key", "value");//request
    }

        public void datal(ModelMap model){
            model.addAttribute("key","value");//request
        }
}