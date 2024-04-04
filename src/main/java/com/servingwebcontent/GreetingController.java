package com.servingwebcontent;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GreetingController {

	@RequestMapping("/")
	public String index(Model model) {
		String content = "<h4>Cats available for rent:</h4>\n";
		content += "<div class=\"text-left\" id=\"listing\">\n";
		content += "</div>\n";
		
		content += "  <ol>\r\n"
				+ "    <li><img src=\"/images/cat1.jpg\" alt=\"Jennyanydots\" height=\"300\" width=\"300\"></li>\r\n"
				+ "    <br>\r\n"
				+ "    <li><img src=\"/images/cat2.jpg\" alt=\"Old Deuteronomy\" height=\"300\" width=\"300\"></li>\r\n"
				+ "    <br>\r\n"
				+ "    <li><img src=\"/images/cat3.jpg\" alt=\"Mistoffelees\"  height=\"300\" width=\"300\"></li>\r\n"
				+ "  </ol>";
		model.addAttribute("content", content);
		return "index";
	}

	@RequestMapping("/rent-a-cat")
	public String rentACat(Model model) {
		String content = "<h4>Cats available for rent:</h4>\n";
		content += "<div class=\"text-left\" id=\"listing\">\n";
		content += "</div>\n";
		
		content += "<div class=\"form-group row\">\n";
		content += "<div class=\"col-xs-5 text-left\">\n";
		content += "<label for=\"rentID\">Enter the ID of the cat to rent:</label>\n";
		content += "</div>\n";
		content += "<div class=\"col-xs-2 text-left \">\n";
		content += "<input type=\"text\" class=\"form-control\" id=\"rentID\">\n";
		content += "</div>\n";
		content += "<div class=\"col-xs-2 text-left\">\n";
		content += "<button class=\"btn btn-primary\" onclick=\"rentSubmit()\">Rent</button>\n";
		content += "</div>\n";
		content += "<div class=\"col-xs-3 text-left\" id=\"rentResult\">\n";
		content += "</div>\n";
		content += "</div>\n";
		
		content += "<div class=\"form-group row\">\n";
		content += "<div class=\"col-xs-5 text-left\">\n";
		content += "<label for=\"returnID\">Enter the ID of the cat to return:</label>\n";
		content += "</div>\n";
		content += "<div class=\"col-xs-2 text-left \">\n";
		content += "<input type=\"text\" class=\"form-control\" id=\"returnID\">\n";
		content += "</div>\n";
		content += "<div class=\"col-xs-2 text-left\">\n";
		content += "<button class=\"btn btn-primary\" onclick=\"returnSubmit()\">Return</button>\n";
		content += "</div>\n";
		content += "<div class=\"col-xs-3 text-left\" id=\"returnResult\">\n";
		content += "</div>\n";
		content += "</div>\n";
			
		model.addAttribute("content", content);
		return "index";
	}

	@RequestMapping("/feed-a-cat")
	public String feedACat(Model model) {
		String content = "<h4>Cats available for rent:</h4>\n";
		content += "<div class=\"text-left\" id=\"listing\">\n";
		content += "</div>\n";
		
		content += "<div class=\"form-group row\">\n";
		content += "<div class=\"col-xs-5 text-left\">\n";
		content += "<label for=\"catnips\">Number of catnips to feed:</label>\n";
		content += "</div>\n";
		content += "<div class=\"col-xs-2 text-left \">\n";
		content += "<input type=\"text\" class=\"form-control\" id=\"catnips\">\n";
		content += "</div>\n";
		content += "<div class=\"col-xs-2 text-left\">\n";
		content += "<button class=\"btn btn-primary\" onclick=\"feedSubmit()\">Feed</button>\n";
		content += "</div>\n";
		content += "<div class=\"col-xs-3 text-left\" id=\"feedResult\">\n";
		content += "</div>\n";
		content += "</div>\n";
		
		content += "<div class=\"form-group row\">\n";
		content += "<div class=\"col-xs-10 text-left\">\n";
		content += "Warning: If catnips cannot be divided evenly, fights may occur.";
		content += "</div>\n";
		content += "</div>\n";
		
		model.addAttribute("content", content);
		return "index";
	}

	@RequestMapping("/greet-a-cat")
	public String greetACat(Model model) {
		String content = "<h4>Cats available for rent:</h4>\n";
		content += "<div class=\"text-left\" id=\"listing\">\n";
		content += "</div>\n";
		
		content += "<div class=\"row\" id=\"greeting\"><h4>\n";
		content += "</h4></div>\n";
		content += "<script>greetSubmit(\"\");</script>\n";
		
		model.addAttribute("content", content);
		return "index";
	}

	@RequestMapping("/greet-a-cat/{name}")
	public String greetACatWithName(@PathVariable String name, Model model) {
		String content = "<h4>Cats available for rent:</h4>\n";
		content += "<div class=\"text-left\" id=\"listing\">\n";
		content += "</div>\n";
		
		content += "<div class=\"row\" id=\"greeting\">\n";
		content += "</div>\n";
		content += "<script>greetSubmit(\"" + name + "\");</script>\n";
		
		model.addAttribute("content", content);
		return "index";
	}
	
	@RequestMapping("/reset")
	public String reset(Model model) {
		String content = "<h4>Cats available for rent:</h4>\n";
		content += "<div class=\"text-left\" id=\"listing\">\n";
		content += "</div>\n";
		
		content += "<script>reset();</script>\n";
		
		model.addAttribute("content", content);
		return "index";
	}
}
