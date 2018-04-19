package pers.janus;

//import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author janus
 * class SchedualServiceHi
 * created On 2018/4/19 22:44
 * description
 */

@FeignClient(value = "service-hi")
public interface SchedualServiceHi {

    @GetMapping(value = "/hi")
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
