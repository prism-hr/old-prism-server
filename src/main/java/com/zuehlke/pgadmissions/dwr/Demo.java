package com.zuehlke.pgadmissions.dwr;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.stereotype.Service;

@Service("springService")
@RemoteProxy(name="dwrService")
public class Demo {

	@RemoteMethod
	 public String sayHello(String name) {
        return "Hello, " + name;
    }
}
