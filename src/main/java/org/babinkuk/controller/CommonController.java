package org.babinkuk.controller;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.common.Echo;
import org.babinkuk.common.ProducesJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.babinkuk.controller.Api.ROOT;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ROOT)
public class CommonController {
	
	@Autowired
	private Environment environment;
	
	// expose GET "/echo"
	@GetMapping("/echo")
	@ProducesJson
	public Echo echo() {
		return new Echo();
	}
	
	// expose GET "/config"
	@GetMapping("/config")
	@ProducesJson
	public ResponseEntity<ApiResponse> getAppConfig() {
		
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("application name", environment.getProperty("info.app.name", "Unknown"));
		propertyMap.put("version", environment.getProperty("info.app.version", "Unknown"));
		propertyMap.put("author", environment.getProperty("info.app.author", "Unknown"));
		
		return new ApiResponse(HttpStatus.OK, null, propertyMap).toEntity();
	}
	
}
