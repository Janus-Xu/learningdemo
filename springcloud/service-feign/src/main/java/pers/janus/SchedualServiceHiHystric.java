package pers.janus;

import org.springframework.stereotype.Component;

/**
 * @author janus
 *         class SchedualServiceHiHystric
 *         created On 2018/4/20 0:13
 *         description
 */
@Component
public class SchedualServiceHiHystric implements SchedualServiceHi {
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry "+name;
    }
}
