package frontend.ctrl;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Time;
import java.util.concurrent.atomic.AtomicInteger;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import frontend.data.GoodSentence;
import frontend.data.Sms;
import jakarta.servlet.http.HttpServletRequest;

import com.github.doda2025_team4.lib.GoodSentenceGenerator;
import com.github.doda2025_team4.lib.VersionUtil;
import java.io.IOException;

@Controller
@RequestMapping(path = "/sms")
public class FrontendController {

    private String modelHost;

    private RestTemplateBuilder rest;

    private final GoodSentenceGenerator goodSentenceGenerator;
    private final String goodSentenceVersionUtilLibName;
    private final String goodSentenceVersionUtilLibVersion;
    // Metrics for A3
    private final AtomicInteger activeRequests;   // GAUGE
    private final Timer predictionTimer;         // TIMER
    private final MeterRegistry registry;

    public FrontendController(RestTemplateBuilder rest, Environment env, MeterRegistry registry) {
        this.rest = rest;
        this.modelHost = env.getProperty("MODEL_HOST");
        this.registry = registry;
        assertModelHost();

        final VersionUtil goodSentenceVersionUtil = new VersionUtil();
        goodSentenceGenerator = new GoodSentenceGenerator();
        goodSentenceVersionUtilLibName = goodSentenceVersionUtil.getName();
        goodSentenceVersionUtilLibVersion = goodSentenceVersionUtil.getVersion();
        // A3 METRICS SETUP
        // 1. GAUGE: Active requests
        this.activeRequests = new AtomicInteger(0);
        Gauge.builder("sms_active_requests", activeRequests, AtomicInteger::get)
             .tag("area", "sms_check") 
             .register(registry);

        // 3. HISTOGRAM (Timer): Distribution of latency
        this.predictionTimer = Timer.builder("sms_prediction_latency")
                .description("Time taken to get a prediction from model service")
                .publishPercentiles(0.5, 0.95, 0.99) // Percentiles for Grafana
                .publishPercentileHistogram(true) // Enable histogram for Grafana
                .register(registry);
    }

    private void assertModelHost() {
        if (modelHost == null || modelHost.strip().isEmpty()) {
            System.err.println("ERROR: ENV variable MODEL_HOST is null or empty");
            System.exit(1);
        }
        modelHost = modelHost.strip();
        if (modelHost.indexOf("://") == -1) {
            var m = "ERROR: ENV variable MODEL_HOST is missing protocol, like \"http://...\" (was: \"%s\")\n";
            System.err.printf(m, modelHost);
            System.exit(1);
        } else {
            System.out.printf("Working with MODEL_HOST=\"%s\"\n", modelHost);
        }
    }

    @GetMapping("")
    public String redirectToSlash(HttpServletRequest request) {
        // relative REST requests in JS will end up on / and not on /sms
        return "redirect:" + request.getRequestURI() + "/";
    }

    @GetMapping("/")
    public String index(Model m) {
        m.addAttribute("hostname", modelHost);
        return "sms/index";
    }

    @PostMapping({ "", "/" })
    @ResponseBody
    public Sms predict(@RequestBody Sms sms) {
        // A3
        activeRequests.incrementAndGet();
        Timer.Sample sample = Timer.start(registry);
        String outcome = "success";
        try{
            System.out.printf("Requesting prediction for \"%s\" ...\n", sms.sms);
            sms.result = getPrediction(sms);
            System.out.printf("Prediction: %s\n", sms.result);
            // Increment counter if prediction was made
            return sms;
        }catch (Exception e) {
            outcome = "error";
            throw e;
         } finally {
            // Record time taken
            sample.stop(predictionTimer);
            activeRequests.decrementAndGet();
            registry.counter("sms_predictions_total", 
                             "component", "frontend_controller", 
                             "outcome", outcome)                
                    .increment();
        }
       
    }

    @GetMapping("/goodsentence")
    @ResponseBody
    public GoodSentence goodsentence() {
        final String goodSentence = goodSentenceGenerator.generateSentence();
        return new GoodSentence(goodSentence, goodSentenceVersionUtilLibName, goodSentenceVersionUtilLibVersion);
    }
    
    private String getPrediction(Sms sms) {
        try {
            var url = new URI(modelHost + "/predict");
            var c = rest.build().postForEntity(url, sms, Sms.class);
            return c.getBody().result.trim();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}