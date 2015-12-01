package alfred.server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/alfred")
public class AlfredEndpoint {

    @RequestMapping(value="/get/{id}", method = RequestMethod.GET)
    public String getStatus(@PathVariable String id) {
        System.out.println(id);
        return "Hello, world!";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processForm() {
        return "success.jsp";
    }

}
